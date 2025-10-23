# Political Party Affiliation Predictor (BSCS Student Project)

> A command-line ML agent that predicts a respondent’s U.S. political party affiliation from a 12-question survey. Built **from scratch in Java** (multinomial logistic regression with softmax), with stratified splits, 5-fold cross-validation, and simple online updates after each survey.

![Status](https://img.shields.io/badge/status-student_project-blue)
![Java](https://img.shields.io/badge/Java-Pure%20Java-orange)
![ML](https://img.shields.io/badge/ML-Logistic%20Regression-green)


▶ **DEMO**: Run it in GitHub Codespaces (requires a free GitHub account):

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/vahanbznuni/Political-Party-Affiliation-Predictor)


## 🔎 Recruiter TL;DR
- **Pure Java** implementation of **multinomial logistic regression** (no ML frameworks).
- **End-to-end pipeline**: survey design → preprocessing (ordinal encoding + z-scoring) → training + CV grid-search → evaluation → **interactive CLI** that retrains after each labeled response.
- **Held-out performance** (synthetic seed set, n≈1,000): up to **~75.7% accuracy** with standardized features.

---

## 🧠 What this project is
- A **CLI “agent” loop**: collect answers → preprocess (encode + standardize) → predict → accept true label → retrain → print updated metrics.
- **Model**: Multinomial Logistic Regression implemented in pure Java (batch gradient descent, cross-entropy loss).
- **Data**: Seeded with a realistic, **synthetic** dataset (n≈1,000) approximating U.S. party proportions; grows as users take the survey.
- **Goal**: Demonstrate end-to-end ML engineering—data handling, feature engineering, model training/tuning, evaluation, and an interactive UX.

## ✨ Results (held-out test)
| Setting                         | Accuracy | Recall | Precision | F1 |
|---------------------------------|---------:|------:|----------:|---:|
| Encoded + **Scaled** (best)     | 75.74%   | 75.74%| 74.49%    | 72.33% |
| Encoded only (no scale/weight)  | 74.75%   | 74.75%| –         | –    |
| Encoded + Scaled + Weighted     | 71.29%   | 71.29%| 69.89%    | 67.58% |

---
**Dependencies**

None
---

## 🗂️ Repository structure

```
project-root/
├── data/
│   ├── data_1000_realistic.csv 
│   └── data.csv
├── docs/
│   └── paper.pdf   
├── lib/
├── out/
│   └── *.class files (compiled Java classes)
├── src/
│   ├── _TEST.java
│   ├── _TestMatrixMath.java
│   ├── CLI.java
│   ├── Controller.java
│   ├── CorruptDataException.java
│   ├── DataStore.java
│   ├── DataUnits.java
│   ├── LogisticRegressionMultinomial.java
│   ├── Main.java
│   ├── MatrixMath.java
│   ├── MetricsMultinomial.java
│   ├── ModelTrainer.java
│   ├── PartyAffiliation.java
│   ├── Predictor.java
│   ├── Preprocessor.java
│   ├── Scaler.java
│   ├── Softmax.java
│   ├── StratifiedDataSplitter.java
│   └── Weighter.java
└── README.md
```

---

## Instructions

1. **Compile the Java Source Files**
   All `.java` files live in the `src/` folder. We recommend compiling them into the `out/` directory.

   * Open a terminal (Command Prompt, PowerShell, or your preferred shell).
   * From the project’s root directory, run:

   ```bash
   javac -cp "lib/*" -d out src/*.java
   ```

   This command tells Java to:

   * Include every JAR in `lib/` on the classpath (`-cp "lib/*"`)*
   * Output compiled `.class` files into `out/` (`-d out`)

2. **Run the Program**
   After successful compilation, you’ll have `.class` files in `out/`. Now run the main class from that directory:

   ```bash
   java -cp "lib/*:out" Main
   ```

   * The `-cp` flag again includes all JARs and your compiled classes.
   * `Main` is the entry point of the application.

---
*Note: Current program does not include any dependencies, so there are actually
no JAR files in lib/. However, the instructions have been kept as is to ensure that
past and previous versions of the program run consistently.
---

## 🖥️ Using the CLI
1. Answer 12 multiple-choice questions.
2. See the model’s predicted affiliation.
3. Provide your true label → the dataset grows and the model retrains → updated **accuracy/recall/precision/F1** are printed.

## 🔬 Methods (short)
- **Encoding**: ordinal (direction depends on item).
- **Scaling**: standardization (z-score) computed from training data; applied consistently to user inputs.
- **Training**: batch gradient descent on cross-entropy; hyperparameters via grid-search with 5-fold CV; selection by mean CV accuracy.
- **Evaluation**: stratified 80/20 holdout; micro/weighted multi-class metrics.

## 🧩 Design decisions
- **Why pure Java?** To demonstrate fundamentals without frameworks and to highlight algorithmic + numerical work.
- **Why a CLI first?** It keeps the core loop simple and testable; a web UI can call the same predictor later.
- **Why synthetic seed data?** To avoid privacy issues and keep the project demonstrative. The model is **not** meant for real-world political profiling.

## ⚖️ Ethical use & limitations
- Seed data is **synthetic** and unverified; this is **not** a voter-targeting tool.
- Linear model, intentionally simple; results are illustrative, not production-grade.
- Please use responsibly and avoid sensitive deployments.

## 📄 Paper
If you are evaluating this for a course or as a recruiter, see the short paper for problem framing, methods, and results:
- **/docs/paper.pdf** (commit the exported PDF to this path and keep the filename stable)

## 🧱 Tech stack
- Language: **Java 17+** (no external ML deps)
- Build: `javac`, standard library
- Optional: external JARs for baseline comparisons
