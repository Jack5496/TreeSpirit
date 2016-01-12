package com.jack.treespirit.filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import net.minecraft.util.org.apache.commons.io.FilenameUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.jack.treespirit.Core;

public class PlayerMap {
	/**
	 * Exat Safe Command for PlayerMap
	 * 
	 * @param f
	 *            File od PlayerMap
	 * @param playermap2
	 *            PlayerMap
	 * @throws FileNotFoundException
	 */
	public static void savePlayerMap(File f,
			HashMap<UUID, List<String>> playermap2, int i, int max)
			throws FileNotFoundException {
		Core.getInstance().checkIfPluginDirsExist();
		if (i < max) {
			try {
				f.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try (FileOutputStream fis = new FileOutputStream(f.getPath());
					ObjectOutputStream o = new ObjectOutputStream(fis)) {
				HashMap<UUID, List<String>> copy = (HashMap<UUID, List<String>>) playermap2
						.clone();
				o.writeObject(copy);
				o.flush();
				o.close();
			} catch (Exception e) {

				Bukkit.broadcastMessage("Couldn't serialize PlayerMap to file: atempt: "
						+ i);
				Bukkit.broadcastMessage(e.getMessage());
				savePlayerMap(f, playermap2, i + 1, max);
			}
			for (Entry<UUID, List<String>> entry : playermap2.entrySet()) {
				UUID uuid = entry.getKey();
				Player p = Bukkit.getPlayer(uuid);
				Bukkit.broadcastMessage("savePlayerMap method");
				savePlayer(p);
				savePlayerInTreeGroup(p);
			}
		}
	}

	/**
	 * Get PlayerMap from predefined File
	 * 
	 * @param f
	 *            File of PlayerMap
	 * @return
	 */

	public static HashMap<UUID, List<String>> getPlayerMapFromFile(File f,
			int i, int max) {
		if (i < max) {
			try {
				f.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try (FileInputStream inputFileStream = new FileInputStream(f);
					ObjectInputStream objectInputStream = new ObjectInputStream(
							inputFileStream);) {
				HashMap<UUID, List<String>> map = ((HashMap<UUID, List<String>>) objectInputStream
						.readObject());
				objectInputStream.close();
				inputFileStream.close();
				return map;
			} catch (Exception e) {

				Bukkit.broadcastMessage("Deserialising of PlayerMap failed: atempt: "
						+ i);
				return getPlayerMapFromFile(f, i + 1, max);
			}
		}
		return new HashMap<UUID, List<String>>();
	}

	public static void createPlayer(Player p) {
		OfflinePlayer op = p;
		createPlayer(op);
	}

	public static void createPlayer(OfflinePlayer p) {
		File adr = new File(Core.getInstance().getPlugindirDataPlayers() + "/"
				+ p.getName() + ".yml");
		if (!adr.exists()) {
			try {
				adr.createNewFile();
				YamlConfiguration temp = YamlConfiguration
						.loadConfiguration(adr);
				temp.addDefault("TreeGroup", p.getName());
				temp.addDefault("TreeType", "None");
				temp.addDefault("Language", "Default");
				temp.addDefault("Blocks", "0");

				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				// System.out.println(dateFormat.format(date)); //2014/08/06
				// 15:59:48
				temp.addDefault("First Join", dateFormat.format(date));

				temp.options().copyDefaults(true);
				temp.save(adr);
			} catch (IOException ex) {

			}
		}
		YamlConfiguration ycf = YamlConfiguration.loadConfiguration(adr);
	}

	public static void savePlayer(Player p) {
		if (p != null) {
			File adr = new File(Core.getInstance().getPlugindirDataPlayers()
					+ "/" + p.getName() + ".yml");
			if (!adr.exists()) {
				createPlayer(p);
			}
			YamlConfiguration ycf = YamlConfiguration.loadConfiguration(adr);
			ycf.set("TreeGroup",
					Core.getInstance().getRepresentant(p.getUniqueId()));
			ycf.set("TreeType",
					Core.getInstance().getTreeForPlayer(p.getUniqueId()));

			int amount_of_player = 1;
			amount_of_player = Core.getKeysByValue(Core.hashmap,
					p.getUniqueId()).size();
			ycf.set("Blocks", amount_of_player + "");

			ycf.options().copyDefaults(true);
			try {
				ycf.save(adr);
			} catch (IOException e) {
				Bukkit.broadcastMessage("Error by saving Player");
			}
		}

	}

	public static void savePlayerInTreeGroup(Player p) {
		String playerRep = getPlayerTreeGroup(p);
		File adr = new File(Core.getInstance().getPlugindirDataTreeGroups()
				+ "/" + playerRep + ".yml");
		if (!adr.exists()) {
			createTreeGroup(playerRep);
		}
		YamlConfiguration ycf = YamlConfiguration.loadConfiguration(adr);
		ycf.set(p.getName(), "Member");
		if (p.getName().equalsIgnoreCase(playerRep)) {
			ycf.set(p.getName(), "Owner");
		}

		ycf.options().copyDefaults(true);
		try {
			ycf.save(adr);
		} catch (IOException e) {
			Bukkit.broadcastMessage("Error by saving Player");
		}
	}

	public static void removePlayerInTreeGroup(Player p) {
		String playerRep = getPlayerTreeGroup(p);
		File adr = new File(Core.getInstance().getPlugindirDataTreeGroups()
				+ "/" + playerRep + ".yml");
		if (!adr.exists()) {
			createTreeGroup(playerRep);
		}
		YamlConfiguration ycf = YamlConfiguration.loadConfiguration(adr);
		ycf.set(p.getName(), null);
		ycf.options().copyDefaults(true);
		try {
			ycf.save(adr);
		} catch (IOException e) {
			Bukkit.broadcastMessage("Error by saving Player");
		}
	}

	public static void setPlayerTree(OfflinePlayer p, String tree) {
		YamlConfiguration ycf = getPlayerConfigFile(p);
		ycf.set("TreeGroup", tree);
	}

	/**
	 * Merge all Player from a to b
	 * 
	 * @param a
	 * @param b
	 */
	public static void mergeTreeToTree(OfflinePlayer a, OfflinePlayer b) {
		List<OfflinePlayer> toMove = new ArrayList<OfflinePlayer>();
		String playerRepA = getPlayerTreeGroup(a);
		String playerRepB = getPlayerTreeGroup(b);
		if (!playerRepA.equalsIgnoreCase(playerRepB)) {
			File adrA = new File(Core.getInstance()
					.getPlugindirDataTreeGroups() + "/" + playerRepA + ".yml");
			if (!adrA.exists()) {
				createTreeGroup(playerRepA);
			}
			YamlConfiguration ycfA = YamlConfiguration.loadConfiguration(adrA);
			Bukkit.broadcastMessage("Start Colleting all Players");
			for (String name : ycfA.getKeys(false)) {
				OfflinePlayer op = Bukkit.getOfflinePlayer(name);
				toMove.add(op);
				ycfA.set(name, null);
			}
			adrA.delete();
			Bukkit.broadcastMessage("Colleting complete, found: "
					+ toMove.size() + " Players to Move");

			File adrB = new File(Core.getInstance()
					.getPlugindirDataTreeGroups() + "/" + playerRepB + ".yml");
			if (!adrB.exists()) {
				createTreeGroup(playerRepB);
			}
			YamlConfiguration ycfB = YamlConfiguration.loadConfiguration(adrB);
			for (OfflinePlayer op : toMove) {
				ycfB.addDefault(op.getName(), "Member");
				setPlayerTree(op, playerRepB);
			}
			Bukkit.broadcastMessage("Merge Complete");
		}
	}

	public static void leaveTree(OfflinePlayer p) {
		String playerRepA = getPlayerTreeGroup(p);
		File adrA = new File(Core.getInstance().getPlugindirDataTreeGroups()
				+ "/" + playerRepA + ".yml");
		if (!adrA.exists()) {
			createTreeGroup(playerRepA);
		}
		YamlConfiguration ycfA = YamlConfiguration.loadConfiguration(adrA);
		ycfA.set(p.getName(), null);
	}

	public static void createTreeGroup(String playerRep) {
		File adr = new File(Core.getInstance().getPlugindirDataTreeGroups()
				+ "/" + playerRep + ".yml");
		if (!adr.exists()) {
			try {
				adr.createNewFile();
				YamlConfiguration temp = YamlConfiguration
						.loadConfiguration(adr);
				temp.options().copyDefaults(true);
				temp.save(adr);
			} catch (IOException ex) {

			}
		}
	}

	public static void savePlayerLanguage(Player p, String language) {
		File adr = new File(Core.getInstance().getPlugindirDataPlayers() + "/"
				+ p.getName() + ".yml");
		if (!adr.exists()) {
			createPlayer(p);
		}
		YamlConfiguration ycf = YamlConfiguration.loadConfiguration(adr);

		ycf.set("Language", language);
		ycf.options().copyDefaults(true);
		try {
			ycf.save(adr);
		} catch (IOException e) {
			Bukkit.broadcastMessage("Error by saving Player Language");
		}
	}

	public static YamlConfiguration getPlayerConfigFile(Player p) {
		return getPlayerConfigFile((OfflinePlayer) p);
	}

	public static YamlConfiguration getPlayerConfigFile(OfflinePlayer p) {
		File adr = new File(Core.getInstance().getPlugindirDataPlayers() + "/"
				+ p.getName() + ".yml");
		return getPlayerConfigFile(adr);
	}

	public static OfflinePlayer getPlayerFromFile(File adr) {
		String fileNameWithOutExt = FilenameUtils
				.removeExtension(adr.getName());
		return Bukkit.getOfflinePlayer(fileNameWithOutExt);
	}

	public static YamlConfiguration getPlayerConfigFile(File adr) {
		if (!adr.exists()) {
			createPlayer(getPlayerFromFile(adr));
		}
		YamlConfiguration ycf = YamlConfiguration.loadConfiguration(adr);
		return ycf;
	}

	/**
	 * GetInformation from Player
	 * 
	 * @param p
	 *            Player
	 * @param search
	 *            String
	 * @return
	 */
	public static String getPlayerInformation(Player p, String search) {
		YamlConfiguration ycf = getPlayerConfigFile(p);
		return ycf.getString(search);
	}

	/**
	 * GetInformation from Player
	 * 
	 * @param p
	 *            OfflinePlayer
	 * @param search
	 *            String
	 * @return
	 */
	public static String getPlayerInformation(OfflinePlayer p, String search) {
		YamlConfiguration ycf = getPlayerConfigFile(p);
		return ycf.getString(search);
	}

	/**
	 * GetInformation from Player
	 * 
	 * @param adr
	 *            File from Player
	 * @param search
	 *            String
	 * @return
	 */
	public static String getPlayerInformation(File adr, String search) {
		YamlConfiguration ycf = getPlayerConfigFile(adr);
		return ycf.getString(search);
	}

	/**
	 * get Players Tree Group
	 */

	public static String getPlayerTreeGroup(Player p) {
		return getPlayerInformation(p, "TreeGroup");
	}

	public static String getPlayerTreeGroup(OfflinePlayer op) {
		return getPlayerInformation(op, "TreeGroup");
	}

	public static String getPlayerTreeGroup(File adr) {
		return getPlayerInformation(adr, "TreeGroup");
	}

	/**
	 * get Players Tree Type
	 */

	public static String getPlayerTreeType(Player p) {
		return getPlayerInformation(p, "TreeType");
	}

	public static String getPlayerTreeType(OfflinePlayer p) {
		return getPlayerInformation(p, "TreeType");
	}

	public static String getPlayerTreeType(File adr) {
		return getPlayerInformation(adr, "TreeType");
	}

	/**
	 * get Players First Join
	 */

	public static String getFirstJoin(Player p) {
		return getPlayerInformation(p, "First Join");
	}

	public static String getFirstJoin(OfflinePlayer p) {
		return getPlayerInformation(p, "First Join");
	}

	public static String getFirstJoin(File adr) {
		return getPlayerInformation(adr, "First Join");
	}

	/**
	 * Get Player Language
	 */

	public static String getPlayerLanguage(Player p) {
		return getPlayerInformation(p, "Language");
	}

	public static String getPlayerLanguage(OfflinePlayer p) {
		return getPlayerInformation(p, "Language");
	}

	public static String getPlayerLanguage(File adr) {
		return getPlayerInformation(adr, "Language");
	}

	/**
	 * Get Player total Blocks
	 */
	public static String getPlayerTotalBlocks(Player p) {
		return getPlayerInformation(p, "Blocks");
	}

	public static String getPlayerTotalBlocks(OfflinePlayer p) {
		return getPlayerInformation(p, "Blocks");
	}

	public static String getPlayerTotalBlocks(File adr) {
		return getPlayerInformation(adr, "Blocks");
	}

}
