package com.jack.treespirit.ce;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jack.treespirit.Core;
import com.jack.treespirit.filemanager.CustomTree;
import com.jack.treespirit.functions.OverChange;
import com.jack.treespirit.functions.SaveNewTree;

public class NewTreeImport {
	public static void execute(CommandSender sender, Command cmd, String s,
			String[] args) {
		try {
			if (args[1] != null) {
				if (args[1].equalsIgnoreCase("help")) {
					sender.sendMessage(ChatColor.GOLD + "[TreeSpirit] "
							+ ChatColor.RED
							+ "Usage: /tree newTree save <Name> <Max Blocks>");
				}
				if (args[1].equalsIgnoreCase("begin")) {
					Location player_loc = ((Player) sender).getLocation();
					TreeComands.setNTBegin(player_loc);
					((Player) sender).sendMessage("Begin Position : "
							+ getStringFromLoc(player_loc));
				}
				if (args[1].equalsIgnoreCase("reload")) {
					if (TreeComands.getNTBegin() != null) {
						if (args[2] != null) {
							CustomTree.spawnTree(TreeComands.getNTBegin(),
									args[2], (Player)sender);
						}
					}
				}
				if (args[1].equalsIgnoreCase("save")) {
					try {
						String save_name = args[2];
						if (Core.getInstance().getConfigurations()
								.getDebug_Mode()) {
							Bukkit.broadcastMessage("Try to save as "
									+ save_name);
						}
						if (TreeComands.getNTBegin() != null) {
							if (Core.getInstance().getConfigurations()
									.getDebug_Mode()) {
								Bukkit.broadcastMessage("End Loc not null");
							}
							SaveNewTree oc = new SaveNewTree(save_name,
									TreeComands.getNTBegin().clone(),
									Integer.parseInt(args[3]), (Player) sender);
							Thread t = new Thread(oc);
							if (Core.getInstance().getConfigurations()
									.getDebug_Mode()) {
								Bukkit.broadcastMessage("Tread Started");
							}
							t.start();

							TreeComands.setNTBegin(null);
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						sender.sendMessage(ChatColor.GOLD
								+ "[TreeSpirit] "
								+ ChatColor.RED
								+ "Usage: /tree newTree save <Name> <Max Blocks>");
					}
				}
			} else {

			}

		} catch (ArrayIndexOutOfBoundsException e) {
			sender.sendMessage(ChatColor.GOLD + "[TreeSpirit] " + ChatColor.RED
					+ "Usage: /tree newTree save <Name> <Max Blocks");
		}

	}

	public static String getStringFromLoc(Location l) {
		return "" + (int) l.getX() + " | " + (int) l.getY() + " | "
				+ (int) l.getZ() + " | ";
	}

}
