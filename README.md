# Humio CLI log viewer written in ClojureScript

[![CircleCI](https://circleci.com/gh/kipz/oak.svg?style=svg)](https://circleci.com/gh/kipz/oak)

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