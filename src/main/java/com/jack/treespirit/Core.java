package com.jack.treespirit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.jack.treespirit.API.TreeSpirit;
import com.jack.treespirit.API.CreateMenuAPI;
import com.jack.treespirit.API.FileManagerAPI;
import com.jack.treespirit.ce.TreeComands;
import com.jack.treespirit.ce.PendingTimer;
import com.jack.treespirit.data.ConfigLoader;
import com.jack.treespirit.data.Configurations;
import com.jack.treespirit.data.Messages;
import com.jack.treespirit.data.ResourceLoader;
import com.jack.treespirit.data.SignConfigurations;
import com.jack.treespirit.events.Events;
import com.jack.treespirit.filemanager.FileManager;
import com.jack.treespirit.filemanager.PlayerMap;
import com.jack.treespirit.functions.LocationSerialize;
import com.jack.treespirit.knots.Knot;
import com.jack.treespirit.languages.Languages;
import com.jack.treespirit.menu.CreateMenu;
import com.jack.treespirit.threads.DamageController;
import com.jack.treespirit.threads.RootsGarbageCollector;
import com.jack.treespirit.threads.ScoreboardController;
import com.jack.treespirit.threads.TreeGarbageCollector;

public class Core extends JavaPlugin implements TreeSpirit {

	private static Core instance;

	/** Plugin Ordner */
	public static File plugindir = new File("plugins/TreeSpirit");
	/** Data Ordner */
	public static File plugindirdata = new File("plugins/TreeSpirit/Data");
	public static File plugindirdatatrees = new File(
			"plugins/TreeSpirit/Data/Trees");
	public static File plugindirdataplayers = new File(
			"plugins/TreeSpirit/Data/Players");
	public static File plugindirdatatreegroups = new File(
			"plugins/TreeSpirit/Data/TreeGroups");
	public static File plugindirdatalanguages = new File(
			"plugins/TreeSpirit/Data/Languages");

	/**
	 * Speicherorte der Ordner
	 */
	public static File configfile = new File("plugins/TreeSpirit/Config.yml");
	public static File messagefile = new File("plugins/TreeSpirit/Messages.yml");
	public static File signsfile = new File("plugins/TreeSpirit/Signs.yml");
	public static File materialsfile = new File(
			"plugins/TreeSpirit/Materials.yml");
	public static File scoreboardsfile = new File(
			"plugins/TreeSpirit/Scoreboard.yml");

	private File hashmapfile = new File("plugins/TreeSpirit/Data/hashmap.ser");
	private File playermapfile = new File(
			"plugins/TreeSpirit/Data/playermap.ser");
	private File rootmapfile = new File("plugins/TreeSpirit/Data/rootmap.ser");
	private File knotmapfile = new File("plugins/TreeSpirit/Data/knotsmap.ser");

	private File treegarbagemapfile = new File(
			"plugins/TreeSpirit/Data/garbagelist.ser"); // Wenn die HauptWurzel
														// zerstört wurde
	private File blockgarbagemapfile = new File(
			"plugins/TreeSpirit/Data/blockgarbagelist.ser"); // Wenn eine Block
																// gelöscht
																// werden soll

	/** Ende Data Ordner */

	public Location start;
	public Location end;

	/**
	 * LOCATION UND BESITZER
	 * 
	 * @param String
	 *            Location
	 * @param String
	 *            Besitzer/Representant
	 */
	public static HashMap<String, UUID> hashmap;

	/**
	 * ROOT LOCATION UND BESITZER
	 * 
	 * @param UUID
	 *            Besitzer
	 * @param String
	 *            Location
	 */
	public static HashMap<UUID, String> rootmap;

	/**
	 * SPIELER UUID - BESITZER/REPRESENTANT
	 * 
	 * @param UUID
	 *            UUID of Player
	 * @param String
	 *            Representant des Spieler/ Group Leader
	 */
	public static HashMap<UUID, List<String>> playermap;

	/**
	 * KNOTENPUNKT Location - KNOTEN an Location
	 * 
	 * @param String
	 *            Player
	 * @param Knot
	 *            Knoten
	 */
	public static HashMap<String, Knot> knotmap;

	/**
	 * Liste aller zu laufenden Materialien
	 */
	public static List<Material> vaildMats;

	/**
	 * Zu Verändernde/Löschende Map an Locations
	 */
	public static HashMap<String, UUID> treegarbagemap;

	/**
	 * Zu Prüfende abgetrennte Wurzeln
	 */
	public static List<String> blocksgarbagemap;

