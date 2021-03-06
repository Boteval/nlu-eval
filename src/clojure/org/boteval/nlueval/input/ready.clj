(ns org.boteval.nlueval.input.ready
  (:require
    [org.boteval.nlueval.util :refer :all]
    [clojure.pprint :refer [pprint]]
    [puget.printer :refer [cprint]]
    [clojure.inspector :as inspect :refer [inspect-tree]]
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [cheshire.core :refer [generate-string] :rename {generate-string to-json}]
    [org.boteval.nlueval.input.read :refer :all]
    [org.boteval.nlueval.input.canonicalize :refer :all]))


(defn ready-data [config-label]

  " create a map with all we need to use the input data "

  (let
    [data (read-data config-label)

     gold (:gold-set data)

     gold-taggings (doall (get-canonical-tagging data gold))

     target-tag-set
       (get-tag-set (flatten (map :taggings gold-taggings)))

     classifiers-under-test
       (:result-sets data)

     classifiers-under-test-taggings
       (flatten
          (map
             (partial get-canonical-tagging data)
             classifiers-under-test))

     all-taggings
       (apply merge gold-taggings classifiers-under-test-taggings)

     ; list of all objects for classification
     objects-tagging
       (group-by :object-id all-taggings)]

     (println "gold tagging contains" (count target-tag-set) "unique tags:")
     (cprint target-tag-set)

     (to-map
       gold
       classifiers-under-test
       target-tag-set
       objects-tagging)))
