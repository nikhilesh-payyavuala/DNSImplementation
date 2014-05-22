all: target

target: Server.java Client.java DNSresponse.java DNSlookup.java

	javac *.java

clean:
	rm *.class
