(ns grave.util
  (:use rip.core
        rip.util
        rip.validation
        rip.middleware
        ring.util.response))

(defmacro defview
  [name layout args & body]
  `(defn ~name
     ~args
     (~layout ~@body)))

(defmacro edit
  [scope & body]
  `(GET ~scope {:name :edit :path "/:id/edit"} ~@body))

(defmacro new*
  [scope & body]
  `(GET ~scope {:name :new :path "/new"} ~@body))

(defmacro if->
  ""
  [value pred & body]
  `(if-not ~pred
     ~value
     (-> ~value
         ~@body)))

(defn find-resource
  [scope handler resource-name & [opts]]
  (wrap scope
        (fn [h]
          (fn [r]
            (if-let [resource (handler r)]
              (h (assoc-globals r {resource-name resource})))))
        opts))

(defn parse-route-params
  [scope route-params & [opts]]
  (wrap scope
        (fn [h]
          (fn [r]
            (h (assoc r :params
                      (reduce
                       (fn [params [param type]]
                         (if-let [value (param params)]
                           (assoc params
                             param
                             ((parsers type) value))
                           params))
                       (:params r)
                       route-params)))))
        opts))
