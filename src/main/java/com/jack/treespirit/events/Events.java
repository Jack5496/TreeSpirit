package com.jack.treespirit.events;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.StructureGrowEvent;

import com.jack.treespirit.Core;
import com.jack.treespirit.ce.ImportBlocks;
import com.jack.treespirit.ce.TreeChoose;
import com.jack.treespirit.ce.TreeComands;
import com.jack.treespirit.data.ResourceLoader;
import com.jack.treespirit.filemanager.CustomTree;
import com.jack.treespirit.filemanager.PlayerMap;
import com.jack.treespirit.functions.LocationSerialize;
import com.jack.treespirit.functions.MessageGuild;
import com.jack.treespirit.functions.WayTrace2;
import com.jack.treespirit.knots.Knot;
import com.jack.treespirit.languages.Languages;
import com.jack.treespirit.menu.CreateMenu;
import com.jack.treespirit.signs.Signs;
import com.jack.treespirit.threads.DamageController;
import com.jack.treespirit.threads.TreeGrowController;

public class Events implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerRespawn(final PlayerRespawnEvent event) {
		Core.getInstance().removePlayer(event.getPlayer());

		Core.getInstance();
		String owner_string_name = Core.getInstance().getRepresentant(
				event.getPlayer().getUniqueId());
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("Owner of Player: " + owner_string_name);
		}

		Core.getInstance();
		List<UUID> lp = Core.getKeysByValueForPlayerMap(Core.playermap,
				owner_string_name);
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("UUID lp List size? " + lp.size());
		}

		int lpsize = lp.size();
		for (int i = 0; i < lpsize; i++) {
			Random randomGenerator = new Random();
			int index = randomGenerator.nextInt(lp.size());
			UUID uuid = lp.get(index);
			lp.remove(index);
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Get Player Number: " + index);
			}

			Core.getInstance();
			List<String> loc_list = Core.getKeysByValue(Core.hashmap, uuid);
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Size of Blocks of Player: "
						+ loc_list.size());
			}
			int loc_list_size = loc_list.size();

			for (int j = 0; j < loc_list_size; j++) {

				Random randomLocGenerator = new Random();
				int loc_index = randomLocGenerator.nextInt(loc_list.size());
				String s_loc = loc_list.get(loc_index);
				loc_list.remove(index);
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Get Block Number: " + index);
				}

				Location l = LocationSerialize.serializeToLocation(s_loc);
				final Location top = l.getWorld().getHighestBlockAt(l)
						.getLocation();

				int ly = (int) l.getBlockY();
				int ty = (int) top.getBlockY() - 1;

				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Heigth of Block: " + ly
							+ " | Heigth of Top: " + ty);
				}

				if (ly == ty) {
					if (Core.getInstance().getMessenger()
							.getInform_Guild_on_Respawn()) {
						String args = top.getX() + " | " + top.getY() + " | "
								+ top.getZ() + " | ";
						MessageGuild.messageGuild(event.getPlayer(), "<GuildMemberRespawn>", args);
					}
					Core.getInstance()
							.getServer()
							.getScheduler()
							.scheduleSyncDelayedTask(Core.getInstance(),
									new Runnable() {
										public void run() {
											if (Core.getInstance()
													.getConfigurations()
													.getDebug_Mode()) {
												Core.getInstance()
														.getServer()
														.broadcastMessage(
																"This message is broadcast by the main thread");
											}
											Location adjusted = top.clone();
											adjusted = adjusted
													.add(0.5, 0, 0.5);
											event.getPlayer()
													.teleport(adjusted);
											event.getPlayer().setHealth(20.0);
										}
									}, 30L);
					return;
				}

			}
		}
	}

	@EventHandler
	public void OnPlayerJoin(org.bukkit.event.player.PlayerJoinEvent e) {
		if (!Core.playermap.containsKey(e.getPlayer().getUniqueId())) {
			for(Player p : Bukkit.getOnlinePlayers()){
				p.sendMessage(Languages.getNewPlayerJoined(p, e.getPlayer().getName()));
			}
			Core.getInstance().resetGroupForPlayer(e.getPlayer());
		}
		
		e.getPlayer().setGameMode(GameMode.SURVIVAL);
		if (Core.getInstance().getMessenger().getInform_Guild_on_PlayerJoin()) {
			MessageGuild.messageGuild(e.getPlayer(), "<GuildMemberOnline>", e.getPlayer().getName());
		}

		Core.getInstance().sbc.addPlayersScoreboard(e.getPlayer());
		PlayerMap.savePlayer(e.getPlayer());
		PlayerMap.savePlayerInTreeGroup(e.getPlayer());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		PlayerMap.savePlayer(e.getPlayer());
	}

	
	@EventHandler
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent e){
		if(Core.getInstance().getTreeForPlayer(e.getPlayer().getUniqueId()).equalsIgnoreCase("None")){
			Material m = e.getItem().getItemStack().getType();
			if(Core.getInstance().vaildMat(m) || m==Material.SAPLING){
				e.getPlayer().sendMessage(Languages.getYouCantPickThatUp(e.getPlayer(),m.name()));
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent e){
		if(Core.getInstance().getTreeForPlayer(e.getPlayer().getUniqueId()).equalsIgnoreCase("None")){
			Material m = e.getItemDrop().getItemStack().getType();
			if(Core.getInstance().vaildMat(m) || m==Material.SAPLING){
				e.getPlayer().sendMessage(Languages.getCantDropIt(e.getPlayer(), m.name()));
				e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount()-e.getItemDrop().getItemStack().getAmount());
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void playerHitPlayerEvent(EntityDamageByEntityEvent event) {
		Entity damage_dealer = event.getDamager();
		Entity damage_taker = event.getEntity();
		if (damage_taker instanceof Player) {
			Player player_taker = (Player) damage_taker;
			if (damage_dealer instanceof Player) {
				Player player_dealer = (Player) damage_dealer;

				Core.getInstance();
				String dealer_rep = Core.getInstance().getRepresentant(
						player_dealer.getUniqueId());
				Core.getInstance();
				String taker_rep = Core.getInstance().getRepresentant(
						player_taker.getUniqueId());
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Damage Dealer Rep: " + dealer_rep);
					Bukkit.broadcastMessage("Damage Taker Rep: " + taker_rep);
				}

				if (dealer_rep.equalsIgnoreCase(taker_rep)) {
					if (!Core.getInstance().getConfigurations().getTree_Pvp()) {
						event.setDamage(0);
						event.setCancelled(true);
						if (Core.getInstance().getMessenger()
								.getInform_Player_of_Non_Tree_PvP()) {
							player_dealer.sendMessage(Languages.getPvpDisabled(player_dealer, player_taker.getName()));
						}
						if (Core.getInstance().getConfigurations()
								.getDebug_Mode()) {
							Bukkit.broadcastMessage("Event Canceld");
						}
					}
				} else {
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage("Damage Dealed: "
								+ event.getDamage());
					}
				}

			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block b = e.getBlock();
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("Block Broken");
		}

		if (e.getPlayer().isOp()) {
			if (e.getPlayer().getItemInHand().getTypeId() == Material.DEAD_BUSH
					.getId()) {
				e.setCancelled(true);
				giveInformationAbout(e.getBlock());
				return;
			}
		}

		if (!Core.getInstance().isInHashMap(b.getLocation())) {
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Block not in Map");
			}
			if (b.getType() == Material.LOG) {
				e.setCancelled(true);
				b.setType(Material.AIR);
			}
			if (b.getType() == Material.LEAVES) {
				e.setCancelled(true);
				b.setType(Material.AIR);
			}
			if (b.getType() == Material.LOG_2) {
				e.setCancelled(true);
				b.setType(Material.AIR);
			}
			if (b.getType() == Material.LEAVES_2) {
				e.setCancelled(true);
				b.setType(Material.AIR);
			}

			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Thats Noones Tree");
			}

			BlockBreaked(b, "Unkown Reason");
			return;
		}

		String player_rep = Core.getInstance().getRepresentant(
				e.getPlayer().getUniqueId());
		String block_rep = Core.getInstance().getRepresentant(
				Core.getInstance().getUUIDFromHashMap(b.getLocation()));
		if (!player_rep.equalsIgnoreCase(block_rep)) {
			// e.setCancelled(true);
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Thats not your Tree");
			}
			// b.setType(Material.AIR);
			if (Core.getInstance().getMessenger()
					.getInform_Guild_on_Enemy_Grief()) {
				Location l = e.getBlock().getLocation();
				UUID uuid = Core.getInstance().getUUIDFromHashMap(l);
				String player = e.getPlayer().getName();
				String position = ImportBlocks.getStringFromLoc(l);
				MessageGuild.messageGuild(uuid, "<GuildTreeDestroy>", player, position);
				if (Core.getInstance().isInRootMap(l)) {
					String name_of_owner = Core.getInstance().getRepresentant(
							uuid);
					e.getPlayer().sendMessage(Languages.getRootDestroy(e.getPlayer(), name_of_owner));
				}

			}
			BlockBreaked(b, e.getPlayer().getName());
			return;
		} else {
			BlockBreaked(b, e.getPlayer().getName());
			return;
		}

	}

	public void giveInformationAbout(Block b) {
		Location l = b.getLocation();
		String info = "";
		if (Core.getInstance().isInHashMap(l)) {
			info = "TreeSpirit Block: ";
			if (Core.getInstance().isInRootMap(l)) {
				info = info + "Root: true | ";
			} else {
				info = info + "Root: false | ";
			}

			if (Core.getInstance().isInKnotMap(l)) {
				info = info + "Knot: true | " + "OnUpdate: "
						+ Core.getInstance().getKnot(l).onUpdate + " | ";

			} else {
				info = info + "Knot: false | ";
			}
		} else {
			info = "This Block is not a TreeSpiritBlock";
		}
		Bukkit.broadcastMessage(info);
		
		boolean way = false;
		if(b.getType()==Material.LOG || b.getType()==Material.LOG_2){
			way = WayTrace2.connectionToRoot(l);
		}
		else{
			way = WayTrace2.connectionToRootWithLeaves(l);
		}
		Bukkit.broadcastMessage("WayTrace: " + way);
		Bukkit.broadcastMessage("");

		if (Core.getInstance().isInKnotMap(l)) {
			Knot k = Core.getInstance().getKnot(l);
			List<String> sortedKeys = new ArrayList<String>(k.inhalt.keySet());
			// Collections.sort(sortedKeys);
			for (String dir_s : sortedKeys) {
				Location dest = LocationSerialize.serializeToLocation(k
						.getInhalt(dir_s, 0));
				Integer cost_per_way = Integer.parseInt(k.getInhalt(dir_s, 1));
				Location dir = LocationSerialize.serializeToLocation(dir_s);
				if (Core.getInstance().isInRootMap(dest)) {
					Bukkit.broadcastMessage("Cost: " + cost_per_way.intValue()
							+ " in Direction: " + dir.getBlockX() + " | "
							+ dir.getBlockY() + " | " + dir.getBlockZ() + " ; "
							+ " to Root " + dest.getBlockX() + " | "
							+ dest.getBlockY() + " | " + dest.getBlockZ());
				} else {
					Bukkit.broadcastMessage("Cost: " + cost_per_way.intValue()
							+ " in Direction: " + dir.getBlockX() + " | "
							+ dir.getBlockY() + " | " + dir.getBlockZ() + " ; "
							+ " to Knot " + dest.getBlockX() + " | "
							+ dest.getBlockY() + " | " + dest.getBlockZ()
							+ " which aditionally Cost: " + k.i_kosten);

				}

			}
			Bukkit.broadcastMessage("");
			if (k.isRoot()) {
				Bukkit.broadcastMessage("I'm gRoot");
			} else {
				if (k.s_kosten == null || k.i_kosten == null) {
					Bukkit.broadcastMessage("Steps to Root cost infinity/arent possible");
				} else {
					Location dir = LocationSerialize
							.serializeToLocation(k.s_kosten);
					Bukkit.broadcastMessage("Steps to Root: " + k.i_kosten
							+ " over Direction: " + dir.getBlockX() + " | "
							+ dir.getBlockY() + " | " + dir.getBlockZ());
				}
			}

		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityExplode(org.bukkit.event.entity.EntityExplodeEvent event) {
		List<Block> destroyed = event.blockList();
		Iterator<Block> it = destroyed.iterator();
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage(ChatColor.GOLD + "[TreeSpirit]"
					+ ChatColor.WHITE + "Explosion destroyed " + ChatColor.RED
					+ destroyed.size() + ChatColor.WHITE + " Blocks");
		}
		if (Core.getInstance().getMessenger().getInform_Guild_on_Explosion()) {

			Block b = destroyed.get(0);
			Location l = b.getLocation();
			for (int i = 0; i < destroyed.size(); i++) {
				b = destroyed.get(i);
				if (Core.getInstance().vaildMat(b.getType())) {
					l = b.getLocation();
				}
			}
			UUID uuid = Core.getInstance().getUUIDFromHashMap(l);
			if (!(uuid == null)) {
				MessageGuild.messageGuild(uuid, "<GuildExplosionDamage>", ""+destroyed.size());
			}

		}

		while (it.hasNext()) {
			Block block = it.next();
			if (!Core.getInstance().getConfigurations()
					.getDamage_All_Blocks_on_Explosion()) {
				if (Core.getInstance().getConfigurations()
						.getOnly_Damage_Tree_on_Explosion()) {
					if (!Core.getInstance().vaildMat(block.getType())) {
						it.remove(); // Normal blocks wont break
						/** Removes Block from List --> Block wont be broken */
					} else {
						BlockBreaked(block, "an Explosion"); // Tree Blocks break
					}
				} else {
					it.remove(); // No Block will get destroyed
				}

			} else {
				BlockBreaked(block, "an Explosion"); // Jeder Block
			}

		}
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		if (Core.getInstance().getMessenger().getInform_Guild_on_Fire()) {
			if (event.getBlock().getType() == Material.LOG) {
				UUID uuid = Core.getInstance().getUUIDFromHashMap(
						event.getBlock().getLocation());
				MessageGuild.messageGuild(uuid, "<GuildFireDamage>", ImportBlocks.getStringFromLoc(event.getBlock().getLocation()));
			}
		}
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage(ChatColor.GOLD + "[TreeSpirit]"
					+ ChatColor.WHITE + " Fire destroyed a Block");
		}
		BlockBreaked(event.getBlock(), "a Fire");
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onStructureGrow(StructureGrowEvent event) {
		Location l = event.getLocation();
		if (Core.getInstance().isInHashMap(l)) {
			UUID player_uuid = Core.getInstance().getUUIDFromHashMap(l);
			event.setCancelled(true);
			Player p = Bukkit.getPlayer(player_uuid);
			String name = ResourceLoader.getNameBySapling(l.getBlock());
			CustomTree.spawnTree(l, name, p);

			if (Core.getInstance().getMessenger().getInform_Guild_on_TreeGrow()) {
				UUID uuid = Core.getInstance().getUUIDFromHashMap(l);
				String position = ImportBlocks.getStringFromLoc(event.getLocation());
				MessageGuild.messageGuild(uuid, "<GuildTreeGrowEvent>", position);
			}
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage(ChatColor.GOLD + "[TreeSpirit]"
						+ ChatColor.WHITE + "Your Tree grew up");
			}
		} else {
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage(ChatColor.GOLD + "[TreeSpirit]"
						+ ChatColor.WHITE + "It's not your tree :-/");
			}
		}
		
	}

	public static void BlockBreaked(Block b, String reason) {
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			if (b.getType() == Material.WOOL) {
				Bukkit.broadcastMessage("Destroyed WayPoint:");
				DyeColor color = DyeColor.getByWoolData(b.getData());
				if (color == DyeColor.GREEN) {
					Bukkit.broadcastMessage("Green: StartBlock");
					Core.getInstance().start = null;
					b.setType(Material.AIR);
					return;
				}

				if (color == DyeColor.RED) {
					Bukkit.broadcastMessage("Destroyed: EndBlock");
					Core.getInstance().end = null;
					b.setType(Material.AIR);
					return;
				}
			}
		}

		Material mat_of_block = b.getType();
		if (mat_of_block == Material.GLOWSTONE) {
			
			if(reason.equalsIgnoreCase("Merge Groups")){
				Bukkit.broadcastMessage("Merge Group Start");
				Location l = b.getLocation();
				if(Core.rootmap.containsValue(LocationSerialize.serializeFromLocation(l))){
					
					UUID uuidold = Core.getKeysByValue(Core.rootmap, LocationSerialize.serializeFromLocation(l)).get(0);
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage("Okay is in Rootmap");
						Bukkit.broadcastMessage("Old UUID: "+uuidold);
						Bukkit.broadcastMessage("Belongs to PLayer: "+Bukkit.getPlayer(uuidold).getName());
					}
					
					Core.rootmap.remove(Bukkit.getPlayer(uuidold).getName());
					CheckNeighborsOnConnection(l);
					b.setType(Material.LOG);
				}
				
			}
			else{
				Location l = b.getLocation();
				UUID uuid = Core.getInstance().getUUIDFromHashMap(l);
				boolean root_broken = Core.getInstance().checkIfRootAndBreakThen(l);
				CheckNeighborsOnConnection(l);
				if (root_broken) {
	
					if (Core.getInstance().getMessenger()
							.getInform_Guild_on_RootDestroy()) {
							MessageGuild.messageGuild(uuid, "<GuildRootDestroy>", reason);
						
					}
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage(ChatColor.GOLD + "[TreeSpirit]"
								+ ChatColor.WHITE + " - Root Destroyed");
					}
				}
			}
		}
		if (mat_of_block == Material.SAPLING) {
			Location l = b.getLocation();
			Core.getInstance().removeFromHashMap(l);
		} else if (Core.getInstance().vaildMat(mat_of_block)) {
			Location l = b.getLocation();
			CheckNeighborsOnConnection(l);

		}
		Location l = b.getLocation();
		UUID uuid = Core.getInstance().getUUIDFromHashMap(l);
		PlayerMap.savePlayer(Bukkit.getPlayer(uuid));
	}

	public static void CheckNeighborsOnConnection(Location l) {
		List<String> locs = DamageController.getNeighbors26(l);

		if (Core.getInstance().isInHashMap(l)) {
			String representant_player = Core.getInstance().getRepresentant(
					Core.getInstance().getUUIDFromHashMap(l));

			// NACH UNTEN VERSCHOBEN
			Core.getInstance().removeFromHashMap(l);

			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Is in Knotmap: "+Core.getInstance().isInKnotMap(l));
			}
			if (Core.getInstance().isInKnotMap(l)) { // Bei knoten abbau, kommt
														// das problem, dass er
														// alle nachbarn
														// benachrichtigen
														// möchte
				// Es wird getan als wenn der knoten seinen besten weg verliert
				// um redundanz zu vermeiden
				Knot ko = Core.getInstance().getKnot(l);
				Core.getInstance().removeFromKnotMap(l);

				List<String> Keys = new ArrayList<String>(ko.inhalt.keySet());
				for (String way : Keys) {
					if (!way.equalsIgnoreCase(ko.s_kosten)) {
						String knot_loc = ko.getInhalt(way, 0);
						Knot k = Core.getInstance()
								.getKnot(
										LocationSerialize
												.serializeToLocation(knot_loc));
						if (k != null) {
							if (k.s_kosten != null) {
								String pos_my = k.getInhalt(k.s_kosten, 0);
								if (pos_my != null) {
									if (!pos_my.equalsIgnoreCase(ko.s_loc)) {
										// Bukkit.broadcastMessage("Lösche nicht besten weg: "
										// + way);

										ko.removeLocationWarningSpecialUseOnly(LocationSerialize
												.serializeToLocation(way));
									}
								}
							}
						}
					}
				}

				// Bukkit.broadcastMessage("Lösche den besten");
				try {
					Location best = LocationSerialize
							.serializeToLocation(ko.s_kosten);

					ko.removeLocationFromList(best);
				} catch (NullPointerException e) {
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage("Haha NullPointer");
					}
					Core.getInstance().addToRootsGarbageMap(
							LocationSerialize.serializeToLocation(ko.s_loc));
				}
				ko.updateCost();

				for (String s : locs) {
					Location loc = LocationSerialize.serializeToLocation(s);
					Core.getInstance().addToRootsGarbageMap(loc);
				}

			} else {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("is Log? : "+(l.getBlock().getType() == Material.LOG
						|| l.getBlock().getType() == Material.LOG_2));
				}
				// Bukkit.broadcastMessage("Rep: "+representant_player);
				// Bukkit.broadcastMessage("Block wurde zerstört, Sage nachbar Knoten bescheid");
				if (l.getBlock().getType() == Material.LOG
						|| l.getBlock().getType() == Material.LOG_2) {
					for (String s : locs) {
						Location loc = LocationSerialize.serializeToLocation(s);

						String representant_loc = Core.getInstance()
								.getRepresentant(
										Core.getInstance().getUUIDFromHashMap(
												loc));
						// Bukkit.broadcastMessage("Rep: "+representant_loc);
						if (representant_player
								.equalsIgnoreCase(representant_loc)) {

							if (Core.getInstance().isInKnotMap(loc)) {
								// Bukkit.broadcastMessage("Der Nachbar ist direkt Knoten");
								Knot k = Core.getInstance().getKnot(loc);
								// Bukkit.broadcastMessage("Lösche in event");
								k.removeLocationFromList(l);
								k.updateCost();
							} else {
								// Bukkit.broadcastMessage("Der Nachbar ist kein Knoten, suche weiter");
								Object[] obj = Knot.findNextKnot(loc, Core
										.getInstance().getUUIDFromHashMap(loc));
								if (obj != null) {
									Knot k = Core.getInstance().getKnot(
											(Location) obj[0]);
									Location direction = (Location) obj[2];
									// Bukkit.broadcastMessage("Knoten gefunden mit inhaltslänge: "+k.inhalt.size()+"; Lösche richtung: "+direction);
									k.removeLocationFromList(direction);
									k.updateCost();
								}
							}
							Core.getInstance().addToRootsGarbageMap(loc);
						}
					}
				}
				else {
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage("add to Garbage Leaves");
					}
					for (String s : locs) {
						Location loc = LocationSerialize.serializeToLocation(s);
						Core.getInstance().addToRootsGarbageMap(loc);
					}
				
				}

			}

		}
	}


	@EventHandler
	public void EntityChangeBlockEvent(
			org.bukkit.event.entity.EntityChangeBlockEvent e) {
		e.setCancelled(false);
		Location l = e.getBlock().getLocation();
		if (Core.getInstance().isInHashMap(l)) {
			if (!Core.getInstance().vaildMat(e.getTo())) {
				BlockBreaked(e.getBlock(), "A Monster");
			}
		}
	}

	@EventHandler
	public void BlockPistonExtendEvent(
			org.bukkit.event.block.BlockPistonExtendEvent e) {
		e.setCancelled(false);
		List<Block> list = e.getBlocks();
		List<Block> back = new ArrayList<Block>();
		for (Block b : list) {
			back.add(b);
		}
		BlockFace dir = e.getDirection();
		int x = dir.getModX();
		int y = dir.getModY();
		int z = dir.getModZ();
		for (Block b : back) {
			Location l = b.getLocation();
			if (Core.getInstance().isInHashMap(l)) {
				Location moved = new Location(l.getWorld(), l.getBlockX() + x,
						l.getBlockY() + y, l.getBlockZ() + z);
				Player p = (Player) Bukkit.getOfflinePlayer(Core.getInstance()
						.getUUIDFromHashMap(l));

				PlaceLog(moved, b.getType(), p);
				// Bukkit.broadcastMessage("Place Log at: "+moved.toString());
				BlockBreaked(b, "a pushing Piston");

			}
		}
	}

	@EventHandler
	public void BlockPistonRetractEvent(
			org.bukkit.event.block.BlockPistonRetractEvent e) {
		BlockFace dir = e.getDirection();
		int x = -dir.getModX();
		int y = -dir.getModY();
		int z = -dir.getModZ();
		Location l = e.getRetractLocation();
		if (Core.getInstance().isInHashMap(l)) {
			Location moved = new Location(l.getWorld(), l.getBlockX() + x,
					l.getBlockY() + y, l.getBlockZ() + z);
			Player p = (Player) Bukkit.getOfflinePlayer(Core.getInstance()
					.getUUIDFromHashMap(l));
			PlaceLog(moved, l.getBlock().getType(), p);
			BlockBreaked(l.getBlock(), "a retracting Piston");

		}
	}

	@EventHandler
	public void LeavesDecayEvent(org.bukkit.event.block.LeavesDecayEvent event) {
		Material mat_of_block = event.getBlock().getType();
		if (Core.getInstance().vaildMat(mat_of_block)) {
			Location l = event.getBlock().getLocation();

			if (!Core.getInstance().isInHashMap(l)) {
				event.setCancelled(true);
				event.getBlock().setType(Material.AIR);
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Not Leave of Planted tree");
				}
			}
			Core.getInstance().removeFromHashMap(l);

		}
	}

	public static void checkNeighbors(Location l) {
		List<String> neigh = DamageController.getNeighbors26(l);
		int amount = 0;
		if (Core.getInstance().isInHashMap(l)) {
			if (!Core.getInstance().isInKnotMap(l)) {

				for (String s : neigh) {
					Location la = LocationSerialize.serializeToLocation(s);
					if (Core.getInstance().isInHashMap(la)) {
						if (Core.getInstance()
								.vaildMat(la.getBlock().getType())) {
							if (la.getBlock().getType() != Material.LEAVES
									|| la.getBlock().getType() != Material.LEAVES_2) {
								// Bukkit.broadcastMessage("Location to Check: "+la.toString());
								if (!la.equals(l)) {
									amount++;
								}
							}
						}
					}
				}
				if (amount > 2) {
					// Bukkit.broadcastMessage("Erstelle Knoten an: "+l.getX()+" | "+l.getY()+" | "+l.getZ());
					if (l.getBlock().getType() != Material.LEAVES
							&& l.getBlock().getType() != Material.LEAVES_2) {
						Knot k = new Knot(l);
						Core.getInstance().addToKnotMap(l, k);
						k.updateMeOnce();
					}
				}
				// } else {
				// Bukkit.broadcastMessage("Knoten wurde gefunden");
				// }
			} else {
				// Bukkit.broadcastMessage("Bin bereits Knoten");
			}
		}
	}

	public static void checkForKnot(Location l, Player player) {

		List<Location> neigh = Knot.getNeighbors(l, player.getUniqueId());
		// Bukkit.broadcastMessage("Checke für Block: " + l.toString());

		// Bukkit.broadcastMessage("Checke umliegende Blöcke");
		for (Location s : neigh) {
			// Location loc = LocationSerialize.serializeToLocation(s);
			if (!Core.getInstance().isInKnotMap(l)) {
				checkNeighbors(s);
			}
		}
		checkNeighbors(l);
	}

	public static void checkForConnectionBetweenKnots(Location l, Player p) {
		Knot.checkForConnectionBetweenKnots(l, p.getUniqueId());
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {

		Material mat_of_block = event.getBlock().getType();
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			if (mat_of_block == Material.SPONGE) {
				Location l = event.getBlockPlaced().getLocation();
				DestroyRootIfThere(event.getPlayer());

				TreeGrowController.GenerateStartTree(l, event.getPlayer());
				event.getPlayer().setCompassTarget(l);
				/**
			 * 
			 */
				Knot root = new Knot(l, true);
				// Bukkit.broadcastMessage("Before putting in KnotMap: "
				// + root.isRoot());
				Core.getInstance().addToKnotMap(l, root);
				// Bukkit.broadcastMessage("After putting in KnotMap: "
				// + Core.getInstance().getKnot(l).isRoot());
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Created Root");
				}

				if (Core.getInstance().getMessenger()
						.getInform_Guild_on_RootDestroy()) {
					event.getPlayer().sendMessage(Languages.getRootPlacedMessage(event.getPlayer()));
				}
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage(ChatColor.GOLD + "[TreeSpirit]"
							+ ChatColor.WHITE + " - Root Placed");
				}
			}
		}
		if (mat_of_block == Material.SAPLING) {
			if (Core.getInstance()
					.getTreeForPlayer(event.getPlayer().getUniqueId())
					.equalsIgnoreCase("None")) {

				Player p = event.getPlayer();

				Location l = event.getBlockPlaced().getLocation();
				DestroyRootIfThere(event.getPlayer());

				/**
				 * 
				 */
				String treeType = TreeChoose.getTreeType(event.getBlock());
				Core.getInstance().changeTreeTypeForPlayerMap(p.getUniqueId(),
						treeType);
				p.getInventory().clear();

				Knot root = new Knot(l, true);
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				 Bukkit.broadcastMessage("Before putting in KnotMap: "
				 + root.isRoot());
				}
				Core.getInstance().addToKnotMap(l, root);
				Core.getInstance().addToHashMap(l, p);

				l.getBlock().setType(Material.AIR);

				TreeGrowController.GenerateStartTree(l, p);

				event.getPlayer().setCompassTarget(l);

				if (Core.getInstance().getMessenger()
						.getInform_Guild_on_RootDestroy()) {
					event.getPlayer().sendMessage(Languages.getRootPlacedMessage(event.getPlayer()));
				}
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage(ChatColor.GOLD + "[TreeSpirit]"
							+ ChatColor.WHITE + " - Root Placed");
				}
				
				PlayerMap.savePlayer(p);
				PlayerMap.savePlayerInTreeGroup(p);

			} else {
				// if(Core.getInstance().isChained(event.getPlayer(),
				// event.getBlock().getLocation(), (int)10)){ //find ich mehr
				// realistischer
				if (Core.getInstance().isChained(event.getPlayer(),
						event.getBlock().getLocation())) {
					if (Core.getInstance().getConfigurations()
							.getSapling_must_Set_Next_To_Log()) {
						if (Core.getInstance().isChainedToLog(
								event.getPlayer(),
								event.getBlock().getLocation())) {
							Location l = event.getBlockPlaced().getLocation();
							Core.getInstance().addToHashMap(l,
									event.getPlayer());
						} else {
							if (Core.getInstance()
									.getMessenger()
									.getInform_Player_on_Sapling_must_Set_Next_To_Log()) {
								event.getPlayer()
										.sendMessage(Languages.getSetSaplingNextToLog(event.getPlayer()));
							}
						}
					} else {
						if (Core.getInstance().getConfigurations()
								.getDebug_Mode()) {
							Bukkit.broadcastMessage(ChatColor.GOLD
									+ "[TreeSpirit]" + ChatColor.WHITE
									+ " - Sapling is Chained");
						}
						Location l = event.getBlockPlaced().getLocation();
						Core.getInstance().addToHashMap(l, event.getPlayer());
					}
				} else {
					if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
						event.getPlayer()
								.sendMessage(Languages.getCreativeModeAndNotChained(event.getPlayer()));
						return;
					}
					event.setCancelled(true);
					if (Core.getInstance().getMessenger()
							.getInform_Player_on_Sapling_must_Set_Next_To_Log()) {
						event.getPlayer()
								.sendMessage(Languages.getSetSaplingNextToLog(event.getPlayer()));
					}
				}
			}
		}
		if (Core.getInstance().vaildMat(mat_of_block)) {
			if (Core.getInstance().isChained(event.getPlayer(),
					event.getBlock().getLocation())) {
				if (Core.getInstance().getConfigurations()
						.getOnly_Walk_on_Log()) {
					if (mat_of_block == Material.LOG) {
						if (Core.getInstance().getConfigurations()
								.getDebug_Mode()) {
							Bukkit.broadcastMessage(ChatColor.GOLD
									+ "[TreeSpirit]" + ChatColor.WHITE
									+ " - Log is Chained");
						}
						Location l = event.getBlockPlaced().getLocation();
						Core.getInstance().addToHashMap(l, event.getPlayer());

						/**
						 * 
						 */

						checkForKnot(event.getBlock().getLocation(),
								event.getPlayer());
						checkForConnectionBetweenKnots(event.getBlock()
								.getLocation(), event.getPlayer());

					} else if (mat_of_block == Material.LOG_2) {
						if (Core.getInstance().getConfigurations()
								.getDebug_Mode()) {
							Bukkit.broadcastMessage(ChatColor.GOLD
									+ "[TreeSpirit]" + ChatColor.WHITE
									+ " - Log2 is Chained");
						}
						Location l = event.getBlockPlaced().getLocation();
						Core.getInstance().addToHashMap(l, event.getPlayer());

						/**
						 * 
						 */

						checkForKnot(event.getBlock().getLocation(),
								event.getPlayer());
						checkForConnectionBetweenKnots(event.getBlock()
								.getLocation(), event.getPlayer());

					}
				} else {
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage(ChatColor.GOLD + "[TreeSpirit]"
								+ ChatColor.WHITE + " - Block is Chained");
					}
					Location l = event.getBlockPlaced().getLocation();
					Core.getInstance().addToHashMap(l, event.getPlayer());

					/**
					 * 
					 */
					checkForKnot(event.getBlock().getLocation(),
							event.getPlayer());
					checkForConnectionBetweenKnots(event.getBlock()
							.getLocation(), event.getPlayer());

				}
			} else {
				if (Core.getInstance().getConfigurations()
						.getOnly_Walk_on_Log()) {
					if (mat_of_block == Material.LEAVES) {
						event.setCancelled(true);
						return;
					}
				}
				if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
					event.getPlayer().sendMessage(Languages.getCreativeModeAndNotChained(event.getPlayer()));
					return;
				}
				event.setCancelled(true);
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage(ChatColor.GOLD + "[TreeSpirit]"
							+ ChatColor.WHITE + " - Block is not Chained :-/");
				}
				if (Core.getInstance().getMessenger()
						.getInform_Player_on_Log_must_Set_Next_To_Log()) {
					event.getPlayer().sendMessage(Languages.getBlockIsNotChained(event.getPlayer()));
				}
			}

		}

		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			Bukkit.broadcastMessage("After Placed UUID is: "
					+ Core.getInstance().getUUIDFromHashMap(
							event.getBlock().getLocation()));
		}
		PlayerMap.savePlayer(event.getPlayer());
	}

	public static void PlaceLog(Location l, Material m, Player p) {
		if (Core.getInstance().getConfigurations().getDebug_Mode()) {
		 Bukkit.broadcastMessage("Place Log");
		}
		Material mat_of_block = m;
		if (Core.getInstance().isChained(p, l)) {
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
			 Bukkit.broadcastMessage("Chained");
			}
			if (Core.getInstance().getConfigurations().getOnly_Walk_on_Log()) {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				 Bukkit.broadcastMessage("Walk on Log");
				}
				if (mat_of_block == Material.LOG) {
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage(ChatColor.GOLD + "[TreeSpirit]"
								+ ChatColor.WHITE + " - Log is Chained");
					}

					Core.getInstance().addToHashMap(l, p);

					checkForKnot(l, p);
					checkForConnectionBetweenKnots(l, p);

				}
				if (mat_of_block == Material.LOG_2) {
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage(ChatColor.GOLD + "[TreeSpirit]"
								+ ChatColor.WHITE + " - Log2 is Chained");
					}

					Core.getInstance().addToHashMap(l, p);

					checkForKnot(l, p);
					checkForConnectionBetweenKnots(l, p);
				}
			} else {
				if (Core.getInstance().getConfigurations()
						.getOnly_Walk_on_Log()) {
					if (mat_of_block == Material.LEAVES) {
						return;
					}
				}
			}

		}
		PlayerMap.savePlayer(p);
	}

	@EventHandler
	public void eventSignChanged(SignChangeEvent e) {
		Signs.createASign(e);
	}

	public void DestroyRootIfThere(Player p) {
		TreeComands.leaveGuild(p);
		Core.getInstance().deleteAllBlocksFromUUID(p.getUniqueId());
		Core.getInstance().RemoveFromRootMap(p);

	}

}