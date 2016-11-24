package com.jadeningle.pluginmanager;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginManagerTabCompleter implements TabCompleter {
	
	private List<String> params = Arrays.asList("disable", "enable", "sreload", "reload", "unload", "show",
			"configreload");

	private boolean checkparam(String args) {
		for (String param : params) {
			if (args.equalsIgnoreCase(param))
				return true;
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> list = new ArrayList<>();
		if (args.length == 1) {
			for (String param : params) {
				if (param.toLowerCase().startsWith(args[0].toLowerCase()))
					list.add(param);
			}
		} else if (args.length > 1 && args.length < 3) {
			if (checkparam(args[0])) {
				for (Plugin pl : Bukkit.getServer().getPluginManager().getPlugins()) {
					String plname = pl.getDescription().getName();
					if (plname.toLowerCase().startsWith(args[1].toLowerCase()))
						list.add(plname);
				}
			}
		}
		return list;
	}

}
