# CLASS_DIR=./lib
# CP="$(CLASSPATH):$(CLASS_DIR):. "


all: tt Application

tt:
	javac -classpath "./lib/junit-4.12.jar:hamcrest-core-1.3.jar:src/" tests/*.java -d bin/

Application:
	javac src/*.java -d bin/

run:
	java -cp "bin/" Application

clean:
	rm bin/*.class