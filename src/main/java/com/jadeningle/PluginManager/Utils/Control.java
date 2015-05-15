package com.jadeningle.PluginManager.Utils;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.UnknownDependencyException;

import com.jadeningle.PluginManager.PluginManagerReloaded;

public class Control {
	
	PluginManagerReloaded plugin;
	
	public Control( PluginManagerReloaded instance )
	{
		plugin = instance;
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
	
	public PluginDescriptionFile getDescription( final File file )
	{
		try
		{
		    final JarFile jar = new JarFile( file );
		    final ZipEntry zip = jar.getEntry( "plugin.yml" );
		    if( zip == null )
		    {
		        jar.close();
		        return null;
		    }
		    final PluginDescriptionFile pdf = new PluginDescriptionFile( jar.getInputStream( zip ) );
		    jar.close( );
		    return pdf;
		}
		catch( InvalidDescriptionException | IOException ioe )
		{
		    ioe.printStackTrace( );
		}
		return null;
		
	}
	
	public void enablePlugin( final Plugin plugin )
	{
		Bukkit.getServer( ).getPluginManager( ).enablePlugin( plugin );
	}
	
	public void disablePlugin( final Plugin plugin )
	{
        Bukkit.getServer( ).getPluginManager( ).disablePlugin( plugin );
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
            enablePlugin( p );
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
	        disablePlugin( p );
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

        PluginDescriptionFile desc = getDescription( toLoad );
        if( desc == null )
        {
            sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Response.Error.NoDescriptionFile" ) ) );
            return true;
        }

        if( Bukkit.getPluginManager().getPlugin( desc.getName( ) ) != null )
        {
            sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Response.Error.AlreadyLoaded" ) ) );
            return true;
        }

        Plugin p = null;
        if( ( p = loadPlugin( toLoad ) ) != null)
        {
        	enablePlugin( p );
            sender.sendMessage(ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Action.PluginLoaded" ), p.getDescription( ).getName( ), p.getDescription( ).getVersion( ) ) ) );
        }
        else
            sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.PluginNotLoaded" ), split[1] ) ) );
        
        return true;
        
	}
	
	public Plugin loadPlugin( final File plugin )
	{
        Plugin pl; 
        try
        {
            pl = Bukkit.getServer( ).getPluginManager( ).loadPlugin( plugin );
            try
            {
                pl.onLoad( );
            }
            catch( final Exception e )
            {
            	System.out.println( String.format( this.plugin.language.getString( "Response.Error.FailedonLoad" ), plugin.getName( ) ) );
                e.printStackTrace( );
            }
            return pl;
        }      
        catch( InvalidPluginException | InvalidDescriptionException | UnknownDependencyException e )
        {
            e.printStackTrace( );
        }
        return null;
	}
}
