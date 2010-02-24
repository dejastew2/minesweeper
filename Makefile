all:
	javac -source 1.4 -classpath jess.jar -d build @source

run:
	cd build; \
	java -classpath ../jess.jar:. StartMinesweeper; \
	cd ..;

clean:
	rm -rf *.class
