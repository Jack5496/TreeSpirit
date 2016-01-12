package com.jack.treespirit.functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.jack.treespirit.Core;
import com.jack.treespirit.events.Events;
import com.jack.treespirit.filemanager.CustomTree;

public class SaveNewTree implements Runnable {

	private String save_name;
	private int max;
	private Location begin;
	private Player p;

	public SaveNewTree(String save_name, Location begin, int max, Player p) {
		this.save_name = save_name;
		this.begin = begin;
		this.max = max;
		this.p = p;
	}

	@Override
	public void run() {
		Bukkit.broadcastMessage("Saving new Tree: " + save_name);

		Location begin_c = begin.clone();
		List<Location> save = giveMeAll(begin_c, max);
		List<String> tree = new ArrayList<String>();
		for (Location l : save) {
			Location adj = l.clone();
			adj.add(new Vector(-begin_c.getBlockX(), -begin_c.getBlockY()-1,	//-1 da irgendwie das nicht ganz will
					-begin_c.getBlockZ()));
			String s = LocationSerialize.serializeFromLocation(adj)+"#"+l.getBlock().getTypeId()+"#"+l.getBlock().getData();
			tree.add(s);
		}
		File savedir = new File(Core.getInstance().getPlugindirDataTrees()
				+ "/" + save_name + ".ser");
		try {
			Core.getInstance().getFileManager().saveCustomTree(savedir, tree);
			p.sendMessage("Save succses");
		} catch (FileNotFoundException e) {
			p.sendMessage("Tree Save failed");
		}

	}

	public static List<Location> giveMeAll(Location l, int max) {
		// Macht eine Koopie
		int amount = 0;
		List<Location> besucht = new ArrayList<Location>();
		List<Location> noch_zu_prüfen = new ArrayList<Location>();
		noch_zu_prüfen.add(l);		

		while (!noch_zu_prüfen.isEmpty() && amount < max) {
			Location loc = noch_zu_prüfen.get(0);
			if(Core.getInstance().vaildMat(loc.getBlock().getType())){
			besucht.add(loc);	//Es wird mit dem untersten angefangn
			}
			List<Location> neig = getNeighbors26(loc);
			for (Location le : neig) {
				if (Core.getInstance().vaildMat(le.getBlock().getType())) {
					if(!besucht.contains(le)){
						noch_zu_prüfen.add(le);
						besucht.add(le);
					}
				}
			}
			noch_zu_prüfen.remove(0);
			amount++;
		}
		if(amount>max-1){
		Bukkit.broadcastMessage("Amount reached limit: "+amount);
		Bukkit.broadcastMessage("Better retry with larger max");
		}
		return besucht;

	}


	public static List<Location> getNeighbors26(Location l) {
		List<Location> back = new ArrayList<Location>();

		int x = (int) l.getX();
		int y = (int) l.getY();
		int z = (int) l.getZ();

		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				for (int w = -1; w < 2; w++) {
					if (!(i == 0 && j == 0 && w == 0)) {
						Location s = new Location(l.getWorld(), (i + x),
								(j + y), (z + w));
						back.add(s);
					}
				}
			}
		}

		return back;
	}
}
