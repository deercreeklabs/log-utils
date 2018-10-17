(ns deercreeklabs.log-utils
  (:require
   [#?(:clj clj-time.format :cljs cljs-time.format) :as f]
   [#?(:clj clj-time.core :cljs cljs-time.core) :as t]
   #?(:clj [puget.printer :refer [cprint]])
   [schema.core :as s]
   [taoensso.timbre :as timbre :refer [debugf errorf infof]])
  #?(:cljs
     (:require-macros
      deercreeklabs.log-utils)))

(defmacro sym-map
  "Builds a map from symbols.
   Symbol names are turned into keywords and become the map's keys.
   Symbol values become the map's values.
  (let [a 1
        b 2]
    (sym-map a b))  =>  {:a 1 :b 2}"
  [& syms]
  (zipmap (map keyword syms) syms))

(s/defn ex-msg :- s/Str
  [e]
  #?(:clj (.toString ^Exception e)
     :cljs (.-message e)))

(s/defn ex-stacktrace :- s/Str
  [e]
  #?(:clj (clojure.string/join "\n" (map str (.getStackTrace ^Exception e)))
     :cljs (.-stack e)))

(s/defn ex-msg-and-stacktrace :- s/Str
  [e]
  (str "\nException:\n"
       (ex-msg e)
       "\nStacktrace:\n"
       (ex-stacktrace e)))

(defn log-ex [e]
  (errorf (ex-msg-and-stacktrace e)))

(s/defn short-log-output-fn :- s/Str
  [data :- {(s/required-key :level) s/Keyword
            s/Any s/Any}]
  (let [{:keys [level msg_ ?ns-str ?file ?line]} data
        formatter (f/formatters  :hour-minute-second-ms)
        timestamp (f/unparse formatter (t/now))]
    (str
     timestamp " "
     (clojure.string/upper-case (name level))  " "
     "[" (or ?ns-str ?file "?") ":" (or ?line "?") "] - "
     @msg_)))

(defn current-time-ms
  []
  #?(:clj (System/currentTimeMillis)
     :cljs (.getTime (js/Date.))))

;; We have to name this pprint* to not conflict with
;; clojure.repl/pprint, which gets loaded into the repl's namespace
(defn pprint*
  "Pretty-prints its argument. Color is used in clj, but not cljs."
  [x]
  (#?(:clj cprint
      :cljs cljs.pprint/pprint) x)
  nil)

(defn pprint-str
  "Like pprint, but returns a string."
  [x]
  (with-out-str
    (pprint* x)))

(defmacro debugs [& syms]
  (let [fmt-str (->> syms
                     (map #(str (name %) ": \n%s"))
                     (clojure.string/join)
                     (str "\n"))
        pps (map (fn [sym]
                   `(pprint-str ~sym)) syms)]
    `(debugf ~fmt-str ~@pps)))

(defmacro do-spy [label x]
  `(do
     (debugf "= spy = %s: \n%s" ~label (pprint-str ~x))
     ~x))

(defmacro spy
  "For debugging in -> expressions.  Identical to spy-first."
  [x label]
  `(do-spy ~label ~x))

(defmacro spy-first
  "For debugging in -> expressions. Identical to spy."
  [x label]
  `(do-spy ~label ~x))

(defmacro spy-last
  "For debugging in ->> expressions."
  [label x]
  `(do-spy ~label ~x))
