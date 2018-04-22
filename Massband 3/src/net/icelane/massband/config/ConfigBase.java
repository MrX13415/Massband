package net.icelane.massband.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.icelane.massband.Plugin;
import net.icelane.massband.Server;

public abstract class ConfigBase<T extends ConfigBase<T>> {

	public static boolean debug;

	public static String version = "2";
			
	private static String format_entry    = "%1$s%2$s: %3$s";
	private static String format_comment  = "%1$s# %2$s";
	private static String format_values   = "Values: %s";
	private static String format_valSep   = " | ";
	private static String[] format_header = new String[]{
			"#-------------------------------------------------------------------------------",
			"#- %1$s",
			"#- Config: %2$s",
			"#- Format Version: %3$s",
			"#-------------------------------------------------------------------------------",
			};
	
	private static String regex_entry    = "(\\s*)([^:]*)\\s*:(.*(?:\\\\#).*|[^#]*)(?:#(.*))?";
	private static String regex_comment  = "\\s*#.*";
	private static String regex_header   = "\\s*#-.*";
	
	private Class<T> configClass;
	  
	
	public static <T extends ConfigBase<T>> T initialize(Class<T> cfgclass) {
		try {
			T config = cfgclass.newInstance();
			config.setConfigClass(cfgclass);
			return config;
		} catch (InstantiationException | IllegalAccessException ex) {
			Server.logger().log(Level.WARNING, "An error occured while initializing the configuration.", ex);
		}
		return null;
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

	/**
	 * Use to alter the defined entries after they are initialized.
	 */
	public void postInitialize() {
		
	}
	
	public T load() {
		process(getFilePath(), false, true);  // load
		postInitialize();
		return configClass.cast(this);
	}

	public void save() {
		process(getFilePath(), true, true);  // save
	}
	
	protected void process(File file, boolean save, boolean createAlways) {
		try {
			
			ArrayList<String> sections = new ArrayList<>();
			String lastKey = "";
			int  sectionlvl = 0;
			
			// create the file if not present			
			if (!file.exists()) {
				if (createAlways) save = true;
				if (save) creatFile(file);
				else return;
			}
			
			List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);			
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
							//String outcmt = commentStr.length() > 0 ? String.format(format_comment, commentStr) : "";	
							String outLine = String.format(format_entry,  indentStr, keyStr, entry.get().toString());
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
				// seach for a header ...
				boolean headerFound = false;
				for (String line : lines) {
					if (line.trim().isEmpty()) continue;
					if (line.matches(regex_header)) headerFound = true;
					if (line.matches(regex_entry)) break;
				}
				if (!headerFound) {
					int index = 0;
					for (String fhLine : format_header) {
						lines.add(index, String.format(fhLine,
								Plugin.get().getName(),
								name(),
								version));
						index++;
					}
				}
					
				// search for missing values and add them ...
				searchEntries(lines, queue);

				// write the actual file (lines) to disk ...
				saveToDisk(file, lines);
			}
	
			lines.clear();
		} catch (IOException ex) {
			Server.logger().log(Level.WARNING, "Error: Unable process config data while " + (save ? "saving" : "loading") + " the config file.", ex);
		}
	}
	
	public static boolean creatFile(File file) {
		try {
			if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
			return file.createNewFile();
		} catch (IOException e) { }
		return false;
	}

	public static void saveToDisk(File file, List<String> lines) {		
		FileWriter writer = null;
		try {
			if (!file.exists()) creatFile(file);

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
	
//	public static void ssaveDefault() {
//		ArrayList<String> lines = new ArrayList<>();
//		searchEntries(lines, getEntryList(), 0);
//		//saveToDisk(lines);
//	}
	
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
	
	private static List<String> searchEntries(List<String> lines, ArrayList<Entry<?>> queue){;
		return searchEntries(lines, queue, 0, "");
	}
	
	private static List<String> searchEntries(List<String> lines, ArrayList<Entry<?>> queue, int initLevel, String sectionName){;
		int level    = initLevel;
		int index    = -1;
		boolean loop = false;
		
		while (queue.size() > 0){
			index++;
			if (index >= queue.size()){
				index = 0;
				// 3rd loop run: we have searched for entries and sub entries, so break ... 
				if (level > 0 && loop) break;
				loop = true; // init 2nd loop run ...
			}
			
			// get the current entry ...
			Entry<?> entry = queue.get(index);
			String[] sections = entry.getSection().split("\\.");
			
			// make sure we only use entries wich fit to the current section ...
			if (level > 0 && level <= sections.length) {
				if (!sections[level - 1].equalsIgnoreCase(sectionName)) continue;
			}

			if (sections.length == level) {
				// 1st loop run: Add all entries on this section level ...
				queue.remove(entry);
				index -= 1;
				
				// comment ...
				if (entry.getComments().length > 0) {
					for (String comment : entry.getComments()) {
						lines.add(String.format(format_comment, getIndent(level), comment.trim()));
					}
				}
				// values ...
				if (entry.getValues().length > 1) {
					String values = Stream.of(entry.getValues())
						    .map(Object::toString)
						    .collect(Collectors.joining(format_valSep,"",""));
					
					lines.add(String.format(format_comment, getIndent(level),
							String.format(format_values,values)));
				}
				
				// entry ...
				String value = entry.get() == null ? "" : entry.get().toString();
				lines.add(String.format(format_entry, getIndent(level), entry.getKey(), value));
					
			}else if (loop){
				// 2nd loop run: Search for sub entries ...
				if (sections.length >= level) {
					lines.add(""); // an empty line between new sections ...
					lines.add(String.format(format_entry, getIndent(level), sections[level], "", ""));
					searchEntries(lines, queue, level + 1, sections[level]);
					index = 0;
				}
			}
		}
		
		return lines;
	}
	
	public ArrayList<Entry<?>> getEntryList(){
		ArrayList<Entry<?>> list = new ArrayList<>();
		 
		Field[] fields = getConfigClass().getDeclaredFields();

		for (Field field : fields) {
		    if (Entry.class.isAssignableFrom(field.getType())) {		    	
		    	try {
		    		list.add( (Entry<?>) field.get(this) );
					
				} catch (IllegalArgumentException | IllegalAccessException ex) {
					Server.logger().log(Level.WARNING, "Error: Unable process config data.", ex);
				}
		    }
		}
		
		return list;
	}
	
	public Entry<?> getEntry(String path){
		ArrayList<Entry<?>> list = getEntryList();
		path = path.trim().toLowerCase();
		
		for (Entry<?> entry : list) {
			if (entry.getPath().toLowerCase().contains(path)) return entry;
		}
		
		return null;
	}
	
	
	protected Class<T> getConfigClass() {
		return configClass;
	}
	
	protected void setConfigClass(Class<T> configClass) {
		this.configClass = configClass;
	}

}
