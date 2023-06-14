package second;// TODHRI ANGJELO - 3090
import java.io.*;
import java.util.*;

public class WindowSelectionQueries {

	public static final String CLASSPATH = "C:\\Users\\a.todhri\\IdeaProjects\\LyriSearch-Lucene\\src\\main\\java\\assign2\\"; // CHANGE THIS PATH WITH THE PATH OF YOUR FILE ON YOUR OWN MACHINE
	public static double minX, maxX, minY, maxY, cellSizeX, cellSizeY;
	public static int numCellsPerAxis;
	public static Map<String, Long> cellCounts;

	public static void main(String[] args) throws IOException {

		String queriesFile = CLASSPATH + "queries.txt";

		Map<String, List<String[]>> gridData = loadGrid(CLASSPATH + "grid");

		// Perform window queries
		List<WindowQuery> queries = readQueryFile(queriesFile);
		for (int i = 0; i < queries.size(); i++) {
			WindowQuery query = queries.get(i);
			// performQueryPart3(query, gridData); // uncomment this line for execution of part3
			performQuery(query, gridData);

		}
	}

	// THIS METHOD IS USED FOR PART 3
	public static void performQueryPart3(WindowQuery query, Map<String, List<String[]>> grid) {
		// Calculate grid cell IDs that intersect the query window
		int cellX1 = (int) ((query.minX - minX) / cellSizeX);
		int cellX2 = (int) ((query.maxX - minX) / cellSizeX);
		int cellY1 = (int) ((query.minY - minY) / cellSizeY);
		int cellY2 = (int) ((query.maxY - minY) / cellSizeY);

		// Keep track of results and visited cells
		Set<String> results = new HashSet<>();
		Set<String> visitedCells = new HashSet<>();

		// Iterate over all intersecting cells and objects
		for (int x = cellX1; x <= cellX2; x++) {
			for (int y = cellY1; y <= cellY2; y++) {
				// Get cell data from grid
				String cellId = x + "," + y;
				List<String[]> cellData = grid.get(cellId);
				// Cells with no overlap with the query window will be ignored
				if (cellData == null) {
					continue;
				}

				// Check if cell intersects query window
				if (visitedCells.contains(cellId)) {
					continue;
				}
				visitedCells.add(cellId);

				// Iterate over objects in cell and check if they intersect query window
				for (String[] object : cellData) {
					// Parse object data
					String objectId = String.valueOf(Integer.parseInt(object[0]) + 1);
					double minObjectX = Double.POSITIVE_INFINITY;
					double minObjectY = Double.POSITIVE_INFINITY;
					double maxObjectX = Double.NEGATIVE_INFINITY;
					double maxObjectY = Double.NEGATIVE_INFINITY;
					for (int i = 1; i < object[1].split(",").length; i++) {
						String[] point = object[1].split(",")[i].split(" ");
						double xValue = Double.parseDouble(point[0]);
						double yValue = Double.parseDouble(point[1]);
						minObjectX = Math.min(minObjectX, xValue);
						minObjectY = Math.min(minObjectY, yValue);
						maxObjectX = Math.max(maxObjectX, xValue);
						maxObjectY = Math.max(maxObjectY, yValue);
					}

					// Check if object intersects query window
					if (minObjectX > query.maxX || maxObjectX < query.minX ||
							minObjectY > query.maxY || maxObjectY < query.minY) {
						continue;
					}

					// Check if object reference point is within current cell
					double refPointX = Math.max(minObjectX, query.minX);
					double refPointY = Math.max(minObjectY, query.minY);
					int refCellX = (int) ((refPointX - minX) / cellSizeX);
					int refCellY = (int) ((refPointY - minY) / cellSizeY);
					if (refCellX == x && refCellY == y) {
						if (isLinestringInsideWindow(minObjectX, minObjectY, maxObjectX, maxObjectY, query.minX, query.maxX, query.minY, query.maxY)) {
							results.add(objectId);
						}
					}
				}
			}
		}

		// Convert Set to List and sort
		List<String> sortedResults = new ArrayList<>(results);
		Collections.sort(sortedResults);

		// Print query results
		System.out.println("Query " + query.queryId + " results:");
		System.out.println(String.join(" ", sortedResults));
		System.out.println("Cells: " + visitedCells.size());
		System.out.println("Results: " + results.size());
		System.out.println("----------");
	}

