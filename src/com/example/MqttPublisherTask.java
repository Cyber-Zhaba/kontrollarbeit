package com.example;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.util.List;

public class MqttPublisherTask implements Runnable {
    private final SharedMessageQueue messageQueue;
    private final String brokerUrl;
    private final String topic;

    public MqttPublisherTask(SharedMessageQueue messageQueue, String brokerUrl, String topic) {
        this.messageQueue = messageQueue;
        this.brokerUrl = brokerUrl;
        this.topic = topic;
    }

    @Override
    public void run() {
        try {
            MqttClient client = new MqttClient(brokerUrl, MqttClient.generateClientId(), new MemoryPersistence());
            client.connect();

            while (!Thread.currentThread().isInterrupted()) {
                List<String> batch = messageQueue.takeAllWhenFull();

                Thread.sleep(1000);

                for (String msg : batch) {
                    MqttMessage mqttMsg = new MqttMessage(msg.getBytes());
                    client.publish(topic, mqttMsg);
                    System.out.println("Опубликовано: " + msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
