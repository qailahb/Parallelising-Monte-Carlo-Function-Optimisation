/*
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

    static final boolean DEBUG=false;
	
	static long startTime = 0;
	static long endTime = 0;

	//timers - note milliseconds
	private static void tick() {
		startTime = System.currentTimeMillis();
	}
	private static void tock() {
		endTime=System.currentTimeMillis(); 
	}

    private int rows, columns; // grid size
    private double xmin, xmax, ymin, ymax; // x and y terrain limits
    private static TerrainArea terrain;
    private double searchesDensity; // number of searches per grid point

    private static int numSearches; // total number of searches to be performed
    SearchParallel [] searches;
    private Random rand = new Random();

    public MonteCarloMinimizationParallel(int rows, int columns, double xmin, double xmax, double ymin, double ymax,
                                          double searchesDensity) {
        this.rows = rows;
        this.columns = columns;
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
        this.searchesDensity = searchesDensity;
        this.numSearches = (int)(rows * columns * searchesDensity);
        this.terrain = new TerrainArea(rows, columns, xmin, xmax, ymin, ymax);
        searches = new SearchParallel[numSearches];
    }


    @Override
    protected Integer compute() {

        int min = Integer.MAX_VALUE;

        
            // For larger numbers of searches, divide the task into sub-tasks
            MonteCarloMinimizationParallel[] subTasks = new MonteCarloMinimizationParallel[10];

            for (int i = 0; i < 10; i++) {
                subTasks[i] = new MonteCarloMinimizationParallel(rows, columns, xmin, xmax, ymin, ymax, searchesDensity);
            }

            invokeAll(subTasks);

            for (int i = 0; i < 10; i++) {
                int localMin = subTasks[i].join();
                int finder = -1;
                if (localMin < min) {
                    min = localMin;
                    finder = i;
                }
                if(DEBUG) System.out.println("Search " + searches[i].getID()+" finished at  "+ localMin + " in " +searches[i].getSteps());
            }  
            
        return min;
    }

    public static void main(String[] args) {

        //start timer
    	tick();

        if (args.length != 7) {
            System.out.println("Incorrect number of command line arguments provided.");
            //System.out.println("Usage: java MonteCarloMinimizationParallel rows columns xmin xmax ymin ymax searches_density");
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

        MonteCarloMinimizationParallel parallelMC = new MonteCarloMinimizationParallel(rows, columns, xmin, xmax, ymin, ymax, searchesDensity);
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
		System.out.printf("\t Search density: %f (%d searches)\n", searchesDensity,numSearches );

		/*  Total computation time */
		System.out.printf("Time: %d ms\n",endTime - startTime );
		int tmp=terrain.getGrid_points_visited();
		System.out.printf("Grid points visited: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");
		tmp=terrain.getGrid_points_evaluated();
		System.out.printf("Grid points evaluated: %d  (%2.0f%s)\n",tmp,(tmp/(rows*columns*1.0))*100.0, "%");

        System.out.println("Global minimum: " + min);
    }
}
