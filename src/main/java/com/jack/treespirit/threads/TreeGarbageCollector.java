package com.jack.treespirit.threads;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import com.jack.treespirit.Core;
import com.jack.treespirit.functions.LocationSerialize;

public class TreeGarbageCollector implements Runnable{

	private static int delay = 10*1000;

	private boolean activ = true;
	
	public static HashMap<String, UUID> garbagemap;
	
	public TreeGarbageCollector(){
		updateMap();
	}

	@Override
	public void run() {
		while(activ){

			List<UUID> user_in_garbage = new ArrayList<UUID>();

			for(Entry<String, UUID> entry: garbagemap.entrySet()) {
				if(!user_in_garbage.contains(entry.getValue())){
					user_in_garbage.add(entry.getValue());
				}
		    }
			
			for(UUID uuid : user_in_garbage){
				List<String> locs = Core.getKeysByValue(garbagemap, uuid);
				
				int loc_list_size = locs.size();
				
				Random randomLocGenerator = new Random();
				int loc_index = randomLocGenerator.nextInt(locs.size());
				
				String s_loc = locs.get(loc_index);
				
				Location l = LocationSerialize.serializeToLocation(s_loc);
				
				while(l.getBlock().getType() == Material.AIR){
					Core.getInstance().removeFromGarbageMap(l);
					Core.getInstance().removeFromHashMap(l);
					
					loc_list_size = locs.size();
					loc_index = randomLocGenerator.nextInt(locs.size());
					
					s_loc = locs.get(loc_index);
					
					l = LocationSerialize.serializeToLocation(s_loc);
				}
				
				if(l.getBlock().getType() == Material.SAPLING){
					l.getBlock().setType(Material.AIR);
					if(Core.getInstance().getConfigurations().getDebug_Mode()){
						Bukkit.broadcastMessage("Sapling gammelte ab");
					}
				}
				if(l.getBlock().getType() == Material.LOG){
					byte data = l.getBlock().getData();
					data = (byte) (data + (byte)3);
					l.getBlock().setData(data);
					if(Core.getInstance().getConfigurations().getDebug_Mode()){
						Bukkit.broadcastMessage("Log wurde zu Jungle");
					}
				}		
				if(l.getBlock().getType() == Material.GLOWSTONE){
					l.getBlock().setData((byte) 3);
					if(Core.getInstance().getConfigurations().getDebug_Mode()){
						Bukkit.broadcastMessage("Old Root found");
					}
				}		
				if(l.getBlock().getType() == Material.LEAVES){
					l.getBlock().setType(Material.AIR);
					try{
						l.getWorld().spawnFallingBlock(l, Material.LEAVES, (byte)3);
					}
					catch(Exception e){
						
					}
					if(Core.getInstance().getConfigurations().getDebug_Mode()){
						Bukkit.broadcastMessage("Leave fiel runter");
					}
				}		
				
				Core.getInstance().removeFromGarbageMap(l);
				Core.getInstance().removeFromHashMap(l);
			}
			
			
			
			
			
			try {
//				Core.getInstance().saveAllFiles();
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			updateMap();
		}
	}
	
	public void stopMe(){
		this.activ = false;
	}
	
	
	private static void updateMap(){
		garbagemap = Core.getInstance().treegarbagemap;
	}

}
