package first;// TODHRI ANGJELO - 3090
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class Histogram2 {
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: java Histogram2 <a> <b>");
			return;
		}
		double a = Double.parseDouble(args[0]);
		double b = Double.parseDouble(args[1]);

		// read in data from csv file
		String csvFile = "first/acs2015_census_tract_data.csv";
		String line;
		String csvSplitBy = ",";
		ArrayList<Double> incomes = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			br.readLine(); // skip header line
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(csvSplitBy);
				if (!fields[13].equals("")) { // check if Income value is recorded
					incomes.add(Double.parseDouble(fields[13]));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		int numValidIncomes = incomes.size();

		double minIncome = Collections.min(incomes);
		double maxIncome = Collections.max(incomes);

		int numBins = 100;
		double binWidth = (maxIncome - minIncome) / numBins;

		// create equiwidth histogram
		int[] equiwidthHist = new int[numBins];
		for (double income : incomes) {
			int binIndex = (int) ((income - minIncome) / binWidth);
			if (binIndex == numBins) { // handle edge case of income equal to maxIncome
				binIndex--;
			}
			equiwidthHist[binIndex]++;
		}

		// estimate result using equiwidth histogram
		int lowerBinIndex = (int) ((a - minIncome) / binWidth);
		if (lowerBinIndex == numBins) {
			lowerBinIndex--;
		}
		int upperBinIndex = (int) ((b - minIncome) / binWidth);
		if (upperBinIndex == numBins) {
			upperBinIndex--;
		}
		double equiwidthResult = 0;
		for (int i = lowerBinIndex; i <= upperBinIndex; i++) {
			double lowerBound = minIncome + i * binWidth;
			double upperBound = minIncome + (i + 1) * binWidth;
			double overlap = Math.min(upperBound, b) - Math.max(lowerBound, a);
			double binFraction = overlap / binWidth;
			equiwidthResult += equiwidthHist[i] * binFraction;
		}
		System.out.printf(Locale.US, "equiwidth estimated results: %.12f%n", equiwidthResult);


		// create equidepth histogram
		Collections.sort(incomes); // sort the incomes ArrayList in ascending order
		int[] equidepthHist = new int[numBins];
		int numItemsPerBin = numValidIncomes / numBins;
		int remainder = numValidIncomes % numBins;
		int startIndex = 0;

		// print out equidepth histogram
		for (int i = 0; i < numBins; i++) {
			int numItems = numItemsPerBin;
			if (i < remainder) {
				numItems++;
			}
			int endIndex = startIndex + numItems;
			equidepthHist[i] = numItems;
			startIndex = endIndex;
		}

		// estimate equidepth results
		double equidepthEstimate = 0.0;
		startIndex = 0;
		for (int i = 0; i < numBins; i++) {
			double lowerBound = incomes.get(startIndex);
			double upperBound = incomes.get(startIndex + equidepthHist[i] - 1);
			if (lowerBound < b && upperBound >= a) {
				double binPercentage = 1.0;
				if (lowerBound < a) {
					binPercentage = (upperBound - a) / (upperBound - lowerBound);
				} else if (upperBound >= b) {
					binPercentage = (b - lowerBound) / (upperBound - lowerBound);
				}
				equidepthEstimate += equidepthHist[i] * binPercentage;
			}
			startIndex += equidepthHist[i];
		}
		System.out.printf(Locale.US, "equidepth estimated results: %.12f%n", equidepthEstimate);

		// print actual results for incomes within the range
		System.out.println("actual results: " + incomes.stream().filter(income -> income >= a && income < b).count());
	}
}
