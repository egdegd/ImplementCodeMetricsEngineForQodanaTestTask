## Implement code metrics engine for Qodana. Test Task

### Overview
This application computes the complexity of a given Kotlin file, 
focusing on two main metrics: the number of conditional statements 
and the maximum depth of conditional statements. 
Conditional statements considered include **if**, **switch**, **for**, **while**, and **do-while** constructs.

### Functionality
Upon launching the application, users are prompted to provide the following inputs:

1. **File Name**: The name of the Kotlin file for which metrics are to be computed.
2. **Metric Type**: Users select the type of metric they wish to compute.

The application outputs the names of the three methods/functions with the highest complexity scores, 
along with their respective scores.
### Usage
```bash
# build the application
./gradlew build

# run the tests
./gradlew test

# run the application
./gradlew run
```

### Approach
To parse Kotlin files, the application utilizes the [kotlin.ast](https://github.com/kotlinx/ast) library, leveraging ANTLR with the official Kotlin Grammar. 
Metrics computation is achieved by meticulously traversing the generated Abstract Syntax Tree.
### Assumptions and Simplifications
This application is tailored for Kotlin files 
and provides metrics for two simple aspects of code complexity.