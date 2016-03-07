package com.jadeningle.PluginManager;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.jadeningle.PluginManager.Utils.Control;

public class PluginManagerCommand implements CommandExecutor {
	
	private final PluginManagerReloaded plugin;
	private final Control control;
	
	public PluginManagerCommand( PluginManagerReloaded instance )
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
			String[] listArray = {"Enable", "Disable", "Load", "Unload", "Reload", "Sreload", "Show", "List"};
			sender.sendMessage( ChatColor.GREEN + "|------------------" + ChatColor.GRAY + "PluginManager Help" + ChatColor.GREEN + "------------------|" );
			for( String string : listArray )
				sender.sendMessage( ChatColor.GOLD + "/" + label + " " + ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Command.Description." + string ) ) );
			return true;
    	}
    	
    	switch( split[0].toLowerCase( ) )
    	{
    		case "enable":
    			return enablePluginCommand( sender, label, split );
			case "disable":
				return disablePluginCommand( sender, label, split );
			case "load":
				return loadPluginCommand( sender, label, split );
			case "unload":
				return unloadPluginCommand( sender, label, split );
			case "reload":
				return reloadPluginCommand( sender, label, split );
			case "sreload":
				return sreloadPluginCommand( sender, label, split );
			case "show":
				return showPluginCommand( sender, label, split );
			case "list":
				return listPluginCommand( sender, label, split );
			default:
				sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.NoCommand" ), label, split[0] ) ) );
				return true;
    	}
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
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.NoPlugin" ), split[1] ) ) );
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
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.NoPlugin" ), split[1] ) ) );
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
		
		final File toLoad = new File( "plugins" + File.separator + split[1] + ( split[1].endsWith( ".jar" ) ? "" : ".jar" ) );
		
		if( !toLoad.exists( ) )
		{
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.NoSuchFile" ), split[1] + ".jar" ) ) );
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
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Action.PluginLoaded" ), p.getDescription( ).getName( ), p.getDescription( ).getVersion( ) ) ) );
		}
		else
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.PluginNotLoaded" ), split[1] ) ) );
	    
