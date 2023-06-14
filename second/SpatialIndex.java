package second;// TODHRI ANGJELO - 3090
import java.io.*;
import java.util.*;

public class SpatialIndex {
	public static final String CLASSPATH = "C:\\Users\\a.todhri\\IdeaProjects\\LyriSearch-Lucene\\src\\main\\java\\assign2\\"; // CHANGE THIS PATH WITH THE PATH OF YOUR FILE ON YOUR OWN MACHINE

	public static void main(String[] args) throws IOException {
		String filename = CLASSPATH + "tiger_roads.csv";
		List<Record> records = readRecordsFromFile(filename);
		double[] gridLimits = getGridLimits(records);
		Map<String, List<Integer>> grid = createGrid(records, gridLimits);
		writeGridToFile(grid, gridLimits, records);
	}

	public static List<Record> readRecordsFromFile(String filename) throws IOException {
		int num_linestrings;
		List<Record> records = new ArrayList<Record>();

		// Open the file and read the number of linestrings
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		num_linestrings = Integer.parseInt(reader.readLine().trim());

		// Loop through the linestrings and parse them
		for (int i = 0; i < num_linestrings; i++) {
			String line = reader.readLine();
			String[] points_str = line.split(",");
			List<double[]> points = new ArrayList<double[]>();
			double mbr_min_x = Double.POSITIVE_INFINITY;
			double mbr_min_y = Double.POSITIVE_INFINITY;
			double mbr_max_x = Double.NEGATIVE_INFINITY;
			double mbr_max_y = Double.NEGATIVE_INFINITY;

			// Loop through the points in the linestring and parse them
			for (int j = 0; j < points_str.length; j++) {
				String[] point_str = points_str[j].split(" ");
				double x = Double.parseDouble(point_str[0]);
				double y = Double.parseDouble(point_str[1]);
				points.add(new double[]{x, y});

				// Update the MBR of the linestring
				if (x < mbr_min_x) mbr_min_x = x;
				if (y < mbr_min_y) mbr_min_y = y;
				if (x > mbr_max_x) mbr_max_x = x;
				if (y > mbr_max_y) mbr_max_y = y;
			}

			// Create a new Record object and add it to the list of records
			Record record = new Record(i, mbr_min_x, mbr_min_y, mbr_max_x, mbr_max_y, points);
			records.add(record);
		}
		reader.close();

		// Return the list of Records
		return records;
	}

	public static double[] getGridLimits(List<Record> records) {
		// Initialize the minimum and maximum x and y values to positive and negative infinity, respectively
		double min_x = Double.POSITIVE_INFINITY;
		double min_y = Double.POSITIVE_INFINITY;
		double max_x = Double.NEGATIVE_INFINITY;
		double max_y = Double.NEGATIVE_INFINITY;

		// Iterate through all records and update the minimum and maximum x and y values
		for (Record record : records) {
			if (record.mbr_min_x < min_x) min_x = record.mbr_min_x;
			if (record.mbr_min_y < min_y) min_y = record.mbr_min_y;
			if (record.mbr_max_x > max_x) max_x = record.mbr_max_x;
			if (record.mbr_max_y > max_y) max_y = record.mbr_max_y;
		}

		// Return an array of doubles containing the computed limits of the grid
		return new double[]{min_x, min_y, max_x, max_y};
	}