	/**
	 * Thread um absterbende Bäume abzuarbeiten
	 */
	public Thread treegarbagethread;

	/**
	 * Thread um abgetrente Wurzeln abzuarbeiten
	 */
	public Thread blocksgarbagethread;

	/**
	 * Thread zum Scoreboard aktualisieren
	 */
	public Thread scoreboardthread;

	/**
	 * Damager Thread thread, der allen Spielern in lp Schaden zufügt
	 */
	private Thread damagethread;
	private List<Player> lp;
	private HashMap<Player, Integer> reduce_damage = new HashMap<Player, Integer>();

	/**
	 * Anzahl an erlaubten nachbarn für Blocksetzten: 26 : Alle drumrum 10:
	 * Selbe höhe alle und oben unten 6: Nur
	 * Oben,Unten,Links,Rechts,Vorne,Hinten
	 */
	private int neighbors = 26;

	/**
	 * HashMap in der Spieler einladungen temp. gepeichert werden
	 */
	public HashMap<Player, Player> invites_pending = new HashMap<Player, Player>();
	/**
	 * HashMap in der die Art der Einladung gespeichert wird ("guild")
	 */
	public HashMap<Player, String> invite_art = new HashMap<Player, String>();

	/**
	 * Command Excecuter
	 */
	private TreeComands tree_cmd = new TreeComands(this);

	/**
	 * Speichert allerleilei SpielMechanische Configuration
	 */
	public static Configurations configs = new Configurations(instance);

	/**
	 * Speichert alle Texte von Schildern die zur erkennung von TreeSpirit
	 * Schildern genutzt werden
	 */
	public static SignConfigurations signconfigs = new SignConfigurations(
			instance);

	/**
	 * Speichert alle Message Configurationen
	 */
	private Messages messages = new Messages(this);

	/**
	 * Controller für die Threads, sodass man diese mein disablen stoppen kann
	 */
	private DamageController dc;
	private TreeGarbageCollector tgc;
	private RootsGarbageCollector rgc;
	public static ScoreboardController sbc;
	private static ResourceLoader rl;

	public CreateMenuAPI cm;
	private static FileManagerAPI fm;

	public final Logger logger = Logger.getLogger("Minecraft");

	@Override
	public void onEnable() {
		instance = this; // Speichert eigene Instanz
		fm = new FileManager(); // Muss vor loadFiles();
		sbc = new ScoreboardController(); // Muss vor loadFiles();

		try {
			loadFiles(); // Läd alle Dateien, HashMap,PlayerMap,RootMap,...
		} catch (IOException e) {
			Bukkit.broadcastMessage("Error why loading Files");
			e.printStackTrace();
		}

		lp = new ArrayList<Player>(); // Liste von Spielern denen Dammage
										// zugefügt werden soll

		createAndStartThreadController(); // Läd alle Thread Controller

		cm = new CreateMenu(); // Erstellt Menu ! Muss nach loadFiles();
		rl = new ResourceLoader(); // Muss nach instance und FileManager
									// Aufgerufen werden

		this.getServer().getPluginManager().registerEvents(new Events(), this); // Aktiviert
																				// das
																				// Plugin

		this.getCommand("tree").setExecutor(tree_cmd); // Activiert den /tree
														// Command

		System.out.println("[TreeSpirit] enabled!");

		try {
			org.mcstats.Metrics metrics = new org.mcstats.Metrics(this);
			metrics.start();
			System.out.println("Stats Submitted");
		} catch (IOException e) { // Failed to submit the stats :-(
			System.out.println("Error Submitting stats!");
		}
	}

	public static void checkIfPluginDirsExist() {
		if (!plugindir.exists()) {
			plugindir.mkdir();
		}

		// Estellt den Data Ordner
		if (!plugindirdata.exists()) {
			plugindirdata.mkdir();
		}

		// Estellt den Tree Ordner in Data
		if (!plugindirdatatrees.exists()) {
			plugindirdatatrees.mkdir();
		}
		// Estellt den Players Ordner in Data
		if (!plugindirdataplayers.exists()) {
			plugindirdataplayers.mkdir();
		}
		// Estellt den Players Ordner in Data
		if (!plugindirdatatreegroups.exists()) {
			plugindirdatatreegroups.mkdir();
		}
		// Estellt den Language Ordner in Data
		if (!plugindirdatalanguages.exists()) {
			plugindirdatalanguages.mkdir();
			Languages.initLanguage();
		} else {
			Languages.initLanguage();
		}
	}

