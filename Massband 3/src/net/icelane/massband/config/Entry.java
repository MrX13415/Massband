package net.icelane.massband.config;

import net.icelane.massband.config.EntryTypes.Entry_Boolean;
import net.icelane.massband.config.EntryTypes.Entry_Double;
import net.icelane.massband.config.EntryTypes.Entry_Enum;
import net.icelane.massband.config.EntryTypes.Entry_Integer;
import net.icelane.massband.config.EntryTypes.Entry_Long;
import net.icelane.massband.config.EntryTypes.Entry_String;

public abstract class Entry<T> {

	private String path = "";
	private String comment = "";
	private T defaultValue;
	private T value;
	
	public static <T> Entry<T> define(Entry<T> entry, String path, T value, String comment){
		entry.setPath(path);
		entry.setComment(comment);
		entry.setDefault(value);
		entry.set(entry.getDefault());
		return entry;
	}
	
	public static Entry_String define(String path, String value, String comment){
		return (Entry_String) define(new Entry_String(), path, value, comment);
	}
	
	public static Entry_Boolean define(String path, Boolean value, String comment){
		return (Entry_Boolean) define(new Entry_Boolean(), path, value, comment);
	}
	
	public static Entry_Long define(String path, Long value, String comment){
		return (Entry_Long) define(new Entry_Long(), path, value, comment);
	}
	
	public static Entry_Integer define(String path, Integer value, String comment){
		return (Entry_Integer) define(new Entry_Integer(), path, value, comment);
	}
	
	public static Entry_Double define(String path, Double value, String comment){
		return (Entry_Double) define(new Entry_Double(), path, value, comment);
	}

	public static <E extends Enum<E>> Entry_Enum<E> define(String path, Class<E> clazz, E value, String comment) {
		return (Entry_Enum<E>) define(new Entry_Enum<E>(clazz), path, value, comment);
    }
		
//	public static Entry_ define(String path,  value, String comment){
//		return (Entry_) define(new Entry_(), path, value, comment);
//	}
	
	public abstract T valueOf(String value);
	
	@Override
	public String toString() {
		return value.toString();
	}
	
	public String getPath() {
		return path;
	}
	
	public String getSection() {
		if (!path.contains(".")) return "";
		return path.substring(0, path.lastIndexOf("."));
	}

	public String getKey() {
		if (!path.contains(".")) return path;
		return path.substring(path.lastIndexOf(".") + 1);
	}
	
	public String getComment() {
		return comment;
	}

	public T getDefault() {
		return defaultValue;
	}

	public T get() {
		return value;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setDefault(T defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void set(T value) {
		this.value = value;
	}

	public void setValueOf(String value) {
		this.value = valueOf(value);
	}
	
}