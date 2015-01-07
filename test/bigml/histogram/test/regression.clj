;; Copyright 2013, 2014, 2015 BigML
;; Licensed under the Apache License, Version 2.0
;; http://www.apache.org/licenses/LICENSE-2.0

(ns bigml.histogram.test.regression
  (:require [bigml.histogram.core :refer :all]
            [bigml.histogram.test.data :refer :all]
            [clojure.test :refer :all]))

(deftest regression
  (doseq [impl [:array :tree]]
    (let [hist1 (reduce insert!
                        (create :bins 5 :reservoir impl)
                        (normal-data 10000 "foobar"))
          hist2 (reduce insert!
                        (create :bins 5 :gap-weighted? true :reservoir impl)
                        (normal-data 10000 "foobar"))
          hist3 (-> (create :bins 5 :gap-weighted? true :reservoir impl)
                    (merge! hist1)
                    (merge! hist2))
          hist4 (reduce insert!
                        (create :bins 5 :reservoir impl)
                        (range 10000))]
      (is (= (bins hist1)
             [{:mean -2.7259974460814123 :count 2}
              {:mean -1.1574646171369178 :count 3084}
              {:mean 0.2412603184010284 :count 5499}
              {:mean 1.501029261036911 :count 1380}
              {:mean 2.533583238997322 :count 35}]))
      (is (= (uniform hist1 4)
             [-0.7666356829061056 0.021135736998988408 0.6797410001023193]))
      (is (= (percentiles hist1 0.1 0.25 0.5 0.75 0.9)
             {0.1 -1.464097238986707
              0.25 -0.7666356829061056
              0.5 0.021135736998988408
              0.75 0.6797410001023193
              0.9 1.2985544934152586}))

      (is (= (bins hist2)
             [{:mean -1.6700668633443512 :count 1253}
              {:mean -0.6569429338227806 :count 2892}
              {:mean 0.17849155903996963 :count 3118}
              {:mean 0.9890975726243382 :count 2046}
              {:mean 1.9159997685247632 :count 691}]))
      (is (= (uniform hist2 4)
             [-0.7280715773614671 -0.011706946280661379 0.7039664863326011]))
      (is (= (percentiles hist2 0.1 0.25 0.5 0.75 0.9)
             {0.1 -1.4112977365563462
              0.25 -0.7280715773614671
              0.5 -0.011706946280661379
              0.75 0.7039664863326011
              0.9 1.362326576879395}))

      (is (= (bins hist3)
             [{:mean -1.3062149467418505 :count 4339}
              {:mean -0.6569429338227806 :count 2892}
              {:mean 0.21854789044608106 :count 8617}
              {:mean 0.9890975726243382 :count 2046}
              {:mean 1.6543455050552967 :count 2106}]))
      (is (= (uniform hist3 4)
             [-0.8226057328357175 0.05155256216027926 0.5999864501637645]))
      (is (= (percentiles hist3 0.1 0.25 0.5 0.75 0.9)
             {0.1 -1.3967362803202819
              0.25 -0.8226057328357175
              0.5 0.05155256216027926
              0.75 0.5999864501637645
              0.9 1.3532639541983427}))

      (is (= (bins hist4)
             '({:mean 669.0 :count 1339}
               {:mean 2675.0 :count 2673}
               {:mean 4680.5 :count 1338}
               {:mean 6685.5 :count 2672}
               {:mean 9010.5 :count 1978})))
      (is (= (uniform hist4 4)
             '(2541.061374142397 5112.189357410602 7424.275271894938)))
      (is (= (percentiles hist4 0.1 0.25 0.5 0.75 0.9)
             {0.1 1114.7853492345948
              0.25 2541.061374142397
              0.5 5112.189357410602
              0.75 7424.275271894938
              0.9 8997.58286262782})))))

(def ^:private sepal-lengths
  [5.1 4.9 4.7 4.6 5.0 5.4 4.6 5.0 4.4 4.9 5.4 4.8 4.8 4.3 5.8 5.7 5.4
   5.1 5.7 5.1 5.4 5.1 4.6 5.1 4.8 5.0 5.0 5.2 5.2 4.7 4.8 5.4 5.2 5.5
   4.9 5.0 5.5 4.9 4.4 5.1 5.0 4.5 4.4 5.0 5.1 4.8 5.1 4.6 5.3 5.0 7.0
   6.4 6.9 5.5 6.5 5.7 6.3 4.9 6.6 5.2 5.0 5.9 6.0 6.1 5.6 6.7 5.6 5.8
   6.2 5.6 5.9 6.1 6.3 6.1 6.4 6.6 6.8 6.7 6.0 5.7 5.5 5.5 5.8 6.0 5.4
   6.0 6.7 6.3 5.6 5.5 5.5 6.1 5.8 5.0 5.6 5.7 5.7 6.2 5.1 5.7 6.3 5.8
   7.1 6.3 6.5 7.6 4.9 7.3 6.7 7.2 6.5 6.4 6.8 5.7 5.8 6.4 6.5 7.7 7.7
   6.0 6.9 5.6 7.7 6.3 6.7 7.2 6.2 6.1 6.4 7.2 7.4 7.9 6.4 6.3 6.1 7.7
   6.3 6.4 6.0 6.9 6.7 6.9 5.8 6.8 6.7 6.7 6.3 6.5 6.2 5.9])

(deftest iris-regression
  (doseq [impl [:array :tree]]
    (is (= (map vals (bins (reduce insert!
                                   (create :bins 32 :reservoir impl)
                                   sepal-lengths)))
           '((4.3 1) (4.425000000000001 4) (4.6 4) (4.771428571428571 7)
             (4.9625 16) (5.1 9) (5.2 4) (5.3 1) (5.4 6) (5.5 7) (5.6 6)
             (5.7 8) (5.8 7) (5.9 3) (6.0 6) (6.1 6) (6.2 4) (6.3 9)
             (6.4 7) (6.5 5) (6.6 2) (6.7 8) (6.8 3) (6.9 4) (7.0 1)
             (7.1 1) (7.2 3) (7.3 1) (7.4 1) (7.6 1) (7.7 4) (7.9 1))))))
