package src.main.java.facilitator;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Logger {
    private final Path logFile;

    // Constructor takes the watchFolder
    public Logger(Path watchFolder) {
        this.logFile = watchFolder.resolve("moves.log");
    }

    public void logMove(Path source, Path target) {
        try (FileWriter fw = new FileWriter(logFile.toFile(), true)) {
            fw.write(LocalDateTime.now() + " | Moved: " + source + " → " + target + "\n");
        } catch (IOException e) {
            System.err.println("⚠️ Failed to log move: " + e.getMessage());
        }
    }
}
