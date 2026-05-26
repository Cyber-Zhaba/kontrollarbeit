import java.io.IOException;
import java.nio.file.*;

public class DirectoryWatcher {
  public static void main(String[] args) {
    Path path = Paths.get("target-dir");

    try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
      path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

      System.out.println("[*] Start subscribtion for target-dir ..." + path);

      while (true) {
        WatchKey key;
        try {
          key = watchService.take();
        } catch (InterruptedException e) {
          System.out.println("/!\\ End subscribtion");
          Thread.currentThread().interrupt();
          break;
        }

        for (WatchEvent<?> event : key.pollEvents()) {
          WatchEvent.Kind<?> kind = event.kind();

          if (kind == StandardWatchEventKinds.OVERFLOW) {
            continue;
          }

          @SuppressWarnings("unchecked")
          WatchEvent<Path> ev = (WatchEvent<Path>) event;
          Path filename = ev.context();

          Path child = path.resolve(filename);

          System.out.printf("Событие: %s | Файл: %s%n", kind.name(), child);
        }

        boolean valid = key.reset();
        if (!valid) {
          break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
