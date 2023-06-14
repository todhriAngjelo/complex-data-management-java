package third;// TODHRI ANGJELO - 3090

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;

public class TopKAlgorithm {

	public static final String SEQ1_TXT_FILE_LOCATION = "C:\\Users\\a.todhri\\Desktop\\MYE041\\exercise3\\ex\\seq1.txt";
	public static final String SEL2_TXT_FILE_LOCATION = "C:\\Users\\a.todhri\\Desktop\\MYE041\\exercise3\\ex\\seq2.txt";
	public static final String RANDOM_FILE_TXT_FILE_LOCATION = "C:\\Users\\a.todhri\\Desktop\\MYE041\\exercise3\\ex\\rnd.txt";

	public static void main(String[] args) {
		// ------------------------------> 1 // You can either get k from user input or assign a default value
		int k = getKFromInput(args);

		// Set the value of k (number of top scores to compute)
		// int k = 20;

		try {
			// Read random scores from a file into a map
			Map<Integer, Double> randomScores = readRandomScores(RANDOM_FILE_TXT_FILE_LOCATION);

			// Compute the top k scores using the random scores map and specified file locations
			//computeTopK(randomScores, SEQ1_TXT_FILE_LOCATION, SEL2_TXT_FILE_LOCATION, k);

			//  ------------------------------> 2 Alternatively, you can compute the top k scores using brute force approach
			bruteForceTopK(randomScores, SEQ1_TXT_FILE_LOCATION, SEL2_TXT_FILE_LOCATION, k);
		} catch (IOException e) {
			System.out.println("An error occurred while reading the input files.");
			e.printStackTrace();
		}
	}

	private static int getKFromInput(String[] args) {
		// Check if the number of command-line arguments is less than 1
		if (args.length < 1) {
			System.out.println("Please provide a positive integer k as an argument.");
		}

		// Parse the first argument (k) into an integer and return it
		return Integer.parseInt(args[0]);
	}

	private static void bruteForceTopK(Map<Integer, Double> randomScores, String seq1File, String seq2File, int k) throws IOException {
		// Create a new map to store the total scores
		Map<Integer, Double> totalScores = new HashMap<>(randomScores);

		try (BufferedReader seq1Reader = new BufferedReader(new FileReader(seq1File));
			 BufferedReader seq2Reader = new BufferedReader(new FileReader(seq2File))) {

			// Read seq1.txt
			String seq1Line;
			while ((seq1Line = seq1Reader.readLine()) != null) {
				String[] parts = seq1Line.split(" ");
				int id = Integer.parseInt(parts[0]);
				double seq1Score = Double.parseDouble(parts[1]);

				if (totalScores.containsKey(id)) {
					double totalScore = seq1Score + totalScores.get(id);
					totalScores.put(id, totalScore);
				}
			}

			// Read seq2.txt
			String seq2Line;
			while ((seq2Line = seq2Reader.readLine()) != null) {
				String[] parts = seq2Line.split(" ");
				int id = Integer.parseInt(parts[0]);
				double seq2Score = Double.parseDouble(parts[1]);

				if (totalScores.containsKey(id)) {
					double totalScore = seq2Score + totalScores.get(id);
					totalScores.put(id, totalScore);
				}
			}
		}

		// Sort the restaurants based on total score in descending order
		List<Map.Entry<Integer, Double>> sortedEntries = new ArrayList<>(totalScores.entrySet());
		sortedEntries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

		// Print the top k restaurants
		for (int i = 0; i < Math.min(k, sortedEntries.size()); i++) {
			Map.Entry<Integer, Double> entry = sortedEntries.get(i);
			int id = entry.getKey();
			double totalScore = entry.getValue();

			// Format the total score to 2 decimal places
			DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
			symbols.setDecimalSeparator('.'); // Set decimal separator to dot (.)
			DecimalFormat decimalFormat = new DecimalFormat("#.00", symbols); // Format to 2 decimal places with dot separator

			System.out.println(id + ": " + decimalFormat.format(totalScore));
		}
	}

	private static Map<Integer, Double> readRandomScores(String filename) throws IOException {
		// Create a new HashMap to store the scores
		Map<Integer, Double> scores = new HashMap<>();

		// Use a try-with-resources block to automatically close the reader
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			// Read each line from the file
			while ((line = reader.readLine()) != null) {
				// Split the line by a space to separate the ID and score
				String[] parts = line.split(" ");
				// Parse the ID as an integer
				int id = Integer.parseInt(parts[0]);
				// Parse the score as a double
				double score = Double.parseDouble(parts[1]);
				// Add the ID and score to the map
				scores.put(id, score);
			}
		}

