package de.MrX13415.Massband;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;

public class Config {
	
	private String configFileName = "config.yml";
	private String configFilePath = "plugins/" + Massband.pluginName + "/";
	
	private static final String keyItemID = "ItemID";
	private static final String keyItemName = "ItemName";
	private static final String fileFormat = "%s: %s"; 
	
	//-- file content --
	public int itemID = 268;
	public String itemName = "wood-sword";
	
	//------------------
	
	public void read() {
		LineNumberReader reader;
		
		try {
			reader = new LineNumberReader(new FileReader(configFilePath + configFileName));
			
			try {
				while (reader.ready()) {
					String[] line = reader.readLine().replace(" ", "").split(":");
					
					if (line[0].equalsIgnoreCase(keyItemID)) {
						itemID = Integer.valueOf(line[1]);
					}

					if (line[0].equalsIgnoreCase(keyItemName)) {
						itemName = line[1];
					}

				}				
			} catch (Exception e) {
				System.err.println(Massband.consoleOutputHeader + " Error: An error occurred while reading.");
			}

		} catch (FileNotFoundException e) {
			System.err.println(Massband.consoleOutputHeader + " Error: config.yml in '" + Massband.pluginName + "' not found.");
			write(); //create new File
		}
	}
	
	public void write() {
		FileWriter writer;
		try {
			File directory = new File(configFilePath);
			if (! directory.exists()) directory.mkdir();
			
			writer = new FileWriter(configFilePath + configFileName);
			
			writer.write(String.format(fileFormat, keyItemID, itemID) + "\n");
			writer.write(String.format(fileFormat, keyItemName, itemName) + "\n");
			
			writer.close();
		
			System.out.println(Massband.consoleOutputHeader + " New Config file created. (" + Massband.pluginName + "/config.yml)");
		} catch (Exception e1) {
			System.err.println(Massband.consoleOutputHeader + " Error: can't create new config file.");
		}
	}
	
}
