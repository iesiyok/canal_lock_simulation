JFLAGS = -g
JC = javac

.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java
CLASSES = \
	Producer.java \
	Consumer.java \
	Lock.java \
	Main.java \
	Operator.java \
	Param.java \
	Section.java \
	Tug.java \
	Vessel.java \

default: classes
classes: $(CLASSES:.java=.class)
clean:
	$(RM) *.class