	@Override
	public void createAndStartThreadController() {
		dc = new DamageController();
		damagethread = new Thread(dc); // Dammage Thread
										// wird mit
										// DamageController
										// Instanziiert
		damagethread.start();

		tgc = new TreeGarbageCollector();
		treegarbagethread = new Thread(tgc); // Instanziierung
												// des
												// GarbageCollector
												// (Toterbaum
												// wird
												// abgearbeitet)
		treegarbagethread.start();

		rgc = new RootsGarbageCollector();
		blocksgarbagethread = new Thread(rgc); // Instanziierung
												// des
												// GarbageCollector
												// (Toterbaum
												// wird
												// abgearbeitet)
		blocksgarbagethread.start();

		scoreboardthread = new Thread(sbc); // Instanziierung des Scoreboard
											// Threads
		scoreboardthread.start();

		Bukkit.broadcastMessage("Starte DamageThread: " + dc.hashCode());
	}

	@Override
	public void loadFiles() throws IOException {
		// Estellt den Directory Ordner
		checkIfPluginDirsExist();

		// Erstellt HashMap in der Alle Blöcke gespeichert werden oder wenn
		// vorhanden läd diese
		if (!hashmapfile.exists()) {
			hashmapfile.createNewFile();
		}
		hashmap = getFileManager().getHashMapFromFile(hashmapfile);

		// Erstellt Spieler HashMap in der Alle Spieler und deren Guilden Leader
		// Gespeichert sind, bzw Läd diese
		if (!playermapfile.exists()) {
			playermapfile.createNewFile();
		}
		playermap = getFileManager().getPlayerMapFromFile(playermapfile);

		// Erstellt RootMap HashMap in der alle Spieler gespeichert werden und
		// der zugehörige Rootblock, ggf läd diese Map
		if (!rootmapfile.exists()) {
			rootmapfile.createNewFile();
		}
		rootmap = getFileManager().getRootMapFromFile(rootmapfile);

		// Erstellt TreeGarbageMap in der sich alle noch zu veränderenden Blöcke
		// befinden, falls vorhanden wird diese geladen
		if (!treegarbagemapfile.exists()) {
			treegarbagemapfile.createNewFile();
			treegarbagemap = new HashMap<String, UUID>();
		}
		treegarbagemap = getFileManager()
				.getHashMapFromFile(treegarbagemapfile);

		// Erstellt RootsGarbageMap in der sich alle noch zu veränderenden
		// Blöcke befinden, von einer Abgetrenten Wurzel
		if (!blockgarbagemapfile.exists()) {
			blockgarbagemapfile.createNewFile();
		}
		blocksgarbagemap = getFileManager().getRootsGarbageMapFromFile(
				blockgarbagemapfile);

		// Erstellt KnotMap in der sich alle Knotenbefinden
		if (!knotmapfile.exists()) {
			knotmapfile.createNewFile();
		}
		knotmap = getFileManager().getKnotMapFromFile(knotmapfile);

		// Läd alle Spielmechanischen Configs
		ConfigLoader.loadAllConfigs();
	}

	@Override
	public void onDisable() {
		// Speicher alle HashMaps vor beenden nochmal
		this.saveGarbageMap();
		this.saveHashMap();
		this.savePlayerMap();
		this.saveRootMap();
		synchronized (lp) {
			lp = new ArrayList<Player>();
		}
		saveConfig();

		dc.stopMe();
		Bukkit.broadcastMessage("Beende Damage Controller: " + dc.hashCode());
		damagethread.stop();

		tgc.stopMe();
		treegarbagethread.stop();

		rgc.stopMe();
		blocksgarbagethread.stop();

		sbc.stopMe();
		scoreboardthread.stop();

		for (BukkitTask task : getServer().getScheduler().getPendingTasks()) {
			int tid = task.getTaskId();
			getServer().getScheduler().cancelTask(tid);
		}

		Bukkit.broadcastMessage("[TreeSpirit] disabled!");
		System.out.println("[TreeSpirit] disabled!");
	}

	/**
	 * Gibt den Singelton Core wieder
	 * 
	 * @return Core des Plugins
	 */
	public static Core getInstance() {
		return instance;
	}

	@Override
	public void addToHashMap(Location l, Player p) {
		addToHashMap(l, p.getUniqueId());
	}

	@Override
	public void addToHashMap(Location l, UUID uuid) {
		String s = LocationSerialize.serializeFromLocation(l);
		synchronized (hashmap) {
			hashmap.put(s, uuid);
			saveHashMap();
		}
	}