	public static Map<String, List<Integer>> createGrid(List<Record> records, double[] gridLimits) {
		// Calculate the cell size of the grid
		double cell_size_x = (gridLimits[2] - gridLimits[0]) / 10.0;
		double cell_size_y = (gridLimits[3] - gridLimits[1]) / 10.0;

		// Initialize the grid with empty lists
		Map<String, List<Integer>> grid = new HashMap<>();

		// Loop through each Record and assign it to the appropriate cells in the grid
		for (Record record : records) {
			// Calculate the range of cells that the Record intersects
			int min_cell_x = (int) Math.floor((record.mbr_min_x - gridLimits[0]) / cell_size_x);
			int min_cell_y = (int) Math.floor((record.mbr_min_y - gridLimits[1]) / cell_size_y);
			int max_cell_x = (int) Math.floor((record.mbr_max_x - gridLimits[0]) / cell_size_x);
			int max_cell_y = (int) Math.floor((record.mbr_max_y - gridLimits[1]) / cell_size_y);

			// Add the Record identifier to each intersecting cell in the grid
			for (int i = min_cell_x; i <= max_cell_x; i++) {
				for (int j = min_cell_y; j <= max_cell_y; j++) {
					String key = String.format("(%d,%d)", i, j);
					List<Integer> ids = grid.get(key);
					if (ids == null) {
						ids = new ArrayList<>();
						grid.put(key, ids);
					}
					ids.add(record.identifier);
				}
			}
		}

		// Return the grid index
		return grid;
	}

	public static void writeGridToFile(Map<String, List<Integer>> grid, double[] gridLimits, List<Record> records) throws IOException {
		// Write directory to file
		// Create a BufferedWriter to write to the directory file
		BufferedWriter dirWriter = new BufferedWriter(new FileWriter(CLASSPATH + "grid.dir"));
		// Write the grid limits to the directory file
		dirWriter.write(String.format(Locale.ENGLISH, "%f %f %f %f\n", gridLimits[0], gridLimits[2], gridLimits[1], gridLimits[3]));
		// Iterate over each cell in the grid and write the corresponding key-value pair to the directory file
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				String key = String.format("(%d,%d)", i, j);
				// Use requestedWriteFormat to write the cell indices to the file
				String requestedWriteFormat = String.format("%d %d", i, j);
				List<Integer> ids = grid.get(key);
				if (ids == null) {
					// Write 0 to the file if the cell is empty
					dirWriter.write(String.format("%s 0\n", requestedWriteFormat));
				} else {
					// Write the number of records in the cell to the file if the cell is not empty
					dirWriter.write(String.format("%s %d\n", requestedWriteFormat, ids.size()));
				}
			}
		}
		// Close the directory file writer
		dirWriter.close();

		// Write grid to file
		// Create a BufferedWriter to write to the grid file
		BufferedWriter grdWriter = new BufferedWriter(new FileWriter(CLASSPATH + "grid.grd"));
		// Iterate over each cell in the grid and write the corresponding records to the grid file
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				String key = String.format("(%d,%d)", i, j);
				List<Integer> ids = grid.get(key);
				if (ids != null) {
					// Iterate over each record in the cell and write its details to the grid file
					for (int id : ids) {
						Record record = records.get(id);
						StringBuilder pointsStr = new StringBuilder();
						for (double[] point : record.points) {
							// Write each point of the record to a StringBuilder
							pointsStr.append(String.format(Locale.ENGLISH, "%.6f %.6f,", point[0], point[1]));
						}
						// Remove the trailing comma from the StringBuilder
						pointsStr.deleteCharAt(pointsStr.length()-1);
						// Write the record's details to the grid file
						grdWriter.write(String.format(Locale.ENGLISH, "%d,%.6f %.6f,%.6f %.6f,%s\n", record.identifier, record.mbr_min_x, record.mbr_min_y, record.mbr_max_x, record.mbr_max_y, pointsStr.toString()));
					}
				}
			}
		}
		// Close the grid file writer
		grdWriter.close();
	}

	static class Record {
		public int identifier;
		public double mbr_min_x;
		public double mbr_min_y;
		public double mbr_max_x;
		public double mbr_max_y;
		public List<double[]> points;

		public Record(int identifier, double mbr_min_x, double mbr_min_y, double mbr_max_x, double mbr_max_y, List<double[]> points) {
			this.identifier = identifier;
			this.mbr_min_x = mbr_min_x;
			this.mbr_min_y = mbr_min_y;
			this.mbr_max_x = mbr_max_x;
			this.mbr_max_y = mbr_max_y;
			this.points = points;
		}
	}
}