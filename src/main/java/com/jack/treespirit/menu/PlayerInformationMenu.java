package com.jack.treespirit.menu;

import java.lang.reflect.Field;

import net.minecraft.server.v1_7_R4.Block;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.jack.treespirit.Core;
import com.jack.treespirit.API.IconMenuAPI;
import com.jack.treespirit.filemanager.PlayerMap;

public class PlayerInformationMenu {
	public static IconMenuAPI createPlayerInformationMenu(final Player p) {
		IconMenuAPI playerinformationmenu = new IconMenu(p,CreateMenu.findMiddle(ChatColor.DARK_PURPLE
				+ "    "+p.getName()), 9, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(IconMenu.OptionClickEvent event) {
				
				Player clicker = event.getPlayer();

				switch (event.getName()) {
				case "Back":
					CreateMenu.getCreateMenuInstance().getMainMenu(clicker).open(clicker);
					event.setWillClose(false);
					break;
				case "Tree Type":
					String treeType = Core.getInstance().getTreeForPlayer(p.getUniqueId());
					if(treeType.equalsIgnoreCase("None")){
						if(clicker.equals(p)){
						CreateMenu.getCreateMenuInstance().createTreeChooseMenu(clicker).open(clicker);
						}
						else{
							Bukkit.broadcastMessage("You cant edit others tree Tpye");
						}
					}
					else{
						Bukkit.broadcastMessage("This will be updated for more Information");
					}
					event.setWillClose(false);
					break;
				case "Tree Group":
					TreeGroupMenu.createTreeGroupMenu(clicker, Core.getInstance().getRepresentant(p.getUniqueId())).open(clicker);
					event.setWillClose(false);
					break;
				case "Languages":
					CreateMenu.getCreateMenuInstance().createLanguageMenu(clicker).open(clicker);
					event.setWillClose(false);
					break;
				default:
					event.setWillClose(false);
					break;
				}
			}
		}).setOption(8,
				new ItemStack(Material.REDSTONE_BLOCK), "Back", "to Main Menu")
		  .setOption(6, new ItemStack(Material.ENCHANTED_BOOK), "Languages", PlayerMap.getPlayerLanguage(p));
		
		playerinformationmenu.setOption(1, new ItemStack(Material.SAPLING), "Tree Type",
						Core.getInstance().getTreeForPlayer(p.getUniqueId()));
		
		int amount_of_player = 1;
		amount_of_player = Core.getKeysByValue(Core.hashmap, p.getUniqueId())
				.size();
		
		playerinformationmenu.setOption(4, new ItemStack(Material.LOG), "Placed Blocks", ""+amount_of_player);
		
		
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		
		playerinformationmenu.setOption(3, skull, "Tree Group",
						Core.getInstance().getRepresentant(p.getUniqueId()));
		
		playerinformationmenu.setOption(5, new ItemStack(Material.WATCH), "First Join",
						PlayerMap.getFirstJoin(p));
		return playerinformationmenu;
	}

	
	
	/**
	 * Information Menu for Other PLayers
	 * @param ofp	OfflinePlayer (which can be Online), whos information should be shown
	 * @param p	Player you want to take a look at others
	 * @return IconMenuAPI
	 */
	public static IconMenuAPI createPlayerInformationMenu(final OfflinePlayer ofp, Player p) {
		IconMenuAPI playerinformationmenu = new IconMenu(p,CreateMenu.findMiddle(ChatColor.DARK_PURPLE
				+ "    "+ofp.getName()), 9, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(IconMenu.OptionClickEvent event) {
				
				Player clicker = event.getPlayer();

				switch (event.getName()) {
				case "Back":
					CreateMenu.getCreateMenuInstance().getMainMenu(clicker).open(clicker);
					event.setWillClose(false);
					break;
				case "Tree Type":
					String treeType = Core.getInstance().getTreeForPlayer(ofp.getUniqueId());
					if(treeType.equalsIgnoreCase("None")){
						if(clicker.equals(ofp)){
						CreateMenu.getCreateMenuInstance().createTreeChooseMenu(clicker).open(clicker);
						}
						else{
							Bukkit.broadcastMessage("You cant edit others tree Tpye");
						}
					}
					else{
						Bukkit.broadcastMessage("This will be updated for more Information");
					}
					event.setWillClose(false);
					break;
				case "Tree Group":
					String treeGroup = PlayerMap.getPlayerTreeGroup(ofp);
					TreeGroupMenu.createTreeGroupMenu(clicker, treeGroup).open(clicker);
					event.setWillClose(false);
					break;
				default:
					event.setWillClose(false);
					break;
				}
			}
		}).setOption(8,
				new ItemStack(Material.REDSTONE_BLOCK), "Back", "to Main Menu")
		  .setOption(6, new ItemStack(Material.ENCHANTED_BOOK), "Languages", PlayerMap.getPlayerLanguage(p));
		
		playerinformationmenu.setOption(1, new ItemStack(Material.SAPLING), "Tree Type",
						Core.getInstance().getTreeForPlayer(ofp.getUniqueId()));
		
		int amount_of_player = 1;
		amount_of_player = Core.getKeysByValue(Core.hashmap, ofp.getUniqueId())
				.size();
		
		playerinformationmenu.setOption(4, new ItemStack(Material.LOG), "Placed Blocks", ""+amount_of_player);
		
		
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		
		playerinformationmenu.setOption(3, skull, "Tree Group",
						Core.getInstance().getRepresentant(p.getUniqueId()));
		
		playerinformationmenu.setOption(5, new ItemStack(Material.WATCH), "First Join",
						PlayerMap.getFirstJoin(p));
		return playerinformationmenu;
	}
}
