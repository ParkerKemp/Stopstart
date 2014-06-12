package com.spinalcraft.stopstart;

import org.bukkit.Bukkit;

public class RestartService implements Runnable {
	
	private final StopStart plugin;
	private int time;
	
	public RestartService(StopStart plugin, int time){
		this.plugin = plugin;
		this.time = time;
	}
	
	@Override
	public void run(){
		//plugin.getLogger().info("Restarting in " + time + " seconds...");
		Bukkit.broadcastMessage("SERVER RESTARTING IN " + time + " SECONDS...");
		waitSeconds(time);
		
		plugin.log("Starting watchdog process...");
		plugin.startWatchdogProcess();
		plugin.log("Shutting down server...");
		Bukkit.shutdown();
	}
	
	public void waitSeconds(int time){
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
