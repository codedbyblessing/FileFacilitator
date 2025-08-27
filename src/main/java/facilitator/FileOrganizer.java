package src.main.java.facilitator;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class FileOrganizer {
    private final ConfigLoader config;
    private final Logger logger;

    public FileOrganizer(ConfigLoader config) {
        this.config = config;
        // ✅ Pass watchFolder into Logger
        this.logger = new Logger(config.getWatchFolder());
    }

    /* One-time organization of existing files */
    public void organizeFiles() {
        Path watchDir = config.getWatchFolder();
        Map<String, String> rules = config.getRules();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(watchDir)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    processFile(file, rules);
                }
            }
        } catch (IOException e) {
            System.err.println("⚠️ Error organizing files: " + e.getMessage());
        }
    }

    /** Continuous watch mode */
    public void watchAndOrganize() {
        Path watchDir = config.getWatchFolder();
        Map<String, String> rules = config.getRules();

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            watchDir.register(watchService, ENTRY_CREATE);

            System.out.println("👀 Watching for new files in " + watchDir + " ... Press Ctrl+C to exit.");

            while (true) {
                WatchKey key = watchService.take(); // blocks until event
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == ENTRY_CREATE) {
                        Path newFile = watchDir.resolve((Path) event.context());
                        if (Files.isRegularFile(newFile)) {
                            processFile(newFile, rules);
                        }
                    }
                }
                if (!key.reset()) {
                    System.out.println("❌ Watch key invalid. Exiting.");
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("⚠️ Watch service error: " + e.getMessage());
        }
    }

    /** Handles moving a file based on extension rules */
    private void processFile(Path file, Map<String, String> rules) {
        String extension = getFileExtension(file);
        if (rules.containsKey(extension)) {
            try {
                // ✅ Ensure target path is inside watchFolder
                Path targetDir = config.getWatchFolder().resolve(rules.get(extension));
                Files.createDirectories(targetDir);

                Path targetFile = targetDir.resolve(file.getFileName());
                Files.move(file, targetFile, StandardCopyOption.REPLACE_EXISTING);

                logger.logMove(file, targetFile);
                System.out.println("✅ Moved " + file.getFileName() + " → " + targetDir);
            } catch (IOException e) {
                System.err.println("⚠️ Failed to move " + file + ": " + e.getMessage());
            }
        }
    }

    private String getFileExtension(Path file) {
        String name = file.getFileName().toString();
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(i + 1).toLowerCase() : "";
    }
}
