package net.icelane.massband.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;

import net.icelane.massband.Plugin;


public abstract class CommandBase implements TabExecutor{

	// Commands global
	// ---
	private static boolean initialized = true;
	//private static String permissionRootNode = "";
	
	// Command specific
	// ---
	protected PluginCommand pluginCommand;
	
	protected String name          = "";
	protected String[] aliases      = {""};
	protected String description    = "";
	protected String usage          = "";
	protected String permissionNode = "";
	protected boolean op            = false; //fallback permission
	
	protected ArrayList<CommandBase> commands = new ArrayList<>();
	protected CommandBase parent = null;

	
	// Command abstract functions
	// ---
	
	/***
	 * Return the name of the command here as defined in the "plugins.yml".
	 * @return Name of the command as String.
	 */
	public abstract String name();
	
	/***
	 * Setup the command here.
	 * Define sub commands here via <code>addCommand(...)</code>.
	 */
	public abstract void initialize();

	/***
	 * Define the command logic here.<br><br>
	 * <b>Note:</b> Overriding the function <code>command(Player player, ...)</code> <b>causes this function not to be called</b> if the source was from class <code>Player</code>.
	 * Calls will only be committed to this function if the object of variable <code>source</code> is not an instance of <code>Player</code>.
	 * @param sender Source of the command.
	 * @param cmd Command which was executed.
	 * @param alias Alias of the command which was used.
	 * @param args Passed command arguments.
	 * @return true if a valid command, otherwise false.
	 */
	public abstract boolean command(CommandSender sender, Command cmd, String alias, String[] args);
	
	/***
	 * Define the command logic here.<br><br>
	 * <b>Note:</b> Overriding this function will redirect calls to this function if the source was from class <code>Player</code>.
	 * Calls will only be committed to <code>command(CommandSender sender, ...)</code> if the object of variable <code>source</code> is not an instance of <code>Player</code>.
	 * @param player Source of the command.
	 * @param cmd Command which was executed.
	 * @param name Alias of the command which was used.
	 * @param args Passed command arguments.
	 * @return true if a valid command, otherwise false.
	 */
	public boolean command(Player player, Command cmd, String alias, String[] args){
		return command((CommandSender) player, cmd, alias, args);
	}
	
	
	// Command base logic
	// ---
	
//	public static void Initialize(){
//		if (permissionRootNode == "") throw new IllegalArgumentException("Root permission node not set!");
//		
//		CommandBase.initialized = true;
//	}
	
//	public static void SetPermissionRootNode(String permissionRootNode){
//		CommandBase.permissionRootNode = permissionRootNode.trim();
//	}
//	
//	public static String GetPermissionRootNode(){
//		return permissionRootNode;
//	}
		
	public static CommandBase register(Class<? extends CommandBase> cmdClass){
		if (!initialized) throw new IllegalArgumentException("CommandBase not initialized!");
		
		// check class is valid
		if (!CommandBase.class.isAssignableFrom(cmdClass)){
			throw new IllegalArgumentException("The provided class is not a valid command class.");
		}
		
		try {
			// register and init the given class as command
			CommandBase cmd = (CommandBase) cmdClass.newInstance();
			cmd.setname(cmd.name());
			cmd.setupCommandDefinition();
			cmd.initialize(); 
			cmd.initPluginCommand();
			return cmd;
			
		} catch (InstantiationException e) {
			throw new RuntimeException("Unable to register the provied class as command.");
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Unable to register the provied class as command.");
		}
	}
	
	public boolean isPlayer(CommandSender sender){
		return sender instanceof Player;
	}
	
	public Player getPlayer(CommandSender sender){
		if (sender instanceof Player) return (Player) sender;
		return null;
	}
	
	public boolean hasPermission(CommandSender sender){
		Plugin plugin = Plugin.get();
		
		if (plugin.isPermissionsEnabled()){
			return sender.hasPermission(getPermission());
		}else if (this.op){
			return sender.isOp(); 
		}else{
			return true;
		}
	}
	
	private void initPluginCommand(){
		pluginCommand = Plugin.get().getCommand(name);
		pluginCommand.setAliases(Arrays.asList(aliases));
		pluginCommand.setExecutor(this);
		pluginCommand.setUsage(usage);
		pluginCommand.setDescription(description);
	}
	
	public void setupCommandDefinition(){
		Plugin plugin = Plugin.get();
		if (!plugin.getDescription().getCommands().containsKey(getName())) return;
		Map<String, Object> command = Plugin.get().getDescription().getCommands().get(getName());
		
		if (command.containsKey("description"))
			setDescription((String) command.get("description"));
		
		if (command.containsKey("aliases"))
			setAliases(((ImmutableList<?>) command.get("aliases")).toArray(new String[0]));

		if (command.containsKey("permission"))
			setPermissionNode((String) command.get("permission"));
		
		if (command.containsKey("usage"))
			setUsage((String) command.get("usage"));
	}

	public List<String> getTabList(String arg){
		ArrayList<String> resultList = new ArrayList<>();
		arg = arg.toLowerCase().trim();
		
		for(CommandBase cmd : commands){
			// search for "arg" in command names ...
			if (arg.length() == 0 || cmd.getName().toLowerCase().contains(arg)){
				resultList.add(cmd.getName());
				continue;
			}
			
			// search for "arg" in aliases ...
			for (String alias : cmd.getAliases()) {
				if (alias.toLowerCase().contains(arg)){
					resultList.add(cmd.getName());
					continue;
				}
			}
		}
		
		// add the "?" as command, cause its available on all commands
		if (arg.length() == 0) resultList.add("?");
		
		return resultList;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return getTabList(args.length > 0 ? args[0] : "");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args){
		
		// check permissions
		if (!hasPermission(sender)){
			sender.sendMessage(CommandText.getPermissionDenied(this));
			return false;
		}

		// check for sub commands ...
		if (args.length > 0){			
			for (CommandBase cmd : this.commands) {
				if (cmd.isCommand(args[0])){
					// call sub command
					return cmd.onCommand(sender, command, alias, getArgsOnly(args));
				}
			}
		}

		// process command
		if (args.length == 1 && args[0].equals("?")){
			sender.sendMessage(CommandText.getHelp(this));
			return true;
		}else{
			boolean result = false;
			if (isPlayer(sender)){
				result = command(getPlayer(sender), command, alias, args);
			}else{
				result = command(sender, command, alias, args);
			}
			
			// send help text on false result
			if (!result) sender.sendMessage(CommandText.getHelp(this));
			
			return true;
		}
	}

	public boolean isCommand(String name){
		if (name.equalsIgnoreCase(this.name)) return true;
		
		for (String alias : aliases) {
			if (alias.equalsIgnoreCase(name)) return true;
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
			cmd.setname(cmd.name());
			cmd.setupCommandDefinition();
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

	public String getName() {
		return name;
	}
	
	public String getNames() {
		CommandBase parent = getParent();
		
		String name = getName();
		while (parent != null) {
			name = parent.getName() + " " + name;
			parent = parent.getParent();
		}
		
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public String getUsage() {
		return usage;
	}

	public String getPermission(){
		CommandBase parent = getParent();
		
		String permission = getPermissionNode();
		while (parent != null) {
			String node = parent.getPermissionNode();
			
			if (node.trim() == "op") return "op";
			
			if (node.trim() != ""){
				permission = node + "." + permission;
			}

			parent = parent.getParent();
		}
		
		return permission;
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
	
	private void setname(String name) {
		this.name = name;
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
	
	public boolean isOP(){
		return this.op;
	}
	
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
