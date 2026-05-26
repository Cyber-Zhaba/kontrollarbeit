run-mqtt:
	docker run --rm -it -p 1883:1883 -v "/home/arseny/projects/plm/kontrollarbeit/mosquitto.conf:/mosquitto/config/mosquitto.conf" eclipse-mosquitto:latest


