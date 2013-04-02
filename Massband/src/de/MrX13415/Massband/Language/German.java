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
	
		THREADS_INTERUPT_TRY = ChatColor.GRAY + "Es wird versucht die Block zählungen zu unterbrechen ...";
		THREADS_INTERUPT_FROM_PLAYER = Massband.consoleOutputHeader + ChatColor.RED + " Blöck zählung unterbrochen von " + ChatColor.GOLD + "%s";
		THREADS_INTERUPT_TRY_ALL = Massband.consoleOutputHeader + ChatColor.RED + " Es wird versucht alle Block zählungen zu unterbrechen ... (Anzahl: %s)";
		THREADS_INTERUPT_ERROR1 = Massband.consoleOutputHeader + ChatColor.RED + " Einige Block zählungen konnten nicht unterbrochen werden! (Anzahl: %s). Bitte erneut versuchen.";
		THREADS_INTERUPT = ChatColor.GRAY + "Block zählungen unterbrochen";
		THREADS_INTERUPT_OK = Massband.consoleOutputHeader + ChatColor.RED + " Alle Block zählungen wurden unterbrochen ! (noch übrig: %s)";
    	THREADS_INTERUPT_NOTHING = ChatColor.RED + " Keine Blöck zählungen aktiv ...";
	
		COUNTBLOCK_TOTAL = ChatColor.WHITE + "Gesamter Inhalt: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Böcke" + ChatColor.GRAY + " (ausßer Luft)";
		COUNTBLOCK_SPEED = ChatColor.WHITE + "Geschwindigkeit: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Blöcke/s";
		COUNTBLOCK_TIME = ChatColor.WHITE + "Zeit: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " ms";
    	COUNTBLOCK = ChatColor.GRAY + "Blöcke werden gezählt ...  (Dieser Prozess könnte einige Minuten dauern)";
		COUNTBLOCK_VOL = ChatColor.WHITE + "Volumen: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Blöcke";	
		COUNTBLOCK_ONCE = ChatColor.RED + "Es kann immer nur eine Blöck zählung gleichzeitig ausgeführt werden ! Bitte warten oder unterbrechen ...";
		COUNTBLOCK_BLPAGE_HEADER1 = ChatColor.GREEN + "= Blöck zählungen === page %s/%s =====================";
		COUNTBLOCK_BLPAGE_HEADER2 = ChatColor.GREEN + "= Blöck zählungen ==================================";
		COUNTBLOCK_BLPAGE_BREAK_LINE = ChatColor.GRAY + "----------------------------------------------------";
		COUNTBLOCK_BLPAGE_FOOTER =  ChatColor.GREEN + "====================================================";
		COUNTBLOCK_BLPAGE_LINE = ChatColor.WHITE + "  + %s: " + ChatColor.GOLD + "%s";
		COUNTBLOCK_BLPAGE_NO_MAT = ChatColor.WHITE + "  + " + ChatColor.RED + "Ungültiges Material: " + ChatColor.WHITE + "%s";
		COUNTBLOCK_CMD_FIRST = ChatColor.RED + "Befehl '/massband countBlocks' oder '/mb cb' nicht ausgeführt ...";
		SFM_ONLY = ChatColor.RED + "Dieser Befehl ist nur im 'Oberfächen-Modus' verfügbar - siehe Hilfe (/massband)";
				
		D_WIDTH = ChatColor.WHITE +  "Breite: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Blöcke";
		D_LENGTH = ChatColor.WHITE +  "Länge: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Blöcke";
		D_HEIGHT = ChatColor.WHITE +  "Höhe: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Blöcke";
		
    	EXPANDED_UP = ChatColor.GRAY + "Auswahl erweitert ... (%s Blöcke nach oben)";
    	EXPANDED_DOWN = ChatColor.GRAY + "Auswahl erweitert ... (%s Blöcke nach unten)";
    	EXPANDED_VERT = ChatColor.GRAY + "Auswahl erweitert ... (unten bis oben)";
    	EXPANDED_DIRECTION = ChatColor.GRAY + "Auswahl erweitert ... (%s Blöcke Richtung %s)";
    	
    	SELECTION_FIRST = ChatColor.RED + "Keine Auswahl vorhanden ... (Befehl %s)";
    	
    	PERMISSION_NOT = ChatColor.RED + "Erforderliche Berechtigung nicht vorhanden ... (" + ChatColor.GOLD + "%s" + ChatColor.RED + ")"; 
		
		MODE_SIMPLE = ChatColor.GRAY + "- Einfacher-Modus ----------------------------------";
		MODE_LENGTH = ChatColor.GRAY + "- Längen-Modus -------------------------------------";
		MODE_SURFACE = ChatColor.GRAY + "- Oberflächen-Modus --------------------------------";
		
		MODE_SIMPLE2 = ChatColor.GRAY + "Einfacher-Modus ausgewählt ...";
		MODE_LENGTH2 = ChatColor.GRAY + "Längen-Modus ausgewählt ...";
		MODE_SURFACE2 = ChatColor.GRAY + "Oberflächen-Modus ausgewählt ...";
		
		LENGTH = ChatColor.WHITE + "Länge: " + ChatColor.GOLD + "%s" + ChatColor.WHITE + " Blöcke";

		POINT_CLR = ChatColor.RED + "Messpunktliste gelöscht";
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
	
		COMMAND_MASSBAND_USAGE = ChatColor.GREEN + "Massband 2.8 - Ein Maßband - Befel: " + ChatColor.RED + "/massband" + ChatColor.GREEN + " oder " + ChatColor.RED + "/mb " + "\n" +
									   ChatColor.RED + "/mb "						+ ChatColor.GOLD + "<enable|disable> "						+ ChatColor.GRAY + "Aktieviert/Deaktiviert Massband für einen selbst" + "\n" +
									   ChatColor.RED + "/mb " 						+ ChatColor.GOLD + "<simplemode|lengthmode|surfacemode> "	+ ChatColor.GRAY + "Schaltet zwischen den einzelnen Modi um" + "\n" +
									   ChatColor.RED + "/mb ignoreaxes "				+ ChatColor.GOLD + "<none|<Achse> [Achse] [Achse]>"			+ ChatColor.GRAY + "Legt die zu ignorierenden Achsen fest" + "\n" +
									   ChatColor.RED + "/mb clear " 				+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Löschte die Messpunktliste" + "\n" +
									   ChatColor.RED + "/mb stop " 					+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Unterbricht die Block zählung" + "\n" +
									   ChatColor.RED + "/mb stopall " 				+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Unterbricht alle Block zählungen auf dem Server" + "\n" +
									   ChatColor.RED + "/mb length "				+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Gibt die letzte gemessene länge zurück" + "\n" +
									   ChatColor.RED + "/mb dimensions "			+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Gibt die Dimensionen der Auswahl zurück" + "\n" +
									   ChatColor.RED + "/mb countBlocks "			+ ChatColor.GOLD + ""										+ ChatColor.GRAY + "Gibt die Anzahl der Blöcke in der Auswahl zurück (ausßer Luft)" + "\n" +
									   ChatColor.RED + "/mb expand "				+ ChatColor.GOLD + "<<Anzahl>|vert|[<Anzahl> <up|down>]>"	+ ChatColor.GRAY + "Erweitert die Auswahl in die angegebene Eichtung (vert = von unten bis ganz oben)" + "\n" +
									   ChatColor.RED + "/mb blockList "				+ ChatColor.GOLD + "[<Seite>|<Material> [...]] "			+ ChatColor.GRAY + "Gibt eine Liste mit der Anzahl der einzelnen Blöcken in der auswahl zurück";
		
		COMMAND_MASSBAND_DESCRIPTION = "Verwaltet das Massband Plugin";
	}
}
