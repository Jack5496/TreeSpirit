package com.jack.treespirit.threads;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.jack.treespirit.Core;
import com.jack.treespirit.API.TreeSpirit;
import com.jack.treespirit.functions.LocationSerialize;
import com.jack.treespirit.scoreboard.PlayerScoreboard;

public class ScoreboardController implements Runnable {

	public static TreeSpirit core;
	public static boolean activ = true;
	public int delay = 30 * 1000;
	public int switchnumber = 0;

	public static boolean showPlayersScoreboard = true;
	public static boolean showTreeScoreboard = true;
	public static boolean showTreeTopsScoreboard = true;
	public static boolean showGlobalTreeScoreboard = true;
	public static boolean showGlobalPlayerScoreboard = true;

	PlayerScoreboard scoreboard;

	public ScoreboardController() {
		ScoreboardController.core = Core.getInstance();
		for (Player p : Bukkit.getOnlinePlayers()) {
			removePlayersScoreboard(p);
			addPlayersScoreboard(p);
		}
	}

	@Override
	public void run() {
		while (activ) {
			
			for (Player p : Bukkit.getOnlinePlayers()) {
				removePlayersScoreboard(p);
				addPlayersScoreboard(p);
			}
			if (switchnumber == 0) {
				if (showPlayersScoreboard) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						updateToPlayerScoreboard(p);
					}
				} else {
					switchnumber++;
				}
			}
			if (switchnumber == 1) {
				if (showTreeScoreboard) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						updateToTreeGroup(p);
					}
				} else {
					switchnumber++;
				}
			}
			if (switchnumber == 2) {
				if (showTreeTopsScoreboard) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						updateToTreeTop(p);
					}
				} else {
					switchnumber++;
				}
			}
			if (switchnumber == 3) {
				if (showGlobalTreeScoreboard) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						updateGlobalTreeTop(p);
					}
				} else {
					switchnumber++;
				}
			}
			if (switchnumber == 4) {
				if (showGlobalPlayerScoreboard) {
					for (Player p : Bukkit.getOnlinePlayers()) {
						updateGlobalPlayerTop(p);
					}
				} else {
					switchnumber++;
				}
			}

			
			switchnumber++;
			if (switchnumber >= 5) {
				switchnumber = 0;
			}
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void addPlayersScoreboard(Player p) {
		PlayerScoreboard.addPlayer(p); // Ein Scoreboard für den Spieler
										// hinzufügen
	}

	public void removePlayersScoreboard(Player p) {
		try {
			PlayerScoreboard.getPlayerScoreboard(p).resetScoreboard(); // Resettet
																		// das
																		// Scoreboard
			PlayerScoreboard.getPlayerScoreboard(p).sendScoreboard(); // Sendet
																		// ein
																		// leeres
																		// Scoreboard
		} catch (NullPointerException e) {
			// AFTER RELOAD
		}
	}

	public void setshowPlayersScoreboard(String s) {
		if (s != null) {
			if (s.equalsIgnoreCase("true")) {
				showPlayersScoreboard = true;
			} else if (s.equalsIgnoreCase("false")) {
				showPlayersScoreboard = false;
			}
		}
	}
	
	public boolean getshowPlayersScoreboard(){
		return showPlayersScoreboard;
	}

	public void setshowTreeScoreboard(String s) {
		if (s != null) {
			if (s.equalsIgnoreCase("true")) {
				showTreeScoreboard = true;
			} else if (s.equalsIgnoreCase("false")) {
				showTreeScoreboard = false;
			}
		}
	}
	
	public boolean getshowTreeScoreboard(){
		return showTreeScoreboard;
	}

	public void setshowTreeTopsScoreboard(String s) {
		if (s != null) {
			if (s.equalsIgnoreCase("true")) {
				showTreeTopsScoreboard = true;
			} else if (s.equalsIgnoreCase("false")) {
				showTreeTopsScoreboard = false;
			}
		}
	}
	
	public boolean getshowTreeTopsScoreboard(){
		return showTreeTopsScoreboard;
	}

	public void setshowGlobalTreeScoreboard(String s) {
		if (s != null) {
			if (s.equalsIgnoreCase("true")) {
				showGlobalTreeScoreboard = true;
			} else if (s.equalsIgnoreCase("false")) {
				showGlobalTreeScoreboard = false;
			}
		}
	}
	
	public boolean getshowGlobalTreeScoreboard(){
		return showGlobalTreeScoreboard;
	}

	public void setshowGlobalPlayerScoreboard(String s) {
		if (s != null) {
			if (s.equalsIgnoreCase("true")) {
				showGlobalPlayerScoreboard = true;
			} else if (s.equalsIgnoreCase("false")) {
				showGlobalPlayerScoreboard = false;
			}
		}
	}
	
	public boolean getshowGlobalPlayerScoreboard(){
		return showGlobalPlayerScoreboard;
	}

	// / TABLES

	public void updateToPlayerScoreboard(Player p) {
		String name_of_owner = Core.getInstance().getRepresentant(p.getUniqueId());

		List<UUID> tree_members = new ArrayList<UUID>();
		tree_members = Core.getKeysByValueForPlayerMap(Core.playermap, name_of_owner);

		int total_amount_of_blocks = 0;
		int amount_of_player = 1;
		amount_of_player = Core.getKeysByValue(Core.hashmap, p.getUniqueId())
				.size();

		for (UUID uuid : tree_members) {
			List<String> blocks_of_uuid = Core.getKeysByValue(Core.hashmap,
					uuid);
			total_amount_of_blocks += blocks_of_uuid.size();
		}

		double percent = 0;
		try {
			percent = ((double) amount_of_player / (double) total_amount_of_blocks) * 100;
			DecimalFormat df2 = new DecimalFormat("###.###");
			percent = Double.valueOf(df2.format(percent));
		} catch (NumberFormatException e) {
			percent = 0;
		}

		PlayerScoreboard.getPlayerScoreboard(p).setDisplayName(
				ChatColor.GREEN + "You"); // Den Display-Name des
												// Scoreboard festlegen, kann
												// natürlich auch was anderes
												// sein
		PlayerScoreboard.getPlayerScoreboard(p).setScore("Block's placed:",
				amount_of_player);
		PlayerScoreboard.getPlayerScoreboard(p).setScore("Percent of Tree:",
				(int) percent);
		PlayerScoreboard.getPlayerScoreboard(p).setScore(" ", 0);
		PlayerScoreboard.getPlayerScoreboard(p).setScore("/tree help", -1);

		PlayerScoreboard.getPlayerScoreboard(p).sendScoreboard(); // Scoreboard
																	// senden
	}

	public void updateToTreeGroup(Player p) {
		String name_of_owner = Core.getInstance().getRepresentant(p.getUniqueId());

		List<UUID> tree_members = new ArrayList<UUID>();
		tree_members = Core.getKeysByValueForPlayerMap(Core.playermap, name_of_owner);

		int amount_of_members = tree_members.size();

		int total_amount_of_blocks = 0;
		Map<Integer, String> treerank = new TreeMap<Integer, String>();

		for (UUID uuid : tree_members) {
			List<String> blocks_of_uuid = Core.getKeysByValue(Core.hashmap,
					uuid);
			String p_name = Core.getInstance().getRepresentant(uuid);
			treerank.put(blocks_of_uuid.size(), p_name);
			total_amount_of_blocks += blocks_of_uuid.size();
		}

		PlayerScoreboard.getPlayerScoreboard(p).setDisplayName(
				ChatColor.GREEN + "Your Tree"); // Den Display-Name
																// des
		// Scoreboard festlegen,
		// kann natürlich auch was
		// anderes sein

		PlayerScoreboard.getPlayerScoreboard(p).setScore("Members:",
				amount_of_members);
		PlayerScoreboard.getPlayerScoreboard(p).setScore("Total Block's:",
				total_amount_of_blocks);
		PlayerScoreboard.getPlayerScoreboard(p).setScore(" ", 0);
		PlayerScoreboard.getPlayerScoreboard(p).setScore("/tree help", -1);
		PlayerScoreboard.getPlayerScoreboard(p).sendScoreboard(); // Scoreboard
																	// senden
	}

	public void updateToTreeTop(Player p) {
		String name_of_owner = Core.getInstance().getRepresentant(p.getUniqueId());

		List<UUID> tree_members = new ArrayList<UUID>();
		tree_members = Core.getKeysByValueForPlayerMap(Core.playermap, name_of_owner);

		Map<Integer, String> treerank = new TreeMap<Integer, String>();

		for (UUID uuid : tree_members) {
			List<String> blocks_of_uuid = Core.getKeysByValue(Core.hashmap,
					uuid);
			String p_name = Core.getInstance().getRepresentant(uuid);
			treerank.put(blocks_of_uuid.size(), p_name);
		}

		PlayerScoreboard.getPlayerScoreboard(p).setDisplayName(
				ChatColor.GREEN + "Best in Tree"); // Den Display-Name des
													// Scoreboard festlegen,
													// kann natürlich auch was
													// anderes sein

		int i = 0;
		for (Map.Entry<Integer, String> entry : treerank.entrySet()) {
			if (i > 9) {
				break;
			} else {
				PlayerScoreboard.getPlayerScoreboard(p).setScore(
						"" + entry.getValue(), entry.getKey());
				i++;
			}
		}

		PlayerScoreboard.getPlayerScoreboard(p).sendScoreboard(); // Scoreboard
																	// senden
	}

	public void updateGlobalTreeTop(Player p) {
		Map<Integer, String> treerank = new TreeMap<Integer, String>();

		for (UUID key : Core.rootmap.keySet()) {
			String name_of_owner = Core.getInstance().getRepresentant(key);

			List<UUID> tree_members = new ArrayList<UUID>();
			tree_members = Core.getKeysByValueForPlayerMap(Core.playermap, name_of_owner);

			int total_amount_of_blocks = 0;

			for (UUID uuid : tree_members) {
				List<String> blocks_of_uuid = Core.getKeysByValue(Core.hashmap,
						uuid);
				total_amount_of_blocks += blocks_of_uuid.size();
			}

			treerank.put(total_amount_of_blocks, name_of_owner);
		}

		int i = 0;
		for (Map.Entry<Integer, String> entry : treerank.entrySet()) {
			if (i > 9) {
				break;
			} else {
				PlayerScoreboard.getPlayerScoreboard(p).setScore(
						"" + entry.getValue(), entry.getKey());
				i++;
			}
		}

		PlayerScoreboard.getPlayerScoreboard(p).setDisplayName(
				ChatColor.GREEN + "Global Tree's");

		PlayerScoreboard.getPlayerScoreboard(p).sendScoreboard(); // Scoreboard
																	// senden
	}

	public void updateGlobalPlayerTop(Player p) {
		Map<Integer, String> playerrank = new TreeMap<Integer, String>();

		for (UUID key : Core.playermap.keySet()) {
			String name_of_player = Bukkit.getOfflinePlayer(key).getName();
			List<String> blocks_of_uuid = Core
					.getKeysByValue(Core.hashmap, key);
			int total_amount_of_blocks = blocks_of_uuid.size();

			playerrank.put(total_amount_of_blocks, name_of_player);
		}

		int i = 0;
		for (Map.Entry<Integer, String> entry : playerrank.entrySet()) {
			if (i > 9) {
				break;
			} else {
				PlayerScoreboard.getPlayerScoreboard(p).setScore(
						"" + entry.getValue(), entry.getKey());
				i++;
			}
		}

		PlayerScoreboard.getPlayerScoreboard(p).setDisplayName(
				ChatColor.GREEN + "Global Players");

		PlayerScoreboard.getPlayerScoreboard(p).sendScoreboard();
	}

	public void stopMe() {
		activ = false;
	}

}
