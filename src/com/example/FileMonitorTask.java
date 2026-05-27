package com.example;

import java.io.File;
import java.nio.file.*;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class FileMonitorTask implements Runnable {
  private final Path dir;
  private final BlockingQueue<File> fileQueue;
  private final Consumer<String> onFileAdded;

  public FileMonitorTask(Path dir, BlockingQueue<File> fileQueue, Consumer<String> onFileAdded) {
    this.dir = dir;
    this.fileQueue = fileQueue;
    this.onFileAdded = onFileAdded;
  }

  @Override
  public void run() {
    try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
      dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

      while (!Thread.currentThread().isInterrupted()) {
        WatchKey key = watchService.take();
        for (WatchEvent<?> event : key.pollEvents()) {
          Path context = (Path) event.context();
          File newFile = dir.resolve(context).toFile();

          Thread.sleep(300);

          fileQueue.put(newFile);
          onFileAdded.accept(newFile.getName());
        }
        key.reset();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
