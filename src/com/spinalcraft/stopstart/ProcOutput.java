package com.spinalcraft.stopstart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class ProcOutput implements Runnable{
	BufferedReader reader;
	Logger logger;
	
	public ProcOutput(InputStream inputStream, Logger logger){
		reader = new BufferedReader(new InputStreamReader(inputStream));
		this.logger = logger;
	}
	@Override
	public void run(){
		String line;
		try {
			while((line = reader.readLine()) != null)
				logger.info("WatchHamster: " + line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
