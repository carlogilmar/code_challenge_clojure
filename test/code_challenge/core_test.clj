(ns code-challenge.core-test
  (:use clojure.test
        code-challenge.core))

(deftest account-not-initialized-test
         (testing "Given an not initialized account I expect an error"
                  (def account {:status :not_initialized})
                  (def transaction {:amount 10})
                  (is (= [:error, :not_initialized] (verify_account_initialized account transaction)))
                  ))

(deftest account-initialized-test
         (testing "Given an initialized account I expect the account and the transaction"
                  (def account {:status :initialized})
                  (def transaction {:amount 10})
                  (is (= [:ok, account, transaction] (verify_account_initialized account transaction)))
                  ))

(deftest account-not-active-test
         (testing "Given an error from previous validation I expect the previous error"
                  (is (= [:error, :not_initialized] (verify_account_active [:error, :not_initialized])))
                  )
         )

(deftest account-not-active-test
         (testing "Given a not active account from previous success validation I expect an error"
                  (def account {:active_card false})
                  (def transaction {:amount 10})
                  (def success_previous_validation [:ok, account, transaction])
                  (is (= [:error :card_not_active] (verify_account_active success_previous_validation)))
                  )
         )

(deftest account-active-test
         (testing "Given an active account from previous success validation I expect the account and transaction"
                  (def account {:active_card true})
                  (def transaction {:amount 10})
                  (def success_previous_validation [:ok account transaction])
                  (is (= [:ok account transaction] (verify_account_active success_previous_validation)))
                  )
         )

(deftest insufficient-limit-error-test
         (testing "Given an error from previous validation I expect the previous error"
                  (is (= [:error, :card_not_active] (verify_account_limit [:error, :card_not_active])))
                  )
         )

(deftest insufficient-limit-verify-with-error-test
         (testing "Given an account and a transaction with different amounts I expected an error"
                  (def account {:active_card true :available_limit 100})
                  (def transaction {:amount 200})
                  (def success_previous_validation [:ok account transaction])
                  (is (= [:error :insufficient_limit account] (verify_account_limit success_previous_validation)))
                  )
         )

(deftest insufficient-limit-verify-success-test
         (testing "Given an account and a transaction with less amount than the limit I expected the account and transaction"
                  (def account {:active_card true :available_limit 100})
                  (def transaction {:amount 20})
                  (def success_previous_validation [:ok account transaction])
                  (is (= [:ok account transaction] (verify_account_limit success_previous_validation)))
                  )
         )

