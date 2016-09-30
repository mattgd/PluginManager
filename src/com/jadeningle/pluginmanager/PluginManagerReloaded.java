package com.jadeningle.pluginmanager;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginManagerReloaded extends JavaPlugin implements Listener {

	public FileConfiguration language;
	private Control control = new Control(this);

	@Override
	public void onEnable() {
		getCommand("pluginmanager").setExecutor(new PluginManagerCommand(this));
		getCommand("pluginmanager").setTabCompleter(new PluginManagerTabCompleter());

		Bukkit.getPluginManager().registerEvents(this, this);

		initConfig();

		File file = new File(getDataFolder(), "List.yml");
		if (file.exists()) {
			final FileConfiguration disabled = YamlConfiguration.loadConfiguration(file);
			boolean isReload = disabled.contains("Reload") && disabled.getBoolean("Reload");
			if (isReload) {
				disabled.set("Reload", null);
				getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						List<String> list = disabled.getStringList("Disabled");
						for (String name : list) {
							control.disablePlugin(Bukkit.getPluginManager().getPlugin(name));
						}
					}
				});
			} else
				disabled.set("Disabled", null);

			try {
				disabled.save(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		getLogger().info("Plugin Manager Reloaded is now enabled!");
	}

	@Override
	public void onDisable() {
		language = null;
		getLogger().info("Plugin Manager Reloaded is now disabled!");
	}

	@EventHandler
	public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
		String message = e.getMessage();
		String[] args = message.split(" ");
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("/reload") || args[0].equalsIgnoreCase("/rl")) {
				File file = new File(getDataFolder(), "List.yml");
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				FileConfiguration disabled = YamlConfiguration.loadConfiguration(file);
				disabled.set("Reload", true);

				try {
					disabled.save(file);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

			}
		}
	}

	private void initConfig() {
		if (!new File(getDataFolder(), "config.yml").exists())
			saveResource("config.yml", false);

		String[] langArray = { "en_US" };
		for (String string : langArray) {
			if (!new File(getDataFolder() + File.separator + "localization", string + ".yml").exists())
				saveResource("localization" + File.separator + string + ".yml", false);
		}

		language = YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator + "localization",
				this.getConfig().getString("Language") + ".yml"));
	}

}
