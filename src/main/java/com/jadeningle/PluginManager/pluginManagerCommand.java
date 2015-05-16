package com.jadeningle.PluginManager;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import com.jadeningle.PluginManager.Utils.Control;

public class pluginManagerCommand implements CommandExecutor {
	
	private final PluginManagerReloaded plugin;
	private final Control control;
	
	public pluginManagerCommand( PluginManagerReloaded instance )
	{
		plugin = instance;
		control = new Control( instance );
	}

	private boolean hasPermission( CommandSender sender, String permission )
	{
		if( sender != Bukkit.getServer( ).getConsoleSender( ) )
		{
			if( sender.isOp( ) || sender.hasPermission( permission ) )
				return true;
			
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Response.Error.NoPermission" ) ) );
			return false;
		}
		return true;
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
    			return enablePluginCommand( sender, label, split );
			case "disable":
				return disablePluginCommand( sender, label, split );
			case "load":
				return loadPluginCommand( sender, label, split );
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
    
	public boolean enablePluginCommand( CommandSender sender, String label, String[] split )
	{
		if( !hasPermission( sender, "pluginmanager.enable" ) )
			return true;
		
		if( split.length < 2 )
		{
			sender.sendMessage( ChatColor.GOLD + "/" + label + " " + ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Command.Description.Enable" ) ) );
			return true;
		}
		
		final Plugin p = Bukkit.getServer( ).getPluginManager( ).getPlugin( split[1] );
		
		if( p == null )
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', "Response.Error.NoPlugin" ) );
		else if( p.isEnabled( ) )
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.AlreadyEnabled" ), split[1] ) ) );
		else
		{
			control.enablePlugin( p );
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Action.PluginEnabled" ), split[1] ) ) );
		}
		return true;
	}
	
	public boolean disablePluginCommand( CommandSender sender, String label, String[] split )
	{
		if( !hasPermission( sender, "pluginmanager.disable" ) )
			return true;
		
		if( split.length < 2 )
		{
			sender.sendMessage( ChatColor.GOLD + "/" + label + " " + ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Command.Description.Disable" ) ) );
			return true;
		}
	    
		final Plugin p = Bukkit.getServer( ).getPluginManager().getPlugin( split[1] );
	    
		if( p == null )
			sender.sendMessage(ChatColor.translateAlternateColorCodes( '&', "Response.Error.NoPlugin" ) );
		else if( !p.isEnabled( ) )
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.AlreadyDisabled" ), split[1] ) ) );
		else
		{
			control.disablePlugin( p );
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Action.PluginDisabled" ), split[1] ) ) );
		}
		return true;
	}
	
	public boolean loadPluginCommand( CommandSender sender, String label, String[] split )
	{
		if( !hasPermission( sender, "pluginmanager.load" ) )
			return true;
		
		if( split.length < 2 )
		{
			sender.sendMessage( ChatColor.GOLD + "/" + label + " " + ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Command.Description.Load" ) ) );
			return true;
		}
		
		final File toLoad = new File("plugins" + File.separator + split[1] + ( split[1].endsWith( ".jar" ) ? "" : ".jar" ) );
		
		if( !toLoad.exists( ) )
		{
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Response.Error.NoSuchFile" ) ) );
			return true;
		}
	
		PluginDescriptionFile desc = control.getDescription( toLoad );
		if( desc == null )
		{
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Response.Error.NoDescriptionFile" ) ) );
			return true;
		}
	
		if( Bukkit.getPluginManager( ).getPlugin( desc.getName( ) ) != null )
		{
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Response.Error.AlreadyLoaded" ) ) );
			return true;
		}
	
		Plugin p = null;
		if( ( p = control.loadPlugin( toLoad ) ) != null)
		{
			control.enablePlugin( p );
			sender.sendMessage(ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Action.PluginLoaded" ), p.getDescription( ).getName( ), p.getDescription( ).getVersion( ) ) ) );
		}
		else
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.PluginNotLoaded" ), split[1] ) ) );
	    
		return true;
	}
}
