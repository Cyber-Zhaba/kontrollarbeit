package com.example;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.function.Consumer;

public class MqttListenerTask implements Runnable {
    private final String brokerUrl;
    private final String topic;
    private final Consumer<String> onMessageReceived;

    public MqttListenerTask(String brokerUrl, String topic, Consumer<String> onMessageReceived) {
        this.brokerUrl = brokerUrl;
        this.topic = topic;
        this.onMessageReceived = onMessageReceived;
    }

    @Override
    public void run() {
        try {
            MqttClient client = new MqttClient(brokerUrl, MqttClient.generateClientId(), new MemoryPersistence());
            client.setCallback(new MqttCallback() {
                public void connectionLost(Throwable cause) {}
                public void messageArrived(String topic, MqttMessage message) {
                    try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    onMessageReceived.accept(new String(message.getPayload()));
                }
                public void deliveryComplete(IMqttDeliveryToken token) {}
            });
            client.connect();
            client.subscribe(topic);
            System.out.println("Слушатель подключен к топику: " + topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
