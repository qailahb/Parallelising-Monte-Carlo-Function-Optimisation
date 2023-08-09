JAVAC = /usr/bin/javac
JAVA = /usr/bin/java
.SUFFIXES: .java .class
SRCDIR = src
BINDIR = bin

CLASSES = MonteCarloMinimization.class \ Search.class \TerrainArea.class \ 
SearchParallel.class \MonteCarloMinimizationParallel.class

build:
	@echo "Compiling application"
	@$(JAVAC) -d $(BINDIR)/ *.java

clean:
	@echo "Cleaning bin directory"
	@rm -rf $(BINDIR)/MonteCarloMini/*.class

run: 
	@echo "Compiling and running application"
	@$(JAVA) -cp bin MonteCarloMini.MonteCarloMinimization 1000 1000 0 100 0 100 0.5
	@$(JAVA) -cp bin MonteCarloMini.MonteCarloMinimizationParallel 1000 1000 0 100 0 100 0.5