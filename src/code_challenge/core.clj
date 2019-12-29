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

   (def response (nth previous_validation 0))

   (cond
     (= response :error)
     [:error (first (rest previous_validation))]

     (and (= response :ok)
          (= (get (nth previous_validation 1) :active_card) false))
     [:error :card_not_active]

     (and (= (nth previous_validation 0) :ok)
          (= (get (nth previous_validation 1) :active_card) true))
     [:ok (nth previous_validation 1) (nth previous_validation 2)]
     )
   )
  )
