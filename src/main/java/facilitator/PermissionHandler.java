package src.main.java.facilitator;

import java.io.IOException;
import java.nio.file.*;

public class PermissionHandler {

    public static void checkPermissions(Path watchFolder) {
        if (!Files.exists(watchFolder)) {
            try {
                Files.createDirectories(watchFolder);
                System.out.println("📂 Created watch folder: " + watchFolder);
            } catch (IOException e) {
                System.err.println("❌ Failed to create watch folder: " + e.getMessage());
                System.exit(1);
            }
        }

        if (!Files.isReadable(watchFolder)) {
            System.err.println("❌ Folder is not readable: " + watchFolder);
            System.exit(1);
        }

        if (!Files.isWritable(watchFolder)) {
            System.err.println("❌ Folder is not writable: " + watchFolder);
            System.exit(1);
        }

        System.out.println("✅ Permissions OK for " + watchFolder);
    }
}
