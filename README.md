# 🌱 Crop Recommendation with Decision Tree 

<img src="https://github.com/AnasAlSayed18/img/blob/5c588284c5fa2a5e4039cf90ef772874536fe989/AILogo2.png" width="250" />

## 📜 Overview
This project uses a **Decision Tree (J48)** classifier to recommend the most suitable crop for a given set of environmental conditions (e.g., soil nutrients, temperature, humidity, pH, rainfall).

Built with **Java + JavaFX** and powered by **Weka**, the app features a user-friendly GUI for live prediction, cross-validation reporting, and decision tree visualization.

---

## 🌾 Dataset
- **Source:** [Kaggle Crop Recommendation Dataset](https://www.kaggle.com/datasets/madhuraatmarambhagat/crop-recommendation-dataset)
- **Features:**
  - `N` (Nitrogen)
  - `P` (Phosphorus)
  - `K` (Potassium)
  - `temperature` (°C)
  - `humidity` (%)
  - `ph` (soil pH)
  - `rainfall` (mm)
- **Label:** `crop` (e.g., rice, maize, cotton)

---

## 🧠 Algorithm Details

- **Model:** J48 (Weka’s implementation of C4.5)
- **Validation:** 5-Fold Cross Validation
- **Metrics:** Accuracy, Precision, Recall (weighted averages)

### Sample Evaluation Results

| Fold | Accuracy | Precision | Recall |
|------|----------|-----------|--------|
| 1    | 99.32%   | 0.9935    | 0.9932 |
| 2    | 98.41%   | 0.9852    | 0.9841 |
| 3    | 96.82%   | 0.9692    | 0.9682 |
| 4    | 98.86%   | 0.9887    | 0.9886 |
| 5    | 99.55%   | 0.9957    | 0.9955 |

---

## 🚀 Features

- **🌱 Recommend Crop** based on user-input soil and climate data
- **📉 5-Fold Cross-Validation** with evaluation metrics display
- **🌳 Visualize Decision Tree** using Weka’s Swing visualizer
- **🖥️ JavaFX GUI** with clean design and interactive controls

---

## 📸 Screenshots

### GUI Crop Input Form
![Form](https://github.com/AnasAlSayed18/img/blob/fea36a481c3103be2014b366037bea3cc0d21cb6/ai2.png)

### Decision Tree View
![Tree](https://github.com/AnasAlSayed18/img/blob/fea36a481c3103be2014b366037bea3cc0d21cb6/ai3.png)

---

## 📚 References

- [Decision Tree – Wikipedia](https://en.wikipedia.org/wiki/Decision_tree)
- [Weka J48 Documentation](https://weka.sourceforge.io/doc.dev/weka/classifiers/trees/J48.html)
- [Kaggle Crop Dataset](https://www.kaggle.com/datasets/madhuraatmarambhagat/crop-recommendation-dataset)
- COMP338 AI Course Materials

---

## 👨‍💻 Authors
- **Anas Al Sayed** (1221020)
- **Abd Al-Rheem Yaseen** (1220783)

Supervised by: *Dr. Radi Jarrar*

---

## 🎬 Demo Video
[Click here to view demo (Coming Soon)](#)
