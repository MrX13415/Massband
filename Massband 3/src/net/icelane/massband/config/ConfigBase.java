package net.icelane.massband.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.icelane.massband.Plugin;
import net.icelane.massband.Server;
import net.icelane.massband.config.configs.Defaults;

public abstract class ConfigBase {

	public static boolean debug;
	
	private static ConfigBase config; 
	
	private static String format_entry   = "%s: %s      %s";
	private static String format_comment = "# %s";
	private static String regex_entry    = "(\\s*)([^:]*)\\s*:(.*(?:\\\\#).*|[^#]*)(?:#(.*))?";
	private static String regex_comment  = "\\s*#.*";
	
	public static void initialize(Class<? extends ConfigBase> cfgclass) {
		try {
			config = cfgclass.newInstance();
		} catch (InstantiationException | IllegalAccessException ex) {
			Server.logger().log(Level.WARNING, "An error occured while initializing the configuration.", ex);
		}
	}
	
	public static ConfigBase get() {
		return config;
	}
	
	/**
	 * The name and extension of this config file. May also specify a relative
	 * path.<br>
	 * <br>
	 * Example: <code>"things/config.yml"</code>
	 * 
	 * @return the name and the extension as string
	 */
	public abstract String name();

	public static void load() {
		process(false);  // load
	}
	
	public static void save() {
		process(true);  // save
	}
	
	private static void process(boolean save) {
		try {
			
			ArrayList<String> sections = new ArrayList<>();
			String lastKey = "";
			int  sectionlvl = 0;
						
			List<String> lines = Files.readAllLines(get().getFilePath().toPath(), StandardCharsets.UTF_8);			
			ArrayList<Entry<?>> queue = getEntryList(); 

			int lineIndex = -1;
			for (String line : lines) {
				lineIndex++;
				
				Matcher matcher = Pattern.compile(regex_entry).matcher(line);
				
				if (line.matches(regex_comment)) continue;
				if (!matcher.matches()) continue;
				
				// get matched information ...
				String indentStr  = matcher.group(1);
				String keyStr     = matcher.group(2);
				String valueStr   = matcher.group(3);
				String commentStr = matcher.group(4);		

				// make sure there is no "null" value ...
				indentStr  = indentStr != null ? indentStr : "";
				keyStr     = keyStr != null ? keyStr : "";
				valueStr   = valueStr != null ? valueStr : "";
				commentStr = commentStr != null ? commentStr : "";
				
				// format inputs ...
				int keylvl   = indentStr.length() / 2;
				String key   = keyStr.trim();
				String value = valueStr.trim();
				value = value.replaceAll("\\#", "#");
				value = value.replaceAll("\"", "");
				value = value.replaceAll("\'", "");
				
				// find current sections ...
				if (keylvl > sectionlvl){
					sections.add(lastKey);
					sectionlvl = keylvl;
				}else if (keylvl < sectionlvl){
					for (int i = sectionlvl - 1; i >= keylvl; i--) {
						sections.remove(i);
						sectionlvl = keylvl;
					}
				}
				
				// define the entry path ... 
				String section = String.join(".", sections) + ".";
				String path = (keylvl > 0 ? section : "") + key;
				
				if (debug) Server.get().getConsoleSender().sendMessage("§c[level: " + keylvl + "] §6" + path + "§7 = §9" + value + " §a(" + commentStr.trim() + ")");

				for (int index = 0; index < queue.size(); index++) {
					Entry<?> entry = queue.get(index);
					
					if (!entry.getPath().toLowerCase().equals(path.toLowerCase())) continue;
					
					if (save){
						// check if the value has changed ...
						if(!value.equals(entry.get().toString())){
							// define new config line ...
							String outcmt = commentStr.length() > 0 ? String.format(format_comment, commentStr) : "";	
							String outLine = String.format(format_entry,  indentStr + keyStr, entry.get().toString(), outcmt);
							lines.set(lineIndex, outLine);
							
							if (debug) Server.get().getConsoleSender().sendMessage("§bConig <--- §7Entry: §d" + entry.get().toString());
						}else{
							if (debug) Server.get().getConsoleSender().sendMessage("§7Conig --- Entry: §7" + entry.get().toString());
						}
						
						queue.remove(index);
					}else{
						// load value to the config ...
						entry.setValueOf(value);
						
						if (debug) Server.get().getConsoleSender().sendMessage("§7Conig §b---> Entry: §d" + entry.get().toString());
					}
					
					break;
				}

				// memory key of last entry ...
				lastKey = key;
			}
			
			if (save){
				// search for missing values and add them ...
				searchEntries(lines, queue, 0);

				// write the actual file (lines) to disk ...
				saveToDisk(lines);
			}
	
			lines.clear();
		} catch (IOException ex) {
			Server.logger().log(Level.WARNING, "Error: Unable process config data while " + (save ? "saving" : "loading") + " the config file.", ex);
		}
	}

