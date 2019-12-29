(ns code-challenge.core)

(defn verify_account_initialized
  ([account, transaction]
   (cond
     (= (get account :status) :not_initialized) [:error, :not_initialized]
     (= (get account :status) :initialized) [:ok, account, transaction]
     )
   )
  )
