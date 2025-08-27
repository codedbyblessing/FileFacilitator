package src.facilitator;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class Logger {
    private static final String LOG_FILE = "moves.log";

    public void logMove(Path source, Path target) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write("Moved: " + source + " → " + target + "\n");
        } catch (IOException e) {
            System.err.println("⚠️ Failed to log move: " + e.getMessage());
        }
    }
}
