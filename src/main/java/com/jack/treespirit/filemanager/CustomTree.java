package com.jack.treespirit.filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.jack.treespirit.Core;
import com.jack.treespirit.data.ResourceLoader;
import com.jack.treespirit.events.Events;
import com.jack.treespirit.functions.LocationSerialize;

public class CustomTree {

	/**
	 * Exact Save Command used by Easy Save Command
	 * 
	 * @param f
	 *            File of HashMap
	 * @param map
	 *            HashMap
	 * @throws FileNotFoundException
	 */
	public static void saveCustomTree(File treefile, List<String> tree, int i,
			int max) throws FileNotFoundException {
		Core.getInstance().checkIfPluginDirsExist();
		if (i < max) {
			try {
				treefile.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try (FileOutputStream fis = new FileOutputStream(treefile.getPath());
					ObjectOutputStream o = new ObjectOutputStream(fis)) {
				o.writeObject(tree);
				o.flush();
				o.close();
			} catch (Exception e) {

				Bukkit.broadcastMessage("Couldn't serialize HashMap to file: atempt: "
						+ i);
				saveCustomTree(treefile, tree, i + 1, max);
			}
		}
	}

	public static void saveCustomTree(File treefile, String tree, int i, int max)
			throws FileNotFoundException {
		Core.getInstance().checkIfPluginDirsExist();
		if (i < max) {
			try {
				treefile.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try (FileOutputStream fis = new FileOutputStream(treefile.getPath());
					ObjectOutputStream o = new ObjectOutputStream(fis)) {
				o.writeObject(tree);
				o.flush();
				o.close();
			} catch (Exception e) {

				Bukkit.broadcastMessage("Couldn't serialize HashMap to file: atempt: "
						+ i);
				saveCustomTree(treefile, tree, i + 1, max);
			}
		}
	}

	/**
	 * Gets the Right Tree
	 * @param treefile
	 * @param i Startnumber
	 * @param max Int tryes
	 * @return
	 */
	public static List<String> getCustomTree(File treefile, int i, int max) {
		if (i < max) {
			try (FileInputStream inputFileStream = new FileInputStream(treefile);
					ObjectInputStream objectInputStream = new ObjectInputStream(
							inputFileStream);) {
				List<String> map = (List<String>) objectInputStream
						.readObject();
				objectInputStream.close();
				inputFileStream.close();
				return map;
			} catch (Exception e) {

				Bukkit.broadcastMessage("Load Tree from File atempt: "
						+ i);
				return getCustomTree(treefile, i + 1, max);
			}
		}
		return null;
	}
	
	/**
	 * Genenerate a Tree for Player and TreeType
	 * @param l Location where should spawn
	 * @param name String Name of TreeType
	 * @param p Player for which Tree should spawn
	 */
	public static void spawnTree(Location l, String name, final Player p) {
		File loadFile = ResourceLoader.getFileByName(name);
		if(!loadFile.exists()){
			loadFromResource(loadFile);
		}
		Bukkit.broadcastMessage("Name of Sapling: "+name);
		Bukkit.broadcastMessage("Get Tree From File: "+loadFile.getPath());
		final List<String> build = getCustomTree(loadFile, 1, 10);
		final Location la = l.clone();
		new Thread()
        {
            public void run() {
            	for (String s : build) {
        			Object[] info = LocationSerialize.getInformation(s);
        			Location loc = (Location) info[0];
        			loc.add(la);
        			int id = (int) info[1];
        			byte data = (byte) info[2];
        			loc.getBlock().setType(Material.getMaterial(id));
        			loc.getBlock().setData(data);
        			Events.PlaceLog(loc, Material.getMaterial(id), p);
        			if (Material.getMaterial(id) == Material.LEAVES
        					|| Material.getMaterial(id) == Material.LEAVES_2) {
        				Core.getInstance().addToHashMap(loc, p);
        			}
        			try {
						this.sleep(50L);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		}
            }
        }.start();
		
	}

	public static void loadFromResource(File f) {
		try {
			if (!f.exists()) {
				File par = f.getParentFile();
				CopyFile("/Resource/" + par.getName() +"/"+ f.getName(), f);
			}
		} catch (Exception e) {
		}
	}

	private static void CopyFile(String von, File outFile) {
		try {
			outFile.createNewFile();
			InputStream in = getResource(von);
			OutputStream out = new FileOutputStream(outFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			in.close();
		} catch (Exception e) {
		}
	}

	private static InputStream getResource(String fileName) {
		try {
			URL url = Core.getInstance().getClass().getResource(fileName);
			if (url == null) {
				return null;
			}
			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			return connection.getInputStream();
		} catch (Exception e) {
		}
		return null;
	}
}