	@Override
	public void removeFromHashMap(Location l) {
		String s = LocationSerialize.serializeFromLocation(l);
		synchronized (hashmap) {
			hashmap.remove(s);
			saveHashMap();
		}

	}

	@Override
	public boolean isInHashMap(Location l) {
		String s = LocationSerialize.serializeFromLocation(l);
		synchronized (hashmap) {
			return hashmap.containsKey(s);
		}
	}

	@Override
	public UUID getUUIDFromHashMap(Location l) {
		String s = LocationSerialize.serializeFromLocation(l);
		synchronized (hashmap) {
			return hashmap.get(s);
		}
	}

	@Override
	public boolean isInHashmapForPlayer(Player p, Location l) {

		// Representant des Spielers
		String player_representant;
		try {
			// Hole Representanten
			player_representant = getRepresentant(p.getUniqueId());
		}
		// Spieler besitzt keinen Representanten --> Spieler nicht in PlayerMap
		catch (NullPointerException e) {
			// Resete Spieler
			resetGroupForPlayer(p);
			// Hole nun Representanten
			player_representant = getRepresentant(p.getUniqueId());

		}
		if (player_representant == null) {
			resetGroupForPlayer(p.getUniqueId(), p.getName());
		}

		String loc_representant;
		try {
			UUID loc_uuid = getUUIDFromHashMap(l);
			loc_representant = getRepresentant(loc_uuid);
		} catch (NullPointerException e) {
			loc_representant = null;
			return false;
		}
		if (loc_representant == null) {
			return false;
		}

		if (player_representant.equalsIgnoreCase("" + loc_representant)) {
			return true;
		}
		return false;
	}

	@Override
	public void addToKnotMap(Location l, Knot knot) {
		String s = LocationSerialize.serializeFromLocation(l);
		synchronized (knotmap) {
			knotmap.put(s, knot);
			saveKnotMap();
		}
	}

	@Override
	public void removeFromKnotMap(Location l) {
		String s = LocationSerialize.serializeFromLocation(l);
		synchronized (knotmap) {
			knotmap.remove(s);
			saveKnotMap();
		}
	}

	@Override
	public boolean isInKnotMap(Location l) {
		try {
			String s = LocationSerialize.serializeFromLocation(l);
			synchronized (knotmap) {
				return knotmap.containsKey(s);
			}
		} catch (NullPointerException e) {
			return false;
		}
	}

	@Override
	public Knot getKnot(Location l) {
		String s = LocationSerialize.serializeFromLocation(l);
		synchronized (knotmap) {
			return knotmap.get(s);
		}
	}

	@Override
	public void addToGarbageMap(Location l, UUID uuid) {
		if (l != null) {
			String s = LocationSerialize.serializeFromLocation(l);
			synchronized (treegarbagemap) {
				treegarbagemap.put(s, uuid);
				saveGarbageMap();
			}
		}
	}

	@Override
	public void removeFromGarbageMap(Location l) {
		if (l != null) {
			String s = LocationSerialize.serializeFromLocation(l);
			synchronized (treegarbagemap) {
				treegarbagemap.remove(s);
				saveGarbageMap();
			}
		}
	}

	@Override
	public void addToRootsGarbageMap(Location l) {
		if (l != null) {
			String s = LocationSerialize.serializeFromLocation(l);
			synchronized (blocksgarbagemap) {
				blocksgarbagemap.add(s);
				saveRootsGarbageMap();
			}
		}
	}

	@Override
	public void removeFromRootsGarbageMap(Location l) {
		String s = LocationSerialize.serializeFromLocation(l);
		synchronized (blocksgarbagemap) {
			blocksgarbagemap.remove(s);
			saveRootsGarbageMap();
		}
	}

	@Override
	public void addToPlayerMap(UUID uuid, String rep, String tree) {
		List<String> info = new ArrayList<String>();
		info.add(rep);
		info.add(tree);
		synchronized (playermap) {
			playermap.put(uuid, info);
			savePlayerMap();
		}
	}

	@Override
	public void changeTreeTypeForPlayerMap(UUID uuid, String tree) {
		List<String> info = new ArrayList<String>();
		info.add(getRepresentant(uuid));
		info.add(tree);
		synchronized (playermap) {
			playermap.put(uuid, info);
			savePlayerMap();
		}
	}

	@Override
	public void removeFromPlayerMap(UUID uuid) {
		synchronized (playermap) {
			playermap.remove(uuid);
			savePlayerMap();
		}
	}

	@Override
	public String getRepresentant(UUID uuid) {
		if (uuid == null) {
			return null;
		}
		synchronized (playermap) {
			List<String> list = playermap.get(uuid);
			return list.get(0);
		}
	}

