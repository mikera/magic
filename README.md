# Magic

Magic is an experimental language, strongly influenced by Clojure. 

## Objectives

- Productivity like **Clojure**
- Performance like **Java**
- Type safety like **Haskell**

## Examples
```clojure
;; Obligatory....
(println "Hello World")

;; Typical functional programming operations such as map, reduce etc. are available
(defn add2 [x] (+ x 2))
(map add2 [1 2 3])
;; result: [3 4 5]

;; Compilation is delayed until all symbolic dependencies are available 
(def a [1 b])
(def b 2)
(println a)
;; result: [1 2]


```


## Intended features

This is an EXPERIMENT in programming language design, combining several big ideas from different programming languages:

- A smart **static type system** that makes it easy to write correct code without boilerplate
- Lisp concepts of **homoiconicity** and macro-driven metaprogramming
- **Functional programming** concepts of programming with pure functions and **immutable values**
- The ability to run on the excellent **JVM platform** and take advantage of the huge library ecosystem this gives you
- The (I believe novel?) concept of programming with a succession of **immutable environments**

## Documentation

See the [Wiki|https://github.com/mikera/magic/wiki]

## Contributing

Magic is a 100% open source project, licensed under the EPL.

You are encouraged to try it out, get involved and give feedback.

Please do not use Magic for production products or serious applications at present, unless you have an extremely high tolerance for frequent breaking changes.
