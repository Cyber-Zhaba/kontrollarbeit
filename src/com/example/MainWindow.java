package com.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.BlockingQueue;

public class MainWindow extends JFrame {
  private DefaultListModel<String> fileListModel;
  private JProgressBar queueProgressBar;
  private DefaultListModel<String> mqttListModel;

  public MainWindow() {
    setTitle("File & MQTT Thread Manager");
    setSize(900, 500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new GridLayout(1, 3, 10, 10));

    fileListModel = new DefaultListModel<>();
    JList<String> fileList = new JList<>(fileListModel);
    JPanel col1 = new JPanel(new BorderLayout());
    col1.add(new JLabel("Очередь файлов", SwingConstants.CENTER), BorderLayout.NORTH);
    col1.add(new JScrollPane(fileList), BorderLayout.CENTER);

    queueProgressBar = new JProgressBar(0, 100);
    queueProgressBar.setStringPainted(true);
    JPanel col2 = new JPanel(new BorderLayout());
    col2.add(new JLabel("Очередь на отправку", SwingConstants.CENTER), BorderLayout.NORTH);
    col2.add(queueProgressBar, BorderLayout.CENTER);

    mqttListModel = new DefaultListModel<>();
    JList<String> mqttList = new JList<>(mqttListModel);
    JPanel col3 = new JPanel(new BorderLayout());
    col3.add(new JLabel("Получено из MQTT", SwingConstants.CENTER), BorderLayout.NORTH);
    col3.add(new JScrollPane(mqttList), BorderLayout.CENTER);

    add(col1);
    add(col2);
    add(col3);
  }

  public void addFileToList(String fileName) {
    SwingUtilities.invokeLater(() -> {
      fileListModel.addElement(fileName);
    });
  }

  public void removeFileFromList(String fileName) {
    SwingUtilities.invokeLater(() -> {
      fileListModel.removeElement(fileName);
    });
  }

  public void updateFileQueueUI(BlockingQueue<File> fileQueue) {
    SwingUtilities.invokeLater(() -> {
      fileListModel.clear();
      for (File f : fileQueue) {
        fileListModel.addElement(f.getName());
      }
    });
  }

  public void updateMessageQueueUI(SharedMessageQueue msgQueue) {
    SwingUtilities.invokeLater(() -> {
      int max = msgQueue.getCapacity();
      int current = msgQueue.getSize();
      queueProgressBar.setMaximum(max);
      queueProgressBar.setValue(current);
      queueProgressBar.setString(current + " / " + max);
    });
  }

  public void appendMqttMessage(String msg) {
    SwingUtilities.invokeLater(() -> {
      mqttListModel.addElement(msg);
    });
  }
}
