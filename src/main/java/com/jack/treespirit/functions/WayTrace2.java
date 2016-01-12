package com.jack.treespirit.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import com.jack.treespirit.Core;
import com.jack.treespirit.knots.Knot;
import com.jack.treespirit.threads.DamageController;

public class WayTrace2 {
	
	public static boolean connectionToRoot(Location start) {
//		Bukkit.broadcastMessage("Start WayTrace 2.0");
//		Bukkit.broadcastMessage("");
		Object[] obj = Knot.findNextKnot(start, Core.getInstance()
				.getUUIDFromHashMap(start));
		if (obj == null){
			return false;
		}
		
		Knot k = Core.getInstance().getKnot((Location) obj[0]);
		
		return k.isConnectedToRoot();
		
	}
	
	public static boolean connectionToRootWithLeaves(Location start) {
//		Bukkit.broadcastMessage("Start WayTrace 2.0");
//		Bukkit.broadcastMessage("");
		Object[] obj = Knot.findNextKnotWithLeaves(start, Core.getInstance()
				.getUUIDFromHashMap(start));
		if (obj == null){
			return false;
		}
		
		Knot k = Core.getInstance().getKnot((Location) obj[0]);
		
		return k.isConnectedToRoot();
		
	}

}