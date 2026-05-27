package com.example;

import java.util.ArrayList;
import java.util.List;

public class SharedMessageQueue {
  private final int capacity;
  private final List<String> messages = new ArrayList<>();
  private final Runnable guiCallback;

  public SharedMessageQueue(int capacity, Runnable guiCallback) {
    this.capacity = capacity;
    this.guiCallback = guiCallback;
  }

  public synchronized void addMessage(String key, String value) {
    if (messages.size() < capacity) {
      messages.add(key + ":" + value);
      guiCallback.run();

      if (messages.size() == capacity) {
        notifyAll();
      }
    }
  }

  public synchronized List<String> takeAllWhenFull() throws InterruptedException {
    while (messages.size() < capacity) {
      wait();
    }

    List<String> batch = new ArrayList<>(messages);
    messages.clear();
    guiCallback.run();
    return batch;
  }

  public synchronized int getSize() {
    return messages.size();
  }

  public int getCapacity() {
    return capacity;
  }
}
