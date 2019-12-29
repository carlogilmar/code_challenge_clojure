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
