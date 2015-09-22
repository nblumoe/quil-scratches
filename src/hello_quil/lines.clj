(ns hello-quil.lines
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn setup []
  (q/smooth)
  (q/frame-rate 1))

(defn update-state [state]
  (let [x-range 600
        y-range 100
        xs (range 0 x-range 10)
        ys (repeatedly (count xs) #(rand-int y-range))]
    {:x-range x-range
     :y-range y-range
     :line-segments (partition 2 (interleave xs ys))}))

(defn draw-state [{:keys [x-range y-range line-segments]}]
  (q/background 230)
  (q/stroke 20 50 70)
  (q/stroke-weight 5)
  (q/translate (/ (- (q/width) x-range) 2)
               (/ (- (q/height) y-range) 2))
  (loop [[head & tail] line-segments]
    (when tail
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
