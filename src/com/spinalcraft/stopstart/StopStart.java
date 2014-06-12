package com.spinalcraft.stopstart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class StopStart extends JavaPlugin{
	
	@Override
	public void onEnable(){
		//String watchdogPid;
		
		getDataFolder().mkdirs();
		processExists("12345");
		
		log("\n\n");
		storeOwnPid();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("stopstart")){
			
			String scriptArgs[] = {"nohup", "sh", "restart.sh"};
			
			try {
				Runtime.getRuntime().exec(scriptArgs);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//if(args.length > 0)
			//	runRestartScript();
				//restartServer(Integer.parseInt(args[0]), sender, label, args);
			//else
			//	;//restartServer(5);
			return true;
		}
		return false;
	}
	
	private void runRestartScript(){
		//String path = System.getProperty("user.dir") + "/plugins/StopStart.jar";
		
		ProcessBuilder processBuilder = new ProcessBuilder("sh", "restart.sh");
		try {
			processBuilder.directory(new File(System.getProperty("user.dir")));
			processBuilder.redirectErrorStream(false);
			processBuilder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void restartServer(int time, CommandSender sender, String label, String[] args){
		log(sender.getName() + " used " + rebuildCommandString(label, args));
		log("Confirming: current pid is " + ownPid() + ", written pid is " + Watchdog.getTarget() + ".");
		new Thread(new RestartService(this, time)).start();
	}
	
	public String rebuildCommandString(String label, String[] args){
		String ret = "/" + label;
		for(int i = 0; i < args.length; i++)
			ret += " " + args[i];
		return ret;
	}
	
	public void logToFile(String file, String message){
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(file, true));
			writer.println(dateAndTime() + " " + message);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void log(String message){
		logToFile(System.getProperty("user.dir") + "/plugins/StopStart/usage.log", "StopStart: " + message);
	}
	
	public String dateAndTime(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
	}
	
	public void startWatchdogProcess(){
		String pathToPlugin = System.getProperty("user.dir") + "/plugins/StopStart.jar";
		
		ProcessBuilder processBuilder = new ProcessBuilder("java", "-cp", pathToPlugin, "com.spinalcraft.stopstart.Watchdog");
		Process p;
		try {
			processBuilder.directory(new File(System.getProperty("user.dir")));
			processBuilder.redirectErrorStream(true);
			p = processBuilder.start();
			new Thread(new ProcOutput(p.getInputStream(), getLogger())).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean processExists(String pid){
		ArrayList<String> pids = new ArrayList<String>();
        
		try {
	        String line;
	        Process p = Runtime.getRuntime().exec("ps -e");
	        BufferedReader input =
	                new BufferedReader(new InputStreamReader(p.getInputStream()));
	        while ((line = input.readLine()) != null) {
	        	pids.add(line.split(" ")[0]);
	        	//String[] strings = line.split(" ");
	        	//StringTokenizer tokenizer = new StringTokenizer(line);
	        	//getLogger().info("Full line: " + line);
	        	//tokenizer = new StringTokenizer(line);
	        	//if(tokenizer.hasMoreTokens())
	        	//	getLogger().info(tokenizer.nextToken());//while(tokenizer.hasMoreTokens())
	        	//	getLogger().info(tokenizer.nextToken());
	        	//for(int i = 0; i < strings.length; i++)
	        	//	getLogger().info("Token #" + i + ": " + strings[i]);
	        }
	        input.close();
	    } catch (Exception err) {
	        err.printStackTrace();
	    }
		
		for(Iterator<String> i = pids.iterator(); i.hasNext(); )
			if(i.next().equals(pid))
				return true;
		return false;
	}
	
	public String getWatchdogPid(){
		String temp = "(invalid)";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(getDataFolder().toString() + "/watchdogPid.txt")));
			temp = reader.readLine();
			getLogger().info(temp);
			reader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
	public void storeOwnPid(){
		String s = ownPid();
		log("Writing own pid at " + s + ".");
		getLogger().info(s);
		try {
			PrintWriter writer = new PrintWriter(getDataFolder().toString() + "/currentPid.txt", "UTF-8");
			writer.println(s);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String ownPid(){
		return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
	}
	
	@Override
	public void onDisable(){
		getLogger().info("Disabled it");
	}
}
