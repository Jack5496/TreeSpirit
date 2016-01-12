package com.jack.treespirit.threads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;

import com.jack.treespirit.Core;
import com.jack.treespirit.functions.LocationSerialize;
//import com.jack.treespirit.functions.WayTrace;
import com.jack.treespirit.functions.WayTrace2;
import com.jack.treespirit.signs.Signs;

public class RootsGarbageCollector implements Runnable {

	private static int seconds = 1;
	private static int delay = seconds * 1000;

	private boolean activ = true;

	public static List<String> rootsgarbagemap;

	public RootsGarbageCollector() {
		updateMap();
	}

	@Override
	public void run() {
		while (activ) {

			List<String> start_check_locs = new ArrayList<String>();

			for (String entry : rootsgarbagemap) {
				if (!start_check_locs.contains(entry)) {
					start_check_locs.add(entry);
				}
			}

			for (String start_loc : start_check_locs) {
				Location start_location = LocationSerialize
						.serializeToLocation(start_loc);
				if (Core.getInstance().getConfigurations()
						.getDebug_Mode()) {
				 Bukkit.broadcastMessage("Prüfe Location auf Verbindung zum Stamm: "+start_location.getX()+" | "+start_location.getY()+" | "+start_location.getZ()+" | ");
				}
				 UUID owner_uuid_of_block = Core.getInstance()
						.getUUIDFromHashMap(start_location);
				// Bukkit.broadcastMessage("Owner_UUID: "+owner_uuid_of_block);
				String name_of_representant = Core.getInstance()
						.getRepresentant(owner_uuid_of_block);
				// Bukkit.broadcastMessage("Owner Representant: "+name_of_representant);
				try {
					UUID owner_uuid = Bukkit.getOfflinePlayer(
							name_of_representant).getUniqueId();
					Location root = Core.getInstance().getLocFromRootMap(
							owner_uuid);
					// boolean found = WayTrace.findWayPath(start_location,
					// root, Material.LOG)!=null;
					boolean found = false;
					if (start_location.getBlock().getType() == Material.LOG
							|| start_location.getBlock().getType() == Material.LOG_2) {
						found = WayTrace2.connectionToRoot(start_location);
					} else {
						found = WayTrace2
								.connectionToRootWithLeaves(start_location);
					}
					if (Core.getInstance().getConfigurations()
							.getDebug_Mode()) {
					 Bukkit.broadcastMessage("Weg gefunden: "+found);
					}
					if (found) {
						Core.getInstance().removeFromRootsGarbageMap(
								start_location);
					} else {
						List<Location> loc_list = getNeighbors(start_location);
						for (Location s : loc_list) {
							// Location l =
							// LocationSerialize.serializeToLocation(s);
							UUID temp_uuid = Core.getInstance()
									.getUUIDFromHashMap(s);
							// Bukkit.broadcastMessage("Temp_UUID: "+temp_uuid);
							String temp_name = Core.getInstance()
									.getRepresentant(temp_uuid);
							// Bukkit.broadcastMessage("Temp Representant: "+temp_name);
							if (temp_name != null) {
								if (!temp_name
										.equalsIgnoreCase(name_of_representant)) {
									// Core.getInstance().removeFromRootsGarbageMap(l);
								} else {
									Core.getInstance().addToRootsGarbageMap(s);
									// Bukkit.broadcastMessage("Einen Nachbarn gefunden");
								}
							} else {
								// Core.getInstance().removeFromRootsGarbageMap(l);
							}
						}
						Core.getInstance().removeFromHashMap(start_location);
						Core.getInstance().removeFromKnotMap(start_location);
						Core.getInstance().removeFromRootsGarbageMap(
								start_location);

						Block start_block = start_location.getBlock();
						Material start_mat = start_block.getType();

						if (start_mat == Material.SAPLING) {
							start_block.setType(Material.AIR);
							if (Core.getInstance().getConfigurations()
									.getDebug_Mode()) {
								Bukkit.broadcastMessage("Sapling gammelte ab");
							}
						}
						if (start_mat == Material.LOG
								|| start_mat == Material.LOG_2) {
							byte data = start_location.getBlock().getData();
							data = (byte) (data + (byte) 3);
							start_location.getBlock().setData(data);
							try{
								start_block.setType(Material.AIR);
								FallingBlock b = start_location.getWorld()
										.spawnFallingBlock(start_location,
												start_mat, data);
							}
							catch(Exception e){
								
							}
							if (Core.getInstance().getConfigurations()
									.getDebug_Mode()) {
								Bukkit.broadcastMessage("Log wurde zu Jungle");
							}
						}
						if (start_mat == Material.GLOWSTONE) {
							start_location.getBlock().setData((byte) 3);
							if (Core.getInstance().getConfigurations()
									.getDebug_Mode()) {
								Bukkit.broadcastMessage("Old Root found");
							}
						}
						if (start_mat == Material.LEAVES
								|| start_mat == Material.LEAVES_2) {
							if (Signs.isSignAbouveLocation(start_location)) {
								Location abouve = new Location(start_location
										.getBlock().getWorld(), start_location
										.getBlock().getX(), start_location
										.getBlock().getY() + 1, start_location
										.getBlock().getZ());
								abouve.getBlock().setType(Material.AIR);
							}
							start_block.setType(Material.AIR);
							try {
								double d = Math.random() * 100;
								int chance = 10;
								if ((d -= chance) < 0) {
									FallingBlock b = start_location.getWorld()
											.spawnFallingBlock(start_location,
													start_mat, (byte) 3);
								}
							} catch (Exception e) {

							}
							if (Core.getInstance().getConfigurations()
									.getDebug_Mode()) {
								Bukkit.broadcastMessage("Leave fiel runter");
							}
						}

						// byte data = start_location.getBlock().getData();
						// data = (byte) (data + (byte)3);
						// start_location.getBlock().setData(data);
						

					}
				} catch (IllegalArgumentException e) {
					if (Core.getInstance().getConfigurations()
							.getDebug_Mode()) {
					 Bukkit.broadcastMessage("Kein Owner Gefunden --> Remove aus Garbage");
					}
					Core.getInstance()
							.removeFromRootsGarbageMap(start_location);
				}

			}

			try {
//				Core.getInstance().saveAllFiles();
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			updateMap();
		}
	}

	public void stopMe() {
		this.activ = false;
	}

	public static List<Location> getNeighbors(Location l) {
		List<Location> back = new ArrayList<Location>();
		if (Core.getInstance().isInHashMap(l)) {
			UUID owner_uuid_of_block = Core.getInstance().getUUIDFromHashMap(l);
			String name_of_representant = Core.getInstance().getRepresentant(
					owner_uuid_of_block);
			// Bukkit.broadcastMessage("Hole Nachbarn von Block mit Representanten: "+name_of_representant);

			for (int i = -1; i < 2; i++) {
				for (int j = -1; j < 2; j++) {
					for (int w = -1; w < 2; w++) {
						Location temp = new Location(l.getWorld(),
								l.getBlockX() + i, l.getBlockY() + j,
								l.getBlockZ() + w);
						if (Core.getInstance().isInHashMap(temp)) {
							// Bukkit.broadcastMessage("Block an der Stelle: "+temp.getX()+" | "+temp.getY()+" | "+temp.getZ()+" | ");
							UUID owner_uuid_of_temp = Core.getInstance()
									.getUUIDFromHashMap(temp);
							String name_of_representant_temp = Core
									.getInstance().getRepresentant(
											owner_uuid_of_temp);
							// Bukkit.broadcastMessage("Representanten: "+name_of_representant_temp);
							if (name_of_representant_temp != null) {
								if (name_of_representant
										.equalsIgnoreCase(name_of_representant_temp)) {
									back.add(temp);
								}
							}
							// if(temp.getBlock().getType()==Material.LOG){
							// back.add(temp);
							// }
						}

					}

				}
			}
			if (Core.getInstance().getConfigurations()
					.getDebug_Mode()) {
			 Bukkit.broadcastMessage("Found neighbors: "+back.size());
			}
		}

		return back;
	}

	private static void updateMap() {
		Core.getInstance();
		rootsgarbagemap = Core.blocksgarbagemap;
	}

}
