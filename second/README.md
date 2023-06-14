# Complex Data Management - _Διαχείριση Σύνθετων Δεδομένων_

## Second exercise - Spatial Indexes

Spatial data refers to any data that has a geographic or locational element. This means that the data is associated with a specific place or location on the Earth's surface. Spatial data can encompass a wide range of information, such as maps, satellite images, GPS coordinates, and other types of geographic data.

Spatial data is typically represented in various formats, including vector data (such as points, lines, and polygons), raster data (such as satellite imagery or elevation data), and tabular data (where the data is organized in a table format with geographic coordinates).

Spatial data is used in a wide range of applications, including urban planning, natural resource management, transportation design, emergency management, and many other fields. Analyzing spatial data can help us better understand patterns and relationships in the natural and built environment, and it can assist us in making informed decisions regarding resource management and utilization.

![example spatial map](https://www.azavea.com/wp-content/uploads/2017/08/bikeparking_anime3-1024x791.gif)

For this exercise and inside the "second" folder you can find:
- 2 java code exercises, one spatial data indexer(SpatialIndex.java) and one spatial data window selection query(WindowSelectionQueries.java)
- pdf document with description of the deliverables
- the output files grid.dir and grid.grd
- the query file queries.txt
- the input file tiger_roads.csv which we will use to create the spatial index

## Part 1 (SpatialIndex.java)

- In the first part of the exercise , we were asked to implement a spatial index in Java. The index is created based on a set of records, where each record represents a line in a two-dimensional space. The linestrings are stored in a CSV file (tiger_roads.csv) in the format "x1 y1,x2 y2,...,xn yn", where x and y are the coordinates of each point in the linestring.


- The spatial index divides the space into a grid of cells and assigns each linestring to the cells that overlap with its minimum bounding rectangle (MBR). The MBR is the smallest rectangle that contains the linestring.


- The grid is represented as a map of cell keys. The cell key is a string that represents the coordinates of the cell in the grid, for example, "(0,0)", "(0,1)", and so on.


- The code reads the CSV file, creates the grid, and writes the grid and directory files to disk as grid.grd and grid.dir, respectively.


- These files will be used in the subsequent part of the exercise for creating window selection queries.


## Part 2 (WindowSelectionQueries.java

- This functionality allows the user to input a query file (queries.txt) and generate the results for each query.


- During program execution, the grid data is loaded, which includes the objects contained in each cell. Additionally, the grid cells intersected by each query window are calculated.


- Next, it checks if the objects in each cell intersect with the query window and if the relative position of the object is within the cell.


- The results of the queries are displayed at the end of the program execution and include the identified objects, the visited cells by the algorithm, and the total number of objects found.

## Credits

[semister course page](https://www.cse.uoi.gr/course/complex-data-management/?lang=en) & chatGPT3