		return true;
	}
	
    private boolean unloadPluginCommand( final CommandSender sender, final String label, final String[] split )
    {
    	if( !hasPermission( sender, "pluginmanager.unload" ) )
			return true;
		
		if( split.length < 2 )
		{
			sender.sendMessage( ChatColor.GOLD + "/" + label + " " + ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Command.Description.Unload" ) ) );
			return true;
		}
		
		final Plugin p = Bukkit.getServer( ).getPluginManager( ).getPlugin( split[1] );
		
		if( p == null )
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.NoPlugin" ), split[1] ) ) );
		else 
		{
			if( control.unloadPlugin( p, true ) )
			    sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Action.PluginUnloaded" ), p.getDescription( ).getName( ), p.getDescription( ).getVersion( ) ) ) );
			else
				sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.PluginNotUnloaded" ), split[1] ) ) );
		}

        return true;
    }
    
    private boolean reloadPluginCommand( final CommandSender sender, final String label, final String[] split )
    {
    	if( !hasPermission( sender, "pluginmanager.reload" ) )
			return true;
		
		if( split.length < 2 )
		{
			sender.sendMessage( ChatColor.GOLD + "/" + label + " " + ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Command.Description.Reload" ) ) );
			return true;
		}
		
		final Plugin p = Bukkit.getServer( ).getPluginManager( ).getPlugin( split[1] );
		
		if( p == null )
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.NoPlugin" ), split[1] ) ) );
		else
		{
			final File file = control.getFile( (JavaPlugin) p );
			
			if( file == null )
			{
				sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.NoJar" ), p.getName( ) ) ) );
				return true;
			}
			
			File name = new File( "plugins" + File.separator + file.getName( ) );
			JavaPlugin loaded = null;
			if( !control.unloadPlugin( p, false ) )
				sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.UnloadError" ), split[1] ) ) );
			else if( ( loaded = (JavaPlugin)control.loadPlugin( name ) ) == null )
				sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.ReloadError" ), split[1] ) ) );
			
			control.enablePlugin( loaded );
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Action.PluginReloaded" ), split[1] ) ) );
		}
		
		return true;
    }
    
    private boolean sreloadPluginCommand( final CommandSender sender, final String label, final String[] split )
    {
    	if( !hasPermission( sender, "pluginmanager.sreload" ) )
			return true;
		
		if( split.length < 2 )
		{
			sender.sendMessage( ChatColor.GOLD + "/" + label + " " + ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Command.Description.Sreload" ) ) );
			return true;
		}
		
		final Plugin p = Bukkit.getServer( ).getPluginManager().getPlugin( split[1] );
	    
		if( p == null )
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.NoPlugin" ), split[1] ) ) );
		else if( !p.isEnabled( ) )
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.AlreadyDisabled" ), split[1] ) ) );
		else
		{
			control.disablePlugin( p );
			control.enablePlugin( p );
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Action.PluginSreloaded" ), p.getDescription( ).getName( ), p.getDescription( ).getVersion( ) ) ) );
		}
		
		return true;
    }
    
    private boolean showPluginCommand( final CommandSender sender, final String label, final String[] split )
    {
    	if( !hasPermission( sender, "pluginmanager.show" ) )
			return true;
		
		if( split.length < 2 )
		{
			sender.sendMessage( ChatColor.GOLD + "/" + label + " " + ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Command.Description.Show" ) ) );
			return true;
		}
		
		final Plugin p = Bukkit.getServer( ).getPluginManager().getPlugin( split[1] );
		
		if( p == null)
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.NoPlugin" ), split[1] ) ) );
		else
		{
			File file = control.getFile( (JavaPlugin) p );
			sender.sendMessage( ChatColor.AQUA + "|----" + ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Show.Name" ), p.getName( ) ) + ChatColor.AQUA + "----|" ) );
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Show.Status.Main" ), ( p.isEnabled( ) ? plugin.language.getString( "Response.Show.Status.Enabled" ) : plugin.language.getString( "Response.Show.Status.Disabled" ) ) ) ) );
			if( p.getDescription( ).getDescription( ) != null )
				sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Show.Description" ), p.getDescription( ).getDescription( ) ) ) );

			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Show.Version" ), p.getDescription( ).getVersion( ) ) ) );
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Show.Main" ), p.getDescription( ).getMain( ) ) ) );
			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Show.File" ), file.getName( ) ) ) );
			final StringBuffer authors = new StringBuffer( );

			if( p.getDescription( ).getAuthors( ) != null )
			{
				if( !p.getDescription( ).getAuthors( ).isEmpty( ) )
				{
					for( final String a : p.getDescription( ).getAuthors( ) )
					{
						if( authors.length( ) > 0 )
							authors.append( ", " );
						authors.append( a );
					}
				}
			}

			if( authors != null )
            	sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( ( p.getDescription( ).getAuthors( ).size( ) == 1 ? plugin.language.getString( "Response.Show.Author.Single" ) : plugin.language.getString( "Response.Show.Author.Multiple" ) ), authors ) ) );

            if( p.getDescription( ).getWebsite( ) != null )
            	sender.sendMessage(  ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Show.Website" ), p.getDescription( ).getWebsite( ) ) ) );
        }
			
		return true;
    }
    
    private boolean listPluginCommand( final CommandSender sender, final String label, final String[] split )
    {
    	if( !hasPermission( sender, "pluginmanager.list" ) )
			return true;
		
        boolean versions = false, options = false, alphabetical = false;
        String search = "";

		for( int i = 1; i < split.length; i++ )
		{
			final String s = split[i];
			if( s.equalsIgnoreCase( "-v" ) || s.equalsIgnoreCase( "-version" ) )	
		    	versions = true;
		    else if( s.equalsIgnoreCase( "-options" ) || s.equalsIgnoreCase( "-o" ) )
		    	options = true;
		    else if( s.equalsIgnoreCase( "-alphabetical" ) || s.equalsIgnoreCase( "-a" ) )
		    	alphabetical = true;
		    else if( s.startsWith( "-s:" ) || s.startsWith( "-search:" ) )
		    {
		    	final String[] t = s.split( "[:]", 2 );
		    	if( t.length != 2 )
		        	continue;
		        search = t[1];
		    }
		}
		
		if( options )
		{
			String[] options_s = plugin.language.getString( "Command.Description.ListOptions" ).split( "\n" );
			for( String s : options_s )
				sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', s ) );
		    return true;
		}
		
		final Plugin[] pl = Bukkit.getPluginManager( ).getPlugins( );
		String enabledList = "", disabledList = "";

		ArrayList<Plugin> plugins = new ArrayList<Plugin>( java.util.Arrays.asList( pl ) );
		
		if( !search.isEmpty( ) )
		{
			final java.util.Iterator<Plugin> it = plugins.iterator();
			while( it.hasNext( ) )
			{
				final Plugin p = it.next( );
				if( !p.getName( ).contains( search ) )
					it.remove( );
			}
		}

		if( alphabetical )
		{
			final ArrayList<String> s = new ArrayList<String>( );
			for( final Plugin p : plugins )
			{
		        s.add( p.getName( ) );
		    }
		    java.util.Collections.sort( s );
		    plugins = new ArrayList<Plugin>( );
		    for( final String a : s )
		        plugins.add( Bukkit.getPluginManager( ).getPlugin( a ) );
		}
	
		for( final Plugin p : plugins )
		{
			final String l = p.getName( ) + ( versions ? " " + p.getDescription( ).getVersion( ) : "" );
			if( p.isEnabled( ) )
			{
				if( enabledList.isEmpty( ) )
					enabledList = l;
				else
					enabledList = enabledList + ", " + l;
		    }
			else if( disabledList.isEmpty( ) )
				disabledList = l;
			else
				disabledList = disabledList + ", " + l;
		}
	
		if( !enabledList.isEmpty( ) )
		    sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Action.List.Enabled" ), enabledList ) ) );
		if( !disabledList.isEmpty( ) )
		    sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Action.List.Disabled" ), disabledList ) ) );
			
		return true;
	}
}
