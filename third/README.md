# Complex Data Management - _Διαχείριση Σύνθετων Δεδομένων_

## Third exercise - Top k queries

Top-k queries refer to a technique used in information retrieval and data searching. They represent the most significant or popular queries submitted from a set of queries, based on a specific criterion of importance or popularity.

Top-k queries are useful for various reasons. Firstly, they can be used to optimize information retrieval and improve the performance of search systems. By analyzing the top-k queries, we can identify the most frequent searches and optimize the corresponding responses to provide faster and more accurate results.

In summary, top-k queries allow us to rank and optimize search results, understand user behavior, and provide personalized and effective search experiences.


![top q queries](https://dblab.kaist.ac.kr/Research/images/top_k_queries_figure1.gif)

For this exercise and inside the "second" folder you can find:
- 1 java code exercise algorithm, TopKAlgorithm.java)
- pdf document with description of the deliverables
- the input files with the scores data rnd.txt, seq1.txt and seq2.txt which we will use to perform the queries

## The algorithm (TopKAlgorithm.java)

The provided Java code implements the Top-K algorithm for computing the top-k scores. It reads random scores from a file and computes the top-k scores using either the brute force or optimized approach. The results are then printed, including the number of sequential accesses and the top-k objects with their respective IDs and scores. The code uses classes like BufferedReader, PriorityQueue, and DecimalFormat for file handling, data structures, and formatting.

## Credits

[semister course page](https://www.cse.uoi.gr/course/complex-data-management/?lang=en) & chatGPT3
