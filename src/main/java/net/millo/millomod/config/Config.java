package net.millo.millomod.config;

import net.fabricmc.loader.api.FabricLoader;
import net.millo.millomod.mod.features.FeatureHandler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Config {

    private static Config INSTANCE;
    private static final HashMap<String, String> config = new HashMap<>();

    private final File file;

    private boolean broken = false;


    public Config(String filename) {
        this(Config.of(filename));
    }
    public Config(File file) {
        this.file = file;

        if (!file.exists()) {
            try {
                createConfig();
            } catch (IOException e) {
                broken = true;
            }
        }

        if (broken) return;

        try {
            loadConfig();
        } catch (IOException e) {
            broken = true;
        }

    }


    public static Config getInstance() {
        if (INSTANCE == null) INSTANCE = new Config("options");
        return INSTANCE;
    }

    public <T extends Comparable<? super T>> void set(String key, T value) {
        if (!(value instanceof Integer || value instanceof Double || value instanceof String || value instanceof Boolean)) {
            throw new IllegalArgumentException("Type T must be Integer, Double, String, or Boolean.");
        }
        config.put(key, value.toString());
    }


    private void createConfig() throws IOException {
        file.getParentFile().mkdirs();
        Files.createFile(file.toPath());

        ModConfigs.loadDefaults();

        saveConfig();
    }
    private void loadConfig() throws IOException {
        Scanner reader = new Scanner(file);
        for (int line = 1; reader.hasNextLine(); line ++) {
            parseConfigEntry(reader.nextLine(), line);
        }
    }
    public void saveConfig() throws IOException {
        // Generate the config string
        StringBuilder configString = new StringBuilder();
        for (Map.Entry<String, String> entry : config.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            configString.append(key).append("=").append(value).append("\n");
        }

        System.out.println(configString);

        // Write the config in the file
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            Files.createFile(file.toPath());
        } else {
            Files.write(file.toPath(), new byte[0]);
        }

        PrintWriter writer = new PrintWriter(file, "UTF-8");
        writer.write(configString.toString());
        writer.close();

        FeatureHandler.configUpdate(this);
    }


    public static File of(String filename) {
        Path path = FabricLoader.getInstance().getConfigDir();
        return path.resolve(filename + ".config").toFile();
    }

    private void parseConfigEntry(String entry, int line) {
        if (entry.isEmpty() || entry.startsWith("#")) return;
        String[] parts = entry.split("=", 2);
        if (parts.length == 2) {
            config.put(parts[0], parts[1]);
        } else {
            throw new RuntimeException("Syntax error in config file on line " + line);
        }
    }


    private String get(String key) {
        return config.get(key);
    }
    public boolean getOrDefault(String key, boolean def) {
        String val = get(key);
        if (val != null) {
            return val.equalsIgnoreCase("true");
        }
        return def;
    }
    public int getOrDefault(String key, int def) {
        try {
            return Integer.parseInt(get(key));
        } catch (NumberFormatException e) {
            return def;
        }
    }
    public double getOrDefault(String key, double def) {
        try {
            return Double.parseDouble(get(key));
        } catch (NumberFormatException e) {
            return def;
        }
    }
    public String getOrDefault(String key, String def) {
        String val = get(key);
        return val == null ? def : val;
    }
}
