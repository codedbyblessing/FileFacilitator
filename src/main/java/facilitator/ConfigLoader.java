package src.main.java.facilitator;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ConfigLoader {
    private final Map<String, String> rules = new HashMap<>();
    private Path watchFolder;

    public ConfigLoader(String configFile) {
        try {
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(configFile));
            JsonObject obj = gson.fromJson(reader, JsonObject.class);

            // Load rules
            JsonObject rulesObj = obj.getAsJsonObject("rules");
            for (String ext : rulesObj.keySet()) {
                rules.put(ext, rulesObj.get(ext).getAsString());
            }

            // Load watch folder
            this.watchFolder = Paths.get(obj.get("watchFolder").getAsString());

        } catch (FileNotFoundException e) {
            System.err.println("❌ Config file not found: " + configFile);
            System.exit(1);
        }
    }

    public Map<String, String> getRules() {
        return rules;
    }

    public Path getWatchFolder() {
        return watchFolder;
    }
}
