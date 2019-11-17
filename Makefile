CLASS_DIR=./lib
CP="$(CLASSPATH):$(CLASS_DIR):. "

javac src/*.class -d /bin
run:
clean:
	$(RM) *.class