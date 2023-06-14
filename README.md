# Data Processing Exercises - Complex Data Management - Διαχείριση Σύνθετων Δεδομένων

This repository contains a collection of exercises and code samples related to data processing and algorithms. Each exercise focuses on a specific topic and provides implementation examples in Java.

## Table of Contents

- [Exercise 1: Histogram Generation](./first/README.md)
- [Exercise 2: Spatial Indexing](./second/README.md)
- [Exercise 3: Top-K Algorithm](./third/README.md)

## Exercise 1: Histogram Generation

The first exercise is about generating histograms from income data stored in a CSV file. The program reads the CSV file, creates two types of histograms (equi-width and equi-depth), and prints out various statistics such as minimum and maximum incomes, the number of valid income values, and the number of bins used for the histograms.

## Exercise 2: Spatial Indexing

In the second exercise, a spatial index is implemented in Java. The index is created based on a set of records representing points in a two-dimensional space. The program divides the space into a grid of cells and assigns each record to the cells that overlap with its minimum bounding rectangle (MBR). The grid is represented as a key-value map, and the program generates grid and directory files. These files can be used for window selection queries in subsequent exercises.

## Exercise 3: Top-K Algorithm

The third exercise focuses on the top-k algorithm for computing the top-k scores. It reads random scores from a file and computes the top-k scores using both the brute force and optimized approaches. The program provides options to specify the value of k and input file locations. The results are printed, including the number of sequential accesses and the top-k objects with their respective IDs and scores.

## Special Taking Cares

- Its important to note the special care I took at the code examples, the documentation and the pdf files. I tried to make them as clear as possible. I hope you will find them useful. (the pdfs are in greek)
- The pdf have all the information and commenting as per the code examples and also information about the known issues for each exercise.

## Prerequisites

- Java Development Kit (JDK) installed
- Apache Maven (if applicable)

## Getting Started

1. Clone the repository: `git clone https://github.com/todhriAngjelo/complex-data-management-java`
2. Navigate to the exercise folder of your choice: `cd exercise1` (replace "exercise1" with the desired exercise folder)
3. Compile the Java code: `javac Main.java`
4. Run the program: `java Main`

## License

This repository is licensed under the [MIT License](LICENSE).

Feel free to explore each exercise folder for detailed instructions, source code, and additional resources.

If you have any questions or need further assistance, please don't hesitate to contact me.

Happy coding!
