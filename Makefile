all: General

TT:
	javac -classpath "./lib/junit-4.12.jar:hamcrest-core-1.3.jar:src/" tests/*.java -d bin/

General:
	javac -classpath "./lib/junit-4.12.jar:hamcrest-core-1.3.jar:src/"  src/*/*/*.java -d bin/
	javac -classpath "./lib/junit-4.12.jar:hamcrest-core-1.3.jar:src/"  src/presentacion/*.java -d bin/
	javac -classpath "./lib/junit-4.12.jar:hamcrest-core-1.3.jar:src/"  src/Main.java -d bin/presentacion/

run:
	java -cp "bin/presentacion/"  Main

run-tests-lzw:
	java -classpath "lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:bin/" org.junit.runner.JUnitCore Compressor_LZWTest

run-tests-lz78:
	java -classpath "lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:bin/" org.junit.runner.JUnitCore Compressor_LZ78Test
	java -classpath "lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:bin/" org.junit.runner.JUnitCore Decompressor_LZ78Test

run-tests-lzss:
	java -classpath "lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:bin/" org.junit.runner.JUnitCore Compressor_LZSSTest

run-tests-jpeg:
	java -classpath "lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:bin/" org.junit.runner.JUnitCore Compressor_JPEGTest
	java -classpath "lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:bin/" org.junit.runner.JUnitCore Decompressor_JPEGTest

clean:
	rm bin/*/*.class bin/*.class

clean_test_txt:
	rm src/persistencia/testing_files/txt/*.lzw
	rm src/persistencia/testing_files/txt/*_decompressed.txt

clean_test_ppm:
	rm src/persistencia/testing_files/ppm/*.jpeg
	rm src/persistencia/testing_files/ppm/*_decompressed.ppm