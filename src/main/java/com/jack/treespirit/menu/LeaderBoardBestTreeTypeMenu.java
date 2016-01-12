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
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.jack.treespirit.Core;
import com.jack.treespirit.API.IconMenuAPI;
import com.jack.treespirit.ce.TreeChoose;
import com.jack.treespirit.data.ConfigLoader;
import com.jack.treespirit.filemanager.PlayerMap;
import com.jack.treespirit.languages.Languages;
import com.jack.treespirit.scoreboard.PlayerScoreboard;

public class LeaderBoardBestTreeTypeMenu {

	static int size = 36;

	public static IconMenuAPI createLeaderBoardBestTreeTypeMenu(final Player p) {
		return createLeaderBoardBestTreeTypeMenu(p, 1, 0);
	}
	
	public static String[] treeTypes = {"All","Oak","Spruce","Birch","Jungle","Acacia","Dark"};
	
	
	public static int giveNextTreeType(int actualTreeType){
		if(actualTreeType>=treeTypes.length-1){
			return 0;
		}
		else{
			return actualTreeType+1;
		}
	}
	
	public static int givePreviousTreeType(int actualTreeType){
		if(actualTreeType<=0){
			return treeTypes.length-1;
		}
		else{
			return actualTreeType-1;
		}
	}

	public static IconMenuAPI createLeaderBoardBestTreeTypeMenu(final Player p,
			final int page, final int treeType) {
		
		String myTree = "";

		File playerFile = Core.getInstance().getPlugindirDataTreeGroups();
		final File[] listOfFiles = playerFile.listFiles();

		Map<Integer, OfflinePlayer> rank = new TreeMap<Integer, OfflinePlayer>();
		for (File f : listOfFiles) {
			String treeName = FilenameUtils.removeExtension(f.getName());
			OfflinePlayer owner = Bukkit.getOfflinePlayer(treeName);
			
			YamlConfiguration ycf = YamlConfiguration.loadConfiguration(f);
			
			if(PlayerMap.getPlayerTreeType(owner).equalsIgnoreCase(treeTypes[treeType]) || treeTypes[treeType].equalsIgnoreCase("All")){
			
			
			int totalBlocks = 0;

			for (String name : ycf.getKeys(false)) {
				OfflinePlayer op = Bukkit.getOfflinePlayer(name);
				if(op.equals(p)){
					myTree = treeName;
				}
				List<String> blocks_of_uuid = Core.getKeysByValue(Core.hashmap,
						op.getUniqueId());
				totalBlocks+=blocks_of_uuid.size();
			}
			
			
			rank.put(new Integer(totalBlocks), owner);
			
			}
			
		}
		
		final String previous = treeTypes[givePreviousTreeType(treeType)];
		final String next = treeTypes[giveNextTreeType(treeType)];

		IconMenuAPI leaderboardMenu = new IconMenu(p,
				CreateMenu.findMiddle(ChatColor.DARK_PURPLE
						+ "    "+treeTypes[treeType]+" Tree's #"+(((page - 1) * 27)+1)+" - #"+((page * 27)+1)), size,
				new IconMenu.OptionClickEventHandler() {
					@Override
					public void onOptionClick(IconMenu.OptionClickEvent event) {

						switch (event.getName()) {
						case "Back":
							LeaderBoardMenu.createLeaderBoardMenu(p).open(p);
							event.setWillClose(false);
							break; 
						case "Next":
							createLeaderBoardBestTreeTypeMenu(p, page + 1, treeType)
									.open(p);
							event.setWillClose(false);
							break;
						case "Previous":
							createLeaderBoardBestTreeTypeMenu(p, page - 1, treeType)
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
						if(event.getName().equalsIgnoreCase(next)){
							createLeaderBoardBestTreeTypeMenu(p, 1, giveNextTreeType(treeType))
							.open(p);
							event.setWillClose(false);
						}
						else if(event.getName().equalsIgnoreCase(previous)){
							createLeaderBoardBestTreeTypeMenu(p, 1, givePreviousTreeType(treeType))
							.open(p);
							event.setWillClose(false);
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
		
		leaderboardMenu.setOption(size - 9, new ItemStack(Material.GOLD_INGOT), previous, "Previous");
		leaderboardMenu.setOption(size - 8, new ItemStack(Material.GOLD_INGOT), next, "Next");

		Integer[] numbers = new Integer[rank.size()];
		int c =1;
		for (Entry<Integer, OfflinePlayer> entry : rank.entrySet()) {
			numbers[numbers.length-c] = entry.getKey();
			c++;
		}

		for (int j = 0, i = (page - 1) * 27; i < (page * 27) - 1; i++) {
			if ((i < numbers.length)) {
				OfflinePlayer player = rank.get(numbers[i]);
				ItemStack sapling = TreeChoose.giveSaplingByString(PlayerMap.getPlayerTreeType(player),i+1);
				if(player.getName().equalsIgnoreCase(myTree)){
					sapling.addUnsafeEnchantment(Enchantment.THORNS, 1);
				}
				leaderboardMenu.setOption(j, sapling, player.getName(),
						""+numbers[i]);
				j++;
			}
		}

		return leaderboardMenu;
	}
	
	
	
	
	
	
	
}


