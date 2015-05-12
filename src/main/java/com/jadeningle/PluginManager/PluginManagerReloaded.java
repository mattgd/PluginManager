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
		getLogger( ).info("Plugin Manager Reloaded is now enabled!");
	}
	
	@Override
	public void onDisable( ) 
	{
		getLogger( ).info("Plugin Manager Reloaded is now disabled!");
	}
	
	private void initConfig( )
	{
		FileConfiguration config = getConfig( );
		if( !new File( getDataFolder( ), "config.yml").exists( ) ) 
		{
			config.addDefault( "Updater.Notify", true );
			config.addDefault( "Language", "en_US" );
			config.addDefault( "ConfigVersion", 0.1);
			config.addDefault( "Debug", false );
			config.options( ).copyDefaults( true );
			saveConfig( );
		}
		
		String[] langArray = {"en_US"};
		for( String string : langArray )
			saveResource( "locale" + File.separator + string + ".yml", false );
		
		language = YamlConfiguration.loadConfiguration(  new File( getDataFolder( ) + File.separator + "locale", this.getConfig( ).getString( "Language" ) + ".yml") );
	}
	
}