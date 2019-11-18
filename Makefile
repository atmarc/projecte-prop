all: TT Application

TT:
	javac -classpath "./lib/junit-4.12.jar:hamcrest-core-1.3.jar:src/" tests/*.java -d bin/

Application:
	javac src/*/*.java -d bin/

run:
	java -cp "bin/" presentacion.Application

run-lzw-test:
	java -classpath "lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:bin/" org.junit.runner.JUnitCore Compressor_LZWTest

testingCompressJPEG:
	java -cp "bin/" Compressor_JPEGTest

clean:
	rm bin/*/*.class bin/*.class

clean_test_txt:
	rm src/persistencia/testing_files/txt/*.lzw
	rm src/persistencia/testing_files/txt/*_decompressed.txt