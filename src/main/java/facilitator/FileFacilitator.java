package src.main.java.facilitator;

import java.nio.file.Paths;

public class FileFacilitator {
    public static void main(String[] args) {
        // Checks permissions
        
        // ✅ Check permissions on the watch folder from config
        PermissionHandler.checkPermissions(Paths.get(config.getWatchFolder()));

        // Load configuration from config.json
        ConfigLoader config = new ConfigLoader("config.json");

        // Initialize the organizer with rules from config
        FileOrganizer organizer = new FileOrganizer(config);

        System.out.println("📂 FileFacilitator started. Watching: " + config.getWatchFolder());

        // Run continuous watch mode
        organizer.watchAndOrganize();
    }
}