	@Override
	public String getTreeForPlayer(UUID uuid) {
		synchronized (playermap) {
			List<String> list = playermap.get(uuid);
			return list.get(1);
		}
	}

	@Override
	public void resetGroupForPlayer(Player p) {
		resetGroupForPlayer(p.getUniqueId(), p.getName());
	}

	@Override
	public void resetGroupForPlayer(UUID uuid, String name) {
		try {
			removeFromPlayerMap(uuid);
		} // Entferne Spieler aus Playermap, falls nicht drinne wird abgefangen
			// un
		catch (NullPointerException e) {

		}
		try {
			addToPlayerMap(uuid, name, "None");
		} // Eigentlich kein Fehler aber, da wir an einer Stelle aus einer UUID
			// den Spieler bekommen
		// Wollten könnte es sein, dass zu diesem Spieler keine UUID zugehört
		catch (Exception ex) {

		}
		savePlayerMap();
	}

	@Override
	public void addToRootMap(Location l, Player p) {
		String s = LocationSerialize.serializeFromLocation(l);
		synchronized (rootmap) {
			rootmap.put(p.getUniqueId(), s);
			saveRootMap();
		}
	}

	@Override
	public Location getLocFromRootMap(Player p) {
		return getLocFromRootMap(p.getUniqueId());
	}

	@Override
	public Location getLocFromRootMap(UUID uuid) {
		try {
			synchronized (rootmap) {
				return LocationSerialize.serializeToLocation(rootmap.get(uuid));
			}
		} catch (NullPointerException e) {
			return null;
		}
	}

	@Override
	public boolean isInRootMap(Location l) {
		String s = LocationSerialize.serializeFromLocation(l);
		synchronized (rootmap) {
			return rootmap.containsValue(s);
		}
	}

	@Override
	public void RemoveFromRootMap(Player p) {
		RemoveFromRootMap(p.getUniqueId());
	}

	@Override
	public void RemoveFromRootMap(UUID uuid) {
		synchronized (rootmap) {
			rootmap.remove(uuid);
			saveRootMap();
		}
	}

	@Override
	public boolean checkIfRootAndBreakThen(Location l) {

		boolean root_broken = false;

		if (l != null) {
			String s = LocationSerialize.serializeFromLocation(l);

			// Prüfe ob Location eine Wurzel ist
			synchronized (rootmap) {
				root_broken = rootmap.containsValue(s);
				if (configs.getDebug_Mode()) {
					Bukkit.broadcastMessage("Root hit: " + root_broken);
				}

				// Wenn Wurzel dann
				if (root_broken) {
					// Hole owner uuid
					List<UUID> owner_of_root = new ArrayList<UUID>();
					owner_of_root = getKeysByValue(rootmap, s);

					// Hole owner Namen
					String owner_name = getRepresentant(owner_of_root.get(0));

					// Füge den Lösche auf jedenfall den Rootblock
					RemoveFromRootMap(owner_of_root.get(0));
					l.getBlock().setType(Material.AIR);

					if (configs.getDebug_Mode()) {
						Bukkit.broadcastMessage("Owner of Guild: " + owner_name);
					}

					// Hole alle Member der Gilde
					// Owner should be in
					List<UUID> guild_members = new ArrayList<UUID>();
					guild_members = getKeysByValueForPlayerMap(playermap,
							owner_name);

					if (configs.getDebug_Mode()) {
						Bukkit.broadcastMessage("Members in Guild: "
								+ guild_members.size());
					}

					for (UUID uuid : guild_members) {
						deleteAllBlocksFromUUID(uuid);
					}

				}
			}

			// Gib an ob Wurzel zerstört wurde
		}
		return root_broken;
	}

