# Humio CLI log viewer written in ClojureScript

[![CircleCI](https://circleci.com/gh/kipz/oak.svg?style=svg)](https://circleci.com/gh/kipz/oak)
[![npm](https://img.shields.io/npm/v/%40kipz%2Foak)](https://www.npmjs.com/package/@kipz/oak)

## Installation

```
npm i @kipz/oak -g
```

## Running

Export an environment variable called `HUMIO_API_KEY`

```
oak query -r "<repo-name>" -s ["<human readable start time e.g. 20s>"] -f ["<field to include>"...] ["<query defaults to all>"]
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
