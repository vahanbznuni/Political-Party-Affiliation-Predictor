**Dependencies**

* **Smile (Statistical Machine Intelligence and Learning Engine) v4.4.0**

  * **Website & API**: [haifengl.github.io](https://haifengl.github.io/)
  * **GitHub**: [github.com/haifengl/smile](https://github.com/haifengl/smile)
  * **Quick Start**: [haifengl.github.io/quickstart.html](https://haifengl.github.io/quickstart.html)
  * **Releases**: [github.com/haifengl/smile/releases](https://github.com/haifengl/smile/releases)

---

## Program Folder Structure

```
project-root/
├── data/
│   └── data.csv
├── lib/
│   ├── ch.qos.logback.logback-core-1.5.18.jar
│   ├── com.github.haifengl.smile-base-4.4.0.jar
│   ├── com.github.haifengl.smile-core-4.4.0.jar
│   └── org.slf4j.slf4j-api-2.0.17.jar
├── out/
├── src/
│   ├── _TEST.java
│   ├── CLI.java
│   ├── Controller.java
│   ├── CorruptDataException.java
│   ├── DataStore.java
│   ├── DataUnits.java
│   ├── Main.java
│   ├── ModelTrainer.java
│   ├── PartyAffiliation.java
│   ├── Predictor.java
│   ├── Preprocessor.java
│   ├── Scaler.java
│   ├── StratifiedDataSplitter.java
│   └── Weighter.java
└── README.md
```

---

## Instructions

Follow these steps to setup and run the program.
1. **Install Smile v4.4.0**

   1. Download the [smile-4.4.0.zip](https://github.com/haifengl/smile/releases) file.
   2. Extract the contents to a directory of your choice.

2. **Prepare the `lib/` Directory**
   Make sure the following four JAR files are in the `lib/` folder of your project. You can find them in the `lib/` directory of the Smile download from step 1:

   * `com.github.haifengl.smile-base-4.4.0.jar`

   * `com.github.haifengl.smile-core-4.4.0.jar`

   * `ch.qos.logback.logback-core-1.5.18.jar`

   * `org.slf4j.slf4j-api-2.0.17.jar`

   > **Tip:** You only need these four to run the program. If you want additional Smile modules, feel free to add them, or install Smile with another method—you’re not limited to this approach.

3. **Compile the Java Source Files**
   All `.java` files live in the `src/` folder. We recommend compiling them into the `out/` directory.

   * Open a terminal (Command Prompt, PowerShell, or your preferred shell).
   * From the project’s root directory, run:

   ```bash
   javac -cp "lib/*" -d out src/*.java
   ```

   This command tells Java to:

   * Include every JAR in `lib/` on the classpath (`-cp "lib/*"`)
   * Output compiled `.class` files into `out/` (`-d out`)

3. **Run the Program**
   After successful compilation, you’ll have `.class` files in `out/`. Now run the main class from that directory:

   ```bash
   java -cp "lib/*:out" Main
   ```

   * The `-cp` flag again includes all JARs and your compiled classes.
   * `Main` is the entry point of the application.

---

For more details on using Smile, check out the [Smile Quick Start Guide](https://haifengl.github.io/quickstart.html).
