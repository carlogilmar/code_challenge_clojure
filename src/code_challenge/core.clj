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

  (def first_transaction (first(get account :authorized_transactions)))
  (def second_transaction (second(get account :authorized_transactions)))
  (def third_transaction (first(rest(rest(get account :authorized_transactions)))))

  (def first_transaction_diff (- (get transaction :time) (get first_transaction :time)))
  (def second_transaction_diff (- (get transaction :time) (get second_transaction :time)))
  (def third_transaction_diff (- (get transaction :time) (get third_transaction :time)))

  (def first_comparation (< first_transaction_diff 120000))
  (def second_comparation (< second_transaction_diff 120000))
  (def third_comparation (< third_transaction_diff 120000))

  (cond
    (= [first_comparation second_comparation third_comparation] [true true true])
    [:error :high_frequency_small_interval account]
    (= [first_comparation second_comparation third_comparation] [true true false])
    [:ok :last_transaction_in_interval account transaction]
    (= [first_comparation second_comparation third_comparation] [true false false])
    [:ok :second_transaction_in_interval account transaction]
    )
  )

(defn verify_frequency_interval
  ([previous_validation]
   (def response (nth previous_validation 0))
   (cond
     (= response :error)
     [:error (nth previous_validation 1) (nth previous_validation 2)]
     (< (count (get (nth previous_validation 1) :authorized_transactions)) 3)
     [:ok (nth previous_validation 1) (nth previous_validation 2)]
     (> (count (get (nth previous_validation 1) :authorized_transactions)) 2)
     (apply_validation_interval (nth previous_validation 1) (nth previous_validation 2))
     )
   )
  )

(defn verify_doubled_transaction
  ([previous_validation]
   (def response (nth previous_validation 0))
   (cond
     (= response :error)
     [:error (nth previous_validation 1) (nth previous_validation 2)]
     )
   )
  )
