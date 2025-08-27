package src.facilitator;

public class FileFacilitator {
    public static void main(String[] args) {
        PermissionHandler.checkPermissions();

        ConfigLoader config = new ConfigLoader("config.json");
        FileOrganizer organizer = new FileOrganizer(config);

        System.out.println("📂 FileFacilitator started. Watching: " + config.getWatchFolder());
        organizer.watchAndOrganize();
    }
}
