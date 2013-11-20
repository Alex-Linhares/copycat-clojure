(ns copycat-clojure.formulas
  )

(declare weighted-average)
(declare fake-reciprocal)


(defn update-temperature [ws rule & rule-weakness]
  1/0 ;; untested
  (let [rule (:rule ws)
        clamp-temperature (:clamp-temperature ws)
        total-unhappiness (:total-strength ws)]
    ;; inverted the if-logic
    (if clamp-temperature
      [:temperature (weighted-average [[total-unhappiness 0.8]
                                       [rule-weakness 0.2]])]
      [:rule-weakness (if rule
                        (fake-reciprocal (:total-strength rule))
                        100)])))


(defn temperature-adjusted-value [temperature x]
  (Math/pow x
            (+ 0.5
               (/ (- 100.0 temperature)
                  30.0))))

  
(defn temperature-adjusted-probability [temperature x]
  "Compared with this modified python code:
    def temperatureAdjustedProbability(temperature, x):
            if not x or x == 0.5 or not temperature:
                    return x
            if x < 0.5:
                    return 1.0 - temperatureAdjustedProbability(temperature, 1.0 - x)
            coldness = 100.0 - temperature
            a = math.sqrt(coldness)
            b = 10.0 - a
            c = b / 100
            d = c * (1.0 - (1.0 - x))  # aka c * x, but we're following the java
            e = (1.0 - x) + d
            f = 1.0 - e
            return max(f, 0.5)
"
  (cond (or (zero? x)
            (= x 0.5)
            (zero? temperature)) x
        (< x 0.5) (- 1.0 (temperature-adjusted-probability temperature (- 1 x)))
        :else (let [coldness (- 100 temperature)
                    a (Math/sqrt coldness)
                    b (- 10 a)
                    c (/ b 100)
                    d (* c (- 1 (- 1 x)))
                    e (+ (- 1 x) d)
                    f (- 1 e)
                    ]
                (max f 0.5))))

