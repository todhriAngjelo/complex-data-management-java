# Complex Data Management - _Διαχείριση Σύνθετων Δεδομένων_
## 
## First exercise - Histograms

Histograms are a useful tool for visualizing the distribution of a dataset. They are a valuable tool for exploratory data analysis. In this exercise, using Java, we will practice working with real-world data to gain an understanding of how histograms function and how to use them for data analysis and visualization.

![example histogram](https://chartio.com/images/tutorials/charts/histograms/histogram-example-1.png)

For this exercise there were developed:
- 2 java code exercises
- 1 python script to visualize the histogram
- pdf document with description of the deliverables

## Part 1

In the first part of the exercise(Histogram.java), we were asked to write a Java program that reads a CSV file containing income data and generates two types of histograms:

- An equi-width histogram.
- An equi-depth histogram.

An equi-width histogram divides the range of incomes into bins of equal width, while an equi-depth histogram divides the range of incomes in a way that each bin contains approximately the same number of income values.

The program also prints the minimum and maximum income values, the number of valid income values, and the number of bins used for the histograms.

## Part 2

In a large part of the second part(Histogram2.java), the program is an extension of the program in Part A. The creation of the equi-depth and equi-width histograms is done in a shared manner.

The difference is that the program now requires two income values, A and B, as input (program arguments).

The program will attempt to estimate the number of values within the range [A, B] using the two histograms generated in Part 1 of the exercise.

Finally, it will print the equi-width estimate, the equi-depth estimate, and the actual number of values in the dataset.

## Credits

[semister course page](https://www.cse.uoi.gr/course/complex-data-management/?lang=en) & chatGPT3
