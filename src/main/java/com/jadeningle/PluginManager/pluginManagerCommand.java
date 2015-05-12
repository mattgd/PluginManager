package com.jadeningle.PluginManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class pluginManagerCommand implements CommandExecutor 
{
	private final PluginManagerReloaded plugin;
	
	public pluginManagerCommand( PluginManagerReloaded instance )
	{
		plugin = instance;
	}

    public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
    {
    	
    	if( split.length == 0 )
        {
    		String[] listArray = {"Help", "Enable", "Disable", "Load", "Unload", "Reload", "Sreload", "Show", "List"};
	        sender.sendMessage( ChatColor.GREEN + "|------------------" + ChatColor.GRAY + "PluginManager Help" + ChatColor.GREEN + "------------------|" );
	        for( String string : listArray )
	        	sender.sendMessage( ChatColor.GOLD + "/" + label + " " + ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Command.Description." + string ) ) );
        	return true;
        }
    	return false;
    }
}