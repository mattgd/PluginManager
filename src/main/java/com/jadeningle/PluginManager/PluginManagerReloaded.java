package com.jadeningle.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginManagerReloaded extends JavaPlugin {
	
	@Override
	public void onEnable( ) 
	{
		getCommand( "plm" ).setExecutor( new pluginManagerCommand( this ) );
		getLogger().info("Plugin Manager Reloaded is now enabled!");
	}
	
	@Override
	public void onDisable( ) 
	{
		getLogger().info("Plugin Manager Reloaded is now disabled!");
	}

}