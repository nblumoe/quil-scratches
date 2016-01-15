(ns hello-quil.fuzzicle
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn point-on-circle
  ([angle]
   (point-on-circle angle 1))
  ([angle radius]
   [(* radius (q/cos angle))
    (* radius (q/sin angle))]))

(defn points-on-circle [start diff]
  (lazy-seq (cons (point-on-circle start)
                  (points-on-circle (+ start diff) diff))))

(defn setup []
  (q/smooth)
  (q/frame-rate 30)
  {:noise-seed (q/random 10)
   :radius 10})

(defn update-state [state]
  (-> state
      (assoc :radius 300
             :noise-seed (q/random 100))))

(defn- draw-fuzzicle [angle position radius]
  (q/with-translation position
    (q/line (point-on-circle angle radius)
            (point-on-circle (+ angle Math/PI) radius))))

(defn draw-state [{:keys [radius noise-seed]}]
  (q/background 0)
  (q/stroke-weight 0.1)
  (q/stroke 200)
  (doseq [angle (range 0 Math/PI 0.002)]
    (let [rand-angle (+ (q/noise (+ (* 2 angle) noise-seed))
                        angle)
          position [(/ (q/width) 2) (/ (q/height) 2)]
          rand-position (mapv #(+ % (* 100 (q/noise (+ (* 2 angle) noise-seed))))
                              position)
          rand-radius (* (q/noise (+ (* 2 angle) noise-seed))
                         radius)]
      (draw-fuzzicle rand-angle
                     rand-position
                     rand-radius
                     ))))

#_
(q/defsketch circles
  :title "Perlin noise circles"
  :size [200 200]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top :resizable]
  :middleware [m/fun-mode m/pause-on-error])
