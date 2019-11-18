# CLASS_DIR=./lib
# CP="$(CLASSPATH):$(CLASS_DIR):. "


all: tt Application cc

cc:
	set CLASSPATH='bin/'

tt:
	javac -classpath "./lib/junit-4.12.jar:hamcrest-core-1.3.jar:src/:bin/" tests/*.java -d bin/

Application:
	javac -classpath "bin/" src/*.java -d bin/

run:
	java -cp "bin/" Application

clean:
	rm bin/*.class