package com.jadeningle.PluginManager.Utils;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
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
