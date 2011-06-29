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
	private static final String keyUsePermissions = "UsePermissions";
	private static final String keyBlockCountingSpeedLimit = "BlockCountingSpeedLimit";
	private static final String fileFormat = "%s: %s"; 
	
	private int minLineCount = 4;
	//-- file content --
	public int itemID = 268;
	public String itemName = "wood-sword";
	public boolean usePermissions = false;
	public boolean blockCountingSpeedLimit = false;
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
					
					if (line[0].equalsIgnoreCase(keyUsePermissions)) {
						usePermissions = getBoolean(line[1]);
					}

					if (line[0].equalsIgnoreCase(keyBlockCountingSpeedLimit)) {
						blockCountingSpeedLimit = getBoolean(line[1]);
					}
				}			

				if (reader.getLineNumber() < minLineCount) write();
				
			} catch (Exception e) {
				System.err.println(Massband.consoleOutputHeader + " Error: An error occurred while reading.");
			}

		} catch (FileNotFoundException e) {
			System.err.println(Massband.consoleOutputHeader + " Error: config.yml in '" + Massband.pluginName + "' not found.");
			
			if (write()) { //create new File
				System.out.println(Massband.consoleOutputHeader + " New Config file created. (" + Massband.pluginName + "/config.yml)");
			}
		}
	}
	
	public boolean getBoolean(String string){
		String tmpValue = string.toLowerCase();
		if (tmpValue.contains("true")) {
			return true;
		}else if (tmpValue.contains("false")) {
			return false;
		}else if (tmpValue.contains("1")) {
			return true;
		}else if (tmpValue.contains("0")) {
			return false;
		}
		return false;
	}
	
	public boolean write() {
		FileWriter writer;
		try {
			File directory = new File(configFilePath);
			if (! directory.exists()) directory.mkdir();
			
			writer = new FileWriter(configFilePath + configFileName);
			
			writer.write(String.format(fileFormat, keyItemID, itemID) + "\n");
			writer.write(String.format(fileFormat, keyItemName, itemName) + "\n");
			writer.write("\n");
			writer.write(String.format(fileFormat, keyUsePermissions, usePermissions) + "\n");
			writer.write("\n");
			writer.write("#This can infect the Server performance ...\n");
			writer.write(String.format(fileFormat, keyBlockCountingSpeedLimit, blockCountingSpeedLimit) + "\n");
			
			writer.close();
		
			return true;
		} catch (Exception e1) {
			System.err.println(Massband.consoleOutputHeader + " Error: can't create new config file.");		
		}
		return false;
	}
	
}
