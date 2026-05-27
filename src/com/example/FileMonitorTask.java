package com.example;

import java.io.File;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

        List<File> batch = new ArrayList<>();
        for (WatchEvent<?> event : key.pollEvents()) {
          Path context = (Path) event.context();
          File newFile = dir.resolve(context).toFile();
          Thread.sleep(300);
          batch.add(newFile);
        }
        key.reset();

        batch.sort(Comparator.comparingLong(
            f -> {
              try {
                return Files.readAttributes(f.toPath(), BasicFileAttributes.class)
                    .creationTime().toMillis();
              } catch (Exception e) {
                return 0L;
              }
            }));

        for (File file : batch) {
          fileQueue.put(file);
          onFileAdded.accept(file.getName());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
