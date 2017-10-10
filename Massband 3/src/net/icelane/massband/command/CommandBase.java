package net.icelane.massband.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import com.google.common.collect.ImmutableList;

import net.icelane.massband.Plugin;

/**
 * Abstract API for easy command implementation in Bukkit.
 * @author MrX13415
 * @version 2.1
 */
public abstract class CommandBase implements TabExecutor{

	// Commands global
	// ---
	/**
	 * The visibility stages a command can have.
	 * This will control how and if a command is listed in the help output.
	 * <li>{@link #Hidden}</li>
	 * <li>{@link #Visible}</li>
	 * <li>{@link #Permission}</li>
	 */
	public enum Visibility{
		/**
		 * The command will never be listed in the help output.
		 */
		Hidden,
		/**
		 * The command is visible to everyone at all time in the help output.
		 */
		Visible, 
		/**
		 * The command will only be visible if the command sender has the required permission to use it.
		 */
		Permission
	}
	
	// Command specific
	// ---
	protected PluginCommand pluginCommand;
	
	protected String name           = "";
	protected String[] aliases      = {};
	protected String description    = "";
	protected String help           = "";
	protected String usage          = "";
	protected Permission permission = null;
	protected boolean isNode        = false;
	protected boolean op            = false;
	protected boolean opOnly        = false;
	//protected boolean explicit      = false; // Permission has to be set explicit. Excluded from wildcard.
	protected boolean inGameOnly    = false;
	protected Visibility visibility = Visibility.Permission;
	protected String[] tabList      = {};
	
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
	 * For Commands defined in the<code>plugin.yml</code>settings like name, etc. will be retrieved from there.
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
	 * @return <code>true</code> if a valid command, otherwise false.
	 */
	public abstract boolean command(CommandSender sender, Command cmd, String alias, String[] args);
	
	/***
	 * Define the command logic here.<br><br>
	 * <b>Note:</b> Overriding this function will redirect calls to this function if the source was from class <code>Player</code>.
	 * Calls will only be committed to <code>command(CommandSender sender, ...)</code> if the object of variable <code>source</code> is not an instance of <code>Player</code>.
	 * @param player Source of the command.
	 * @param cmd Command which was executed.
	 * @param alias Alias of the command which was used.
	 * @param args Passed command arguments.
	 * @return <code>true</code> if a valid command, otherwise false.
	 */
	public boolean command(Player player, Command cmd, String alias, String[] args){
		return command((CommandSender) player, cmd, alias, args);
	}
	
	
	// Command base logic
	// ---
	
