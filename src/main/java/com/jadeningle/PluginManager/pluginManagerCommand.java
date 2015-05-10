package com.jadeningle.PluginManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class pluginManagerCommand implements CommandExecutor 
{
	private final PluginManagerReloaded plugin;
	
	public pluginManagerCommand( PluginManagerReloaded pluginManagerReloaded )
	{
		plugin = pluginManagerReloaded;
	}

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
    {
    	Player player = (Player) sender;
    	
    	if( split.length == 0 )
        {
        	player.sendMessage( ChatColor.GREEN + "|------------------" + ChatColor.GRAY + "PluginManagerReloaded Help" + ChatColor.GREEN + "------------------|" );
        	player.sendMessage( ChatColor.GOLD + "/plm help <command> "+ ChatColor.AQUA +"- Command help" );
        		
        	return true;
        }
    	return false;
    }
}