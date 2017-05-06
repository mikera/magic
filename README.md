# Magic

An experimental language, strongly influenced by Clojure

## Intended features

- Immutable "contexts"
- Smart *optional* static type system with dependent types and type inference
- First class abstractions 
- Expansion passing style for ultra-powerful macros
- Strong integration with Java 8 functional code
- Mostly compatible with Clojure (most idiomatic regular Clojure code will work in Magic, advanced features may not be compatible)

## Notes on compilation approach
- Text representation of source is read as forms by the Reader
- Forms are converted to AST nodes
- AST nodes are expanded into new AST nodes. During expansion, any symbols used are recorded and used to build a symbol dependency list