	/**
	 * Registers a command to the game. Has to be called for every command which should be directly available.
	 * Any sub command (added to a command via <code>addCommand(...)</code>) must not be registered.
	 * @param cmdClass A class extending the class <code>CommandBase</code>
	 * @return An instance of the given command class if the given it has been registered successfully.
	 * @throws IllegalArgumentException If the given class is not extending the class <code>CommandBase</code>
	 * @throws RuntimeException If any error occurred while registering the command.
	 */
	public static CommandBase register(Class<? extends CommandBase> cmdClass){
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
			cmd.buildPermission();
			cmd.initPluginCommand();
			return cmd;
			
		} catch (InstantiationException e) {
			throw new RuntimeException("Unable to register the provied class as command.");
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Unable to register the provied class as command.");
		}
	}
	
	private void initPluginCommand(){
		pluginCommand = Plugin.get().getCommand(name);
		pluginCommand.setAliases(Arrays.asList(aliases));
		pluginCommand.setExecutor(this);
		pluginCommand.setUsage(usage);
		pluginCommand.setDescription(description);
		//pluginCommand.setPermission("");
		
		initCommandPermission();
	}
	
	private void initCommandPermission() {
		if (getPermission() == null) return;
		
		org.bukkit.permissions.Permission p = Bukkit.getPluginManager().getPermission(getPermission().getFullPermission());
		if (p != null) {
			p.setDefault(getPermission().getDefaultValue());
		}else {
			Bukkit.getPluginManager().addPermission(getPermission().getPermission());	
		}
	}
	
	/**
	 * Reads any command settings defined in the <code>plugin.yml</code> for this command name and sets them.
	 * Settings not defined in the <code>plugin.yml</code> won't be changed.
	 */
	public void setupCommandDefinition(){
		Plugin plugin = Plugin.get();
		if (!plugin.getDescription().getCommands().containsKey(getName())) return;
		Map<String, Object> command = Plugin.get().getDescription().getCommands().get(getName());
		
		if (command.containsKey("description"))
			setDescription((String) command.get("description"));
		
		if (command.containsKey("aliases"))
			setAliases(((ImmutableList<?>) command.get("aliases")).toArray(new String[0]));

//		if (command.containsKey("permission"))
//			setPermissionNode((String) command.get("permission"));
		
		if (command.containsKey("usage"))
			setUsage((String) command.get("usage"));
	}
	
	/**
	 * Whether the given <code>CommandSender</code> object is a player.
	 * @param sender An instance of class <code>CommandSender</code>
	 * @return <code>true</code> if the given object is a instance of class <code>Player</code>
	 */
	public boolean isPlayer(CommandSender sender){
		return sender instanceof Player;
	}
	
	/**
	 * 
	 * @param sender An instance of class <code>CommandSender</code>.
	 * @return An object of class <code>Player</code> for the given <code>CommandSender</code> object. <code>null</code> if the given sender object is not a player.
	 */
	public Player getPlayer(CommandSender sender){
		if (sender instanceof Player) return (Player) sender;
		return null;
	}
	
	/**
	 * Whether the given <code>CommandSender</code> object has permission to use this command.
	 * Permission will also be granted if a wildcard permission of any parent command is present.
	 * <p>
	 * Wildcard permission can be disabled by setting <code>setExplicit(...)</code> of command object in question to <code>false</code>.
	 * Permission will then only granted if the permission has been set explicit to the <code>CommandSender</code> object.</p>
	 * <p>
	 * If permissions has been disable for this plug-in, access will be determined if this command has been marked for "OP only" and whether the player is OP.
	 * </p>
	 * @param sender An instance of class <code>CommandSender</code>
	 * @return <code>true</code> if the given <code>CommandSender</code> object has use permissions.
	 */
	public boolean hasPermission(CommandSender sender){
		//TODO Wildcard permission and disallowed permission not working together.
		Plugin plugin = Plugin.get();
		
		// advanced permissions arn't available ...
		if (!plugin.isPermissionsEnabled() || this.opOnly ) {
			return ( this.op ? sender.isOp() : true );
		}
		
		if (sender.hasPermission(getPermission().getFullPermission())) return true;
		
		// This permission is also granted if the sender has a wildcard permission of a parent command.
		//if (!this.explicit) {
		return hasAsteriskPermission(sender);
		//}
		
		//return false;
	}
	
	/**
	 * Recursively checks if the <code>CommandSender</code> object provided, has a wild card permission.
	 * First we check if there is a wildcard permission of our own permission.
	 * If not, we check if the permission of the next parent command is present and so on. 
	 * @param sender An instance of class <code>CommandSender</code>
	 * @return <code>true</code> if the given <code>CommandSender</code> object has a wildcard permission.
	 */
	protected boolean hasAsteriskPermission(CommandSender sender){
		Permission permission = new Permission(Permission.Asterisk, PermissionDefault.FALSE);
		permission.setParent(getPermission());
		permission = buildPermission(permission);

		if (sender.hasPermission(permission.getFullPermission())) return true;
		if (parent == null) return false;
		
		return parent.hasAsteriskPermission(sender);
	}
	
	/**
	 * Rebuild the permission object of this command object based the <code>permissionNode</code> field of this and all parent commands.
	 * @return The builded Permission object which as been set to this command.
	 */
	public Permission buildPermission(){
		if (!isNode) return permission;
		permission = buildPermission(new Permission(getPermission()));
		return permission;
	}

	/**
	 * Build a permission object using the given node as base and the <code>permissionNode</code> field all parent commands.
	 * @return The builded Permission object.
	 */
	protected Permission buildPermission(Permission node){
		CommandBase parent = getParent();
		Permission permission = node;
				
		while (parent != null) {
			Permission parentNode = new Permission(parent.getPermission());
			
			if (permission.getNode().isEmpty()) {
				permission = parentNode;
			}else {
				permission.setParent(parentNode);	
			}
			
			parent = parent.getParent();  // next
		}
		
		return permission;
	}
	
	/**
	 * Returns a list of all matching sub commands and defined tab values for the given argument.
	 * @param arg A command argument.
	 * @return A list of all matching strings.
	 */
	public List<String> getTabList(String arg){
		ArrayList<String> resultList = new ArrayList<>();
		arg = arg.toLowerCase().trim();
		
		for(CommandBase cmd : commands){
			// search for "arg" in command names ...
			if (arg.length() == 0 || cmd.getName().toLowerCase().contains(arg)){
				resultList.add(cmd.getName());
			}
		}
		
		if (resultList.size() == 0){
			for(CommandBase cmd : commands){
				// search for "arg" in aliases ...
				for (String alias : cmd.getAliases()) {
					if (alias.toLowerCase().contains(arg)){
						resultList.add(cmd.getName());
					}
				}
			}
		}
		
		for (String value : tabList){
			// search for "arg" in command names ...
			if (arg.length() == 0 || value.toLowerCase().contains(arg)){
				resultList.add(value);
			}
		}
		
		// add the "?" as command, cause its available on all commands
		if (arg.length() == 0) resultList.add("?");
		
		resultList.sort(Comparator.naturalOrder());
		return resultList;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		// check for sub commands ...
		if (args.length > 1){			
			for (CommandBase cmd : this.commands) 
			{
				if (cmd.isCommand(args[args.length - 2])){
					// call sub command
					return cmd.onTabComplete(sender, command, alias, getArgsOnly(args));
				}
			}
		}
		
		return getTabList(args.length > 0 ? args[args.length - 1] : "");
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
			sender.sendMessage(CommandText.getHelp(this, sender));
			return true;
		}else{
			boolean result = false;
			if (isPlayer(sender)){
				result = command(getPlayer(sender), command, alias, args);
			}else{
				if (isInGameOnly()){
					sender.sendMessage(CommandText.getIngameOnly(this));
					result = true;
				}else{
					result = command(sender, command, alias, args);
				}
			}
			
			// send help text on false result
			if (!result) sender.sendMessage(CommandText.getHelp(this, sender));
			
			return true;
		}
	}

	/**
	 * Whether the given name is the name or an alias of this command.
	 * @param name A name of a command
	 * @return <code>true</code> if the given name does match this command.
	 */
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
			//TODO improve permission
			cmd.buildPermission();
			cmd.initCommandPermission();
			
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
	
	public String getHelp() {
		return help;
	}

	public String getUsage() {
		return usage;
	}

	public Permission getPermission(){
		return permission;
	}
	
	public String getPermissionNode(){
		return permission.getNode();
	}
	
	public String[] getTabList() {
		return tabList;
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

	public void setHelp(String help) {
		this.help = help;
	}
	
	public void setUsage(String usage) {
		this.usage = usage;
	}
		
	public void setPermission(String permission){
		setPermission(permission, PermissionDefault.FALSE);
	}
	
	public void setPermission(String permission, PermissionDefault defaultValue){
		setPermissionNode(permission, defaultValue);
		isNode = false;
	}
	
	public void setPermissionNode(String permissionNode){
		setPermissionNode(permissionNode, PermissionDefault.FALSE);
	}
	
	public void setPermissionNode(String permissionNode, PermissionDefault defaultValue){
		this.permission = new Permission(permissionNode, defaultValue);
		isNode = true;
	}
	
	public void setTabList(String...tabValue){
		tabList = tabValue;
	}
	
	public void setOp(boolean op) {
		this.op = op;
	}

	public boolean isOP(){
		return this.op;
	}
	
	public boolean isOpOnly() {
		return opOnly;
	}

	public void setOpOnly(boolean opOnly) {
		this.opOnly = opOnly;
		setOp(opOnly);
	}

	public boolean isOp() {
		return op;
	}

	public boolean isInGameOnly() {
		return inGameOnly;
	}

	public void setInGameOnly(boolean inGameOnly) {
		this.inGameOnly = inGameOnly;
	}

//	public boolean isexplicit() {
//		return explicit;
//	}
//
//	/**
//	 * Weather the command allows permission via wildcard permission of a parent command.
//	 * @param explicit
//	 */
//	public void setExplicit(boolean explicit) {
//		this.explicit = explicit;
//	}

	public Visibility getVisibility() {
		return visibility;
	}

	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
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
