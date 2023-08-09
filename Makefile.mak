JAVAC = /usr/bin/javac
JAVA = /usr/bin/java
.SUFFIXES: .java .class
SRCDIR = src
BINDIR = bin

build:
	@echo "Compiling application"
	@mkdir -p $(BINDIR)
	@$(JAVAC) -d $(BINDIR)/ *.java

clean:
	@echo "Cleaning bin directory"
	@rm -rf $(BINDIR)/

run: 
	@echo "Compiling and running application"
	@mkdir -p $(BINDIR)
	@$(JAVAC) -d $(BINDIR)/ *.java
	@$(JAVA) -cp bin MonteCarloMini.MonteCarloMinimization 1000 1000 0 100 0 100 0.5
	@$(JAVA) -cp bin MonteCarloMini.MonteCarloMinimizationParallel 1000 1000 0 100 0 100 0.5