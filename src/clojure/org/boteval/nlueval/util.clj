(ns org.boteval.nlueval.util
  (:require
    [clojure.pprint :refer [pprint]]
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
))

(defn as-float [variable]
  " takes an argument of unknown primitive type, and transforms it to a float without crashing on any case.
    useful e.g. for data being read from text or csv files "
  (condp instance? variable
    String (Float/parseFloat variable)
    Long (float variable)))


(defn abs-distance
  [a b]
  (let [distance (- a b)]
       (max distance (- distance))))

(defn square [distance] (* distance distance))


(defn undef-or-divide [a b]
  " divides a by b, or returns :undef if b is zero "
  (if (= b 0) :undef
    (float (/ a b))))


(defn has-nth? [col n]
  (let [max-index (- (count col) 1)]
    (>= max-index n)))


(defn capped! [coll]
  " for use when outputing collections as exception text
    alternatively `(binding [*print-length* 4] (print-str coll))` "
  (let [max-to-include 4
        maybe-truncated (take max-to-include coll)]
    (if (> (count coll) max-to-include)
      (print-str maybe-truncated "...")
      (print-str maybe-truncated))))


(defn get! [map key]
  " get the value of key in the map, or throw if the key is not found "
  (or (get map key) (throw (Exception. (str "key " key " not found in map " (capped! map))))))


(defn get-single [pred coll]
  " get collection element matching the given predicate,
    expecting only a single match to exist "
  (let [matches (filter pred coll)]
    (case (count matches)
      1 (first matches)
      0 (throw (Exception. (str "no match found in the collection: " (capped! matches))))
      (throw (Exception. (str "a unique match is expected but multiple matches are found in the collection:" (capped! matches)))))))


(defn val=! [map key expected-value]
  " checks whether the value of the given key equals the expected value.
    if the given key does not exists, throws an exception. "
  (= (get! map key) expected-value))


(defn write-csv [headers data filename]
  " not in use "
  (with-open [out-file (io/writer "output" "out-file.csv")]
  (csv/write-csv out-file
                 [["abc" "def"]
                  ["ghi" "jkl"]])))
