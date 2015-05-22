package com.jadeningle.PluginManager;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.jadeningle.PluginManager.Utils.JoinNotify;
import com.jadeningle.PluginManager.Utils.SelfUpdateChecker;

public class PluginManagerReloaded extends JavaPlugin {
	
	public FileConfiguration language;
	private SelfUpdateChecker selfUpdateChecker;
	public String update = null;
	
	@Override
	public void onEnable( ) 
	{
		getCommand( "pluginmanager" ).setExecutor( new pluginManagerCommand( this ) );
		Bukkit.getPluginManager( ).registerEvents( new JoinNotify( this ), this );
		initConfig( );
		selfUpdateChecker = new SelfUpdateChecker( this );
		selfUpdateChecker.startUpdateCheck( );
		try
		{
	        Metrics metrics = new Metrics( this );
	        metrics.start( );
	    }
		catch( IOException e )
		{
	        e.printStackTrace( );
	    }
		getLogger( ).info( "Plugin Manager Reloaded is now enabled!" );
	}
	
	@Override
	public void onDisable( ) 
	{
		language = null;
		selfUpdateChecker = null;
		update = null;
		getLogger( ).info( "Plugin Manager Reloaded is now disabled!" );
	}
	
	private void initConfig( )
	{
		if( !new File( getDataFolder( ), "config.yml" ).exists( ) ) 
			saveResource( "config.yml", false );
		
		String[] langArray = {"en_US"};
		for( String string : langArray )
		{
			if( !new File( getDataFolder( ) + File.separator + "localization", string + ".yml" ).exists( ) )
				saveResource( "localization" + File.separator + string + ".yml", false );
		}
		
		language = YamlConfiguration.loadConfiguration(  new File( getDataFolder( ) + File.separator + "localization", this.getConfig( ).getString( "Language" ) + ".yml") );
	}
	
}
