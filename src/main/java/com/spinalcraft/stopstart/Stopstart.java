package com.spinalcraft.stopstart;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.spinalcraft.spinalpack.SpinalcraftPlugin;

public final class Stopstart extends SpinalcraftPlugin{
	
	@Override
	public void onEnable(){
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("stopstart")){
			
			String scriptArgs[] = {"nhp", "server-restart", "."};
			
			try {
				Runtime.getRuntime().exec(scriptArgs);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onDisable(){
		
	}
}
