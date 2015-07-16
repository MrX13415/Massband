package de.MrX13415.Massband.Language;

import org.bukkit.ChatColor;

import de.MrX13415.Massband.Massband;


public class English extends Language{

	public English(){
		 _LastVersion = "1.5.1"; 
		_languageName = "english";
		_version = "1.5.1";
		
    	MB_ENABLED = ChatColor.GRAY + "Massband is now " + ChatColor.GOLD + "Enabled" + ChatColor.GRAY + " for you";
    	MB_DISABLED = ChatColor.GRAY + "Massband is now " + ChatColor.GOLD + "Disabled"+ ChatColor.GRAY + " for you";
	
		THREADS_INTERUPT_TRY = ChatColor.GRAY + "Tray to Interrrupt Block-counting ...";
		THREADS_INTERUPT_FROM_PLAYER = Massband.consoleOutputHeader + ChatColor.RED + " Your block-counting was interrupted by " + ChatColor.GOLD + "%s";
		THREADS_INTERUPT_TRY_ALL = Massband.consoleOutputHeader + ChatColor.RED + " Try to interrupt all Block countings ... (count: %s)";
		THREADS_INTERUPT_ERROR1 = Massband.consoleOutputHeader + ChatColor.RED + " Could not interrupt some block-countings ! (count: %s). Please try again.";
		THREADS_INTERUPT = ChatColor.GRAY + "Block counting Interrupted";
		THREADS_INTERUPT_OK = Massband.consoleOutputHeader + ChatColor.RED + " All running block-countings interrupted ! (left: %s)";
    	THREADS_INTERUPT_NOTHING = ChatColor.RED + " Nothing to Interrupt ...";
	
		COUNTBLOCK_TOTAL = ChatColor.GREEN + "Total content: " + ChatColor.GOLD + "%s" + ChatColor.GREEN + " Blocks" + ChatColor.GRAY + " (exept air)";
		COUNTBLOCK_SPEED = ChatColor.WHITE + "Speed: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Blocks/s";
		COUNTBLOCK_TIME = ChatColor.WHITE + "Time: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " ms";
    	COUNTBLOCK = ChatColor.GRAY + "Counting Blocks ...  (could take some time)";
		COUNTBLOCK_VOL = ChatColor.WHITE + "Cuboid-Volume: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Blocks";	
		COUNTBLOCK_ONCE = ChatColor.RED + "You can count Blocks once at the same time only ! Wait until it's ready or interrupt it";
		COUNTBLOCK_BLPAGE_HEADER1 = ChatColor.GREEN + "= Block counts === page %s/%s ========================";
		COUNTBLOCK_BLPAGE_HEADER2 = ChatColor.GREEN + "= Block counts =====================================";
		COUNTBLOCK_BLPAGE_BREAK_LINE = ChatColor.GRAY + "----------------------------------------------------";
		COUNTBLOCK_BLPAGE_FOOTER =  ChatColor.GREEN + "====================================================";
		COUNTBLOCK_BLPAGE_LINE = ChatColor.WHITE + "  + %s: " + ChatColor.GOLD + "%s";
		COUNTBLOCK_BLPAGE_NO_MAT = ChatColor.WHITE + "  + " + ChatColor.RED + "Invalid Material: " + ChatColor.WHITE + "%s";
		COUNTBLOCK_CMD_FIRST = ChatColor.RED + "Use the Command '/massband countBlocks' or '/mb cb' first ...";
		COUNTBLOCK_PERCENTAGE = ChatColor.GRAY + "Counting Blocks ... %s%%";
		SFM_ONLY = ChatColor.RED + "This command is only in the 'surface-mode' available - see help (/massband)";
				
		D_WIDTH = ChatColor.WHITE +  "Width: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Blocks";
		D_LENGTH = ChatColor.WHITE +  "Length: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Blocks";
		D_HEIGHT = ChatColor.WHITE +  "Height: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Blocks";
		
    	EXPANDED_UP = ChatColor.GRAY + "Selection expanded ... (%s Blocks up)";
    	EXPANDED_DOWN = ChatColor.GRAY + "Selection expanded ... (%s Blocks down)";
    	EXPANDED_VERT = ChatColor.GRAY + "Selection expanded ... (bottom - top)";
    	EXPANDED_DIRECTION = ChatColor.GRAY + "Selection expanded ... (%s Blocks towards %s)";
    	
    	SELECTION_FIRST = ChatColor.RED + "Make a Selection first ... (use %s)";
    	
		PERMISSION_NOT = ChatColor.RED + "You don't have the required Permission (" + ChatColor.GOLD + "%s" + ChatColor.RED + ")"; 
		
		MODE_SIMPLE = ChatColor.GRAY + "- Simple-mode --------------------------------------";
		MODE_LENGTH = ChatColor.GRAY + "- Length-mode --------------------------------------";
		MODE_FIXED = ChatColor.GRAY +  "- Fixed-Length-mode --------------------Lenght: " + ChatColor.GOLD + "%s" + ChatColor.GRAY + "--";
		MODE_SURFACE = ChatColor.GRAY + "- Surface-mode -------------------------------------";
		
		MODE_SIMPLE2 = ChatColor.GRAY + "Simple-mode selected ...";
		MODE_LENGTH2 = ChatColor.GRAY + "Length-mode selected ...";
		MODE_FIXED2 = ChatColor.GRAY + "Fixed-Length-mode selected ... Lenght: " + ChatColor.GOLD + "%s";
		MODE_SURFACE2 = ChatColor.GRAY + "Surface-mode selected ...";
		
		FIXED_MODE_ERROR = ChatColor.RED + "Invalid value ...";
		
		LENGTH = ChatColor.WHITE + "Length: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Blocks";
		DIFF = "Length: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " from " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Blocks " + ChatColor.GRAY + "Difference: " + ChatColor.GOLD + "%s" + ChatColor.GRAY + " Blocks";
		
		POINT_CLR = ChatColor.GRAY + "Points-list cleared.";
	    POINT = ChatColor.GREEN + "Point #" + ChatColor.GRAY + "%s" + ChatColor.GREEN + ": " + ChatColor.RED + "%s" + ChatColor.GREEN  + "," + ChatColor.RED + "%s" + ChatColor.GREEN  + "," + ChatColor.RED + "%s" + ChatColor.GREEN  + " Ignoring axes: " + ChatColor.GOLD + "%s";
	   	AXIS_X = "X";
		AXIS_Y = "Y";
		AXIS_Z = "Z";
		AXIS_NONE = "None";
		IGNORE_AXIS = ChatColor.GRAY + "Ignoring axes: " + ChatColor.GOLD + "%s";
		
		TRUE = "true";
		FALSE = "false";
		MASK_CHR = ChatColor.GRAY + "*";
		SEPERATE_CHR = ", ";
	
		COMMAND_MASSBAND_USAGE = ChatColor.GREEN + "Massband 2.9 - A Measuring Tape - Command: " + ChatColor.RED + "/massband" + ChatColor.GREEN + " or " + ChatColor.RED + "/mb " + "\n" +
									   ChatColor.RED + "/mb "						+ ChatColor.GOLD + "<enable|disable> "						+ ChatColor.GRAY + "Enables/Disables Massband for your self" + "\n" +
									   ChatColor.RED + "/mb " 						+ ChatColor.GOLD + "<simplemode|lengthmode|surfacemode> "	+ ChatColor.GRAY + "Switchs between the different measure mods" + "\n" +
									   ChatColor.RED + "/mb fixedmode"				+ ChatColor.GOLD + "<value> "								+ ChatColor.GRAY + "Switchs to the fixed mode with the given value" + "\n" +
									   ChatColor.RED + "/mb ignoreaxes "			+ ChatColor.GOLD + "<none|<axis> [axis] [axis]>"			+ ChatColor.GRAY + "Sets the axis to ignore" + "\n" +
									   ChatColor.RED + "/mb clear " 				+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Clears all measuring points" + "\n" +
									   ChatColor.RED + "/mb stop " 					+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Interrupt your current Block-counting" + "\n" +
									   ChatColor.RED + "/mb stopall " 				+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Interrupts all Block-countings of the server" + "\n" +
									   ChatColor.RED + "/mb length "				+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Returns the last measured length" + "\n" +
									   ChatColor.RED + "/mb dimensions "			+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Returns the dimensions of the selection" + "\n" +
									   ChatColor.RED + "/mb countBlocks "			+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Returns the count of Blocks in the selection (exept air)" + "\n" +
									   ChatColor.RED + "/mb expand "				+ ChatColor.GOLD + "<<amount>|vert|[<amount> <up|down>]>"	+ ChatColor.GRAY + "expands the selection in the given direction. (vert = from bottom to the top)" + "\n" +
									   ChatColor.RED + "/mb blockList "				+ ChatColor.GOLD + "[<page>|<material> [...]] "				+ ChatColor.GRAY + "Returns a List of all Blocks in the selection";
		
		COMMAND_MASSBAND_DESCRIPTION = "Manages Massband";
	}
}
