package de.MrX13415.Massband;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;

public class Config {
	
	public String version = "2.7";	//config version
	private String configFileName = "config.yml";
	private String configFilePath = "plugins/" + Massband.pluginName + "/";
	
	private static final String keyConfigFileVersion = "ConfigFileVersion";
	private static final String keyItemID = "ItemID";
	private static final String keyItemName = "ItemName";
	private static final String keyCommandShortForm_blockList = "BlockListCommand_ShortForm";
	private static final String keyCommandShortForm_clear = "ClearCommand_ShortForm";
	private static final String keyCommandShortForm_lenght = "LenghtCommand_ShortForm";
	private static final String keyCommandShortForm_dimensions = "DimensionsCommand_ShortForm";
	private static final String keyCommandShortForm_countblocks = "CountblocksCommand_ShortForm";
	private static final String keyCommandShortForm_lengthmode = "LengthmodeCommand_ShortForm";
	private static final String keyCommandShortForm_surfacemode = "SurfacemodeCommand_ShortForm";
	private static final String keyCommandShortForm_simplemode = "SimplemodeCommand_ShortForm";
	private static final String keyCommandShortForm_expand = "ExpandCommand_ShortForm";
	private static final String keyCommandShortForm_stop = "StopCommand_ShortForm";
	private static final String keyCommandShortForm_stopall = "StopallCommand_ShortForm";
	private static final String keyUsePermissions = "UsePermissions";
	private static final String keyBlockCountingSpeedLimit = "BlockCountingSpeedLimit";
	private static final String fileFormat = "%s: %s"; 
	
	private int minLineCount = 4;
	//#-- file content --#
	public String configFileVersion = "----";
	public int itemID = 268;
	public String itemName = "wood-sword";
	public boolean usePermissions = true;
	public boolean blockCountingSpeedLimit = false;
	//-- shortforms --
	public String commandShortForm_blockList = "bl";
	public String commandShortForm_clear = "clr";
	public String commandShortForm_lenght = "l";
	public String commandShortForm_dimensions = "d";
	public String commandShortForm_countblocks = "cb";
	public String commandShortForm_lengthmode = "lm";
	public String commandShortForm_surfacemode = "sfm";
	public String commandShortForm_simplemode = "sim";
	public String commandShortForm_expand = "ex";
	public String commandShortForm_stop = "stp";
	public String commandShortForm_stopall = "all";
	//#-----------------#
	
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
					
					if (line[0].equalsIgnoreCase(keyCommandShortForm_clear)) {
						commandShortForm_clear = line[1];
					}
					
					if (line[0].equalsIgnoreCase(keyCommandShortForm_lenght)) {
						commandShortForm_lenght = line[1];
					}
					
					if (line[0].equalsIgnoreCase(keyCommandShortForm_dimensions)) {
						commandShortForm_dimensions = line[1];
					}
					
					if (line[0].equalsIgnoreCase(keyCommandShortForm_countblocks)) {
						commandShortForm_countblocks = line[1];
					}
					
					if (line[0].equalsIgnoreCase(keyCommandShortForm_lengthmode)) {
						commandShortForm_lengthmode = line[1];
					}
					
					if (line[0].equalsIgnoreCase(keyCommandShortForm_surfacemode)) {
						commandShortForm_surfacemode = line[1];
					}
					
					if (line[0].equalsIgnoreCase(keyCommandShortForm_simplemode)) {
						commandShortForm_simplemode = line[1];
					}
					
					if (line[0].equalsIgnoreCase(keyCommandShortForm_expand)) {
						commandShortForm_expand = line[1];
					}
					
					if (line[0].equalsIgnoreCase(keyCommandShortForm_stop)) {
						commandShortForm_stop = line[1];
					}
					
					if (line[0].equalsIgnoreCase(keyCommandShortForm_stopall)) {
						commandShortForm_stopall = line[1];
					}
					
					if (line[0].equalsIgnoreCase(keyCommandShortForm_blockList)) {
						commandShortForm_blockList = line[1];
					}
					
					if (line[0].equalsIgnoreCase(keyConfigFileVersion)) {
						configFileVersion = line[1];
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
			
			writer.write("#" + Massband.pdfFile.getFullName() + "  by: " + Massband.pdfFile.getAuthors() + "\n");
			writer.write(String.format(fileFormat, keyConfigFileVersion, configFileVersion) + "\n");
			writer.write("\n");
			writer.write(String.format(fileFormat, keyItemID, itemID) + "\n");
			writer.write(String.format(fileFormat, keyItemName, itemName) + "\n");
			writer.write("\n");
			writer.write(String.format(fileFormat, keyUsePermissions, usePermissions) + "\n");
			writer.write("\n");
			writer.write("#This can infect the Server performance ...\n");
			writer.write(String.format(fileFormat, keyBlockCountingSpeedLimit, blockCountingSpeedLimit) + "\n");
			writer.write("\n");
			writer.write("#Command short-forms ...\n");
			writer.write(String.format(fileFormat, keyCommandShortForm_clear, commandShortForm_clear) + "\n");
			writer.write(String.format(fileFormat, keyCommandShortForm_lenght, commandShortForm_lenght) + "\n");
			writer.write(String.format(fileFormat, keyCommandShortForm_dimensions, commandShortForm_dimensions) + "\n");
			writer.write(String.format(fileFormat, keyCommandShortForm_countblocks, commandShortForm_countblocks) + "\n");
			writer.write(String.format(fileFormat, keyCommandShortForm_lengthmode, commandShortForm_lengthmode) + "\n");
			writer.write(String.format(fileFormat, keyCommandShortForm_surfacemode, commandShortForm_surfacemode) + "\n");
			writer.write(String.format(fileFormat, keyCommandShortForm_simplemode, commandShortForm_simplemode) + "\n");
			writer.write(String.format(fileFormat, keyCommandShortForm_expand, commandShortForm_expand) + "\n");
			writer.write(String.format(fileFormat, keyCommandShortForm_stop, commandShortForm_stop) + "\n");
			writer.write(String.format(fileFormat, keyCommandShortForm_stopall, commandShortForm_stopall) + "\n");
			writer.write(String.format(fileFormat, keyCommandShortForm_blockList, commandShortForm_blockList) + "\n");
			
			writer.close();
		
			return true;
		} catch (Exception e1) {
			System.err.println(Massband.consoleOutputHeader + " Error: can't create new config file.");		
		}
		return false;
	}
	
}
