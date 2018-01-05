package net.icelane.massband.config;

import java.util.Arrays;

public final class EntryTypes {

	public static class Entry_String extends Entry<String>{
		@Override
		public String valueOf(String value){
			return String.valueOf(value);
		}
	}

	public static class Entry_Boolean extends Entry<Boolean>{
		@Override
		public Boolean valueOf(String value){
			return Boolean.valueOf(value);
		}

		@Override
		public String[] getValues() {
			return new String[] {"true", "false"};
		}
	}

	public static class Entry_Long extends Entry<Long>{
		@Override
		public Long valueOf(String value){
			return Long.valueOf(value);
		}
	}
	
	public static class Entry_Integer extends Entry<Integer>{
		@Override
		public Integer valueOf(String value){
			return Integer.valueOf(value);
		}
	}
	
	public static class Entry_Double extends Entry<Double>{
		@Override
		public Double valueOf(String value){
			return Double.valueOf(value);
		}
	}
	
	public static class Entry_Enum<T extends Enum<T>> extends Entry<Enum<T>>{
		private Class<T> clazz;
		
		public Entry_Enum(Class<T> clazz) {
			this.clazz = clazz;
		}
		
		@Override
		public Enum<T> valueOf(String value){
			return Enum.valueOf(clazz, value);
		}

		@Override
		public String[] getValues() {	
			return getNames(clazz);
		}
		
		public static String[] getNames(Class<? extends Enum<?>> e) {
		    return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
		}
	}
}

