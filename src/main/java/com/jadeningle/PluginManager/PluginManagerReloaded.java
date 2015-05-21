package com.jadeningle.PluginManager;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginManagerReloaded extends JavaPlugin {
	
	public FileConfiguration language;
	
	@Override
	public void onEnable( ) 
	{
		getCommand( "pluginmanager" ).setExecutor( new pluginManagerCommand( this ) );
		initConfig( );
		getLogger( ).info( "Plugin Manager Reloaded is now enabled!" );
	}
	
	@Override
	public void onDisable( ) 
	{
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