	@Override
	public void deleteAllBlocksFromUUID(UUID uuid) {
		// Hole Namen des Spielers
		synchronized (playermap) {
			String member_name = getRepresentant(uuid);

			// Resette Gilde des Spielers
			resetGroupForPlayer(uuid, member_name);
		}

		// Unnötig jeden einzelnen Spieler nach RootBlock zu frageb?
		// RemoveFromRootMap(uuid);

		// Hole Liste aller vorhandenen Bklöcke
		List<String> toChange = new ArrayList<String>();
		synchronized (hashmap) {
			toChange = getKeysByValue(hashmap, uuid);
		}

		// Sollen Blöcke Langsam zerfallen?
		if (configs.getDecay_tree()) {
			// Gehe durch alle Blöcke
			for (String s : toChange) {
				// Füge in GarbageMap hinzu
				Location l = LocationSerialize.serializeToLocation(s);
				addToGarbageMap(l, uuid);
			}
			// Ausgabe im DebugMode
			if (configs.getDebug_Mode()) {
				Bukkit.broadcastMessage("Put Tree in Garbage map: "
						+ toChange.size());
			}
		}
		// Sonst zerstöre alle Direkt
		else {
			// Gehe durch alle Blöcke
			for (String s : toChange) {
				// Lösche jeden einzeln
				Location l = LocationSerialize.serializeToLocation(s);
				l.getBlock().setType(Material.AIR);
				Core.getInstance().removeFromHashMap(l);
			}
			// Ausgabe im DebugMode
			if (configs.getDebug_Mode()) {
				Bukkit.broadcastMessage("Tree despawned with size : "
						+ toChange.size());
			}
		}
	}

	@Override
	public void mergeGroup(Player a, Player b) {
		String key_a = a.getName();
		String key_b = b.getName();
		Location p_a = getLocFromRootMap(a);
		Location b_l = getLocFromRootMap(b);
		String tree_a = getTreeForPlayer(a.getUniqueId());

		/**
		 * Sichere Wurzel des Spielers, falls der Spieler mit sich selber mischt
		 */
		if (!a.equals(b)) {
			// Hole alle Member von Spieler B
			List<UUID> toChange = new ArrayList<UUID>();
			synchronized (playermap) {
				toChange = getKeysByValueForPlayerMap(playermap, key_b);
			}
			// Laufe durch alle Member von Spieler B
			for (UUID s : toChange) {
				// Entferne zugehörigkeit von einzelnen Spieler
				removeFromPlayerMap(s);
				// Aktualisiere zugehörigkeit auf neuen Owner
				addToPlayerMap(s, key_a, tree_a);
			}

			Events.BlockBreaked(b_l.getBlock(), "Merge Groups");

		}
		b.setCompassTarget(p_a);
	}

	/**
	 * Gibt alle zugehörigen Spieler eines Vertreters wieder
	 * 
	 * @param playermap
	 * @param value
	 *            String Name des Vertreter
	 * @return list of Players (Keys)
	 */
	public static <T, E> List<UUID> getKeysByValue(
			HashMap<UUID, String> playermap2, String value) {
		List<UUID> keys = new ArrayList<UUID>();

		// Macht eine Kopie der HashMap, damit bei dem Orginal gearbeitet werden
		// kann, ohne das ein Eintag gelöscht wird hierbei
		HashMap<UUID, String> copy = (HashMap<UUID, String>) playermap2.clone();

		if (copy != null) {
			// Läufe durch alle UUID's in der PlayerMap
			for (Entry<UUID, String> entry : copy.entrySet()) {
				// Wenn Verteter des Spielers(UUID) zum übergebenen Vertreter
				// (value) übereinstimmt
				if (value.equals(entry.getValue())) {
					// Füge in Rückgabe Liste hinzu
					keys.add(entry.getKey());
				}
			}
		}
		return keys;
	}

	public static <T, E> List<UUID> getKeysByValueForPlayerMap(
			HashMap<UUID, List<String>> playermap2, String value) {
		List<UUID> keys = new ArrayList<UUID>();
		// Macht eine Kopie der HashMap, damit bei dem Orginal gearbeitet werden
		// kann, ohne das ein Eintag gelöscht wird hierbei
		HashMap<UUID, List<String>> copy = (HashMap<UUID, List<String>>) playermap2
				.clone();

		if (copy != null) {
			// Läufe durch alle UUID's in der PlayerMap
			for (Entry<UUID, List<String>> entry : copy.entrySet()) {
				// Wenn Verteter des Spielers(UUID) zum übergebenen Vertreter
				// (value) übereinstimmt
				if (value.equals(entry.getValue().get(0))) {
					// Füge in Rückgabe Liste hinzu
					keys.add(entry.getKey());
				}
			}
		}
		return keys;
	}

	/**
	 * Gibt alle Locations(String)/bzw. Blöcke eines Spielers wieder
	 * 
	 * @param hashmap
	 *            HashMap
	 * @param value
	 *            UUID vom Spieler
	 * @return list of Locations
	 */
	public static <T, E> List<String> getKeysByValue(
			HashMap<String, UUID> hashmap, UUID value) {
		List<String> keys = new ArrayList<String>();

		// Macht eine Kopie der HashMap, damit bei dem Orginal gearbeitet werden
		// kann, ohne das ein Eintag gelöscht wird hierbei
		HashMap<String, UUID> copy = (HashMap<String, UUID>) hashmap.clone();
		// Läufe durch alle Blöcke der HashMap
		if (copy != null) {
			for (Entry<String, UUID> entry : copy.entrySet()) {
				// Prüfe ob Block zur UUID gehört
				if (value.equals(entry.getValue())) {
					// Füge in Rückgabe Liste hinzu
					keys.add(entry.getKey());
				}
			}
		}
		return keys;
	}

