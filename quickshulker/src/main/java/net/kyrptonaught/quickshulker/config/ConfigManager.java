package net.kyrptonaught.quickshulker.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// Minimal replacement for kyrptconfig's ConfigManager, which is not
// available for 26.3. Config is a plain JSON file in the config directory.
public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path configFile;
    private final ConfigOptions config;

    public ConfigManager(String modId, ConfigOptions config) {
        this.configFile = FabricLoader.getInstance().getConfigDir().resolve(modId + ".json");
        this.config = config;
    }

    public void load() {
        if (Files.exists(this.configFile)) {
            try {
                ConfigOptions loaded = GSON.fromJson(Files.readString(this.configFile), ConfigOptions.class);
                if (loaded != null) copyInto(loaded, this.config);
            } catch (Exception e) {
                System.out.println("[QuickShulker]: failed to load config, using defaults");
                e.printStackTrace();
            }
        }
        save();
    }

    public void save() {
        try {
            Files.createDirectories(this.configFile.getParent());
            Files.writeString(this.configFile, GSON.toJson(this.config));
        } catch (IOException e) {
            System.out.println("[QuickShulker]: failed to save config");
            e.printStackTrace();
        }
    }

    public ConfigOptions getConfig() {
        return this.config;
    }

    private static void copyInto(ConfigOptions from, ConfigOptions to) {
        to.rightClickToOpen = from.rightClickToOpen;
        to.keybind = from.keybind;
        to.keybingInInv = from.keybingInInv;
        to.rightClickInv = from.rightClickInv;
        to.rightClickClose = from.rightClickClose;
        to.supportsBundlingInsert = from.supportsBundlingInsert;
        to.supportsBundlingPickup = from.supportsBundlingPickup;
        to.supportsBundlingExtract = from.supportsBundlingExtract;
        to.quickShulkerBox = from.quickShulkerBox;
        to.quickCraftingTables = from.quickCraftingTables;
        to.quickStonecutter = from.quickStonecutter;
        to.quickEChest = from.quickEChest;
    }
}
