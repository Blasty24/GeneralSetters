package com.blasty;

import org.bukkit.plugin.java.JavaPlugin;
import com.blasty.managers.PluginManager;
import com.blasty.listeners.PlayerListener;

public class GeneralStartters extends JavaPlugin {
    
    @Override
    public void onEnable() {
        
        // Initialize managers
        PluginManager.getInstance().initialize();
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        
        getLogger().info("GeneralStartters has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("GeneralStartters has been disabled!");
    }
    
}