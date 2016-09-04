package net.icelane.massband.config;

public final class EntryTypes {

	public static class Entry_String extends Entry<String>{
		public String valueOf(String value){
			return String.valueOf(value);
		}
	}

	public static class Entry_Boolean extends Entry<Boolean>{
		public Boolean valueOf(String value){
			return Boolean.valueOf(value);
		}
	}

	public static class Entry_Long extends Entry<Long>{
		public Long valueOf(String value){
			return Long.valueOf(value);
		}
	}
	
	public static class Entry_Integer extends Entry<Integer>{
		public Integer valueOf(String value){
			return Integer.valueOf(value);
		}
	}
	
	public static class Entry_Double extends Entry<Double>{
		public Double valueOf(String value){
			return Double.valueOf(value);
		}
	}
	
	public static class Entry_Enum<T extends Enum<T>> extends Entry<Enum<T>>{
		private Class<T> clazz;
		
		public Entry_Enum(Class<T> clazz) {
			this.clazz = clazz;
		}
		
		public Enum<T> valueOf(String value){
			return Enum.valueOf(clazz, value);
		}
	}
}

