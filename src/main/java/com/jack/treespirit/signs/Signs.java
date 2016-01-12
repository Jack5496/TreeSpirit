package com.jack.treespirit.signs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.jack.treespirit.Core;
import com.jack.treespirit.functions.LocationSerialize;
import com.jack.treespirit.knots.Knot;

public class Signs {

	private static String tree_text = Core.signconfigs.get_tree_text();
	private static String tree = "[" + tree_text + "]";
	private static String tree_color = "[" + ChatColor.DARK_GREEN + tree_text
			+ ChatColor.BLACK + "]";
	private static String tree_error = "[" + ChatColor.RED + tree_text
			+ ChatColor.BLACK + "]";

	private static String tree_player_text = Core.signconfigs
			.get_tree_player_text();
	private static String tree_player = "[" + tree_player_text + "]";
	private static String tree_player_color = "[" + ChatColor.DARK_GREEN
			+ tree_player_text + ChatColor.BLACK + "]";
	private static String tree_player_error = "[" + ChatColor.RED
			+ tree_player_text + ChatColor.BLACK + "]";

	private static String to_root_text = Core.signconfigs.get_to_root_text();
	private static String to_root = "[" + to_root_text + "]";
	private static String to_root_color = "[" + ChatColor.DARK_GREEN
			+ to_root_text + ChatColor.BLACK + "]";
	private static String to_root_error = "[" + ChatColor.DARK_RED
			+ to_root_text + ChatColor.BLACK + "]";

	private static String roots_text = Core.signconfigs.get_roots_text();
	private static String roots = "[" + roots_text + "]";
	private static String roots_color = "[" + ChatColor.DARK_GREEN + roots_text
			+ ChatColor.BLACK + "]";
	private static String roots_error = "[" + ChatColor.DARK_RED + roots_text
			+ ChatColor.BLACK + "]";

	private static boolean isToRootText(String s) {
		return (s.equalsIgnoreCase(to_root)
				|| s.equalsIgnoreCase(to_root_color) || s
					.equalsIgnoreCase(to_root_error));
	}

	private static boolean isPlayerText(String s) {
		return (s.equalsIgnoreCase(tree_player)
				|| s.equalsIgnoreCase(tree_player_color) || s
					.equalsIgnoreCase(tree_player_error));
	}

	private static boolean isTreeText(String s) {
		return (s.equalsIgnoreCase(tree) || s.equalsIgnoreCase(tree_color) || s
				.equalsIgnoreCase(tree_error));
	}

	private static boolean isRootsText(String s) {
		return (s.equalsIgnoreCase(roots) || s.equalsIgnoreCase(roots_color) || s
				.equalsIgnoreCase(roots_error));
	}

