// TODHRI ANGJELO - 3090
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class Histogram {
	public static void main(String[] args) {
		// read in data from csv file
		String csvFile = "acs2015_census_tract_data.csv";
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
		System.out.println(numValidIncomes + " valid income values");

		double minIncome = Collections.min(incomes);
		double maxIncome = Collections.max(incomes);
		System.out.println("minimum income = " + minIncome + " maximum income = " + maxIncome);

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

		// print out histogram
		System.out.println("equiwidth:");
		for (int i = 0; i < numBins; i++) {
			double lowerBound = minIncome + i * binWidth;
			double upperBound = minIncome + (i+1) * binWidth;
			System.out.printf(Locale.US, "range: [%.2f,%.2f), numtuples: %d\n", lowerBound, upperBound, equiwidthHist[i]);
		}

		// create equidepth histogram
		Collections.sort(incomes); // sort the incomes ArrayList in ascending order
		int[] equidepthHist = new int[numBins];
		int numItemsPerBin = numValidIncomes / numBins;
		int remainder = numValidIncomes % numBins;
		int startIndex = 0;

		// print out equidepth histogram
		System.out.println("equidepth:");
		for (int i = 0; i < numBins; i++) {
			int numItems = numItemsPerBin;
			if (i < remainder) {
				numItems++;
			}
			int endIndex = startIndex + numItems;
			double lowerBound = incomes.get(startIndex);
			double upperBound = incomes.get(endIndex - 1);
			equidepthHist[i] = numItems;
			System.out.printf(Locale.US, "range: [%.2f,%.2f), numtuples: %d\n", lowerBound, upperBound, numItems);
			startIndex = endIndex;
		}
	}
}
