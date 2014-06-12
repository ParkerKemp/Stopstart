package com.spinalcraft.stopstart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.StringTokenizer;

public class Watchdog {
	
	public static void main(String[] args){
		String serverPid;
		try {
			PrintStream errorStream = new PrintStream(System.getProperty("user.dir") + "/plugins/StopStart/watchdog.error");
			System.setErr(errorStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeOwnPid();
		System.err.println("Text");
		serverPid = getTarget();
		log("Watching main process at pid " + serverPid + ".");
		//System.out.println(System.getProperty("user.dir") + "/../");
		
		//System.out.println("Testtt");
		boolean warned = false;
		Calendar warningTime = Calendar.getInstance();
		
		while(processExists(serverPid)){
			log("Main process still visible. Waiting...");
			//waitSeconds(1);
			//if(Calendar.getInstance().after(warningTime)){
			//	warningTime.add(Calendar.SECOND, 1);
			//}
		}
		log("Cleaning up after self...");
		deleteOwnPid();
		
		log("Main process is not visible, starting server...");
		
		startServer();
	}
	
	public static void waitSeconds(int time){
		/*try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			log("Error while trying to Thread.sleep");
			e.printStackTrace();
		}*/
		
		Calendar warningTime = Calendar.getInstance();
		warningTime.add(Calendar.SECOND, time);
		while(!Calendar.getInstance().after(warningTime))
			;
	}
	
	public static void logToFile(String file, String message){
		try {
			PrintWriter writer = new PrintWriter(new FileWriter(file, true));
			writer.println(dateAndTime() + " " + message);
			writer.close();
		} catch (IOException e) {
			System.err.println("File IO error");
			e.printStackTrace();
		}
	}
	
	public static void log(String message){
		logToFile(System.getProperty("user.dir") + "/plugins/StopStart/usage.log", "Watchdog: " + message);
	}
	
	public static String dateAndTime(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
	}
	
	public static void startServer(){
		ProcessBuilder processBuilder = new ProcessBuilder("sh", "startBukkitScreen.sh");
		Process p;
		try {
			System.err.println(System.getProperty("user.dir"));
			processBuilder.directory(new File(System.getProperty("user.dir")));
			processBuilder.redirectErrorStream(true);
			p = processBuilder.start();
			//p.getInputStream();
		} catch (Exception e) {
			System.err.println("Error while trying to start server!");
			e.printStackTrace();
		}
	}
	
	public static boolean processExists(String pid){
		ArrayList<String> pids = new ArrayList<String>();
        
		try {
	        String line;
	        StringTokenizer tokenizer;
	        Process p = Runtime.getRuntime().exec("ps -e");
	        BufferedReader input =
	                new BufferedReader(new InputStreamReader(p.getInputStream()));
	        while ((line = input.readLine()) != null) {
	        	tokenizer = new StringTokenizer(line);
	        	if(tokenizer.hasMoreTokens()){
	        		String s = tokenizer.nextToken();
	        		//log(s);
	        		pids.add(s);//pids.add(line.split(" ")[1]);
	        	}
	        }
	        input.close();
	    } catch (Exception err) {
	    	log("Exception while reading process list.");
	        err.printStackTrace();
	    }
		
		for(Iterator<String> i = pids.iterator(); i.hasNext(); ){
			String j = i.next();
			//log("Checking pid " + j);
			if(j.equals(pid)){
				//log("Found match at " + j);
				return true;
			}
		}
		return false;
	}
	
	public static String getTarget(){
		String ret = "invalidTarget";
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(System.getProperty("user.dir") + "/plugins/StopStart/currentPid.txt")));
			ret = reader.readLine();
			reader.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static void writeOwnPid(){
		String ownPid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
		log("Watchdog has pid " + ownPid + ".");
		try {
			PrintWriter writer = new PrintWriter(System.getProperty("user.dir") + "/plugins/StopStart/watchdogPid.txt", "UTF-8");
			writer.println(ownPid);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void deleteOwnPid(){
		File file = new File(System.getProperty("user.dir") + "/plugins/StopStart/watchdogPid.txt");
		file.delete();
	}
}
