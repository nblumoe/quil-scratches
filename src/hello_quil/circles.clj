(ns hello-quil.circles
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn point-on-circle [angle]
  [(q/cos angle)
   (q/sin angle)])

(defn points-on-circle [start diff]
  (lazy-seq (cons (point-on-circle start)
                  (points-on-circle (+ start diff) diff))))

(defn setup []
  (q/smooth)
  (q/frame-rate 30)
  {:noise-seed (q/random 10)
   :radius 10})

(defn update-state [state]
  {:radius (if (:grow state)
             (+ (:radius state) 0.3)
             (- (:radius state) 0.3))
   :grow (cond
           (> (:radius state) 100) false
           (< (:radius state) 20) true
           :default (:grow state))})

(defn- draw-circle
  [pos state]
  (q/translate pos)
  (let [num-points 30]
    (doseq [[x y] (take num-points
                        (points-on-circle (* (q/millis) 0.0009)
                                          (/ Math/PI num-points 0.5)))]
      (let [radius (+ (* 20 (q/noise (rand-int 2))) (:radius state))
            xr (* x radius)
            yr (* y radius)]
        (q/fill (* 200 (q/noise (/ (q/millis) 1000)))
                20 30)
        (q/ellipse xr yr 10 15)
        (q/point xr yr)))))

(defn draw-state [state]
  (q/background 20)
  (q/stroke 0)
  (draw-circle [200 110] state)
  (draw-circle [120 150] state))

(q/defsketch circles
  :title "Perlin noise circles"
  :size [200 200]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top :resizable]
  :middleware [m/fun-mode])
