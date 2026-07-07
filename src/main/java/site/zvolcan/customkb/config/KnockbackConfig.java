package site.zvolcan.customkb.config;

import org.bukkit.configuration.file.FileConfiguration;
import site.zvolcan.customkb.Main;

public final class KnockbackConfig {

    private static final String HORIZONTAL_PATH = "knockback.horizontal-multiplier";
    private static final String VERTICAL_PATH = "knockback.vertical-multiplier";
    private static final double DEFAULT_MULTIPLIER = 1.0;

    private final Main plugin;
    private double horizontalMultiplier;
    private double verticalMultiplier;

    public KnockbackConfig(Main plugin) {
        this.plugin = plugin;
        reload();
    }

    public double getHorizontalMultiplier() {
        return horizontalMultiplier;
    }

    public double getVerticalMultiplier() {
        return verticalMultiplier;
    }

    public void setHorizontalMultiplier(double value) {
        horizontalMultiplier = value;
        save();
    }

    public void setVerticalMultiplier(double value) {
        verticalMultiplier = value;
        save();
    }

    public void reload() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        horizontalMultiplier = config.getDouble(HORIZONTAL_PATH, DEFAULT_MULTIPLIER);
        verticalMultiplier = config.getDouble(VERTICAL_PATH, DEFAULT_MULTIPLIER);
    }

    private void save() {
        FileConfiguration config = plugin.getConfig();
        config.set(HORIZONTAL_PATH, horizontalMultiplier);
        config.set(VERTICAL_PATH, verticalMultiplier);
        plugin.saveConfig();
    }
}
