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

(defn- draw-fuzzicle [position radius noise-seed]
  (doseq [angle (range 0 Math/PI 0.002)]
    (let [rand-angle (+ angle
                        (q/noise (+ (* 2 angle) noise-seed)))
          rand-position (mapv #(+ % (* 100 (q/noise (+ (* 2 angle) noise-seed))) -50)
                              position)
          rand-radius (* (q/noise (+ (* 2 angle) noise-seed))
                         radius)
          color [160 (* 250 (/ angle (* 2 Math/PI))) 100]]
      (apply q/stroke color)
      (q/with-translation rand-position
        (q/line (point-on-circle rand-angle rand-radius)
                (point-on-circle (+ rand-angle Math/PI) rand-radius))))))

(defn draw-state [{:keys [radius noise-seed]}]
  (q/background 0)
  (q/stroke-weight 0.1)
  (q/stroke 200)
  (draw-fuzzicle [(/ (q/width) 2) (/ (q/height) 2)] 400 noise-seed))

#_
(q/defsketch circles
  :title "Perlin noise circles"
  :size [200 200]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top :resizable]
  :middleware [m/fun-mode m/pause-on-error])
