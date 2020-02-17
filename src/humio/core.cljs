(ns humio.core
  (:require [cljs.pprint :refer [pprint]]
            [cljs.core.async :refer [<! >! timeout chan]]
            [cljs-node-io.core :as io :refer [slurp spit]]
            ["request" :as request]
            [goog.string :refer [format]]
            [cli-matic.core :refer [run-cmd]]
            [clojure.string :as str]
            [clojure.walk :as walk])
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]))

(defn json->clj
  [s]
  (when-let [v (js/JSON.parse s)]
    (js->clj v :keywordize-keys true)))

(defn clj->json
  [x]
  (js/JSON.stringify (clj->js x) nil 2))

(defn ->obj
  [s & {:keys [keywordize-keys] :or {keywordize-keys true}}]
  (js->clj (js/JSON.parse s) :keywordize-keys keywordize-keys))

(defn ->str [obj]
  (js/JSON.stringify (clj->js obj) nil 0))

(defn ^:api search
  "Make a query to humio search api - use request api to get chunking to work!"
  [{:keys [api-key query repo start] :as opts} output-chan callback]
  (let [body (->str {:queryString query :isLive true :start start})]
    (.on (request (clj->js {
                            :method "POST"
                            :url (format "https://cloud.humio.com/api/v1/repositories/%s/query" repo)
                            :body body
                            :headers {"Authorization" (format "Bearer %s" api-key)
                                      "Content-Type" "application/json"
                                      "Accept" "application/x-ndjson"}})
                  callback)
         "data"
         #(go (>! output-chan %)))))

(defn render-line
  [fields line]
  (try
    (let [msg (->obj line :keywordize-keys false)
          extra (str/join " " (map #(get msg %) fields))]
      (println (format "%s %s %s" (get msg "@timestamp") extra (get msg "@rawstring"))))
    (catch :default e
      (println "Broken line: " line))))

(defn run-search
  [opts]
  (go
   (let [result-chan (chan)
         chunk-chan (chan)
         callback (fn [error, response, body]
                    (go
                     (>! result-chan {:error error :response response :body body})))]
     (search opts chunk-chan callback)

     ;; try to split up the lines - we get nd-json lines split across chunks!
     (loop [line nil]
       (let [chunk (str line (<! chunk-chan))
             idx (str/index-of chunk "\n")]
         (if idx
           (do
             (render-line (:fields opts) (subs chunk 0 idx))
             (if (= idx (- (count chunk) 1))
               (recur nil)
               (recur (subs chunk (+ idx 1)))))
           (recur chunk))))
     ;; this blocks until chunked response exists - maybe never?
     (<! result-chan))))

(def config
  {:app         {:command     "humio"
                 :description "A humio CLI"
                 :version     "0.0.1"}

   :global-opts [{:option  "api-key"
                  :as      "Humio API key"
                  :type    :string
                  :env      "HUMIO_API_KEY"
                  :default :present}]

   :commands    [{:command     "query"
                  :description "Query logs in humio via REST API"
                  :opts        [{:option "repo" :short "r" :as "Repository" :type :string :default :present}
                                {:option "query" :short 0 :as "Query expressions" :type :string :default ""}
                                {:option "fields" :short "f" :as "Additional fields to display" :type :string :multiple true}
                                {:option "start" :short "s" :as "Relative time e.g. 1minute, 24hours etc" :type :string :default "24hours"}]
                  :runs        run-search}]})

(defn ^:export cli
  "blah"
  [& args]
  (run-cmd args config))