(deftest high-frequency-small-interval-test-preivous_error
         (testing "Given an error from previous validation I expected the error, the error msg and the account"
                  (def account {:active_card true :available_limit 100 :authorized_transactions '()})
                  (def failed_previous_validation [:error :insufficient_limit account])
                  (is (= [:error :insufficient_limit account] (verify_frequency_interval failed_previous_validation)))
                  )
         )

(deftest high-frequency-small-interval-test-case1
         (testing "Given an account without transactions I expected the account and the transaction"
                  (def account {:active_card true :available_limit 100 :authorized_transactions '()})
                  (def transaction {:amount 20})
                  (def success_previous_validation [:ok account transaction])
                  (is (= [:ok :first_transactions account transaction] (verify_frequency_interval success_previous_validation)))
                  )
         )

(deftest high-frequency-small-interval-test-case2
         (testing "Given an account with 1 transaction I expected the account and the transaction"
                  (def account {:active_card true :available_limit 100 :authorized_transactions '({:amount 20})})
                  (def transaction {:amount 20})
                  (def success_previous_validation [:ok account transaction])
                  (is (= [:ok :first_transactions account transaction] (verify_frequency_interval success_previous_validation)))
                  )
         )

(deftest high-frequency-small-interval-test-case3
         (testing "Given an account with 2 transaction (1 in interval) I expected the account and the transaction"
                  (def account
                    {:active_card true
                     :available_limit 100
                     :authorized_transactions
                     '({:amount 20 :time 1577686868777} {:amount 20 :time 1577686688777})
                     }
                    )
                  (def transaction {:amount 20 :time 1577686928777})
                  (def success_previous_validation [:ok account transaction])
                  (is (= [:ok :first_transactions account transaction] (verify_frequency_interval success_previous_validation)))
                  )
         )

(deftest high-frequency-small-interval-test-case4
         (testing "Given an account with 3 transactions (3 in interval) I expected an error"
                  (def account
                    {:active_card true
                     :available_limit 100
                     :authorized_transactions
                     '({:amount 20 :time 1577686918777}
                       {:amount 20 :time 1577686888777}
                       {:amount 20 :time 1577686828777})
                     }
                    )
                  (def transaction {:amount 20 :time 1577686928777})
                  (def success_previous_validation [:ok account transaction])
                  (is (= [:error :high_frequency_small_interval account] (verify_frequency_interval success_previous_validation)))
                  )
         )

(deftest high-frequency-small-interval-test-case5
         (testing "Given an account with 3 transactions (2 in interval) I expected the account and the transaction"
                  (def account
                    {:active_card true
                     :available_limit 100
                     :authorized_transactions
                     '({:amount 20 :time 1577686918777}
                       {:amount 20 :time 1577686888777}
                       {:amount 20 :time 1577686778777})
                     }
                    )
                  (def transaction {:amount 20 :time 1577686928777})
                  (def success_previous_validation [:ok account transaction])
                  (is (= [:ok :last_transaction_in_interval account transaction] (verify_frequency_interval success_previous_validation)))
                  )
         )

(deftest high-frequency-small-interval-test-case6
         (testing "Given an account with 3 transactions (1 in interval) I expected the account and the transaction"
                  (def account
                    {:active_card true
                     :authorized_transactions
                     '({:amount 20 :time 1577686918777}
                       {:amount 20 :time 1577686778777}
                       {:amount 20 :time 1577686778777})
                     }
                    )
                  (def transaction {:amount 20 :time 1577686928777})
                  (def success_previous_validation [:ok account transaction])
                  (is (= [:ok :second_transaction_in_interval account transaction] (verify_frequency_interval success_previous_validation)))
                  )
         )

(deftest doubled-transaction-test-case-previous-error
         (testing "Given an error from previous validation I expect the previous error"
                  (def account {:active_card true})
                  (def transaction {:amount 20 :time 1577686928777})
                  (def previous_validation [:error :high_frequency_small_interval account])
                  (is (= [:error :high_frequency_small_interval account] (verify_doubled_transaction previous_validation)))
                  )
         )

(deftest doubled-transaction-test-case0
         (testing "Given an account with 0 transaction similar to the new transaction I expected the account and transaction"
                  (def account
                    {:active_card true
                     :available_limit 100
                     :authorized_transactions
                     '()
                     }
                    )
                  (def transaction {:amount 20 :merchant "Nubank" :time 1577686928777})
                  (def success_previous_validation [:ok :first_transactions account transaction])
                  (is (= [:ok account transaction] (verify_doubled_transaction success_previous_validation)))
                  )
         )

(deftest doubled-transaction-test-case1
         (testing "Given an account with 1 transaction similar to the new transaction I expected an error"
                  (def account
                    {:active_card true
                     :available_limit 100
                     :authorized_transactions
                     '({:amount 20 :merchant "Nubank" :time 1577686918777})
                     }
                    )
                  (def transaction {:amount 20 :merchant "Nubank" :time 1577686928777})
                  (def success_previous_validation [:ok :first_transactions account transaction])
                  (is (= [:error :doubled_transaction account] (verify_doubled_transaction success_previous_validation)))
                  )
         )

(deftest doubled-transaction-test-case2
         (testing "Given an account with 1 transaction similar to the new transaction I expected an error"
                  (def account
                    {:active_card true
                     :available_limit 100
                     :authorized_transactions
                     '({:amount 20 :merchant "Nubank1" :time 1577686918777},
                       {:amount 40 :merchant "Nubank2" :time 1577686918777}
                       )
                     }
                    )
                  (def transaction {:amount 40 :merchant "Nubank2" :time 1577686928777})
                  (def success_previous_validation [:ok :first_transactions account transaction])
                  (is (= [:error :doubled_transaction account] (verify_doubled_transaction success_previous_validation)))
                  )
         )

(deftest doubled-transaction-test-case3
         (testing "Given an account with 0 transaction similar to the new transaction I expected the account and the transaction"
                  (def account
                    {:active_card true
                     :available_limit 100
                     :authorized_transactions
                     '({:amount 20 :merchant "Nubank" :time 1577686918777})
                     }
                    )
                  (def transaction {:amount 25 :merchant "Nubank" :time 1577686928777})
                  (def success_previous_validation [:ok :first_transactions account transaction])
                  (is (= [:ok account transaction] (verify_doubled_transaction success_previous_validation)))
                  )
         )

(deftest doubled-transaction-test-case4
         (testing "Given an account with 1 transaction similar to the new transaction I expected an error"
                  (def account
                    {:active_card true
                     :available_limit 100
                     :authorized_transactions
                     '({:amount 20 :merchant "Nubank1" :time 1577686918777},
                       {:amount 42 :merchant "Nubank2" :time 1577686917777}
                       {:amount 43 :merchant "Nubank3" :time 1577686917777}
                       {:amount 44 :merchant "Nubank4" :time 1577686917777}
                       )
                     }
                    )
                  (def transaction {:amount 20 :merchant "Nubank1" :time 1577686928777})
                  (def success_previous_validation [:ok :second_transaction_in_interval account transaction])
                  (is (= [:error :doubled_transaction account] (verify_doubled_transaction success_previous_validation)))
                  )
         )

(deftest doubled-transaction-test-case5
         (testing "Given an account with 1 transaction similar to the new transaction I expected an error"
                  (def account
                    {:active_card true
                     :available_limit 100
                     :authorized_transactions
                     '({:amount 20 :merchant "Nubank1" :time 1577686918777},
                       {:amount 42 :merchant "Nubank2" :time 1577686917777}
                       {:amount 43 :merchant "Nubank3" :time 1577686917777}
                       {:amount 44 :merchant "Nubank4" :time 1577686917777}
                       )
                     }
                    )
                  (def transaction {:amount 42 :merchant "Nubank2" :time 1577686928777})
                  (def success_previous_validation [:ok :last_transaction_in_interval account transaction])
                  (is (= [:error :doubled_transaction account] (verify_doubled_transaction success_previous_validation)))
                  )
         )

(deftest doubled-transaction-test-case6
         (testing "Given an account without transactions similar to the new transaction I expected the account and transaction"
                  (def account
                    {:active_card true
                     :available_limit 100
                     :authorized_transactions
                     '({:amount 20 :merchant "Nubank1" :time 1577686918777},
                       {:amount 42 :merchant "Nubank2" :time 1577686917777}
                       {:amount 43 :merchant "Nubank3" :time 1577686917777}
                       {:amount 44 :merchant "Nubank4" :time 1577686917777}
                       )
                     }
                    )
                  (def transaction {:amount 20 :merchant "Nubank" :time 1577686928777})
                  (def success_previous_validation [:ok :last_transaction_in_interval account transaction])
                  (is (= [:ok account transaction] (verify_doubled_transaction success_previous_validation)))
                  )
         )
