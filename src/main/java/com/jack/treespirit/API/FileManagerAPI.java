package com.jack.treespirit.API;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.jack.treespirit.knots.Knot;

public interface FileManagerAPI {
	
	public void saveHashMap(File f, HashMap<String, UUID> map) throws FileNotFoundException;
	public HashMap<String, UUID> getHashMapFromFile(File f);	
	public void savePlayerMap(File f, HashMap<UUID, List<String>> playermap) throws FileNotFoundException;
	public HashMap<UUID, List<String>> getPlayerMapFromFile(File f);
	public void saveRootMap(File f, HashMap<UUID, String> rootmap) throws FileNotFoundException;
	public HashMap<UUID, String> getRootMapFromFile(File f);
	public void saveRootsGarbageMap(File f, List<String> rootmap) throws FileNotFoundException;
	public List<String> getRootsGarbageMapFromFile(File rootsgarbagemapfile);
	public void saveKnotMap(File knotmapfile, HashMap<String, Knot> knotmap) throws FileNotFoundException;
	public HashMap<String, Knot> getKnotMapFromFile(File rootsgarbagemapfile);
	public void saveCustomTree(File treefile, List<String> tree) throws FileNotFoundException;
	public List<String> getCustomTree(File treefile);
}