	public static void saveToDisk(List<String> lines) {		
		FileWriter writer = null;
		try {
			File file = get().getFilePath();
			if (!file.getParentFile().exists()) file.getParentFile().mkdirs();

			writer = new FileWriter(file);
			
			for (String line : lines) {
				writer.write(line + '\n');
			}
		} catch (IOException ex) {
			Server.logger().log(Level.WARNING, "Error: Unable save the config file to disk.", ex);
		}finally {
			try {
				if (writer != null) writer.close();
			} catch (IOException e) { }
		}		
	}
	
	public static void saveDefault() {
		ArrayList<String> lines = new ArrayList<>();
		searchEntries(lines, getEntryList(), 0);
		saveToDisk(lines);
	}
	
	public File getFilePath(){
		return new File(getBasePath(), name());
	}
	
	public File getBasePath(){
		return Plugin.get().getDataFolder();
	}
		
	public static String getIndent(int num){
		String indent = "";
		for(int i = 0; i < num; i++) indent += "  ";
		return indent;
	}
	
	private static List<String> searchEntries(List<String> lines, ArrayList<Entry<?>> queue, int initLevel){;
		int level    = initLevel;
		int index    = 0;
		int size     = queue.size();
		boolean loop = false;
		
		while (queue.size() > 0){
			Entry<?> entry = queue.get(index);
			String[] sections = entry.getSection().split("\\.");
			
			if (sections.length == level){
				queue.remove(entry);
				index -= 1;
				size -= 1;
				
				String value = entry.get() == null ? "" : entry.get().toString();
				String out_comment = "";
				if (entry.getComment().trim().length() > 0)
					out_comment = String.format(format_comment, getIndent(level) + entry.getComment());
					lines.add(String.format(format_entry, getIndent(level) + entry.getKey(), value, out_comment));
				
			}else{
				if (loop){
					level++;
					
					lines.add(String.format(format_entry, getIndent(level - 1) + sections[level - 1], "", ""));
					
					searchEntries(lines, queue, level);	
				}
			}
			
			index++;
			if (index >= size){
				index = 0;
				loop = true;
			}
		}
		
		return lines;
	}
	
	private static ArrayList<Entry<?>> getEntryList(){
		ArrayList<Entry<?>> list = new ArrayList<>();
		 
		Field[] fields = Defaults.class.getDeclaredFields();

		for (Field field : fields) {
		    if (Modifier.isStatic(field.getModifiers()) && Entry.class.isAssignableFrom(field.getType())) {		    	
		    	try {
		    		list.add( (Entry<?>) field.get(null) );
					
				} catch (IllegalArgumentException ex) {
					Server.logger().log(Level.WARNING, "Error: Unable process config data.", ex);
				} catch (IllegalAccessException ex) {
					Server.logger().log(Level.WARNING, "Error: Unable process config data.", ex);
				}
		    }
		}
		
		return list;
	}

}
