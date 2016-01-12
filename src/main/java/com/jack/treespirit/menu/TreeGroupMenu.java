package com.jack.treespirit.menu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.minecraft.util.org.apache.commons.io.FilenameUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.jack.treespirit.Core;
import com.jack.treespirit.API.IconMenuAPI;
import com.jack.treespirit.filemanager.PlayerMap;
import com.jack.treespirit.languages.Languages;

public class TreeGroupMenu {
	
	public static IconMenuAPI createTreeGroupMenu(Player p, String treeGroup) {
		return createTreeGroupMenuPage(1,p, treeGroup);
	}

	private static IconMenuAPI createTreeGroupMenuPage(final int page, Player p, final String treeGroup) {

		int size = 36;
		File treeFile = new File(Core.getInstance().getPlugindirDataTreeGroups()+"/"+treeGroup+".yml");
		YamlConfiguration ycf = YamlConfiguration.loadConfiguration(treeFile);
		Set<String> members = ycf.getKeys(false);
		final Object[] memberslist = members.toArray();

		IconMenuAPI treeGroupmenu = new IconMenu(p,
				CreateMenu.findMiddle(ChatColor.DARK_PURPLE
						+ "    Tree "+treeGroup+" - " + page + " -"), size,
				new IconMenu.OptionClickEventHandler() {
					@Override
					public void onOptionClick(IconMenu.OptionClickEvent event) {

						Player clicker = event.getPlayer();

						switch (event.getName()) {
						case "Back":
							PlayerInformationMenu
									.createPlayerInformationMenu(clicker).open(clicker);
							event.setWillClose(false);
							break;
						case "Next":
							createTreeGroupMenuPage(page+1,clicker,treeGroup).open(clicker);
							event.setWillClose(false);
							break;
						case "Previous":
							createTreeGroupMenuPage(page-1,clicker,treeGroup).open(clicker);
							event.setWillClose(false);
							break;
						default:
							event.setWillClose(false);
							break;
						}
						for (Object name : memberslist) {
							if (event.getName().equalsIgnoreCase(name.toString())) {			
								OfflinePlayer ofp = Bukkit.getOfflinePlayer(name.toString());	//Always open as an OfflinePlayer, different menu's
								PlayerInformationMenu.createPlayerInformationMenu(ofp, clicker).open(clicker);
								event.setWillClose(false);
							}
						}

					}
				}).setOption(size - 1, new ItemStack(Material.REDSTONE_BLOCK),
				"Back", "to Main Menu");
		
				int onlineamount = 0;
		
				for (Object name : memberslist) {							
					OfflinePlayer ofp = Bukkit.getOfflinePlayer(name.toString());
					Player onp = ofp.getPlayer();
					if(onp!=null){
						onlineamount++;
					}
				}
		
				treeGroupmenu.setOption(size-9, new ItemStack(Material.TORCH,1), "Online", ""+onlineamount);
				treeGroupmenu.setOption(size-8, new ItemStack(Material.REDSTONE_TORCH_ON,1), "Offline", ""+(memberslist.length-onlineamount));

				

				List<UUID> tree_members = new ArrayList<UUID>();
				tree_members = Core.getKeysByValueForPlayerMap(Core.playermap, treeGroup);

				int total_amount_of_blocks = 0;

				for (UUID uuid : tree_members) {
					List<String> blocks_of_uuid = Core.getKeysByValue(Core.hashmap,
							uuid);
					total_amount_of_blocks += blocks_of_uuid.size();
				}
				
				treeGroupmenu.setOption(size-2, new ItemStack(Material.LOG,1), "Placed Blocks", ""+total_amount_of_blocks);
		
		if (members.size() > 27 * page) {
			treeGroupmenu.setOption(size - 4, new ItemStack(
					Material.DIAMOND, page + 1), "Next", "");
		}
		if (page > 1) {
			treeGroupmenu.setOption(size - 6, new ItemStack(
					Material.DIAMOND, page - 1), "Previous", "");
		}
		
		
		for (int j=0, i = (page - 1)*page; i < (page * 27) - 1; i++) {
			if ((i < members.size())) {
				
				ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setDisplayName(ChatColor.GREEN + memberslist[i].toString());
                if(memberslist[i].toString().equalsIgnoreCase(p.getName())){
                	skull.addUnsafeEnchantment(Enchantment.THORNS, 1);
                }
				
				treeGroupmenu.setOption(j, skull, memberslist[i].toString(), ycf.getString(memberslist[i].toString()));
				j++;
			}
		}

		return treeGroupmenu;
	}
}
