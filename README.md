**Dependencies**

None
---

## Program Folder Structure

```
project-root/
├── data/
│   └── data.csv
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
