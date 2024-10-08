import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class FileFacilitator {

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    private void showError(String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String directoryPath() {
        return String.format("%s/%s", FileFacilitator.class.getProtectionDomain().
                getCodeSource().getLocation().getPath(), org_file);
    }
    private static final String
            Downloads = System.getProperty("user.home") + "/Downloads"; // Path to the Downloads folder
    private static final String
            org_file = "organizeFolder"; // Folder to organize files
    private static final Map<String, Integer>
            fileCountByType = new HashMap<>(); // Map to track counts of sorted files by MIME type

    public static void main(String[] args) {
        FileFacilitator fileManager = new FileFacilitator();
        fileManager.monitor();
        fileManager.checkFile();
    }

    private void monitor() {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(Downloads);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            System.out.println("Monitoring Downloads directory...");

            // Monitor the directory
            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        continue; 
                    }

                    Path filePath = path.resolve((Path) event.context());
                    System.out.println("New file detected: " + filePath);
                    // Sort the file based on its MIME type
                    sortFiles(filePath);
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sortFiles(Path filePath) {
        try {
            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) {
                mimeType = "unknown";
            }

            String category = mimeType.split("/")[0]; // Use the first part of MIME type
            Path categoryDir = Paths.get(Downloads, category);

            // Create the category directory if it doesn't exist
            if (Files.notExists(categoryDir)) {
                Files.createDirectories(categoryDir);
            }

            // Move the file to the new directory
            Files.move(filePath, categoryDir.resolve(filePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Moved file to: " + categoryDir.resolve(filePath.getFileName()));

            // Use BasicFileAttributes to get additional file details
            BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
            System.out.printf("Size: %d bytes, Created on: %s%n", attrs.size(), attrs.creationTime());

            // Update file count in the map
            fileCountByType.put(category, fileCountByType.getOrDefault(category, 0) + 1);
            System.out.println("Total files sorted in '" + category + "': " + fileCountByType.get(category));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile() {
        String path = FileFacilitator.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File file = new File(path);
        String jarDir = file.getParentFile().getAbsolutePath();
        String directoryPath = String.format("%s/%s", jarDir, org_file);

        if (file != null) { // if file exists then show directory
            showInfo("Path: " + directoryPath);
        } else {
            showError("File not found.");
            return;
        }

        directoryChecker(directoryPath);
        showInfo("File Organizer Complete!");
    }

    private void directoryChecker(String directoryPath) {
        File directory = new File(directoryPath);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            fileFacilitate(files);
        } else {
            showError("Invalid directory: " + directoryPath);
        }
    }

    private void fileFacilitate(File[] files) {
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    String fileExtension = getFileExtension(file.getName());
                    extensionSorter(fileExtension, file);
                }
            }
        }
    }

    private void extensionSorter(String fileExtension, File file) {
        String[][] fileTypes = {
                {"pdf", "PDFs"},
                {"png", "Images"}, {"jpg", "Images"}, {"jpeg", "Images"}, {"gif", "Images"},
                {"doc", "Documents"}, {"docx", "Documents"}, {"pptx", "Documents"}, {"txt", "Documents"},
                {"csv", "Data"}, {"xlsx", "Data"},
                {"zip", "Archives"}, {"rar", "Archives"},
                {"exe", "Executable"},
                {"mp3", "Music"}, {"wav", "Music"},
                {"mp4", "Videos"}, {"avi", "Videos"}, {"flv", "Videos"}, {"wmv", "Videos"}
        };

        for (String[] type : fileTypes) {
            if (type[0].equalsIgnoreCase(fileExtension)) {
                folderChecker(type[1], file);
                return; 
            }
        }
    }

    private void folderChecker(String category, File file) {
        String folderPath = String.format("%s/%s", directoryPath(), category);
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            boolean created = folder.mkdirs();
            if (created) {
                showInfo("Created: " + category);
            } else {
                showError("Failed to create folder: " + folderPath);
            }
        } else {
            System.out.printf("Folder exists: %s%n", category);
        }

        fileMover(file, category);
    }

    private void fileMover(File file, String targetSubfolder) {
        if (file.isFile()) {
            File targetFolder = new File(file.getParentFile(), targetSubfolder);
            Path sourcePath = file.toPath();
            Path targetPath = targetFolder.toPath().resolve(file.getName());

            try {
                Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Moved: " + file.getName());
            } catch (IOException e) {
                showError("Failed to move: " + file.getName());
                e.printStackTrace();
            }
        }
    }
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }
}

