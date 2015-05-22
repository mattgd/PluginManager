package com.jadeningle.PluginManager.Utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.jadeningle.PluginManager.PluginManagerReloaded;

public class JoinNotify implements Listener
{
	PluginManagerReloaded plugin;
	
	public JoinNotify( PluginManagerReloaded instance )
	{
		plugin = instance;
	}

	@EventHandler
	public void onPlayerJoin( PlayerJoinEvent event )
	{
		if( !plugin.getConfig( ).getBoolean( "Updater.Notify" ) )
			return;
		
		Player p = event.getPlayer( );
		
		if( ( p.isOp( ) || p.hasPermission( "pluginmanager.notify" ) ) && plugin.update != null )
			p.sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.update ) );
	}
}
