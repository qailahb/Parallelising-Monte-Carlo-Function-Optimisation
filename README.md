# CSC-Assignment-1

Implemented a parallelised minimum value search on a 2D grid using Java’s Fork/Join framework and a divide-and-conquer approach. The algorithm recursively halves the grid rows into smaller subtasks, processing them concurrently when above/below a defined threshold. RecursiveTask is used for subtask creation and result averaging, and optimizations include pre-initializing instances to reduce memory overhead and tuning the threshold to balance parallelism and task management. This method maximised multi-thread utilisation while avoiding excessive splitting for small workloads, improving speed and efficiency.

## Explanation of how to run the program

The program can be compiled via the Makefile with the following terminal commands:
make clean - cleans bin directory
make build - builds java class files
make run - runs program

The program can also be run with different input parameters than those in the makefile, with the following command:
cp MonteCarloMinimizationParallel 3000 3000 0 3000 0 3000 0.2

