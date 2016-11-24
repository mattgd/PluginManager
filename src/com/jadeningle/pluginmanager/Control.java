package com.jadeningle.pluginmanager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

public class Control {

	PluginManagerReloaded plugin;

	public Control(PluginManagerReloaded instance) {
		plugin = instance;
	}

	public PluginDescriptionFile getDescription(final File file) {
		try {
			final JarFile jar = new JarFile(file);
			final ZipEntry zip = jar.getEntry("plugin.yml");
			if (zip == null) {
				jar.close();
				return null;
			}
			final PluginDescriptionFile pdf = new PluginDescriptionFile(jar.getInputStream(zip));
			jar.close();
			return pdf;
		} catch (InvalidDescriptionException | IOException ioe) {
			ioe.printStackTrace();
		}
		return null;

	}

	public File getFile(final JavaPlugin plugin) {
		Field file;
		try {
			file = JavaPlugin.class.getDeclaredField("file");
			file.setAccessible(true);
			return (File) file.get(plugin);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void enablePlugin(final Plugin plugin) {
		Bukkit.getPluginManager().enablePlugin(plugin);
		File file = new File(this.plugin.getDataFolder(), "list.yml");
		if (file.exists()) {
			FileConfiguration disabled = YamlConfiguration.loadConfiguration(file);
			List<String> list = disabled.getStringList("Disabled");
			if (list.contains(plugin.getName()))
				list.remove(plugin.getName());
			disabled.set("Disabled", list);

			try {
				disabled.save(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void disablePlugin(final Plugin plugin) {
		Bukkit.getPluginManager().disablePlugin(plugin);
		File file = new File(this.plugin.getDataFolder(), "list.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		FileConfiguration disabled = YamlConfiguration.loadConfiguration(file);
		List<String> list = disabled.getStringList("Disabled");
		if (!list.contains(plugin.getName()))
			list.add(plugin.getName());
		disabled.set("Disabled", list);
		try {
			disabled.save(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Plugin loadPlugin(final File plugin) {
		Plugin p;
		try {
			p = Bukkit.getPluginManager().loadPlugin(plugin);
			try {
				p.onLoad();
			} catch (final Exception e) {
				System.out.println(
						String.format(this.plugin.language.getString("Response.Error.FailedonLoad"), plugin.getName()));
				e.printStackTrace();
			}
			return p;
		} catch (InvalidPluginException | InvalidDescriptionException | UnknownDependencyException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public boolean unloadPlugin(Plugin plugin, Boolean ReloadDependents) {
		SimpleCommandMap commandMap;
		PluginManager pluginManager = Bukkit.getPluginManager();

		String pName = plugin.getName();
		List<Plugin> plugins;
		Map<String, Plugin> names;
		Map<String, Command> commands;
		ArrayList<Plugin> reload = new ArrayList<>();

		if (ReloadDependents) {
			for (final Plugin p : pluginManager.getPlugins()) {
				final List<String> depend = p.getDescription().getDepend();
				if (depend != null) {
					for (final String s : depend) {
						if (s.equals(pName)) {
							if (!reload.contains(p)) {
								reload.add(p);
								unloadPlugin(p, false);
							}
						}
					}
				}

				final List<String> softDepend = p.getDescription().getSoftDepend();
				if (softDepend != null) {
					for (final String s : softDepend) {
						if (s.equals(pName)) {
							if (!reload.contains(p)) {
								reload.add(p);
								unloadPlugin(p, false);
							}
						}
					}
				}
			}
		}
		for (Plugin p : reload) {
			Bukkit.getServer().broadcastMessage(p.getName() + "\n");
		}
		try {
			Field pluginsField, lookupNamesField, commandMapField, knownCommandsField;

			pluginsField = pluginManager.getClass().getDeclaredField("plugins");
			lookupNamesField = pluginManager.getClass().getDeclaredField("lookupNames");
			commandMapField = pluginManager.getClass().getDeclaredField("commandMap");
			knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");

			pluginsField.setAccessible(true);
			lookupNamesField.setAccessible(true);
			commandMapField.setAccessible(true);
			knownCommandsField.setAccessible(true);

			plugins = (List<Plugin>) pluginsField.get(pluginManager);
			names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);
			commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);
			commands = (Map<String, Command>) knownCommandsField.get(commandMap);

		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
			return false;
		}

		if (commandMap != null) {
			synchronized (commandMap) {
				final Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, Command> entry = it.next();
					if (entry.getValue() instanceof PluginCommand) {
						PluginCommand c = (PluginCommand) entry.getValue();
						if (c.getPlugin() == plugin) {
							c.unregister(commandMap);
							it.remove();
						}
					}
				}
			}
		}

		disablePlugin(plugin);

		synchronized (pluginManager) {
			if (plugins != null && plugins.contains(plugin))
				plugins.remove(plugin);

			if (names != null && names.containsKey(pName))
				names.remove(pName);
		}

		final JavaPluginLoader jpl = (JavaPluginLoader) plugin.getPluginLoader();
		Field loaders = null;

		try {
			loaders = jpl.getClass().getDeclaredField("loaders");
			loaders.setAccessible(true);
		} catch (final Exception e) {
			e.printStackTrace();
		}

		try {
			final Map<String, ?> loaderMap = (Map<String, ?>) loaders.get(jpl);
			loaderMap.remove(plugin.getDescription().getName());
		} catch (final Exception e) {
			e.printStackTrace();
		}

		ClassLoader cl = plugin.getClass().getClassLoader();

		try {
			((URLClassLoader) cl).close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.gc();

		if (ReloadDependents) {
			for (int i = 0; i < reload.size(); i++) {
				enablePlugin(loadPlugin(getFile((JavaPlugin) reload.get(i))));
			}
		}

		File loaded = getFile((JavaPlugin) plugin);
		File unloaded = new File(getFile((JavaPlugin) plugin) + ".unloaded");

		return loaded.renameTo(unloaded);
	}

}
