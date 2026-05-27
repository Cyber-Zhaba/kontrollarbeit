CP = out:lib/flatlaf-3.7.jar:lib/org.eclipse.paho.client.mqttv3-1.2.5.jar

compile:
	mkdir -p out
	javac -d out -cp "$(CP)" src/com/example/*.java

run: compile
	java -cp "$(CP)" com.example.App

run-mqtt:
	docker run --rm -it -p 1883:1883 -v "/home/arseny/projects/plm/kontrollarbeit/mosquitto.conf:/mosquitto/config/mosquitto.conf" eclipse-mosquitto:latest

.PHONY: compile run run-mqtt


