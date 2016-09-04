package net.icelane.massband.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import net.icelane.massband.Plugin;
import net.icelane.massband.Server;
import net.icelane.massband.config.configs.Config;

public abstract class ConfigBase {

	private static ConfigBase config; 
	
	private static String format_entry   = "%s: %s";
	private static String format_comment = "%s# %s";

	public static void initialize(Class<? extends ConfigBase> cfgclass) {
		try {
			config = cfgclass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			Server.logger().warning("ERROR @ CONFIG!");
			//TODO: errors
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

	}

	public static void save() {
		FileWriter writer = null;

		try {
			File file = get().getFilePath();
			if (file.getParentFile().exists()) file.getParentFile().mkdirs();

			writer = new FileWriter(file);
			
			ArrayList<String> lines = new ArrayList<>();
			searchEntries(lines, getEntryList(), 0);
			
			for (String string : lines) {
				writer.write(string + '\n');
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				if (writer != null) writer.close();
			} catch (IOException e) { }
		}		
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
	
	private static ArrayList<String> searchEntries(ArrayList<String> lines, ArrayList<Entry<?>> queue, int initLevel){;
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
				if (entry.getComment().trim().length() > 0)
					lines.add(String.format(format_comment, getIndent(level), entry.getComment()));
					lines.add(String.format(format_entry, getIndent(level) + entry.getKey(), value));
				
			}else{
				if (loop){
					level++;
					
					lines.add(String.format(format_entry, getIndent(level - 1) + sections[level - 1], ""));
					
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
		 
		Field[] fields = Config.class.getDeclaredFields();

		for (Field field : fields) {
		    if (Modifier.isStatic(field.getModifiers()) && Entry.class.isAssignableFrom(field.getType())) {
		    	
		    	try {
		    		list.add( (Entry<?>) field.get(null) );
					
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		}
		
		return list;
	}

}
