(ns hello-quil.evo
  (:require [quil.core :as q]
            [quil.middleware :as m]))

(def frame-rate 20)

(defn create-cell [position]
  {:generation 0
   :position position
   :energy (rand 1)
   :size 60})

(def ^:dynamic *grid-size* 30)

(defn max-generation [cells]
  (apply max (map :generation cells)))

(defn- latest-generation
  [cells]
  (filter #(= (:generation %) (max-generation cells)) cells))

(def cells-per-generation 20)
(def cells-max-age 20)

(defn kill-oldest-generation [cells]
  (if (> (max-generation cells) cells-max-age)
    (drop cells-per-generation cells)
    cells))

(defn- update-energy
  [noise-seed {:keys [energy position] :as cell}]
  (assoc cell :energy (let [new-energy (-> (q/noise (+ energy (* noise-seed (inc position))))
                                           (- 0.5)
                                           (+ energy))]
                        (cond
                          (< new-energy 0) 0
                          (> new-energy 1) 1
                          :default new-energy))))

(defn evolve-cells [noise-seed cells]
  (->> cells
       latest-generation
       (map #(update-in % [:generation] inc))
       (map (partial update-energy noise-seed))
       (concat cells)))

(defn translate-to-center []
  (q/translate (/ (q/width) 2)
               (/ (q/height) 1.1)))

(defn center-last-generation [cells]
  (translate-to-center)
  (q/translate (- (/ (* *grid-size* cells-per-generation) 2))
               (- (* *grid-size* (max-generation cells)))))

(defn reset-state []
  {:noise-seed (q/random 100)
   :cells (map create-cell (range cells-per-generation))})

(defn setup []
  (q/color-mode :hsb 1.0)
  (q/no-stroke)
  (q/frame-rate frame-rate)
  (q/no-smooth)
  (reset-state))

(defn update-state [{:keys [noise-seed] :as state}]
  (-> state
      (update-in [:noise-seed] #(+ % 0.1))
      (update-in [:cells] (partial evolve-cells noise-seed))
      (update-in [:cells] kill-oldest-generation)))

(defn draw-cell [{:keys [size generation position energy]}]
  (let [size (* *grid-size* 2)]
    (q/fill energy
            1
            energy)
    (q/ellipse (* *grid-size* position)
               (* *grid-size* generation)
               (* size energy)
               (* size energy))))

(defn draw-state [{:keys [cells]}]
  (q/background 5)
  (binding [*grid-size* (/ (q/height) cells-max-age 1.2)]
    (center-last-generation cells)
    (doall (map draw-cell cells))))

#_
(q/defsketch circles
  :title "Evolving noise"
  :size [200 200]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top :resizable]
  :middleware [m/fun-mode])
