(ns hello-quil.evo
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(defn create-cell [position]
  {:generation 0
   :position position
   :energy 0
   :size 10})

(defn max-generation [cells]
  (apply max (map :generation cells)))

(defn- latest-generation
  [cells]
  (filter #(= (:generation %) (max-generation cells)) cells))

(defn evolve-cells [cells]
  (->> cells
       latest-generation
       (map #(update-in % [:generation] inc))
       (map (fn [cell] (update-in cell [:energy] #(mod (+ 10 %) 255))))
       (concat cells)))

(def grid-size 10)

(defn translate-to-center []
  (q/translate (/ (q/width) 2)
               (/ (q/height) 2)))

(defn center-last-generation [cells]
  (translate-to-center)
  (q/translate 0 (- (* 10 (max-generation cells)))))

(defn setup []
  (q/smooth)
  (q/frame-rate 3)
  {:noise-seed (q/random 10)
   :cells (map create-cell (range 10))})

(defn update-state [{:keys [noise-seed] :as state}]
  (-> state
      (update-in [:noise-seed] #(+ % 0.01))
      (update-in [:cells] evolve-cells)))

(defn draw-cell [{:keys [size generation position energy]}]
  (q/fill energy)
  (q/ellipse (* grid-size position) (* grid-size generation) size size))

(defn draw-state [{:keys [cells]}]
  (center-last-generation cells)
  (q/background 20)
  (q/stroke 200)
  (q/fill 200)
  (dorun (map draw-cell cells)))

(q/defsketch circles
  :title "Evolving noise"
  :size [200 200]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top :resizable]
  :middleware [m/fun-mode])
