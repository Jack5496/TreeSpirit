package com.jack.treespirit.data;

import org.bukkit.ChatColor;

import com.jack.treespirit.API.TreeSpirit;

public class SignConfigurations {
	
	TreeSpirit core;	
	
	public SignConfigurations(TreeSpirit core) {
		this.core = core;
	}
	
	
	private static String tree_text = "Tree";
	
	public void set_tree_text(String s) {
		this.tree_text = s;
	}
	public String get_tree_text() {
		return this.tree_text;
	}

	
	private static String tree_player_text = "T Player";
	public void set_tree_player_text(String s) {
		this.tree_player_text = s;
	}
	public String get_tree_player_text() {
		return this.tree_player_text;
	}
	
	
	private static String to_root_text = "To Root";
	public void set_to_root_text(String s) {
		this.to_root_text = s;
	}
	public String get_to_root_text() {
		return this.to_root_text;
	}
	
	
	private static String roots_text = "Roots";
	public void set_roots_text(String s) {
		this.roots_text = s;
	}
	public String get_roots_text() {
		return this.roots_text;
	}
	
	
	
}
