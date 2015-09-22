(ns hello-quil.lines
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/smooth)
  (q/frame-rate 30)
  {:noise-seed (q/random 10)
   :background 100})

(defn update-state [state]
  (let [noise-seed (+ (:noise-seed state) 0.08)
        background (+ (:background state) (- (q/noise noise-seed) 0.5))
        x-range 600
        y-range 100
        xs (range 0 x-range 2)
        ys (map-indexed
            (fn [n _] (* (q/noise (+ noise-seed (* n 0.03))) y-range))
            xs)]
    {:x-range x-range
     :y-range y-range
     :noise-seed noise-seed
     :background background
     :line-segments (partition 2 (interleave xs ys))}))

(defn draw-state [{:keys [x-range y-range line-segments background]}]
  (q/background background)
  (q/translate (/ (- (q/width) x-range) 2)
               (/ (- (q/height) y-range) 2))
  (loop [[head & tail] line-segments]
    (when tail
      (q/stroke (- background (* (/ (first head) x-range) background)))
      (q/stroke-weight (/ (second head) 3))
      (q/line head (first tail))
      (recur tail))))

(q/defsketch lines
  :title "Some random lines"
  :size [200 200]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top :resizable]
  :middleware [m/fun-mode])
