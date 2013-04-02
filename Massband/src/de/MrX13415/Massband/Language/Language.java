package de.MrX13415.Massband.Language;

import org.bukkit.ChatColor;


public class Language {

	public String _LastVersion = "----"; 
	
    public String _languageName = "";
	public String _version = "";
	
	public String MB_ENABLED;
	public String MB_DISABLED;

	public String THREADS_INTERUPT_TRY;
	public String THREADS_INTERUPT_FROM_PLAYER;
	public String THREADS_INTERUPT_TRY_ALL;
	public String THREADS_INTERUPT_ERROR1;
	public String THREADS_INTERUPT;
	public String THREADS_INTERUPT_OK;
	public String THREADS_INTERUPT_NOTHING;
	
	public String COUNTBLOCK_TOTAL;
	public String COUNTBLOCK_SPEED;
	public String COUNTBLOCK_TIME;
	public String COUNTBLOCK;
	public String COUNTBLOCK_VOL;	
	public String COUNTBLOCK_ONCE;
	public String COUNTBLOCK_CMD_FIRST;
	public String COUNTBLOCK_BLPAGE_HEADER1;
	public String COUNTBLOCK_BLPAGE_HEADER2;
	public String COUNTBLOCK_BLPAGE_LINE;
	public String COUNTBLOCK_BLPAGE_NO_MAT;
	public String COUNTBLOCK_BLPAGE_BREAK_LINE;
	public String COUNTBLOCK_BLPAGE_FOOTER;
	
	public String SFM_ONLY;

	public String D_WIDTH;
	public String D_LENGTH;
	public String D_HEIGHT;
	
	public String EXPANDED_UP;
	public String EXPANDED_DOWN;
	public String EXPANDED_VERT;
	public String EXPANDED_DIRECTION;
	
	public String SELECTION_FIRST;
	
	public String PERMISSION_NOT;
	
	public String MODE_SIMPLE;
	public String MODE_LENGTH;
	public String MODE_SURFACE;

	public String MODE_SIMPLE2;
	public String MODE_LENGTH2;
	public String MODE_SURFACE2;
	
	public String LENGTH;
	
	public String POINT_CLR;
	public String POINT;
	public String AXIS_X;
	public String AXIS_Y;
	public String AXIS_Z;
	public String AXIS_NONE;
	public String IGNORE_AXIS;
	
	public String TRUE;
	public String FALSE;
	public String MASK_CHR;
	public String SEPERATE_CHR;
	
	public String COMMAND_MASSBAND_USAGE;
	public String COMMAND_MASSBAND_DESCRIPTION;

	
	public boolean isUptoDate(){
		if (this._version.equalsIgnoreCase(this._LastVersion)){
			return true;
		}
		return false;
	}

	public boolean update(){
		return update(false);
	}
	
	public boolean update(boolean force){
		boolean returnStatus = false;
		if (! isUptoDate() || force) {
		    returnStatus = LanguageLoader.saveLanguage(this);
		}
		return returnStatus;
	}
	
	public String getBoolean(boolean value){
		if (value) return this.TRUE;
		return this.FALSE;
	}
	
	public String getList(Object[] listArray){
		return getList(listArray, true);
	}
	
	public String getList(Object[] listArray, boolean useColors){
		String returnStr = "";
		for (Object object : listArray) {
			if (useColors) {
				returnStr += ChatColor.GOLD + object.toString() + ChatColor.GRAY + this.SEPERATE_CHR;
			}else{
				returnStr += object.toString() + this.SEPERATE_CHR;
			}
		}
		System.out.println(returnStr.substring(0, returnStr.length() - 1));
		if (returnStr.length() >= 2) return returnStr = returnStr.substring(0, returnStr.length() - 1);
		return returnStr;
	}
	
	public String getMaskedText(String text){
		String maskedText = "";
		for (int index = 0; index < text.length(); index++) {
			maskedText += this.MASK_CHR;
		}
		
		return maskedText;
	}

	public String getLanguageFileName() {
		return _languageName + LanguageLoader.getLanguageFileExtention();
	}
}
