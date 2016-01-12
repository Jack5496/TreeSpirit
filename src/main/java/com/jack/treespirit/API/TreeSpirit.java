package com.jack.treespirit.API;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.jack.treespirit.ce.PendingTimer;
import com.jack.treespirit.data.Configurations;
import com.jack.treespirit.knots.Knot;
import com.jack.treespirit.threads.DamageController;
import com.jack.treespirit.threads.ScoreboardController;

public interface TreeSpirit {

	public abstract void onEnable();

	public abstract void createAndStartThreadController();

	public abstract void loadFiles() throws IOException;

	public abstract void onDisable();

	public abstract void addToHashMap(Location l, Player p);

	public abstract void addToHashMap(Location l, UUID uuid);

	public abstract void removeFromHashMap(Location l);

	/**
	 * Checks if a Location is in the Database
	 * @param l Location which could be in
	 * @return boolean true if Block is in HashMap
	 */
	public abstract boolean isInHashMap(Location l);

	/**
	 * Check better before isInHashMap(Locaion l).
	 * Get the UUID of the Block player for a spez. Location
	 * @param l Location from which you want to check
	 * @return UUID or Null
	 */
	public abstract UUID getUUIDFromHashMap(Location l);

	public abstract boolean isInHashmapForPlayer(Player p, Location l);

	public abstract void addToKnotMap(Location l, Knot knot);

	public abstract void removeFromKnotMap(Location l);

	public abstract boolean isInKnotMap(Location l);

	public abstract Knot getKnot(Location l);

	public abstract void addToGarbageMap(Location l, UUID uuid);

	public abstract void removeFromGarbageMap(Location l);

	public abstract void addToRootsGarbageMap(Location l);

	public abstract void removeFromRootsGarbageMap(Location l);

	public abstract void addToPlayerMap(UUID uuid, String rep, String tree);

	public abstract void removeFromPlayerMap(UUID uuid);

	/**
	 * Recieve the Owners Name of a UUID
	 * @param uuid UUID which you want to know the Owner
	 * @return
	 */
	public abstract String getRepresentant(UUID uuid);

	/**
	 * Resets the Group of a Player
	 * @param p Player which should be resetted
	 */
	public abstract void resetGroupForPlayer(Player p);

	/**
	 * Resets the Players Group (set to "None")
	 * @param uuid UUID of the Player
	 * @param name String name of the Player
	 */
	public abstract void resetGroupForPlayer(UUID uuid, String name);

	/**
	 * Adds a Location to the RootMap for a Player
	 * @param l Location of RootBlock
	 * @param p Player which is now the Owner
	 */
	public abstract void addToRootMap(Location l, Player p);

	/**
	 * Gives the Location of the RootBlock from a Player
	 * @param p Player from which Tree is Asked
	 * @return Location of the RootBlock || Null if there is no
	 */
	public abstract Location getLocFromRootMap(Player p);

	/**
	 * Gives the Location of the RootBlock from a PlayerUUID
	 * @param uuid UUID from which Tree is Asked
	 * @return Location of the RootBlock || Null if there is no
	 */
	public abstract Location getLocFromRootMap(UUID uuid);

	/**
	 * Checks if a Location is a RootBlock
	 * @param l Location which should be checked
	 * @return boolean true if this Location is a RootBlock
	 */
	public abstract boolean isInRootMap(Location l);

	/**
	 * Removes the RootBlock from the List of RootBlocks for a Player
	 * This Player must be the owner.
	 * @param p Player which is the owner of the Tree
	 */
	public abstract void RemoveFromRootMap(Player p);

	/**
	 * Removes the RootBlock from the List of RootBlocks for a PlayerUUID
	 * This PlayerUUID must be the owner.
	 * @param uuid PlayerUUID which is the owner of the Tree
	 */
	public abstract void RemoveFromRootMap(UUID uuid);

	/**
	 * Checks if a Locatio is a RootBlock, and changes the Block itself into Log
	 * @param l Location which will be checked
	 * @return boolean true if Block was a RootBlock
	 */
	public abstract boolean checkIfRootAndBreakThen(Location l);

	/**
	 * Remove all Blocks from a Player UUID, so that in the Database there
	 * are no more Information about placed Blocks
	 * @param uuid UUID of the Player which will loose all Blocks
	 */
	public abstract void deleteAllBlocksFromUUID(UUID uuid);

