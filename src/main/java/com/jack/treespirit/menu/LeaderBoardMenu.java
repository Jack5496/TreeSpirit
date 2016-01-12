package com.jack.treespirit.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.jack.treespirit.Core;
import com.jack.treespirit.API.IconMenuAPI;
import com.jack.treespirit.data.ConfigLoader;

public class LeaderBoardMenu {
	
	static ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
	
	public static IconMenuAPI createLeaderBoardMenu(final Player p) {
		IconMenuAPI leaderboardMenu = new IconMenu(p,CreateMenu.findMiddle(ChatColor.DARK_PURPLE
				+ "    Leader Board"), 9, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(IconMenu.OptionClickEvent event) {

				switch (event.getName()) {
				case "Back":
					CreateMenu.getCreateMenuInstance().getMainMenu(p).open(p);
					event.setWillClose(false);
					break;
				case "Best Player's":
					LeaderBoardBestPlayerMenu.createLeaderBoardBestPlayerMenu(p).open(p);
					event.setWillClose(false);
					break;
				case "Best Tree's":
					LeaderBoardBestTreeTypeMenu.createLeaderBoardBestTreeTypeMenu(p).open(p);
					event.setWillClose(false);
					break;
				default:
					event.setWillClose(false);
					break;
				}
			}
		}).setOption(8,
				new ItemStack(Material.REDSTONE_BLOCK), "Back", "to Main Menu")
				.setOption(1, new ItemStack(skull), "Best Player's",
						"Show's best Players on Server")
				.setOption(3, new ItemStack(Material.SAPLING), "Best Tree's",
						"Show's best Tree's on Server"); // <-- ";" ACHTUNG
		return leaderboardMenu;
	}
}
