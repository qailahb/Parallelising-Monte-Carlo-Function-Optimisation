/*
 * BHMQAI001
 * Parallel program using Monte Carlo method
 * Adatpted from code belonging to Michelle Kuttel 2023, University of Cape Town
 * Adapted from "Hill Climbing with Montecarlo"
 * EduHPC'22 Peachy Assignment" 
 * developed by Arturo Gonzalez Escribano  (Universidad de Valladolid 2021/2022)
 */

import java.util.Random;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class MonteCarloMinimizationParallel extends RecursiveTask<Integer> {

    private int startRow;
    private int endRow;

    // Set size of subtasks
    private static final int THRESHOLD = 1;

    static final boolean DEBUG=false;
	
    // Initialize variables to store the starting and ending times
	static long startTime = 0;
	static long endTime = 0;

	// Timers - note milliseconds
	private static void tick() {
		startTime = System.currentTimeMillis();
	}
	private static void tock() {
		endTime=System.currentTimeMillis(); 
	}

    private int rows, columns;              // Grid size
    private double xmin, xmax, ymin, ymax;  // x and y terrain limits
    private static TerrainArea terrain;
    private double searchesDensity;         // Number of searches per grid point

    private static int numSearches;         // Total number of searches to be performed
    //SearchParallel [] searches;
    private Random rand = new Random();     // The random number generator

    private static int finder;
    private static double x_coord;          // x coordinate
    private static double y_coord;          // y coordinate

    public MonteCarloMinimizationParallel(int startRow, int endRow, int rows, int columns, double xmin, double xmax, double ymin, double ymax,
                                          double searchesDensity) {
        this.startRow = startRow;
        this.endRow = endRow;                                    
        this.rows = rows;
        this.columns = columns;
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        this.searchesDensity = searchesDensity;
        MonteCarloMinimizationParallel.numSearches = (int)((endRow-startRow+1) * columns * searchesDensity);
        //this.terrain = new TerrainArea(endRow-startRow+1, columns, xmin, xmax, ymin, ymax);
        //searches = new SearchParallel[numSearches];
    }

    // Parallel implementation
    // Computes the local minimum in the specified range of rows
    @Override
protected Integer compute() {
    if (endRow - startRow <= THRESHOLD) {
        int min = Integer.MAX_VALUE;
        for (int i = startRow; i < endRow; i++) {
            SearchParallel search = new SearchParallel(i+1, rand.nextInt(rows),rand.nextInt(columns),terrain); 
            int localMin = search.compute();
            if (localMin < min) {
                min = localMin;
                finder=i;
                x_coord = terrain.getXcoord(search.getPos_row());
                y_coord = terrain.getYcoord(search.getPos_col());
            }
        }
        return min;
    } 
    
    else {
        // Calculate the midpoint between startRow and endRow
        int mid = (startRow + endRow) / 2;
        // Creates new tasks for each half of range
        MonteCarloMinimizationParallel leftTask = new MonteCarloMinimizationParallel(startRow, mid, rows, columns, xmin, xmax, ymin, ymax, searchesDensity);
        MonteCarloMinimizationParallel rightTask = new MonteCarloMinimizationParallel(mid, endRow, rows, columns, xmin, xmax, ymin, ymax, searchesDensity);

        // Initiating parallel execution of left and right tasks
        leftTask.fork();

        int rightResult = rightTask.compute();
        int leftResult = leftTask.join();

        // Return the minimum of the results from left and right tasks
        return Math.min(leftResult, rightResult);
    }
}


    public static void main(String[] args) {

        //start timer
    	tick();

        if (args.length != 7) {
            System.out.println("Incorrect number of command line arguments provided.");
            System.exit(0);
        }

        /* Read argument values */
        int rows    = Integer.parseInt(args[0]);
        int columns = Integer.parseInt(args[1]);
        double xmin = Double.parseDouble(args[2]);
        double xmax = Double.parseDouble(args[3]);
        double ymin = Double.parseDouble(args[4]);
        double ymax = Double.parseDouble(args[5]);
        double searchesDensity = Double.parseDouble(args[6]);

        if(DEBUG) {
    		/* Print arguments */
    		System.out.printf("Arguments, Rows: %d, Columns: %d\n", rows, columns);
    		System.out.printf("Arguments, x_range: ( %f, %f ), y_range( %f, %f )\n", xmin, xmax, ymin, ymax );
    		System.out.printf("Arguments, searches_density: %f\n", searchesDensity );
    		System.out.printf("\n");
    	}

        // Initialize optimization and perform parallel Monte Carlo search
        MonteCarloMinimizationParallel parallelMC = new MonteCarloMinimizationParallel(0, rows - 1, rows, columns, xmin, xmax, ymin, ymax, searchesDensity);
        MonteCarloMinimizationParallel.terrain = new TerrainArea(rows, columns, xmin, xmax, ymin, ymax);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        int min = forkJoinPool.invoke(parallelMC);

        //end timer
   		tock();

        if(DEBUG) {
    		/* print final state */
    		terrain.print_heights();
    		terrain.print_visited();
    	}

        System.out.printf("Run parameters\n");
		System.out.printf("\t Rows: %d, Columns: %d\n", rows, columns);
		System.out.printf("\t x: [%f, %f], y: [%f, %f]\n", xmin, xmax, ymin, ymax );
		System.out.printf("\t Search density: %f (%d searches)\n", searchesDensity, numSearches );

		/*  Total computation time */
		System.out.printf("Time: %d ms\n",endTime - startTime );
		int tmp=terrain.getGrid_points_visited();
		System.out.printf("Grid points visited: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");
		tmp=terrain.getGrid_points_evaluated();
		System.out.printf("Grid points evaluated: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");

        //System.out.println("Global minimum: " + min);
        System.out.printf("Global minimum: %d at x=%.1f y=%.1f\n\n", min, x_coord, y_coord );

    }
}
