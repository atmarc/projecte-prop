all: TT Application

TT:
	javac -classpath "./lib/junit-4.12.jar:hamcrest-core-1.3.jar:src/" tests/*.java -d bin/

Application:
	javac src/*.java -d bin/

run:
	java -cp "bin/" Application

clean:
	rm bin/*.class