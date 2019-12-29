(ns code-challenge.core)

(defn verify_account_initialized
  ([account, transaction]
   (cond
     (= (get account :status) :not_initialized) [:error, :not_initialized]
     (= (get account :status) :initialized) [:ok, account, transaction]
     )
   )
  )

(defn verify_account_active
  ([previous_validation]
    (cond
      (= (first previous_validation) :error)
        [:error (first (rest previous_validation))]
      (and (= (nth previous_validation 0) :ok)
           (= (get (nth previous_validation 1) :active_card) true))
        [:ok (nth previous_validation 1) (nth previous_validation 2)]
      (and (= (nth previous_validation 0) :ok)
           (= (get (nth previous_validation 1) :active_card) false))
        [:error :card_not_active]
      )
   )
  )
