package com.jadeningle.PluginManager.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

import com.jadeningle.PluginManager.PluginManagerReloaded;
 
public class SelfUpdateChecker {
	
	PluginManagerReloaded plugin;
	private String currentVersion;
	private String readurl = "https://raw.githubusercontent.com/Lvletei/PluginManager-Reloaded/master/version.txt";
	
	public SelfUpdateChecker( PluginManagerReloaded instance )
	{
		plugin = instance;
		currentVersion = plugin.getDescription( ).getVersion( );
	}
	 
	public void startUpdateCheck( )
	{
		if( plugin.getConfig( ).getBoolean( "Updater.StartCheck" ) )
		{
			Logger log = plugin.getLogger( );
			try 
			{
				log.info("Checking for a new version...");
				URL url = new URL( readurl );
				BufferedReader br = new BufferedReader( new InputStreamReader( url.openStream( ) ) );
				String str;
				
				while( ( str = br.readLine( ) ) != null )
				{
					String[] split = str.split( "\\." );
					String[] cVersion = currentVersion.split( "\\." );
					
					int uMajor = Integer.parseInt( split[0] );
					int uMinor = Integer.parseInt( split[1] );
					int uRev;
					String uChannel = "Stable";
					
					
					if( split[2].contains( "-" ) )
					{
						String[] uRevSplit = split[2].split( "-" );
						uChannel = uRevSplit[1];
						uRev = Integer.parseInt( uRevSplit[0] );
					}
					else
						uRev = Integer.parseInt( split[2] );
					
					
					if( plugin.getConfig( ).getString( "Updater.Channel" ).equalsIgnoreCase( uChannel ) )
					{
						if( updateAvailable( uMajor, uMinor, uRev, cVersion ) )
						{
							log.info( String.format( plugin.language.getString( "Response.Action.UpdateAvailable" ), uChannel, uMajor, uMinor, uRev ) );
							plugin.update = String.format( ChatColor.GREEN + plugin.language.getString( "Response.Action.UpdateAvailable" ), ChatColor.RED + uChannel + ChatColor.GREEN, ChatColor.RED + "" + uMajor, uMinor, uRev + "" + ChatColor.GREEN );
							
						}
					}
				}
				br.close();
			}
			catch( IOException e )
			{
				log.severe( "The SelfUpdateCheck URL is invalid! Please inform the developer." );
			}
		}
	}
	
	public boolean updateAvailable( int uMajor, int uMinor, int uRev, String[] cVersion )
	{
		int cMajor = Integer.parseInt( cVersion[0] );
		int cMinor = Integer.parseInt( cVersion[1] );
		int cRev;
		
		if( cVersion[2].contains( "-" ) )
		{
			String[] cRevSplit = cVersion[2].split( "-" );
			cRev = Integer.parseInt( cRevSplit[0] );
		}
		else
			cRev = Integer.parseInt( cVersion[2] );
		
		if( uMajor > cMajor || uMinor > cMinor || uRev > cRev )
			return true;
		
		return false;
	}
}
