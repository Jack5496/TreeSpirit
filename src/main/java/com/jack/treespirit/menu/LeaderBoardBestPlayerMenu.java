package com.jack.treespirit.menu;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import net.minecraft.util.org.apache.commons.io.FilenameUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.jack.treespirit.Core;
import com.jack.treespirit.API.IconMenuAPI;
import com.jack.treespirit.data.ConfigLoader;
import com.jack.treespirit.filemanager.PlayerMap;
import com.jack.treespirit.languages.Languages;
import com.jack.treespirit.scoreboard.PlayerScoreboard;

public class LeaderBoardBestPlayerMenu {

	static ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1,
			(short) SkullType.PLAYER.ordinal());

	static int size = 36;

	public static IconMenuAPI createLeaderBoardBestPlayerMenu(final Player p) {
		return createLeaderBoardBestPlayerMenu(p, 1);
	}

	public static IconMenuAPI createLeaderBoardBestPlayerMenu(final Player p,
			final int page) {

		File playerFile = Core.getInstance().getPlugindirDataPlayers();
		final File[] listOfFiles = playerFile.listFiles();

		Map<Integer, OfflinePlayer> rank = new TreeMap<Integer, OfflinePlayer>();
		for (File f : listOfFiles) {
			// ADDING LATER WHEN FILE MANAGEMENT IS FINISHED
			// String blocks = PlayerMap.getPlayerTotalBlocks(f);
			// Integer amount = Integer.parseInt(blocks);

			OfflinePlayer op = PlayerMap.getPlayerFromFile(f);

			List<String> blocks_of_uuid = Core.getKeysByValue(Core.hashmap,
					op.getUniqueId());
			rank.put(new Integer(blocks_of_uuid.size()), op);
		}

		IconMenuAPI leaderboardMenu = new IconMenu(p,
				CreateMenu.findMiddle(ChatColor.DARK_PURPLE
						+ "    Best Player's #"+(((page - 1) * 27)+1)+" - #"+((page * 27)+1)), size,
				new IconMenu.OptionClickEventHandler() {
					@Override
					public void onOptionClick(IconMenu.OptionClickEvent event) {

						switch (event.getName()) {
						case "Back":
							LeaderBoardMenu.createLeaderBoardMenu(p).open(p);
							event.setWillClose(false);
							break;
						case "Next":
							createLeaderBoardBestPlayerMenu(p, page + 1)
									.open(p);
							event.setWillClose(false);
							break;
						case "Previous":
							createLeaderBoardBestPlayerMenu(p, page - 1)
									.open(p);
							event.setWillClose(false);
							break;
						default:
							event.setWillClose(false);
							break;
						}
						for (File f : listOfFiles) {
							String fileName = FilenameUtils.removeExtension(f
									.getName());
							if (event.getName().equalsIgnoreCase(fileName)) {
								PlayerInformationMenu
										.createPlayerInformationMenu(
												PlayerMap
														.getPlayerFromFile(f),
												p).open(p);
							}
						}
					}
				}).setOption(size - 1, new ItemStack(Material.REDSTONE_BLOCK),
				"Back", "to Leader Board Menu"); // <-- ";" ACHTUNG

		if (listOfFiles.length > 27 * page) {
			leaderboardMenu.setOption(size - 4, new ItemStack(Material.DIAMOND,
					page + 1), "Next", "");
		}
		if (page > 1) {
			leaderboardMenu.setOption(size - 6, new ItemStack(Material.DIAMOND,
					page - 1), "Previous", "");
		}

		Integer[] numbers = new Integer[rank.size()];
		int c =1;
		for (Entry<Integer, OfflinePlayer> entry : rank.entrySet()) {
			numbers[numbers.length-c] = entry.getKey();
			c++;
		}

		for (int j = 0, i = (page - 1) * 27; i < (page * 27) - 1; i++) {
			if ((i < numbers.length)) {
				OfflinePlayer player = rank.get(numbers[i]);
		
				ItemStack skullmenu = new ItemStack(Material.SKULL_ITEM, i+1,
						(short) SkullType.PLAYER.ordinal());
				if(player.equals(p)){
					skullmenu.addUnsafeEnchantment(Enchantment.THORNS, 1);
				}
				leaderboardMenu.setOption(j, skullmenu, player.getName(),
						""+numbers[i]);
				j++;
			}
		}

		return leaderboardMenu;
	}
}
