package com.jack.treespirit.ce;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jack.treespirit.Core;
import com.jack.treespirit.functions.OverChange;

public class ImportBlocks {

	public static void execute(CommandSender sender, Command cmd, String s,
			String[] args) {
		try{
		if(args[1] != null) {
			if(args[1].equalsIgnoreCase("help")) {
				sendUsage((Player) sender);
			}
			if(args[1].equalsIgnoreCase("start")) {
				Location player_loc = ((Player)sender).getLocation();
				TreeComands.setStartLoc(player_loc);
				((Player)sender).sendMessage("Start Position : "+getStringFromLoc(player_loc));
			}
			if(args[1].equalsIgnoreCase("end")) {
				Location player_loc = ((Player)sender).getLocation();
				TreeComands.setEndLoc(player_loc);
				((Player)sender).sendMessage("End Position : "+getStringFromLoc(player_loc));
			}
			if(args[1].equalsIgnoreCase("add")) {
				try{
					String player_name = args[2];
					if(Core.getInstance().getConfigurations().getDebug_Mode()){
						sender.sendMessage("Try to add for "+player_name);
					}
					if(TreeComands.getStartLoc() != null){
						if(Core.getInstance().getConfigurations().getDebug_Mode()){
							sender.sendMessage("First Loc not null");
						}
						if(TreeComands.getStartLoc() != null){
							if(Core.getInstance().getConfigurations().getDebug_Mode()){
								sender.sendMessage("End Loc not null");
							}
							OverChange oc = new OverChange(player_name, TreeComands.getStartLoc().clone(), TreeComands.getEndLoc().clone(), (Player)sender);
							if(Core.getInstance().getConfigurations().getDebug_Mode()){
								sender.sendMessage("Overchange Created");
							}
							Thread t = new Thread(oc);
							if(Core.getInstance().getConfigurations().getDebug_Mode()){
								sender.sendMessage("Tread Started");
							}
							t.start();
							
							TreeComands.setStartLoc(null);
							TreeComands.setEndLoc(null);
						}
					}
				}
				catch(ArrayIndexOutOfBoundsException e){
					sender.sendMessage(ChatColor.GOLD+"[TreeSpirit] " +ChatColor.RED+"Usage: /tree import add <Player>");
				}
			}
		}
		else{
			
		}
		
		}
		catch(ArrayIndexOutOfBoundsException e){
			sender.sendMessage(ChatColor.GOLD+"[TreeSpirit] " +ChatColor.RED+"Usage: /tree import help");
		}
		
		
		
	}
	

	
	public static String getStringFromLoc(Location l){
		return ""+(int)l.getX()+" | "+(int)l.getY()+" | "+(int)l.getZ()+" | ";
	}
	
	public static void sendUsage(Player p){
		String blank = ""+ChatColor.GOLD+"  ||  "+ChatColor.WHITE+"";
		
		p.sendMessage(ChatColor.GOLD+"[TreeSpirit] " +ChatColor.RED+"Import");
		p.sendMessage(ChatColor.GREEN+" - /tree import help"+blank+"Shows Import Help Menü");
		p.sendMessage(ChatColor.GREEN+" - /tree import start"+blank+"Set's the Start Location Corner");
		p.sendMessage(ChatColor.GREEN+" - /tree import end"+blank+"Set's the End Location Corner");
		p.sendMessage(ChatColor.GREEN+" - /tree import add {Player}"+blank+"Imports all Blocks within the Area");
	}
	
	
	
}
