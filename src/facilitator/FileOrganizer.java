package src.facilitator;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;

public class FileOrganizer {
    private final ConfigLoader config;
    private final Logger logger = new Logger();

    public FileOrganizer(ConfigLoader config) {
        this.config = config;
    }

    public void organizeFiles() {
        Path watchDir = config.getWatchFolder();
        Map<String, String> rules = config.getRules();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(watchDir)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    String extension = getFileExtension(file);
                    if (rules.containsKey(extension)) {
                        Path targetDir = Path.of(rules.get(extension));
                        Files.createDirectories(targetDir);

                        Path targetFile = targetDir.resolve(file.getFileName());
                        Files.move(file, targetFile, StandardCopyOption.REPLACE_EXISTING);

                        logger.logMove(file, targetFile);
                        System.out.println("✅ Moved " + file.getFileName() + " → " + targetDir);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("⚠️ Error organizing files: " + e.getMessage());
        }
    }

    private String getFileExtension(Path file) {
        String name = file.getFileName().toString();
        int i = name.lastIndexOf('.');
        return (i > 0) ? name.substring(i + 1).toLowerCase() : "";
    }
}
