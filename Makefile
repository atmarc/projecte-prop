# CLASS_DIR=./lib
# CP="$(CLASSPATH):$(CLASS_DIR):. "

tt:
	javac -classpath "./lib/junit-4.12.jar:hamcrest-core-1.3.jar:src/" tests/*.java -d bin/

Application:
	javac src/*.java -d bin/


clean:
	rm bin/*.class