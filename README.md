# Clojure API Test

A very simple Clojure API test. It extends [this API](http://docs.apis.is/#endpoint-rides) and provides extra information which is available inside the link that is provided with the request.

## Installation

```
git clone https://github.com/mateodif/clj-api-test.git
cd clj-api-test
lein deps
```

## Usage

To start the server, simply do:
```
lein run
```

Routes available:
- /api/drivers
- /api/passengers

Send a GET request to any of the above routes to get a response.

## Using the REPL

If you want to test different functions of this project, start a REPL with `lein repl` and write:
```
=> (jetty/run-jetty app {:port 3000 :join? false})
```

## Missing features

- Asyncronous scraping (!!)
- Testing
- A nice documentation

### Bugs

You tell me (just kidding :])

## License

Copyright © 2021 Mateo Difranco

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
