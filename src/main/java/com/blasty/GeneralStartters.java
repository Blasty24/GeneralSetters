package com.blasty;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import com.blasty.listeners.PlayerListener;
import com.blasty.managers.ItemManager;
import com.blasty.managers.PluginManager;

public class GeneralStartters extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigurationSection bannedSection = getConfig().getConfigurationSection("BannedItems.Items");
        ConfigurationSection enableBans = getConfig().getConfigurationSection("BannedItems");
        ConfigurationSection potionChecks = getConfig().getConfigurationSection("BannedItems.PotionChecks");

        // Initialize managers
        PluginManager.getInstance().initialize(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        if(potionChecks.getBoolean(".enableOnlySpeed1Check") || potionChecks.getBoolean(".enableOnlyStrength1Check")) {
            PluginManager.getInstance().runPotionCheckTimer(0L, 20L);
        }

        if (enableBans.getBoolean("enableItemCap")) {
            PluginManager.getInstance().runTaskTimer(0L, 20L);
            if (bannedSection != null) {
                for (String key : bannedSection.getKeys(false)) {
                    int limit = bannedSection.getInt(key + ".Limit");
                    if (Material.matchMaterial(key) == null) {
                        Bukkit.getLogger().log(Level.CONFIG, "Banned item " + key
                                + " was not found, please look at the config.yml file again, and restart the plguin",
                                key);
                    }
                    ItemManager.limitList.put(Material.matchMaterial(key), limit);
                }
            }
        } else {
            Bukkit.getLogger().log(Level.INFO, "Item cap feature is disabled in config.yml");
        }
        getLogger().info("GeneralStartters has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GeneralStartters has been disabled!");
        saveConfig();
    }

}