package com.jack.treespirit.menu;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.jack.treespirit.Core;
import com.jack.treespirit.API.IconMenuAPI;
import com.jack.treespirit.ce.TreeChoose;
import com.jack.treespirit.ce.TreeComands;
import com.jack.treespirit.data.ConfigLoader;

public class TreeChooseMenu {
	

	static ItemStack oaksapling = new ItemStack(Material.SAPLING, 1);
	static ItemStack sprucesapling = new ItemStack(Material.SAPLING, 1);
	static ItemStack birchsapling = new ItemStack(Material.SAPLING, 1);
	static ItemStack junglesapling = new ItemStack(Material.SAPLING, 1);
	static ItemStack acaciasapling = new ItemStack(Material.SAPLING, 1);
	static ItemStack darksapling = new ItemStack(Material.SAPLING, 1);

	public static void initItemStacks(){
		sprucesapling.setDurability((short) 1);
		birchsapling.setDurability((short) 2);
		junglesapling.setDurability((short) 3);
		acaciasapling.setDurability((short) 4);
		darksapling.setDurability((short) 5);
	}

	
	public static IconMenuAPI createTreeChooseMenu(Player p,File f) {
		
		IconMenuAPI treeChooseMenu = new IconMenu(p,
				CreateMenu.findMiddle(ChatColor.DARK_PURPLE
						+ "   Tree Choose Menu"), 18,
				new IconMenu.OptionClickEventHandler() {
					@Override
					public void onOptionClick(IconMenu.OptionClickEvent event) {

						Player p = event.getPlayer();
						UUID uuid = p.getUniqueId();

						switch (event.getName()) {
						case "Back":
							CreateMenu.getCreateMenuInstance().getMainMenu(p).open(p);
							event.setWillClose(false);
							break;
						case "Oak":
						case "Spruce":
						case "Birch":
						case "Jungle":
						case "Acacia":
						case "Dark":
							boolean close = TreeChoose.execute(p, event.getName());
							if(close){
								event.setWillClose(false);
							}
							else{
								// HIER KOMMT NOCH EINE VERLINKUNG OB MAN SEINEN BAUM VERLASSEN WILL
								Bukkit.broadcastMessage("Updating later, want to Change tree?, leave first");
								event.setWillClose(false);
							}
							break;
						case "None":
							// DIESE BEFEHL wurde
							// Core.getInstance().changeTreeTypeForPlayerMap(
							// event.getPlayer().getUniqueId(), "None");
							TreeComands.leaveGuild(p);
							break;
						default:
							event.setWillClose(false);
							break;
						}

						if (Core.getInstance().getConfigurations()
								.getDebug_Mode()) {
							Bukkit.broadcastMessage("Players Fraction: "
									+ Core.getInstance().getTreeForPlayer(uuid));
						}
					}
				}).setOption(17, new ItemStack(Material.REDSTONE_BLOCK),
				"Back", "Bring me to Main"); // <--
												// ";"
												// ACHTUNG

		treeChooseMenu.setOption(0, oaksapling, "Oak", "Oak is strong");
		treeChooseMenu.setOption(1, sprucesapling, "Spruce", "Spruce is nice");
		treeChooseMenu.setOption(2, birchsapling, "Birch", "Birch is healty");
		treeChooseMenu.setOption(3, junglesapling, "Jungle", "Heavy Controll");
		treeChooseMenu.setOption(4, acaciasapling, "Acacia", "Acacia is fancy");
		treeChooseMenu.setOption(5, darksapling, "Dark",
				"Come to the DarkSide!");
		treeChooseMenu.setOption(13, new ItemStack(Material.DEAD_BUSH, 1),
				"None", "Leave your Tree!");

		return treeChooseMenu;
	}
}
