package com.jack.treespirit.filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.jack.treespirit.Core;
import com.jack.treespirit.API.FileManagerAPI;
import com.jack.treespirit.knots.Knot;

public class FileManager implements FileManagerAPI{
	
	/**
	 * 
	 * 		HASHMAP
	 * 
	 */
	static int max = 10;
	
	/**
	 * Exact Save Command used by Easy Save Command
	 * @param f File of HashMap
	 * @param map HashMap
	 * @throws FileNotFoundException
	 */	
	@Override
	public void saveHashMap(File f, HashMap<String, UUID> map) throws FileNotFoundException{
		MapHash.saveHashMap(f, map,1,max);
	}
	
	/**
	 * Returns the HashMap file from Local place
	 * @param f
	 * @return
	 */
	@Override
	public HashMap<String, UUID> getHashMapFromFile(File f){
		return MapHash.getHashMapFromFile(f,1,max);
	}	
	
	
	

	/**
	 * 
	 * 		PLAYERMAP
	 * 
	 */
	
	/**
	 * Exat Safe Command for PlayerMap
	 * @param f File od PlayerMap
	 * @param playermap2 PlayerMap
	 * @throws FileNotFoundException
	 */
	@Override
	public void savePlayerMap(File f, HashMap<UUID, List<String>> playermap) throws FileNotFoundException{
		PlayerMap.savePlayerMap(f, playermap,1,max);
	}
	
	/**
	 * Get PlayerMap from predefined File
	 * @param f File of PlayerMap
	 * @return
	 */
	@Override
	public HashMap<UUID, List<String>> getPlayerMapFromFile(File f){
		return PlayerMap.getPlayerMapFromFile(f,1,max);
	}
	
	
	
	
	
	
	/**
	 * 
	 * 		ROOTMAP
	 * 
	 */
	
	
	
	/**
	 * Exact Safe Command for RootMap
	 * @param f File of RootMap
	 * @param rootmap RootMap
	 * @throws FileNotFoundException
	 */
	@Override
	public void saveRootMap(File f, HashMap<UUID, String> rootmap) throws FileNotFoundException{
		RootMap.saveRootMap(f, rootmap,1,max);
	}
	
	/**
	 * Läd die WurzelBlock Map
	 * @param f File of RootMap
	 * @return
	 */
	@Override
	public HashMap<UUID, String> getRootMapFromFile(File f){
		return RootMap.getRootMapFromFile(f,1,max);
	}
	
	/**
	 * Exact Safe Command for RootsGarbageMap
	 * @param f File of RootsGarbageMap
	 * @param rootmap RootsGarbageMap
	 * @throws FileNotFoundException
	 */
	@Override
	public void saveRootsGarbageMap(File f, List<String> rootmap) throws FileNotFoundException{
		RootsGarbageMap.saveRootsGarbageMap(f, rootmap,1,max);
	}
	
	/**
	 * Läd die Abgetrenten Wurzel Maps
	 * @param rootsgarbagemapfile
	 * @return
	 */
	@Override
	public List<String> getRootsGarbageMapFromFile(File rootsgarbagemapfile) {
		return RootsGarbageMap.getRootsGarbageMapFromFile(rootsgarbagemapfile,1,max);
	}
	
	
	
	
	/**
	 * Speichert die Knotmap
	 * @param knotmapfile
	 * @param knotmap
	 * @throws FileNotFoundException
	 */
	@Override
	public void saveKnotMap(File knotmapfile, HashMap<String, Knot> knotmap) throws FileNotFoundException {
		KnotMap.saveKnotMap(knotmapfile, knotmap, 1,max);
	}	
	
	/**
	 * Läd die Abgetrenten Wurzel Maps
	 * @param rootsgarbagemapfile
	 * @return
	 */
	@Override
	public HashMap<String, Knot> getKnotMapFromFile(File rootsgarbagemapfile) {
		return KnotMap.getKnotMapFromFile(rootsgarbagemapfile, 1,max);
	}
	
	@Override
	public void saveCustomTree(File treefile, List<String> tree) throws FileNotFoundException {
		CustomTree.saveCustomTree(treefile, tree, 1, max);
	}	

	@Override
	public List<String> getCustomTree(File treefile) {
		return CustomTree.getCustomTree(treefile, 1,max);
	}
}
