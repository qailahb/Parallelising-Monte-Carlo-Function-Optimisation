import java.util.Random;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class MonteCarloMinimizationParallel extends RecursiveTask<Integer> {

    private int rows, columns; // grid size
    private double xmin, xmax, ymin, ymax; // x and y terrain limits
    private double searchesDensity; // number of searches per grid point
    private int numSearches; // total number of searches to be performed
    private TerrainArea terrain;
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
        this.numSearches = (int) (rows * columns * searchesDensity);
        this.terrain = new TerrainArea(rows, columns, xmin, xmax, ymin, ymax);
    }

    @Override
    protected Integer compute() {
        int min = Integer.MAX_VALUE;

        if (numSearches <= 1000) {
            // For small numbers of searches, use sequential Monte Carlo
            for (int i = 0; i < numSearches; i++) {
                SearchParallel search = new SearchParallel(i + 1, rand.nextInt(rows), rand.nextInt(columns), terrain);
                int localMin = search.compute();
                if ((!search.isStopped()) && (localMin < min)) {
                    min = localMin;
                }
            }
        } else {
            // For larger numbers of searches, divide the task into sub-tasks
            MonteCarloMinimizationParallel[] subTasks = new MonteCarloMinimizationParallel[10];

            for (int i = 0; i < 10; i++) {
                subTasks[i] = new MonteCarloMinimizationParallel(rows, columns, xmin, xmax, ymin, ymax, searchesDensity);
            }

            invokeAll(subTasks);

            for (int i = 0; i < 10; i++) {
                int localMin = subTasks[i].join();
                if (localMin < min) {
                    min = localMin;
                }
            }
        }

        return min;
    }

    public static void main(String[] args) {
        if (args.length != 7) {
            System.out.println("Incorrect number of command line arguments provided.");
            System.out.println("Usage: java MonteCarloMinimizationParallel rows columns xmin xmax ymin ymax searches_density");
            System.exit(0);
        }

        int rows = Integer.parseInt(args[0]);
        int columns = Integer.parseInt(args[1]);
        double xmin = Double.parseDouble(args[2]);
        double xmax = Double.parseDouble(args[3]);
        double ymin = Double.parseDouble(args[4]);
        double ymax = Double.parseDouble(args[5]);
        double searchesDensity = Double.parseDouble(args[6]);

        MonteCarloMinimizationParallel parallelMC = new MonteCarloMinimizationParallel(rows, columns, xmin, xmax, ymin, ymax, searchesDensity);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        int min = forkJoinPool.invoke(parallelMC);

        System.out.println("Global minimum: " + min);
    }
}
