(ns code-challenge.core-test
	(:use clojure.test
				code-challenge.core))

(deftest account-not-initialized-test
				 (testing "Given an not initialized account there coudn't be a transaction"
									(def account {:status :not_initialized})
									(def transaction {:amount 10})
									(is (= [:error, :not_initialized] (verify_account_initialized account transaction)))
									))

(deftest account-initialized-test
				 (testing "Given an initialized account there coudn't be a transaction"
									(def account {:status :initialized})
									(def transaction {:amount 10})
									(is (= [:ok, account, transaction] (verify_account_initialized account transaction)))
									))
