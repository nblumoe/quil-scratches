(ns hello-quil.circle
  (:require [quil.middleware :as m]
            [quil.core :as q]))

(defn setup []
  )

(defn update-state [state]
  state)

(defn- draw-spiral []
  (let [num-segments 200
        max-angle (* 20 2 Math/PI)
        diff-angle (/ Math/PI num-segments 0.5)
        diff-radius 0.01
        noise-amplitude 30
        center-x (/ (q/width) 2)
        center-y (/ (q/height) 2)]
    (loop [angle 0 radius 0 noise (rand 100)]
      (let [new-angle (+ diff-angle angle)
            new-radius (+ (+ diff-radius radius)
                          (- (* (q/noise noise) noise-amplitude)
                             (/ noise-amplitude 2)))
            x1 (+ center-x (* radius (Math/cos angle)))
            y1 (+ center-y (* radius (Math/sin angle)))
            x2 (+ center-x (* new-radius (Math/cos new-angle)))
            y2 (+ center-y (* new-radius (Math/sin new-angle)))]
        (q/stroke (+ radius (rand-int 10))
                  100
                  200
                  (mod 2000 (inc radius)))
        (q/line x1 y1 x2 y2)
        (when (< new-angle max-angle)
          (recur new-angle new-radius (+ noise 0.05)))))))

(defn draw-state [state]
  (q/frame-rate 1)
  (q/stroke-weight 0.1)
  (q/background 20)
  (q/color-mode :hsb)
  (dorun (repeatedly 300 draw-spiral)))

(q/defsketch noisy-circle
  :title "A noisy circle"
  :size [200 200]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top :resizable]
  :middleware [m/fun-mode])
