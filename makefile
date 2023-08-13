JAVAC = /usr/bin/javac
JAVA = /usr/bin/java
.SUFFIXES: .java .class
SRCDIR = src
BINDIR = bin

CLASSES = MonteCarloMinimization.class \ Search.class \TerrainArea.class \ SearchParallel.class \ MonteCarloMinimizationParallel.class

run: 
	@echo "Compiling and running application"
	@$(JAVAC) -d $(BINDIR)/ $(SRCDIR)/*.java
	@echo "Running MonteCarloMinimization"
	@$(JAVA) -cp $(BINDIR) MonteCarloMinimization 1000 1000 0 1000 0 1000 0.5
	@echo "Running MonteCarloMinimizationParallel"
	@$(JAVA) -cp $(BINDIR) MonteCarloMinimizationParallel 1000 1000 0 1000 0 1000 0.5

build:
	@echo "Compiling application"
	@$(JAVAC) -d $(BINDIR)/ $(SRCDIR)/*.java

clean:
	@echo "Cleaning bin directory"
	@rm -rf $(BINDIR)/*.class
