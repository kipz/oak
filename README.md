# Humio CLI log viewer written in ClojureScript

[![CircleCI](https://circleci.com/gh/kipz/oak.svg?style=svg)](https://circleci.com/gh/kipz/oak)
[![npm](https://img.shields.io/npm/v/%40kipz%2Foak)](https://www.npmjs.com/package/@kipz/oak)

## Installation

```
npm i @kipz/oak -g
```

## Running

Export an environment variable called `HUMIO_API_KEY` (or provided the `api-key` argument)

```
oak query -r <repo-name> [opts...] ["query string"]
```

Query sub-command options:

```
NAME:
 oak query - Query logs in humio via REST API

USAGE:
 oak query [command options] query ...

OPTIONS:
   -r, --repo S*             Repository
       --query S             Query expressions
   -u, --unix F    false     Unix timestamps instead of human readable ones
   -f, --fields S            Additional fields to display
   -s, --start S   2minutes  Relative time e.g. 1minute, 24hours etc
   -?, --help

```

## Development

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
