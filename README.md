# Humio CLI log viewer written in ClojureScript

To connect a repl:

```bash
npm run watch
```

and in a different shell

```bash
node index.js
```

Connect an nrepl to 8778, and run

```clojure
(shadow/repl :script)
```