	/**
	 * Merge Player A to Player B's Tree, both belongs now to the same Tree
	 * @param a Player which will transfer
	 * @param b Player which will get a new Member
	 */
	public abstract void mergeGroup(Player a, Player b);

	public abstract void saveHashMap();

	public abstract void saveGarbageMap();

	public abstract void saveRootsGarbageMap();

	public abstract void saveRootMap();

	public abstract void saveKnotMap();

	public abstract void savePlayerMap();

	/**
	 * Method to add a Player into the DamageList
	 * @param p Player which will recieve then Damage
	 */
	public abstract void addPlayer(Player p);

	/**
	 * Method to remove a Player from the DamageList
	 * @param p Player which wont recieve any Damage
	 */
	public abstract void removePlayer(Player p);

	/**
	 * Checks if a Material is in the Config Material List
	 * @param mat_of_block Material which should be checked
	 * @return boolean true if Material is in the List
	 */
	public abstract boolean vaildMat(Material mat_of_block);

	/**
	 * Checks if Block belongs to a Tree of the Player, if it is a Vail Material.
	 * Which Neighbors, which are set In Config( if yo want to spez. it, add int)
	 * @param p Player
	 * @param l Location which should be checked
	 * @return boolean true if Block is Chained
	 */
	public abstract boolean isChained(Player p, Location l);

	/**
	 * Checks if Block belongs to a Tree of the Player, if it is a Vaild Material
	 * @param p Player for which should be checked
	 * @param l Location where Block stands
	 * @param next int, How many Neighbors should be chekced (if you dont know leave it)
	 * @return boolean true if Block is chained
	 */
	public abstract boolean isChained(Player p, Location l, int next);

	/**
	 * Checks if A Block is Chained to Log ( Locatio must be Log)
	 * @param p Player for which should be checked
	 * @param l Location where the Block is
	 * @return boolean true if Block is Chained
	 */
	public abstract boolean isChainedToLog(Player p, Location l);

	public abstract List<Player> getDamageList();

	public abstract Thread getDamageThread();

	public abstract Configurations getConfigurations();

	public abstract Player getPlayer(String playername);

	public abstract FileManagerAPI getFileManager();

	public abstract CreateMenuAPI getCreateMenu();

	public abstract File getPluginDir();

	public abstract File getPluginDirData();

	public abstract ScoreboardController getScoreboardController();

	public abstract void saveAllFiles();

	/**
	 * Place a Block for a Player (use Log)
	 * @param l Location where Block should be created
	 * @param m Material of the Block
	 * @param p Player for which it will be placed
	 */
	public abstract void PlaceLog(Location l, Material m, Player p);

	/**
	 * Change the TreeType of a Player
	 * @param uuid UUID of the Player
	 * @param tree String TreeType
	 */
	public abstract void changeTreeTypeForPlayerMap(UUID uuid, String tree);

	/**
	 * Checks if Player gets Reduced Damage, Safe Method
	 * @param p Player
	 * @return boolean true if Player recieve reduced Damage
	 */
	public abstract boolean containsPlayerReduceDamage(Player p);

	
	/**
	 * Adds a Player Damage reduce (damage -= reduce) Permanent
	 * @param p Player which recieve Damage reduce
	 * @param i Reduce
	 * 
	 */
	public abstract void addPlayerReduceDamage(Player p, Integer i);
	
	/**
	 * Remove Player from Damage Reduce List
	 * @param p Player 
	 */
	public abstract void removePlayerReduceDamage(Player p);

	/**
	 * Unsafe Method
	 * get Map of all Player which recieve Reduced Damage
	 * || Better use "containsPlayerReduceDamage(Player p)"
	 * @return HashMap<Player, Integer>
	 */
	public abstract HashMap<Player, Integer> getReduceDamageMap();

	public abstract double getMaxDamageAmount();
	
	public abstract DamageController getDamageController();

	public abstract File getPlugindirDataTrees();

	public abstract String getTreeForPlayer(UUID uuid);

	public abstract File getPlugindirDataPlayers();

	public abstract File getPlugindirDataLanguages();

	public abstract File getPlugindirDataTreeGroups();

}