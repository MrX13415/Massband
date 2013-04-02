package de.MrX13415.Massband.Language;

import org.bukkit.ChatColor;

import de.MrX13415.Massband.Massband;


public class German extends Language{

	public German(){
		_LastVersion = "1.5.1"; 
		_languageName = "german";
		_version = "1.5.1";
		
		MB_ENABLED = ChatColor.GRAY + "Massband ist jetzt " + ChatColor.GOLD + "Aktiviert";
    	MB_DISABLED = ChatColor.GRAY + "Massband ist jetzt " + ChatColor.GOLD + "Deaktiviert";
	
		THREADS_INTERUPT_TRY = ChatColor.GRAY + "Es wird versucht die Block z�hlungen zu unterbrechen ...";
		THREADS_INTERUPT_FROM_PLAYER = Massband.consoleOutputHeader + ChatColor.RED + " Bl�ck z�hlung unterbrochen von " + ChatColor.GOLD + "%s";
		THREADS_INTERUPT_TRY_ALL = Massband.consoleOutputHeader + ChatColor.RED + " Es wird versucht alle Block z�hlungen zu unterbrechen ... (Anzahl: %s)";
		THREADS_INTERUPT_ERROR1 = Massband.consoleOutputHeader + ChatColor.RED + " Einige Block z�hlungen konnten nicht unterbrochen werden! (Anzahl: %s). Bitte erneut versuchen.";
		THREADS_INTERUPT = ChatColor.GRAY + "Block z�hlungen unterbrochen";
		THREADS_INTERUPT_OK = Massband.consoleOutputHeader + ChatColor.RED + " Alle Block z�hlungen wurden unterbrochen ! (noch �brig: %s)";
    	THREADS_INTERUPT_NOTHING = ChatColor.RED + " Keine Bl�ck z�hlungen aktiv ...";
	
		COUNTBLOCK_TOTAL = ChatColor.WHITE + "Gesamter Inhalt: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " B�cke" + ChatColor.GRAY + " (aus�er Luft)";
		COUNTBLOCK_SPEED = ChatColor.WHITE + "Geschwindigkeit: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Bl�cke/s";
		COUNTBLOCK_TIME = ChatColor.WHITE + "Zeit: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " ms";
    	COUNTBLOCK = ChatColor.GRAY + "Bl�cke werden gez�hlt ...  (Dieser Prozess k�nnte einige Minuten dauern)";
		COUNTBLOCK_VOL = ChatColor.WHITE + "Volumen: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Bl�cke";	
		COUNTBLOCK_ONCE = ChatColor.RED + "Es kann immer nur eine Bl�ck z�hlung gleichzeitig ausgef�hrt werden ! Bitte warten oder unterbrechen ...";
		COUNTBLOCK_BLPAGE_HEADER1 = ChatColor.GREEN + "= Bl�ck z�hlungen === page %s/%s =====================";
		COUNTBLOCK_BLPAGE_HEADER2 = ChatColor.GREEN + "= Bl�ck z�hlungen ==================================";
		COUNTBLOCK_BLPAGE_BREAK_LINE = ChatColor.GRAY + "----------------------------------------------------";
		COUNTBLOCK_BLPAGE_FOOTER =  ChatColor.GREEN + "====================================================";
		COUNTBLOCK_BLPAGE_LINE = ChatColor.WHITE + "  + %s: " + ChatColor.GOLD + "%s";
		COUNTBLOCK_BLPAGE_NO_MAT = ChatColor.WHITE + "  + " + ChatColor.RED + "Ung�ltiges Material: " + ChatColor.WHITE + "%s";
		COUNTBLOCK_CMD_FIRST = ChatColor.RED + "Befehl '/massband countBlocks' oder '/mb cb' nicht ausgef�hrt ...";
		SFM_ONLY = ChatColor.RED + "Dieser Befehl ist nur im 'Oberf�chen-Modus' verf�gbar - siehe Hilfe (/massband)";
				
		D_WIDTH = ChatColor.WHITE +  "Breite: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Bl�cke";
		D_LENGTH = ChatColor.WHITE +  "L�nge: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Bl�cke";
		D_HEIGHT = ChatColor.WHITE +  "H�he: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Bl�cke";
		
    	EXPANDED_UP = ChatColor.GRAY + "Auswahl erweitert ... (%s Bl�cke nach oben)";
    	EXPANDED_DOWN = ChatColor.GRAY + "Auswahl erweitert ... (%s Bl�cke nach unten)";
    	EXPANDED_VERT = ChatColor.GRAY + "Auswahl erweitert ... (unten bis oben)";
    	EXPANDED_DIRECTION = ChatColor.GRAY + "Auswahl erweitert ... (%s Bl�cke Richtung %s)";
    	
    	SELECTION_FIRST = ChatColor.RED + "Keine Auswahl vorhanden ... (Befehl %s)";
    	
    	PERMISSION_NOT = ChatColor.RED + "Erforderliche Berechtigung nicht vorhanden ... (" + ChatColor.GOLD + "%s" + ChatColor.RED + ")"; 
		
		MODE_SIMPLE = ChatColor.GRAY + "- Einfacher-Modus ----------------------------------";
		MODE_LENGTH = ChatColor.GRAY + "- L�ngen-Modus -------------------------------------";
		MODE_SURFACE = ChatColor.GRAY + "- Oberfl�chen-Modus --------------------------------";
		
		MODE_SIMPLE2 = ChatColor.GRAY + "Einfacher-Modus ausgew�hlt ...";
		MODE_LENGTH2 = ChatColor.GRAY + "L�ngen-Modus ausgew�hlt ...";
		MODE_SURFACE2 = ChatColor.GRAY + "Oberfl�chen-Modus ausgew�hlt ...";
		
		LENGTH = ChatColor.WHITE + "L�nge: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Bl�cke";

		POINT_CLR = ChatColor.RED + "Messpunktliste gel�scht";
	    POINT = ChatColor.GREEN + "Punkt #" + ChatColor.GRAY + "%s" + ChatColor.GREEN + ": " + ChatColor.RED + "%s" + ChatColor.GREEN  + "," + ChatColor.RED + "%s" + ChatColor.GREEN  + "," + ChatColor.RED + "%s" + ChatColor.GREEN  + " Ignorierte Achsen: " + ChatColor.GOLD + "%s";
	   	AXIS_X = "X";
		AXIS_Y = "Y";
		AXIS_Z = "Z";
		AXIS_NONE = "Keine";
		IGNORE_AXIS = ChatColor.GRAY + "Ignorierte Achsen: " + ChatColor.GOLD + "%s";
		
		TRUE = "Ja";
		FALSE = "Nein";
		MASK_CHR = ChatColor.GRAY + "*";
		SEPERATE_CHR = ", ";
	
		COMMAND_MASSBAND_USAGE = ChatColor.GREEN + "Massband 2.8 - Ein Ma�band - Befel: " + ChatColor.RED + "/massband" + ChatColor.GREEN + " oder " + ChatColor.RED + "/mb " + "\n" +
									   ChatColor.RED + "/mb "						+ ChatColor.GOLD + "<enable|disable> "						+ ChatColor.GRAY + "Aktieviert/Deaktiviert Massband f�r einen selbst" + "\n" +
									   ChatColor.RED + "/mb " 						+ ChatColor.GOLD + "<simplemode|lengthmode|surfacemode> "	+ ChatColor.GRAY + "Schaltet zwischen den einzelnen Modi um" + "\n" +
									   ChatColor.RED + "/mb ignoreaxes "				+ ChatColor.GOLD + "<none|<Achse> [Achse] [Achse]>"			+ ChatColor.GRAY + "Legt die zu ignorierenden Achsen fest" + "\n" +
									   ChatColor.RED + "/mb clear " 				+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "L�schte die Messpunktliste" + "\n" +
									   ChatColor.RED + "/mb stop " 					+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Unterbricht die Block z�hlung" + "\n" +
									   ChatColor.RED + "/mb stopall " 				+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Unterbricht alle Block z�hlungen auf dem Server" + "\n" +
									   ChatColor.RED + "/mb length "				+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Gibt die letzte gemessene l�nge zur�ck" + "\n" +
									   ChatColor.RED + "/mb dimensions "			+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Gibt die Dimensionen der Auswahl zur�ck" + "\n" +
									   ChatColor.RED + "/mb countBlocks "			+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Gibt die Anzahl der Bl�cke in der Auswahl zur�ck (aus�er Luft)" + "\n" +
									   ChatColor.RED + "/mb expand "				+ ChatColor.GOLD + "<<Anzahl>|vert|[<Anzahl> <up|down>]>"	+ ChatColor.GRAY + "Erweitert die Auswahl in die angegebene Eichtung (vert = von unten bis ganz oben)" + "\n" +
									   ChatColor.RED + "/mb blockList "				+ ChatColor.GOLD + "[<Seite>|<Material> [...]] "			+ ChatColor.GRAY + "Gibt eine Liste mit der Anzahl der einzelnen Bl�cken in der auswahl zur�ck";
		
		COMMAND_MASSBAND_DESCRIPTION = "Verwaltet das Massband Plugin";
	}
}
