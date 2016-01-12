package com.jack.treespirit.ce;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.jack.treespirit.Core;
import com.jack.treespirit.functions.OverChange;
import com.jack.treespirit.languages.Languages;

public class TreeChoose {
	public static boolean execute(CommandSender sender, String args) {
		
		Player p = (Player)sender;
		
		try {
			if (args != null) {
				if (args.equalsIgnoreCase("help")) {
					sendUsage(p);
					return true;
				}
				if (isTreeType(args)) {
					if(Core.getInstance().getTreeForPlayer(p.getUniqueId()).equalsIgnoreCase("None")){
						ItemStack back = giveSaplingByString(args);
						Bukkit.broadcastMessage("Test Message");
						if(back!=null){
							p.getInventory().addItem(back);
						}
						return true;
					}
					else{
						p.sendMessage(Languages.getYouMustFirstLeaveYourTree(p));
						return false;
					}
				}
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			p.sendMessage(Languages.getUsage(p)+" /tree import help");
		}
		return true;
	}
	
	public static String getTreeType(Block b) {
		return getTreeType(b.getData());
	}

	public static String getTreeType(short s) {
		if (s == 0)
			return "Oak";
		if (s == 1)
			return "Spruce";
		if (s == 2)
			return "Birch";
		if (s == 3)
			return "Jungle";
		if (s == 4)
			return "Acacia";
		if (s == 5)
			return "Dark";
		return "None";
	}
	
	public static ItemStack giveSaplingByString(String s){
		ItemStack oaksapling, sprucesapling, birchsapling, junglesapling, acaciasapling, darksapling;
		oaksapling = new ItemStack(Material.SAPLING, 1);
		sprucesapling = new ItemStack(Material.SAPLING, 1);
		birchsapling = new ItemStack(Material.SAPLING, 1);
		junglesapling = new ItemStack(Material.SAPLING, 1);
		acaciasapling = new ItemStack(Material.SAPLING, 1);
		darksapling = new ItemStack(Material.SAPLING, 1);

		sprucesapling.setDurability((short) 1);

		birchsapling.setDurability((short) 2);

		junglesapling.setDurability((short) 3);

		acaciasapling.setDurability((short) 4);

		darksapling.setDurability((short) 5);
		
		if(s.equalsIgnoreCase("Oak")) return oaksapling;
		if(s.equalsIgnoreCase("Spruce")) return sprucesapling;
		if(s.equalsIgnoreCase("Birch")) return birchsapling;
		if(s.equalsIgnoreCase("Jungle")) return junglesapling;
		if(s.equalsIgnoreCase("Acacia")) return acaciasapling;
		if(s.equalsIgnoreCase("Dark")) return darksapling;
		return null;
		
	}
	
	public static ItemStack giveSaplingByString(String s, int i){
		ItemStack oaksapling, sprucesapling, birchsapling, junglesapling, acaciasapling, darksapling;
		oaksapling = new ItemStack(Material.SAPLING, i);
		sprucesapling = new ItemStack(Material.SAPLING, i);
		birchsapling = new ItemStack(Material.SAPLING, i);
		junglesapling = new ItemStack(Material.SAPLING, i);
		acaciasapling = new ItemStack(Material.SAPLING, i);
		darksapling = new ItemStack(Material.SAPLING, i);

		sprucesapling.setDurability((short) 1);

		birchsapling.setDurability((short) 2);

		junglesapling.setDurability((short) 3);

		acaciasapling.setDurability((short) 4);

		darksapling.setDurability((short) 5);
		
		if(s.equalsIgnoreCase("Oak")) return oaksapling;
		if(s.equalsIgnoreCase("Spruce")) return sprucesapling;
		if(s.equalsIgnoreCase("Birch")) return birchsapling;
		if(s.equalsIgnoreCase("Jungle")) return junglesapling;
		if(s.equalsIgnoreCase("Acacia")) return acaciasapling;
		if(s.equalsIgnoreCase("Dark")) return darksapling;
		return null;
		
	}
	
	
	
	public static boolean isTreeType(String s){
		if(s.equalsIgnoreCase("Oak")) return true;
		if(s.equalsIgnoreCase("Spruce")) return true;
		if(s.equalsIgnoreCase("Birch")) return true;
		if(s.equalsIgnoreCase("Jungle")) return true;
		if(s.equalsIgnoreCase("Acacia")) return true;
		if(s.equalsIgnoreCase("Dark")) return true;
		return false;
	}

	public static String getStringFromLoc(Location l) {
		return "" + (int) l.getX() + " | " + (int) l.getY() + " | "
				+ (int) l.getZ() + " | ";
	}

	public static void sendUsage(Player p) {
		String blank = "" + ChatColor.GOLD + "  ||  " + ChatColor.WHITE + "";

		p.sendMessage(ChatColor.GREEN + " - /tree choose" + blank
				+ "Oak");
		p.sendMessage(ChatColor.GREEN + " - /tree choose" + blank
				+ "Spruce");
		p.sendMessage(ChatColor.GREEN + " - /tree choose" + blank
				+ "Birch");
		p.sendMessage(ChatColor.GREEN + " - /tree choose" + blank
				+ "Jungle");
		p.sendMessage(ChatColor.GREEN + " - /tree choose" + blank
				+ "Acacia");
		p.sendMessage(ChatColor.GREEN + " - /tree choose" + blank
				+ "Dark");
	}
}