	/**
	 * Gibt Liste von Locations(String)/bzw. Blöcke einer abgeschlagenen Wurzel
	 * wieder
	 * 
	 * @param rootsgarbagehashmap
	 *            HashMap
	 * @param value
	 *            String String Location
	 * @return list of Locations
	 */
	public static <T, E> List<String> getKeysByValueForRootsGarbageMap(
			HashMap<String, List<String>> hashmap, String value) {
		List<String> keys = new ArrayList<String>();

		// Macht eine Kopie der HashMap, damit bei dem Orginal gearbeitet werden
		// kann, ohne das ein Eintag gelöscht wird hierbei
		HashMap<String, List<String>> copy = (HashMap<String, List<String>>) hashmap
				.clone();
		// Läufe durch alle Blöcke der HashMap
		for (Entry<String, List<String>> entry : copy.entrySet()) {
			// Prüfe ob Block zur UUID gehört
			if (value.equals(entry.getValue())) {
				// Füge in Rückgabe Liste hinzu
				keys.add(entry.getKey());
			}
		}
		return keys;
	}

	@Override
	public void saveHashMap() {
		try {
			// Rufe FileManager auf und Speichere HashMap
			synchronized (hashmapfile) {
				synchronized (hashmap) {
					getFileManager().saveHashMap(hashmapfile, hashmap);
				}
			}
		} catch (FileNotFoundException e) {
			Bukkit.broadcastMessage("Error orcurred while saving HashMap");
			e.printStackTrace();
		}
	}

	@Override
	public void saveGarbageMap() {
		try {
			// Rufe FileManager auf und Speichere GarbageMap
			synchronized (treegarbagemapfile) {
				synchronized (treegarbagemap) {
			getFileManager().saveHashMap(treegarbagemapfile, treegarbagemap);
				}
			}
		} catch (FileNotFoundException e) {
			Bukkit.broadcastMessage("Error orcurred while saving HashMap");
			e.printStackTrace();
		}
	}

	@Override
	public void saveRootsGarbageMap() {
		try {
			// Rufe FileManager auf und Speichere GarbageMap
			synchronized (blockgarbagemapfile) {
				synchronized (blocksgarbagemap) {
			getFileManager().saveRootsGarbageMap(blockgarbagemapfile,
					blocksgarbagemap);
				}
			}
		} catch (FileNotFoundException e) {
			Bukkit.broadcastMessage("Error orcurred while saving RootsGarbageHashMap");
			e.printStackTrace();
		}
	}

