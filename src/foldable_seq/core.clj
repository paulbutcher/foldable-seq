(ns foldable-seq.core
  (:require [clojure.core.reducers :as r]))

;; Bind to core.reducer's private ForkJoin functions
(def fjinvoke #'r/fjinvoke)
(def fjfork #'r/fjfork)
(def fjjoin #'r/fjjoin)
(def fjtask #'r/fjtask)

(defn- foldseq [num-tasks coll chunk-size combinef reducef]
  (fjinvoke (fn []
    (let [chunk->task (fn [chunk] (fjfork (fjtask #(reduce reducef (combinef) chunk))))]
      (reduce combinef 
        (map #(fjjoin (first (doall %)))
          (partition-all num-tasks 1 (map chunk->task (partition-all chunk-size coll)))))))))

(defn foldable-seq
  "Given a sequence, return a sequence that supports parallel fold.
  The sequence is consumed incrementally in chunks of size n
  (where n is the group size parameter passed to fold). No more than
  num-tasks (default 10) will be processed in parallel."
  ([coll] (foldable-seq 10 coll))
  ([num-tasks coll]
    (reify
      clojure.core.protocols/CollReduce
      (coll-reduce [_ f]
        (clojure.core.protocols/coll-reduce coll f (f)))
      (coll-reduce [_ f init]
        (clojure.core.protocols/coll-reduce coll f init))
      
      r/CollFold
      (coll-fold [_ n combinef reducef]
        (foldseq num-tasks coll n combinef reducef)))))
