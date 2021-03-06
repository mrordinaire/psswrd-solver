(ns psswrd-solver.selection
  (:require [psswrd-solver.solutions :as solutions]))

(defn last-false-index-of
  ([^booleans array]
   (last-false-index-of array (alength array)))
  ([^booleans array ^Integer upto-excluding]
   (loop [idx (dec upto-excluding)]
     (cond
       (< idx 0) nil
       (= false (aget array idx)) idx
       :else (recur (dec idx))))))

(defn first-false-index-of [^booleans array]
  (loop [idx 0]
     (cond
       (= idx (alength array)) nil
       (= false (aget array idx)) idx
       :else (recur (inc idx)))))

;; NOT thread-safe
(defrecord Selection [^String alphabet ^Integer code-length ^booleans state]
  solutions/Solution
  (current [this]
    (apply str (->> alphabet
                    (map vector state)
                    (filter first)
                    (map second))))
  (next [this]
    (let [first-false-index (first-false-index-of state)
          last-false-index (last-false-index-of state)]
      (if (and (= last-false-index (dec (alength state)))
               (= first-false-index code-length))
        ;; [true, true, ..., true, false, false, ..., false]
        ;; no more valid code
        nil
        (let [selected-false-index (if (= last-false-index (dec (alength state)))
                                     (loop [selected (last-false-index-of state last-false-index)
                                            prev last-false-index]
                                       (if (>= selected (dec prev))
                                         (recur (last-false-index-of state selected)
                                                selected)
                                         selected))
                                     last-false-index)]
          (aset state selected-false-index true)
          (aset state (inc selected-false-index) false)
          (let [nfalse (loop [i (inc selected-false-index)
                              c 0]
                         (cond
                           (= i (alength state)) c
                           (= false (aget state i)) (recur (inc i) (inc c))
                           :else (recur (inc i) c)))]
            (java.util.Arrays/fill state
                                   (inc selected-false-index)
                                   (+ selected-false-index nfalse 1)
                                   false)
            (java.util.Arrays/fill state
                                   (+ selected-false-index nfalse 1)
                                   (alength state)
                                   true)
            this))))))

(defrecord SelectionAllButOne [^String alphabet ^Integer index]
  solutions/Solution
  (current [this] (str (.substring alphabet 0 index)
                       (.substring alphabet (inc index) (.length alphabet))))
  (next [this]
    (let [new-index (inc index)]
      (if (= new-index (.length alphabet))
        nil
        (SelectionAllButOne. alphabet new-index)))))

(defrecord SelectionOne [^String alphabet ^Integer index]
  solutions/Solution
  (current [this] (.charAt alphabet index))
  (next [this]
    (let [new-index (inc index)]
      (if (= new-index (.length alphabet))
        nil
        (SelectionOne. alphabet new-index)))))

(defrecord SelectionAll [^String alphabet]
  solutions/Solution
  (current [this] alphabet)
  (next [this] nil))

(defn make [^String alphabet ^Integer length]
  (let [alphabet-length (.length alphabet)]
    (cond
      (<= length 0) (throw (IllegalArgumentException. (str "length must be greater than zero. Got " length ".")))
      (> length alphabet-length) (throw (IllegalArgumentException. (str "length must be less than or equal to length of alphabet. Got " length ".")))
      (= length alphabet-length) (SelectionAll. alphabet)
      (= length (dec alphabet-length)) (SelectionAllButOne. alphabet 0)
      (= length 1) (SelectionOne. alphabet 0)
      :else (let [array (boolean-array alphabet-length)]
              (java.util.Arrays/fill array 0 (- alphabet-length length) false)
              (java.util.Arrays/fill array (- alphabet-length length) alphabet-length true)
              ;; array is now [false, false, ..., false, true, true, ..., true]
              (Selection. alphabet length array)))))
