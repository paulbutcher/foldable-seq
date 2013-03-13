foldable-seq
============

Clojure's fold function falls back to a sequential reduce when given a lazy sequence.

This library provides a `foldable-seq` function that takes a lazy sequence and returns a sequence that supports parallel fold operations.
