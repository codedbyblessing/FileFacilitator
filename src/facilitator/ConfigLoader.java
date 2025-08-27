package src.facilitator;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ConfigLoader {
    private Map<String, String> rules;
    private String watchFolder;

    public ConfigLoader(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Map<String, Object> config = gson.fromJson(reader, new TypeToken<Map<String, Object>>(){}.getType());
            this.rules = (Map<String, String>) config.get("rules");
            this.watchFolder = (String) config.get("watchFolder");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config file: " + e.getMessage());
        }
    }

    public Map<String, String> getRules() {
        return rules;
    }

    public Path getWatchFolder() {
        return Path.of(watchFolder);
    }
}