	public static void createASign(SignChangeEvent e) {
		if (isToRootText(e.getLine(0))) {
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("String correct");
			}
			Location under = new Location(e.getBlock().getWorld(), e.getBlock()
					.getX(), e.getBlock().getY() - 1, e.getBlock().getZ());
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("under not null");
			}
			if (Core.getInstance().isInHashMap(under)) {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("In hashmap");
				}
				if (Core.getInstance().isInKnotMap(under)) {
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage("Is knot");
					}
					Knot k = Core.getInstance().getKnot(under);
					if (k != null) {
						if (Core.getInstance().getConfigurations()
								.getDebug_Mode()) {
							Bukkit.broadcastMessage("Update");
						}
						((Sign) e.getBlock().getState()).setLine(0,
								to_root_color);
						setSignText((Sign) e.getBlock().getState(),
								e.getBlock(), k);
						e.setLine(0,
								((Sign) e.getBlock().getState()).getLine(0));
						e.setLine(1,
								((Sign) e.getBlock().getState()).getLine(1));
						e.setLine(2,
								((Sign) e.getBlock().getState()).getLine(2));
						e.setLine(3,
								((Sign) e.getBlock().getState()).getLine(3));
					} else {
						e.setLine(0, to_root_error);
						e.setLine(1, "Sorry but this");
						e.setLine(2, "Knot is empty");
						e.setLine(3, ":-/");
					}
				} else {
					e.setLine(0, to_root_error);
					e.setLine(1, "No Knot Point");
					e.setLine(2, "Set on top of");
					e.setLine(3, "a Knot Point");
				}
			} else {
				e.setLine(0, to_root_error);
				e.setLine(1, "NO TREE BLOCK");
				e.setLine(2, "Set on top of");
				e.setLine(3, "a Knot Point");
			}

			return;
		}
		if (isPlayerText(e.getLine(0))) {
			String p_name = e.getLine(1);
			if (p_name == null) {
				p_name = e.getPlayer().getName();
			}
			if (p_name.isEmpty()) {
				p_name = e.getPlayer().getName();
			}
			Player p = Bukkit.getPlayer(p_name);
			if (p != null) {
				e.setLine(0, tree_player_color);
				e.setLine(1, p.getName());
				int size = Core.getKeysByValue(Core.hashmap, p.getUniqueId())
						.size();
				// String s_size = "";
				// if(size > 999){
				// size = size/1000;
				// s_size = " K";
				// }
				e.setLine(3, "Blocks: " + size);
			} else {
				e.setLine(0, tree_player_error);
				e.setLine(1, "");
				e.setLine(2, "No Player found");
				e.setLine(3, "");
			}

			return;
		}
		if (isTreeText(e.getLine(0))) {
			String p_name = e.getLine(1);
			if (p_name == null) {
				p_name = e.getPlayer().getName();
			}
			if (p_name.isEmpty()) {
				p_name = e.getPlayer().getName();
			}
			Player p = Bukkit.getPlayer(p_name);
			if (p != null) {
				e.setLine(0, tree_color);

				String name_of_owner = Core.getInstance().getRepresentant(
						p.getUniqueId());
				e.setLine(1, name_of_owner);
				List<UUID> tree_members = new ArrayList<UUID>();
				tree_members = Core.getKeysByValueForPlayerMap(Core.playermap,
						name_of_owner);

				int total_amount_of_blocks = 0;

				for (UUID uuid : tree_members) {
					List<String> blocks_of_uuid = Core.getKeysByValue(
							Core.hashmap, uuid);
					total_amount_of_blocks += blocks_of_uuid.size();
				}

				e.setLine(3, "Total: " + total_amount_of_blocks);
			} else {
				e.setLine(0, tree_error);
				e.setLine(1, "");
				e.setLine(2, "No Tree found");
				e.setLine(3, "");
			}

			return;
		}
		if (isRootsText(e.getLine(0))) {
			e.setLine(0, roots_color);
			Core.getInstance();
			int total_roots = Core.rootmap.size();
			e.setLine(2, "Tree's: " + total_roots);

			return;
		}

		// NONE OF THESE
		// NO ERROR MESSAGE NEEDED

	}

	public static void updateASign(PlayerInteractEvent e) {
		Sign s = (Sign) e.getClickedBlock().getState();

		if (isPlayerText(s.getLine(0))) {
			String p_name = s.getLine(1);
			Player p2 = Bukkit.getPlayer(p_name);
			if (p2 != null) {
				s.setLine(0, tree_player);
				s.setLine(1, p_name);
				int size = Core.getKeysByValue(Core.hashmap, p2.getUniqueId())
						.size();
				// String s_size = "";
				// if(size > 999){
				// size = size/1000;
				// s_size = " K";
				// }
				s.setLine(3, "Blocks: " + size);
				s.update();
			} else {
				s.setLine(0, tree_player_error);
				s.setLine(1, "");
				s.setLine(2, "No Player found");
				s.setLine(3, "");
			}
		}
		if (isRootsText(s.getLine(0))) {
			s.setLine(0, roots);
			Core.getInstance();
			int total_roots = Core.rootmap.size();
			s.setLine(2, "Tree's: " + total_roots);
		}
	}

	public static void updateSignCost(Knot k) {

		Location loc = LocationSerialize.serializeToLocation(k.s_loc);
		Block b = loc.getWorld().getBlockAt(
				new Location(loc.getWorld(), loc.getBlockX(),
						loc.getBlockY() + 1, loc.getBlockZ()));

		if (b.getType() == Material.SIGN_POST) {
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				Bukkit.broadcastMessage("Sign Post");
			}
			b.setType(Material.SIGN_POST);
			Sign signState = (Sign) b.getState();

			String first = signState.getLine(0);
			if (first != null) {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					Bukkit.broadcastMessage("Sign is not empty: " + first);
				}
				if (isToRootText(first)) {
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage("Right Text");
					}
					setSignText(signState, b, k);
				}
			}

		}
	}

	public static void setSignText(Sign signState, Block b, Knot k) {

		signState.setLine(0, to_root_color);

		BlockFace dir = b.getFace(b);

		if (k.i_kosten == null || k.s_kosten == null) {
			signState.setLine(0, to_root_error);
			signState.setLine(1, "");
			signState.setLine(2, "Blocks: " + ChatColor.MAGIC + "???");
			signState.setLine(3, "No Way found");

		} else {
			signState.setLine(2, "Blocks: " + k.i_kosten);

			Location myloc = LocationSerialize.serializeToLocation(k.s_loc);
			Location best = LocationSerialize.serializeToLocation(k.s_kosten);

			int z = best.getBlockX() - myloc.getBlockX();
			int x = best.getBlockZ() - myloc.getBlockZ();
			int y = best.getBlockY() - myloc.getBlockY();

			if (y == -1) {
				signState.setLine(3, "<-- Down");
			}
			// WEST
			if (y == 1) {
				signState.setLine(3, "<-- Up");
			}
			if (y == 0) {
				signState.setLine(3, "<--");
			}

			// EAST
			if (z == -1) {
				// NORTH WEST
				if (x == -1) {
					dir = BlockFace.SOUTH_WEST;
				}
				// NORTH EAST
				if (x == 1) {
					dir = BlockFace.SOUTH_EAST;
				}
				if (x == 0) {
					dir = BlockFace.SOUTH;
				}
			}
			// WEST
			if (z == 1) {
				// SOUTH WEST
				if (x == -1) {
					dir = BlockFace.NORTH_WEST;
				}
				// SOUTH EAST
				if (x == 1) {
					dir = BlockFace.NORTH_EAST;
				}
				if (x == 0) {
					dir = BlockFace.NORTH;
				}
			}
			if (z == 0) {
				// SOUTH
				if (x == -1) {
					dir = BlockFace.WEST;
				}
				// NORTH
				if (x == 1) {
					dir = BlockFace.EAST;
				}
				// DOWN or Up
				if (x == 0) {
					dir = BlockFace.NORTH;
					if (y == -1) {
						signState.setLine(3, "Down");
					}
					// WEST
					if (y == 1) {
						signState.setLine(3, "Up");
					}
				}
			}

		}

		org.bukkit.material.Sign matSign = new org.bukkit.material.Sign(
				Material.SIGN_POST);
		matSign.setFacingDirection(dir);
		signState.setData(matSign);
		try {
			signState.update(); /* <= Nicht vergessen! */
		} catch (Exception e) {
			// Jemand anderes Löschte den block bereits...
		}
	}

	public static boolean isSignAbouveLocation(Location l) {
		Location abouve = new Location(l.getBlock().getWorld(), l.getBlock()
				.getX(), l.getBlock().getY() + 1, l.getBlock().getZ());
		Block b_abouve = abouve.getBlock();
		if (b_abouve.getType() == Material.SIGN_POST) {
			Sign s = (Sign) abouve.getBlock().getState();
			if (isToRootText(s.getLine(0))) {
				return true;
			}
		}
		return false;
	}
}
