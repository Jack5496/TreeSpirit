package com.jack.treespirit.threads;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.jack.treespirit.Core;
import com.jack.treespirit.events.Events;
import com.jack.treespirit.filemanager.CustomTree;

public class TreeGrowController {

	/**
	 * Generate a custom Tree which is predefined, and choosen by PlayerGroup
	 * @param l Location where Tree/Sapling will grow
	 * @param p Player for which Tree should grow
	 */
	public static void GenerateStartTree(Location l, Player p) {
		p.getItemInHand().setTypeId(0);
		p.updateInventory();

		Location bloc = new Location(l.getWorld(),l.getBlockX(),l.getBlockY()+1,l.getBlockZ());
		String name = Core.getInstance().getTreeForPlayer(p.getUniqueId());		

		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("Try to add To RootMap");
		}
		Core.getInstance().addToRootMap(l, p);
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("Added to RootMap");
		}
		
		CustomTree.spawnTree(bloc, name, p);
		
		p.getWorld().getBlockAt(l).setType(Material.GLOWSTONE);
		Core.getInstance().addToHashMap(l, p);

	}
}
