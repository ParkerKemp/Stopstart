package com.spinalcraft.stopstart;

import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class Stopstart extends JavaPlugin{
	
	@Override
	public void onEnable(){
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("stopstart")){
			
			String scriptArgs[] = {"nohup", "sh", "restart.sh"};
			
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
		getLogger().info("Disabled it");
	}
}
