package com.jadeningle.PluginManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.jadeningle.PluginManager.Utils.Control;

public class pluginManagerCommand implements CommandExecutor {
	
	private final PluginManagerReloaded plugin;
	private final Control control;
	
	public pluginManagerCommand( PluginManagerReloaded instance )
	{
		plugin = instance;
		control = new Control( instance );
	}

    public boolean onCommand( CommandSender sender, Command command, String label, String[] split )
    {
    	
    	if( split.length == 0 )
        {
    		String[] listArray = {"Help", "Enable", "Disable", "Load", "Unload", "Reload", "Sreload", "Show", "List"};
	        sender.sendMessage( ChatColor.GREEN + "|------------------" + ChatColor.GRAY + "PluginManager Help" + ChatColor.GREEN + "------------------|" );
	        for( String string : listArray )
	        	sender.sendMessage( ChatColor.GOLD + "/" + label + " " + ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Command.Description." + string ) ) );
        	return true;
        }
    	
    	switch( split[0].toLowerCase( ) )
    	{
	    	case "help":
	    		//TODO: Fill in help documentation AFTER basic plugin functions added
				break;
	    	case "enable":
	    		return control.enablePluginCommand( sender, label, split );
	    	case "disable":
	    		return control.disablePluginCommand( sender, label, split );
	    	case "load":
				return control.loadPluginCommand( sender, label, split );
	    	case "unload":
				break;
	    	case "reload":
				break;
	    	case "sreload":
				break;
	    	case "show":
				break;
	    	case "list":
				break;
			default:
	        	sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.NoCommand" ), label, split[0] ) ) );
	        	return true;
    	}
    	return false;
    }
}