	// THIS METHOD IS USED FOR PART 3
	public static boolean isLinestringInsideWindow(double x1, double y1, double x2, double y2,
												   double queryMinX, double queryMaxX, double queryMinY, double queryMaxY) {
		// Check if the object's MBR is completely overlapped in at least one dimension
		boolean isOverlapX = (x1 >= queryMinX && x1 <= queryMaxX) || (x2 >= queryMinX && x2 <= queryMaxX) ||
				(x1 < queryMinX && x2 > queryMaxX);
		boolean isOverlapY = (y1 >= queryMinY && y1 <= queryMaxY) || (y2 >= queryMinY && y2 <= queryMaxY) ||
				(y1 < queryMinY && y2 > queryMaxY);

		// If there is an overlap in at least one dimension, then check each line segment
		if (isOverlapX && isOverlapY) {
			double deltaX = x2 - x1;
			double deltaY = y2 - y1;

			// Check if the line segment intersects with any of the window sides
			if (deltaX != 0) {
				double slope = deltaY / deltaX;
				double yIntercept = y1 - slope * x1;
				double topY = slope * queryMaxX + yIntercept;
				double bottomY = slope * queryMinX + yIntercept;
				if ((topY >= queryMinY && topY <= queryMaxY) || (bottomY >= queryMinY && bottomY <= queryMaxY) ||
						(topY < queryMinY && bottomY > queryMaxY)) {
					return true;
				}
			}
			if (deltaY != 0) {
				double slope = deltaX / deltaY;
				double xIntercept = x1 - slope * y1;
				double leftX = slope * queryMinY + xIntercept;
				double rightX = slope * queryMaxY + xIntercept;
				if ((leftX >= queryMinX && leftX <= queryMaxX) || (rightX >= queryMinX && rightX <= queryMaxX) ||
						(leftX < queryMinX && rightX > queryMaxX)) {
					return true;
				}
			}
		}

		return false;
	}

	public static Map<String, List<String[]>> loadGrid(String gridFilename) throws IOException {
		// Read the grid directory to get the number of cells and their counts
		readGridDirectory(gridFilename + ".dir");

		// Initialize an empty map to hold the record data for each cell
		Map<String, List<String[]>> cellDataMap = new HashMap<>();
		for (int i = 0; i < numCellsPerAxis; i++) {
			for (int j = 0; j < numCellsPerAxis; j++) {
				String cellKey = i + "," + j;
				cellDataMap.put(cellKey, new ArrayList<>());
			}
		}

		// Open the grid file and read each line
		BufferedReader reader = new BufferedReader(new FileReader(gridFilename + ".grd"));
		String line;
		while ((line = reader.readLine()) != null) {
			// Split the line into record ID and point data
			String[] parts = line.split(",");
			String id = parts[0];
			String[] points = Arrays.copyOfRange(parts, 1, parts.length);

			// Calculate the cell IDs that this record intersects
			String[] firstPoint = points[0].split(" ");
			double x = Double.parseDouble(firstPoint[0]);
			double y = Double.parseDouble(firstPoint[1]);
			int cellX = (int) ((x - minX) / cellSizeX); // Calculate the X cell ID that this record intersects
			int cellY = (int) ((y - minY) / cellSizeY); // Calculate the Y cell ID that this record intersects

			// Add the record data to each intersected cell's list
			for (int i = cellX; i <= cellX + 1 && i < numCellsPerAxis; i++) { // Iterate over the cells that this record intersects horizontally
				for (int j = cellY; j <= cellY + 1 && j < numCellsPerAxis; j++) { // Iterate over the cells that this record intersects vertically
					String cellKey = i + "," + j;
					cellDataMap.get(cellKey).add(new String[]{id, String.join(",", points)}); // Add the record data to the intersected cell's list
				}
			}
		}

		reader.close();

		return cellDataMap;
	}

	public static void readGridDirectory(String directoryFilename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(directoryFilename));

		// Parse the grid dimensions
		String[] line1 = reader.readLine().split(" ");
		double minX = Double.parseDouble(line1[0]);
		double maxX = Double.parseDouble(line1[1]);
		double minY = Double.parseDouble(line1[2]);
		double maxY = Double.parseDouble(line1[3]);

		// Calculate the size of each cell
		double cellSizeX = (maxX - minX) / 10;
		double cellSizeY = (maxY - minY) / 10;

		// Store the results in instance variables
		WindowSelectionQueries.minX = minX;
		WindowSelectionQueries.maxX = maxX;
		WindowSelectionQueries.minY = minY;
		WindowSelectionQueries.maxY = maxY;
		WindowSelectionQueries.numCellsPerAxis = 10;
		WindowSelectionQueries.cellSizeX = cellSizeX;
		WindowSelectionQueries.cellSizeY = cellSizeY;

