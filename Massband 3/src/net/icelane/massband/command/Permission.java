package net.icelane.massband.command;

import org.bukkit.permissions.PermissionDefault;

public class Permission {

	public static final String Asterisk = "*";
	
	private Permission parent = null;
	private String node = "";
	private PermissionDefault defaultValue;
	
	public Permission(String node, PermissionDefault defaultValue){
		this.node = node.trim();
		this.defaultValue = defaultValue;		
	}
	
	public Permission(Permission permission){
		this(permission.node, permission.defaultValue);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Permission) {
			return ((Permission) obj).getFullPermission().equals(getFullPermission());
		}
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return getFullPermission();
	}
	
	public Permission getParent() {
		return parent;
	}
	
	public void setParent(Permission parent) {
		this.parent = parent;
	}
	
	public String getNode() {
		return node;
	}
	
	public PermissionDefault getDefaultValue() {
		return defaultValue;
	}

	public org.bukkit.permissions.Permission getPermission(){
		return new org.bukkit.permissions.Permission(getFullPermission(), defaultValue);
	}
	
	public String getFullPermission() {
		if (parent == null) return node;
		return String.format("%s.%s", parent.getFullPermission(), node);
	}
}