	@Override
	public void saveRootMap() {
		try {
			// Rufe FileManager auf und Speichere RootMap
			synchronized (rootmapfile) {
				synchronized (rootmap) {
			getFileManager().saveRootMap(rootmapfile, rootmap);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void saveKnotMap() {
		try {
			// Rufe FileManager auf und Speichere RootMap
			synchronized (knotmapfile) {
				synchronized (knotmap) {
			getFileManager().saveKnotMap(knotmapfile, knotmap);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void savePlayerMap() {
		try {
			// Rufe FileManager auf und Speichere PlayerMap
			synchronized (playermapfile) {
				synchronized (playermap) {
			getFileManager().savePlayerMap(playermapfile, playermap);
				}
			}
		} catch (FileNotFoundException e) {
			Bukkit.broadcastMessage("Error orcurred while Saving PlayerMap");
			e.printStackTrace();
		}
	}

	/**
	 * Fragt die PlayerDamageListe ab ob sich ein Spieler in ihr befindet
	 * 
	 * @param p
	 *            Player
	 * @return true, wenn dem Spieler schaden zugefügt werden darf/soll
	 */
	private boolean containsPlayer(Player p) {
		synchronized (lp) {
			return lp.contains(p);
		}
	}

	@Override
	public void addPlayer(Player p) {
		if (!containsPlayer(p)) {
			synchronized (lp) {
				lp.add(p);
				p.sendMessage(Languages.getLeavingTreeMessage(p));
			}
		}
	}

	@Override
	public void removePlayer(Player p) {
		if (this.containsPlayer(p)) {
			synchronized (lp) {
				lp.remove(p);
				p.sendMessage(Languages.getFoundBackTreeMessage(p));
			}
		}
	}

	@Override
	public boolean containsPlayerReduceDamage(Player p) {
		synchronized (reduce_damage) {
			return reduce_damage.containsKey(p);
		}
	}

	@Override
	public void addPlayerReduceDamage(Player p, Integer i) {
		if (!containsPlayerReduceDamage(p)) {
			synchronized (reduce_damage) {
				reduce_damage.put(p, i);
			}
		}
	}

	@Override
	public void removePlayerReduceDamage(Player p) {
		if (this.containsPlayerReduceDamage(p)) {
			synchronized (reduce_damage) {
				reduce_damage.remove(p);
			}
		}
	}

	@Override
	public boolean vaildMat(Material mat_of_block) {
		for (Material vaild_mat : vaildMats) {
			if (vaild_mat == mat_of_block) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isChained(Player p, Location l) {
		return isChained(p, l, this.neighbors);
	}

	@Override
	public boolean isChained(Player p, Location l, int next) {
		List<String> list;
		switch (next) {
		case 26:
			list = DamageController.getNeighbors26(l);
			break;
		case 10:
			list = DamageController.getNeighbors10(l);
			break;
		default:
			list = DamageController.getNeighbors6(l);
			break;
		}

		// Laufe durch alle Locations durch
		for (String s : list) {
			Location loc = LocationSerialize.serializeToLocation(s);
			// Prüfe ob überhaupt gültiges Material
			if (vaildMat(loc.getBlock().getType())) {
				// Prüfe ob zugehörig für Spieler oder Grilde
				if (isInHashmapForPlayer(p, loc)) {
					return true;
				}
				if (Core.getInstance().isInRootMap(loc)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isChainedToLog(Player p, Location l) {
		List<String> list;
		switch (this.neighbors) {
		case 26:
			list = DamageController.getNeighbors26(l);
			break;
		case 10:
			list = DamageController.getNeighbors10(l);
			break;
		default:
			list = DamageController.getNeighbors6(l);
			break;
		}

		// Laufe durch alle Locations durch
		for (String s : list) {
			Location loc = LocationSerialize.serializeToLocation(s);
			// Prüfe ob überhaupt gültiges Material
			if (vaildMat(loc.getBlock().getType())) {
				// Prüfe ob zugehörig für Spieler oder Grilde
				if (isInHashmapForPlayer(p, loc)) {
					return true;
				} else {
				}
			}

		}
		return false;
	}

	@Override
	public List<Player> getDamageList() {
		return lp;
	}

	@Override
	public HashMap<Player, Integer> getReduceDamageMap() {
		return reduce_damage;
	}

	@Override
	public double getMaxDamageAmount() {
		return this.dc.getDamageAmount();
	}

	@Override
	public Thread getDamageThread() {
		return damagethread;
	}

	@Override
	public Configurations getConfigurations() {
		return instance.configs;
	}

	public static Messages getMessenger() {
		return instance.messages;
	}

	@Override
	public Player getPlayer(String playername) {
		Player p = null;
		Player[] po = getServer().getOnlinePlayers();
		for (Player t : po) {
			if (t.getName().toLowerCase()
					.contentEquals(playername.toLowerCase())) {
				p = t;
			}
		}

		return p;
	}

	@Override
	public FileManagerAPI getFileManager() {
		return this.fm;
	}

	@Override
	public CreateMenuAPI getCreateMenu() {
		return this.cm;
	}

	@Override
	public DamageController getDamageController() {
		return this.dc;
	}

	@Override
	public File getPluginDir() {
		return this.plugindir;
	}

	@Override
	public File getPluginDirData() {
		return this.plugindirdata;
	}

	@Override
	public File getPlugindirDataTrees() {
		return this.plugindirdatatrees;
	}

	@Override
	public File getPlugindirDataPlayers() {
		return this.plugindirdataplayers;
	}

	@Override
	public File getPlugindirDataTreeGroups() {
		return this.plugindirdatatreegroups;
	}

	@Override
	public File getPlugindirDataLanguages() {
		return this.plugindirdatalanguages;
	}

	@Override
	public ScoreboardController getScoreboardController() {
		return sbc;
	}

	@Override
	public void saveAllFiles() {
		this.saveGarbageMap();
		this.saveHashMap();
		this.savePlayerMap();
		this.saveRootMap();
		this.saveRootsGarbageMap();
	}

	@Override
	public void PlaceLog(Location l, Material m, Player p) {
		Events.PlaceLog(l, m, p);
	}

}