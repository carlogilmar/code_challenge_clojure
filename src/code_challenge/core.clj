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
     [:error (nth previous_validation 1)]

     (= (get (nth previous_validation 1) :active_card) false)
     [:error :card_not_active]

     (= (get (nth previous_validation 1) :active_card) true)
     [:ok (nth previous_validation 1) (nth previous_validation 2)]
     )
   )
  )

(defn verify_account_limit
  ([previous_validation]
   (def response (nth previous_validation 0))
   (cond
     (= response :error)
     [:error (nth previous_validation 1)]

     (<
       (get (nth previous_validation 1) :available_limit)
       (get (nth previous_validation 2) :amount))
     [:error :insufficient_limit (nth previous_validation 1)]

     (<
       (get (nth previous_validation 2) :amount)
       (get (nth previous_validation 1) :available_limit))
     [:ok (nth previous_validation 1) (nth previous_validation 2)]
     )
   )
  )

(defn apply_validation_interval
  [account transaction]
    [:ok account transaction]
  )

(defn verify_frequency_interval
  ([previous_validation]
   (def response (nth previous_validation 0))
   (cond
     (= response :error)
     [:error (nth previous_validation 1) (nth previous_validation 2)]
     (= (count (get (nth previous_validation 1) :authorized_transactions)) 0)
     [:ok (nth previous_validation 1) (nth previous_validation 2)]
     (= (count (get (nth previous_validation 1) :authorized_transactions)) 1)
     [:ok (nth previous_validation 1) (nth previous_validation 2)]
     (> (count (get (nth previous_validation 1) :authorized_transactions)) 1)
     (apply_validation_interval (nth previous_validation 1) (nth previous_validation 2))
     )
   )
  )
