(ns org.boteval.nlueval.accuracy
  (:require
      [org.boteval.nlueval.util :refer :all]
      [cheshire.core :refer [generate-string] :rename {generate-string to-json}]
      [clojure.data.csv :as csv]
      [clojure.java.io :as io]
      [clojure.set :refer [union]]
      [clojure.pprint :refer [pprint]]
      [puget.printer :refer [cprint]]
      [clojure.inspector :as inspect :refer [inspect-tree]]))


(defn get-accuracy-at
  [{:keys
     [objects-tagging
      gold
      test-tagging-group-name
      test-tag
      n]}]

  {:pre
    [(keyword? gold)
     (keyword? test-tagging-group-name)]
     (keyword? test-tag)
     (number? n)}

  " calculates accuracy at n, over the provided objects taggging collection
    see https://www.wikiwand.com/en/Precision_and_recall#/Precision "

  (letfn
    [(row-evaluation
       [object-tagging]
           (let [object-id (key object-tagging)
                 taggings-groups (val object-tagging)

                 gold-taggings (:taggings (get-single #(val=! % :tagging-group-name gold) taggings-groups))
                 gold-tags (set (map #(:tag %) gold-taggings))

                 test-taggings (:taggings (get-single #(val=! % :tagging-group-name test-tagging-group-name) taggings-groups))
                 test-tags (set (map #(:tag %) (take n test-taggings)))

                 positive?  (contains? gold-tags test-tag)
                 predicted? (contains? test-tags test-tag)
                 true-positive?  (and positive? predicted?)
                 false-positive? (and (not positive?) predicted?)]

             { :positive? positive?
               :predicted? predicted?
               :true-positive? true-positive?
               :false-positive? false-positive? }))]

      (let
        [row-evaluations (map row-evaluation objects-tagging)

         positives (count (filter #(:positive? %) row-evaluations))
         true-positives (count (filter #(:true-positive? %) row-evaluations))
         false-positives (count (filter #(:false-positive? %) row-evaluations))]

          { :positives positives
            :true-positives true-positives
            :false-positives false-positives

            :precision (undef-or-divide
                          true-positives
                          (+ true-positives false-positives))

            :recall (undef-or-divide
                       true-positives
                       positives) }
        )))