		// Return the map of scores
		return scores;
	}

	private static void computeTopK(Map<Integer, Double> randomScores, String seq1File, String seq2File, int k) throws IOException {
		// Create a PriorityQueue to store the top k restaurants based on total score
		PriorityQueue<Restaurant> topK = new PriorityQueue<>(Comparator.comparingDouble(Restaurant::getTotalScore));

		// Variables for tracking scores and access count
		double lastSeq1Score = 0.0;
		double lastSeq2Score = 0.0;
		int seqAccessCount = 0;

		// Initialize a map to store the lower bounds and seen status of each item
		Map<Integer, Restaurant> lowerBounds = new HashMap<>();

		try (BufferedReader seq1Reader = new BufferedReader(new FileReader(seq1File));
			 BufferedReader seq2Reader = new BufferedReader(new FileReader(seq2File))) {

			String seq1Line;
			String seq2Line;
			boolean seq1Finished = false;
			boolean seq2Finished = false;

			while (!seq1Finished || !seq2Finished) {

				// Read from seq1.txt if it's not finished
				if (!seq1Finished) {
					seq1Line = seq1Reader.readLine();
					if (seq1Line != null) {
						// Process seq1Line
						String[] parts = seq1Line.split(" ");
						int id = Integer.parseInt(parts[0]);
						double seq1Score = Double.parseDouble(parts[1]);

						if (!randomScores.containsKey(id))
							continue;

						// Update lower bounds and add the item to topK
						double lowerBound;
						if (!lowerBounds.containsKey(id)) {
							lowerBound = seq1Score + randomScores.get(id);
							Restaurant restaurant = new Restaurant(id, lowerBound);
							lowerBounds.put(id, restaurant);
							topK.offer(restaurant); // Add the item to topK
						} else {
							Restaurant restaurant = lowerBounds.get(id);
							lowerBound = restaurant.getLowerBound() + seq1Score;
							restaurant.setTotalScore(restaurant.getTotalScore() + seq1Score);
							restaurant.setLowerBound(lowerBound);
						}

						lastSeq1Score = seq1Score;

						// Increment seqAccessCount only when reading from seq1.txt
						seqAccessCount++;
					} else {
						seq1Finished = true;
					}
				}

				// Read from seq2.txt if it's not finished
				if (!seq2Finished) {
					seq2Line = seq2Reader.readLine();
					if (seq2Line != null) {
						// Process seq2Line
						String[] parts = seq2Line.split(" ");
						int id = Integer.parseInt(parts[0]);
						double seq2Score = Double.parseDouble(parts[1]);

						if (!randomScores.containsKey(id))
							continue;

						double lowerBound;
						if (!lowerBounds.containsKey(id)) {
							// Update lower bounds and add the item to topK
							lowerBound = seq2Score + randomScores.get(id);
							Restaurant restaurant = new Restaurant(id, lowerBound);
							lowerBounds.put(id, restaurant);
							topK.offer(restaurant); // Add the item to topK
						} else {
							// Update scores and access count
							Restaurant restaurant = lowerBounds.get(id);
							lowerBound = restaurant.getLowerBound() + seq2Score;
							restaurant.setTotalScore(restaurant.getTotalScore() + seq2Score);
							restaurant.setLowerBound(lowerBound);
						}

						lastSeq2Score = seq2Score;

						// Increment seqAccessCount only when reading from seq2.txt
						seqAccessCount++;
					} else {
						seq2Finished = true;
					}
				}

				// Termination condition
				double threshold = lastSeq1Score + lastSeq2Score + 5.0;
				while (!topK.isEmpty() && topK.peek().getTotalScore() < threshold) {
					Restaurant restaurant = topK.poll();
					// Adjust topK based on the threshold and upper/lower bounds
					if (!topK.isEmpty() && restaurant.getUpperBound() >= topK.peek().getLowerBound()) {
						topK.offer(restaurant);
						break;
					}
				}

				// Remove excess elements from topK if necessary
				if (topK.size() > k)
					topK.poll();
			}
		}

		// Call the printOutput method to display the top k restaurants
		printOutput(topK, seqAccessCount);
	}

	private static void printOutput(PriorityQueue<Restaurant> topK, int seqAccessCount) {
		// Print the number of sequential accesses
		System.out.println("Number of sequential accesses = " + seqAccessCount);

		// Print the top k objects
		System.out.println("Top k objects:");

		// Convert the PriorityQueue to a List
		List<Restaurant> topKList = new ArrayList<>();
		while (!topK.isEmpty()) {
			Restaurant restaurant = topK.poll();
			topKList.add(restaurant);
		}

		// Reverse the list to get descending order
		Collections.reverse(topKList);

		// Format the scores to 2 decimal places
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
		symbols.setDecimalSeparator('.'); // Set decimal separator to dot (.)
		DecimalFormat decimalFormat = new DecimalFormat("#.00", symbols); // Format to 2 decimal places with dot separator

		// Print the ID and formatted score of each restaurant
		for (Restaurant restaurant : topKList) {
			String formattedScore = decimalFormat.format(restaurant.getTotalScore());
			System.out.println(restaurant.getId() + ": " + formattedScore);
		}
	}

	private static class Restaurant {
		private final int id;
		private double lowerBound;
		private double totalScore;

		public Restaurant(int id, double lowerBound) {
			this.id = id;
			this.lowerBound = lowerBound;
			this.totalScore = lowerBound;
		}

		public int getId() {
			return id;
		}

		public double getLowerBound() {
			return lowerBound;
		}

		public void setLowerBound(double lowerBound) {
			this.lowerBound = lowerBound;
		}

		public double getTotalScore() {
			return totalScore;
		}

		public void setTotalScore(double totalScore) {
			this.totalScore = totalScore;
		}

		public double getUpperBound() {
			return lowerBound + totalScore;
		}
	}
}