package com.example;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class App {
  public static void main(String[] args) {
    FlatLightLaf.setup();

    String brokerUrl = "tcp://localhost:1883";
    String topic = "iu9";
    int MESSAGE_QUEUE_CAPACITY = 5;
    Path watchDir = Paths.get("target-dir");

    File dir = watchDir.toFile();
    if (!dir.exists())
      dir.mkdirs();

    BlockingQueue<File> fileQueue = new LinkedBlockingQueue<>();
    MainWindow window = new MainWindow();

    final SharedMessageQueue[] msgQueueHolder = new SharedMessageQueue[1];
    SharedMessageQueue messageQueue = new SharedMessageQueue(
        MESSAGE_QUEUE_CAPACITY,
        () -> window.updateMessageQueueUI(msgQueueHolder[0]));
    msgQueueHolder[0] = messageQueue;

    SwingUtilities.invokeLater(() -> {
      window.setVisible(true);
      window.updateMessageQueueUI(messageQueue);
    });

    Thread t1 = new Thread(new FileMonitorTask(watchDir, fileQueue, window::addFileToList));
    Thread t2 = new Thread(new FileParserTask(fileQueue, messageQueue, window::removeFileFromList));
    Thread t3 = new Thread(new MqttPublisherTask(messageQueue, brokerUrl, topic));
    Thread t4 = new Thread(new MqttListenerTask(brokerUrl, topic, window::appendMqttMessage));

    t1.start();
    t2.start();
    t3.start();
    t4.start();
  }
}
