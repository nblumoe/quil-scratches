(ns hello-quil.tree
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn rand-between [start end]
  (+ start (rand (inc (- end start)))))

(defn grow-tree [size]
  (let [tree {:size size
              :angle (rand-between -1 1)
              :branches ()}]
    (if (> (dec size) 0)
      (assoc tree :branches (repeatedly size #(grow-tree (dec size))))
      tree)))

(defn setup []
  (q/frame-rate 1)
  {:trees ()})

(defn update-state [state]
  (update-in state [:trees] conj {:pos [(rand-between -300 300) 0]
                                  :tree (grow-tree 5)}))

(defn draw-tree [start {:keys [size angle branches]}]
  (when (> size 0)
    (let [length (* size 10)
          end [(+ (first start) (* length (Math/sin angle)))
               (- (second start) (* length (Math/cos angle)))]]
      (q/line start end)
      (doseq [branch branches]
        (draw-tree end branch)))))

(defn draw-state [state]
  (q/background 200)
  (q/with-translation [(/ (q/width) 2)
                       (/ (q/height) 2)]
    (doseq [{:keys [pos tree]} (:trees state)]
      (draw-tree pos tree))))
#_
(q/defsketch hello-quil
  :title "A forest of trees"
  :size [200 200]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top :resizable]
  :middleware [m/fun-mode])
