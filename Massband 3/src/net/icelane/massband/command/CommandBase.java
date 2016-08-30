package net.icelane.massband.command;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import net.icelane.massband.Plugin;


public abstract class CommandBase implements CommandExecutor{

	// Commands global
	// ---
	private static boolean initialized = false;
	private static String permissionRootNode = "";
	
	// Command specific
	// ---
	protected PluginCommand pluginCommand;
	
	protected String label = "";
	protected String[] aliases = {""};
	protected String description = "";
	protected String usage = "";
	protected String permissionNode = "";
	
	protected ArrayList<CommandBase> commands = new ArrayList<>();
	protected CommandBase parent = null;

	
	// Command basic functions
	// ---
	
	public abstract void initialize();
	
	public abstract boolean command(CommandSender sender, Command cmd, String label, String[] args);
	
	
	// Command base logic
	// ---
	
	public static void Initialize(){
		if (permissionRootNode == "") throw new IllegalArgumentException("Root permission node not set!");
		
		CommandBase.initialized = true;
	}
	
	public static void SetPermissionRootNode(String permissionRootNode){
		CommandBase.permissionRootNode = permissionRootNode.trim();
	}
	
	public static String GetPermissionRootNode(){
		return permissionRootNode;
	}
		
	public static CommandBase register(Class<? extends CommandBase> cmdClass){
		if (!initialized) throw new IllegalArgumentException("CommandBase not initialized!");
		
		// check class is valid
		if (!CommandBase.class.isAssignableFrom(cmdClass)){
			throw new IllegalArgumentException("The provided class is not a valid command class.");
		}
		
		try {
			// register and init the given class as command
			CommandBase cmd = (CommandBase) cmdClass.newInstance();
			cmd.initialize(); 
			cmd.initPluginCommand();
			return cmd;
			
		} catch (InstantiationException e) {
			throw new RuntimeException("Unable to register the provied class as command.");
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Unable to register the provied class as command.");
		}
	}
	
	private void initPluginCommand(){
		pluginCommand = Plugin.get().getCommand(label);
		pluginCommand.setAliases(Arrays.asList(aliases));
		pluginCommand.setExecutor(this);
		pluginCommand.setUsage(usage);
		pluginCommand.setDescription(description);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		
		// basic permissions
		if (!sender.isOp()){
			sender.sendMessage(CommandText.getPermissionDenied(this));
			return false;
		}
		
		// check for sub commands ...
		if (args.length > 0){			
			for (CommandBase cmd : this.commands) {
				if (cmd.isCommand(args[0])){
					// call sub command
					return cmd.onCommand(sender, command, label, getArgsOnly(args));
				}
			}
		}

		// process command
		if (args.length == 1 && args[0].equals("?"))
			sender.sendMessage(CommandText.getHelp(this));
		else{
			boolean result = command(sender, command, label, args);
			
			// send help text on false result
			if (!result) sender.sendMessage(CommandText.getHelp(this));
			
			return result;
		}
		
		return false;
	}

	public boolean isCommand(String label){
		if (label.equalsIgnoreCase(this.label)) return true;
		
		for (String alias : aliases) {
			if (alias.equalsIgnoreCase(label)) return true;
		}
		
		return false;
	}

	public PluginCommand getPluginCommand() {
		return pluginCommand;
	}
	
	public void addCommand(Class<? extends CommandBase> cmdClass){
		// check class is valid
		if (!CommandBase.class.isAssignableFrom(cmdClass)){
			throw new IllegalArgumentException("The provided class is not a valid command class.");
		}
		
		try {
			// init the given class as sub command
			CommandBase cmd = (CommandBase) cmdClass.newInstance();
			cmd.initialize(); 
			cmd.setParent(this);
			this.commands.add(cmd);
			
		} catch (InstantiationException e) {
			throw new RuntimeException("Unable to register the provied class as sub command.");
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Unable to register the provied class as sub command.");
		}
	}
	
	public ArrayList<CommandBase> getCommands() {
		return commands;
	}

	public CommandBase getParent() {
		return parent;
	}

	public String[] getAliases() {
		return aliases;
	}
	
	public String getAliasesString() {
		String out = "";
		for(String alias : getAliases()){
			out += alias + " ";
		}
		return out.trim();
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}
	
	public String getUsage() {
		return usage;
	}

	public String getPermission(){
		String permission = "";
		CommandBase parent = getParent();
		while (parent != null) {
			String node = parent.getPermissionNode();
			
			if (node.trim() == "op") return "op";
			
			if (node.trim() != ""){
				permission += node + "." + permission;
			}

			parent = parent.getParent();
		}
		
		return GetPermissionRootNode() + "." + permission;
	}
	
	public String getPermissionNode(){
		return permissionNode;
	}
	
	public void setParent(CommandBase parentCommand) {
		this.parent = parentCommand;
	}

	public void setAliases(String...alias){
		aliases = alias;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}
	
	public void setPermissionNode(String permissionNode){
		this.permissionNode = permissionNode;
	}
	
//	public boolean _sendCommandHelp(CommandSender sender, Command cmd, String label, String[] args){
//	
//	if (sender.equals(sender.getServer().getConsoleSender())) 
//		UpdateCraft.sendConsoleMessage(getCommandHelpText());
//	else
//		sender.sendMessage(CommandText.getHelp(this));
//	
//	return false;
//}
	
	/**
	 * Removes the first argument of the array, because it is the current command
	 * @param args
	 * @return
	 */
	private String[] getArgsOnly(String[] args){
		String[] newArgsArray = new String[args.length - 1];
		
		for (int i = 1; i < args.length; i++) {
			newArgsArray[i - 1] = args[i];
		}
		
		return newArgsArray;
	}
	
}
