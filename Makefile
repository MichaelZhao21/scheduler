JAVA=java
JAVAC=javac

.SUFFIXES: $(SUFFIXES) .class .java

.java.class:
	$(JAVAC) $*.java

CLASSES = Process.java Scheduler.java Node.java BinaryTree.java RedBlackTree.java

default: run

run: scheduler

scheduler: all
	$(JAVA) Scheduler

all: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
