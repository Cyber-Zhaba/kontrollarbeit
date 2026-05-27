package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class FileParserTask implements Runnable {
  private final BlockingQueue<File> fileQueue;
  private final SharedMessageQueue messageQueue;
  private final Consumer<String> onFileRemoved;

  public FileParserTask(BlockingQueue<File> fileQueue, SharedMessageQueue messageQueue,
      Consumer<String> onFileRemoved) {
    this.fileQueue = fileQueue;
    this.messageQueue = messageQueue;
    this.onFileRemoved = onFileRemoved;
  }

  @Override
  public void run() {
    try {
      while (!Thread.currentThread().isInterrupted()) {
        File file = fileQueue.take();

        Thread.sleep(1000);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
          String line;
          while ((line = br.readLine()) != null) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
              messageQueue.addMessage(parts[0].trim(), parts[1].trim());
              Thread.sleep(500);
            }
          }
        }
        file.delete();
        onFileRemoved.accept(file.getName());
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