		// Parse the cell counts
		Map<String, Long> cellCounts = new HashMap<>();
		String line;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split("\\s+");
			long count = Long.parseLong(parts[2]);
			String cellKey = parts[0] + "," + parts[1];
			cellCounts.put(cellKey, count);
		}

		reader.close();

		WindowSelectionQueries.cellCounts = cellCounts;
	}

	public static List<WindowQuery> readQueryFile(String queryFilename) throws IOException {
		List<WindowQuery> queries = new ArrayList<>();

		// Open the query file and read each line
		BufferedReader reader = new BufferedReader(new FileReader(queryFilename));
		String line;
		while ((line = reader.readLine()) != null) {
			// Parse the query values
			String[] parts = line.split(",");
			int queryID = Integer.parseInt(parts[0]);
			String[] values = parts[1].split(" ");
			double queryMinX = Double.parseDouble(values[0]);
			double queryMaxX = Double.parseDouble(values[1]);
			double queryMinY = Double.parseDouble(values[2]);
			double queryMaxY = Double.parseDouble(values[3]);
			WindowQuery query = new WindowQuery(queryID, queryMinX, queryMinY, queryMaxX, queryMaxY);
			queries.add(query);
		}

		return queries;
	}

	public static void performQuery(WindowQuery query, Map<String, List<String[]>> grid) {
		// Calculate grid cell IDs that intersect the query window
		int cellX1 = (int) ((query.minX - minX) / cellSizeX);
		int cellX2 = (int) ((query.maxX - minX) / cellSizeX);
		int cellY1 = (int) ((query.minY - minY) / cellSizeY);
		int cellY2 = (int) ((query.maxY - minY) / cellSizeY);

		// Keep track of results and visited cells
		Set<String> results = new HashSet<>();
		Set<String> visitedCells = new HashSet<>();

		// Iterate over all intersecting cells and objects
		for (int x = cellX1; x <= cellX2; x++) {
			for (int y = cellY1; y <= cellY2; y++) {
				// Get cell data from grid
				String cellId = x + "," + y;
				List<String[]> cellData = grid.get(cellId);
				// Cells with no overlap with the query window will be ignored. (edge case requested by exercise part2)
				if (cellData == null) {
					continue;
				}

				// Check if cell intersects query window
				if (visitedCells.contains(cellId)) {
					continue;
				}
				visitedCells.add(cellId);

				// Iterate over objects in cell and check if they intersect query window
				for (String[] object : cellData) {
					// Parse object data
					String objectId = String.valueOf(Integer.parseInt(object[0]) + 1);
					double minObjectX = Double.POSITIVE_INFINITY;
					double minObjectY = Double.POSITIVE_INFINITY;
					double maxObjectX = Double.NEGATIVE_INFINITY;
					double maxObjectY = Double.NEGATIVE_INFINITY;
					for (int i = 1; i < object[1].split(",").length; i++) {
						String[] point = object[1].split(",")[i].split(" ");
						double xValue = Double.parseDouble(point[0]);
						double yValue = Double.parseDouble(point[1]);
						minObjectX = Math.min(minObjectX, xValue);
						minObjectY = Math.min(minObjectY, yValue);
						maxObjectX = Math.max(maxObjectX, xValue);
						maxObjectY = Math.max(maxObjectY, yValue);
					}

					// Check if object intersects query window
					if (minObjectX > query.maxX || maxObjectX < query.minX ||
							minObjectY > query.maxY || maxObjectY < query.minY) {
						continue;
					}

					// Check if object reference point is within current cell
					double refPointX = Math.max(minObjectX, query.minX);
					double refPointY = Math.max(minObjectY, query.minY);
					int refCellX = (int) ((refPointX - minX) / cellSizeX);
					int refCellY = (int) ((refPointY - minY) / cellSizeY);
					if (refCellX == x && refCellY == y) {
						results.add(objectId);
					}
				}
			}
		}

		// Convert Set to List and sort
		List<String> sortedResults = new ArrayList<>(results);
		Collections.sort(sortedResults);

		// Print query results
		System.out.println("Query " + query.queryId + " results:");
		System.out.println(String.join(" ", sortedResults));
		System.out.println("Cells: " + visitedCells.size());
		System.out.println("Results: " + results.size());
		System.out.println("----------");
	}

	static class WindowQuery {
		public final int queryId;
		public final double minX;
		public final double minY;
		public final double maxX;
		public final double maxY;

		public WindowQuery(int queryId, double minX, double minY, double maxX, double maxY) {
			this.queryId = queryId;
			this.minX = minX;
			this.minY = minY;
			this.maxX = maxX;
			this.maxY = maxY;
		}
	}
}

