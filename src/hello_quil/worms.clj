(ns hello-quil.worms
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/frame-rate 120)
  (q/color-mode :hsb)
  ;; setup function returns initial state. It contains
  ;; circle color and position.
  {:color 0
   :angle 0
   :offset 0
   :move 0
   })

(defn sin-scale [value max]
  (->  (/ (mod value max) max)
       (* Math/PI 2)
       Math/sin
       (* (/ max 2))
       (+ (/ max 2))))

(defn update-state [state]
  ;; Update sketch state by changing circle color and position.
  {:color (+ (:color state) (rand 1))
   :angle (+ (:angle state) (rand 0.005))
   :offset (+ (rand 1) (:offset state))
   :move (+ (:move state) (- (rand 2) 1))
   })

(defn draw-state [state]
  (let [offset (sin-scale (:offset state) 50)]
    ;; Set circle color.
    (q/fill (sin-scale (:color state) 255)
            (sin-scale (:color state) 50)
            (+ 20 (sin-scale (:color state) 100))
            100)
    (q/no-stroke)
    ;; Calculate x and y coordinates of the circle.
    (let [angle (:angle state)
          radius (sin-scale (:offset state) 300)
          x (+ (+ (* radius (q/cos angle)) offset) (:move state))
          y (+ (+ (* radius (q/sin angle)) offset) (:move state))
          width (+ 10 (sin-scale (:offset state) 100))
          ]
      ;; Move origin point to the center of the sketch.
      (q/with-translation [(/ (q/width) 2)
                           (/ (q/height) 2)]
        ;; Draw the circle.
        (q/ellipse x y width width)))))
#_
(q/defsketch hello-quil
  :title "You spin my circle right round"
  :size :fullscreen
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode])
