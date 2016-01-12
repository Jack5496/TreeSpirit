package com.jack.treespirit.functions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class LocationSerialize {

	
	public static String serializeFromLocation(Location l) {
		int x = (int) l.getX();
		int y = (int) l.getY();
		int z = (int) l.getZ();
		String world = l.getWorld().getName();
		String ll = world+ "#" + x +"#" + y +"#" + z;
		return ll;
	}
	
	public static Location serializeToLocation(String s) {
		String[] a = s.split("#");
		World w = Bukkit.getWorld(a[0]);
		double x = 0.0;
		double y = 0.0;
		double z = 0.0;
		try {
			x = Double.parseDouble(a[1]);
			y = Double.parseDouble(a[2]);
			z = Double.parseDouble(a[3]);
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
		Location l = new Location(w, x, y, z);
		return l;
	}
	
	public static String serializeFromBlock(Block b) {
		String ll = serializeFromLocation(b.getLocation());
		ll = ll+"#"+b.getTypeId()+"#"+b.getData();
		return ll;
	}
	
	private static Object[] serializeToBlockIDAndData(String s) {
		String[] a = s.split("#");
		
		int id = 0;
		byte data = 0;
		try {
			id = Integer.parseInt(a[4]);
			data = (byte)Integer.parseInt(a[5]);
		} catch(NumberFormatException e) {
			e.printStackTrace();
		}
		Object[] back = new Object[2];
		back[0] = id;
		back[1] = data;
		return back;
	}
	
	/**
	 * [0] Location | [1] ID | [2] Data
	 * Gibt ein info[3] zurrück
	 * @param s Block Serialized
	 * @return
	 */
	public static Object[] getInformation(String s){
		Object[] info = new Object[3];
		info[0] = serializeToLocation(s);
		Object[] idAndData = serializeToBlockIDAndData(s);
		info[1] = idAndData[0];
		info[2] = idAndData[1];
		return info;
	}
	
	
	
	
}
