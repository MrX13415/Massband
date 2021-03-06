package net.icelane.massband.config;

import java.util.Arrays;

import net.icelane.massband.config.EntryTypes.Entry_Boolean;
import net.icelane.massband.config.EntryTypes.Entry_Double;
import net.icelane.massband.config.EntryTypes.Entry_Enum;
import net.icelane.massband.config.EntryTypes.Entry_Integer;
import net.icelane.massband.config.EntryTypes.Entry_Long;
import net.icelane.massband.config.EntryTypes.Entry_String;

public abstract class Entry<T> {

	private String path = "";
	private String[] comments = new String[0];
	private T value;
	private String[] values;
	private T defaultValue;

	
	public static <T> Entry<T> define(Entry<T> entry, String path, T value, String... comment){
		
		entry.setPath(path);
		entry.setComments(comment);
		entry.setDefault(value);
		entry.setValues(entry.getDefault().toString());
		entry.set(entry.getDefault());
				
		return entry;
	}
	
	public static Entry_String define(String path, String value, String... comment){
		return (Entry_String) define(new Entry_String(), path, value, comment);
	}
	
	public static Entry_Boolean define(String path, Boolean value, String... comment){
		return (Entry_Boolean) define(new Entry_Boolean(), path, value, comment);
	}
	
	public static Entry_Long define(String path, Long value, String... comment){
		return (Entry_Long) define(new Entry_Long(), path, value, comment);
	}
	
	public static Entry_Integer define(String path, Integer value, String... comment){
		return (Entry_Integer) define(new Entry_Integer(), path, value, comment);
	}
	
	public static Entry_Double define(String path, Double value, String comment){
		return (Entry_Double) define(new Entry_Double(), path, value, comment);
	}

	public static <E extends Enum<E>> Entry_Enum<E> define(String path, Class<E> clazz, E value, String comment) {
		return (Entry_Enum<E>) define(new Entry_Enum<E>(clazz), path, value, comment);
    }
		
// Template:
//	public static Entry_ define(String path,  value, String comment){
//		return (Entry_) define(new Entry_(), path, value, comment);
//	}
	
	@SuppressWarnings("unchecked")
	public void resetToDefault(ConfigBase<?> config) {
		// reset non player config ...
		if (config == null || !PlayerConfigBase.class.isAssignableFrom(config.getClass())) {
			resetToDefault(); return;
		}
			
		PlayerConfigBase<?> playerConfig = (PlayerConfigBase<?>) config;
		
		if (playerConfig.isDefault()) {
			resetToDefault(); return;
		}
		
		Entry<?> defaultEntry = playerConfig.getDefaultConfig().getEntry(getPath());		
		this.value = (T) defaultEntry.get();
	}
	
	public void resetToDefault() {
		this.value = defaultValue;
	}
	
	public abstract T valueOf(String value) throws IllegalArgumentException, NullPointerException;
	
	public String[] getValues() {
		return values;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
	
	/**
	 * Returns the whole path of the entry.</br>
	 * e.g. "something.section.mykey"
	 * @return The path as String.
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Returns the section part of the entry.</br>
	 * e.g. "something.section" for something.section.mykey
	 * @return The section as String.
	 */
	public String getSection() {
		if (!path.contains(".")) return "";
		return path.substring(0, path.lastIndexOf("."));
	}

	/**
	 * Returns the key part of the entry.</br>
	 * e.g. "mykey" for something.section.mykey
	 * @return The key as String.
	 */
	public String getKey() {
		if (!path.contains(".")) return path;
		return path.substring(path.lastIndexOf(".") + 1);
	}
	
	public String[] getComments() {
		return comments;
	}

	public T getDefault() {
		return defaultValue;
	}

	public T get() {
		return value;
	}
	
	public void setValues(String... values) {
		this.values = values;
	}
	
	public void addValues(String... values) {
	    final int N = this.values.length;
	   
	    String[] arr = Arrays.copyOf(this.values, N + values.length);	    
	    for (int index = 0; index < values.length; index++)
	    	arr[N + index] = values[index];
	    
	    setValues(arr);
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public void setComments(String... comment) {
		this.comments = comment;
	}

	public void setDefault(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void set(T value) {
		this.value = value;
	}

	public boolean setValueOf(String value) {
		try {
			T newValue = valueOf(value);
			this.value = newValue;
			return true;
		} catch (IllegalArgumentException|NullPointerException ex) {
			return false;
		}
	}
			
}
