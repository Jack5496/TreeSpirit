package com.jack.treespirit.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.jack.treespirit.Core;
import com.jack.treespirit.events.Events;

public class OverChange implements Runnable{
	
	private String player_name;
	private Location start;
	private Location end;
	private Player sender;

	public OverChange(String player_name, Location start, Location end, Player sender){
		this.player_name = player_name;
		this.start = start.clone();
		this.end = end.clone();
		this.sender = sender;
	}
	
	
	
	@Override
	public void run() {
		Player p = Bukkit.getPlayer(player_name);
		UUID player_uuid = p.getUniqueId();
		
		if(Core.getInstance().getConfigurations().getDebug_Mode()){
			Bukkit.broadcastMessage("Check Worlds");
		}
		if(start.getWorld() != end.getWorld()){
			sender.sendMessage("Error! Different Worlds!");
			return;
		}
		if(Core.getInstance().getConfigurations().getDebug_Mode()){
			Bukkit.broadcastMessage("Worlds ok");
		}
		
		int sx, sy, sz, ex ,ey , ez;
		
		if(start.getX() < end.getX()){
			sx = (int) start.getX();
			ex = (int) end.getX();
		}
		else{
			ex = (int) start.getX();
			sx = (int) end.getX();
		}
		if(start.getY() < end.getY()){
			sy = (int) start.getY();
			ey = (int) end.getY();
		}
		else{
			ey = (int) start.getY();
			sy = (int) end.getY();
		}
		if(start.getZ() < end.getZ()){
			sz = (int) start.getZ();
			ez = (int) end.getZ();
		}
		else{
			ez = (int) start.getZ();
			sz = (int) end.getZ();
		}
				
		int progress = 0;
		int progress_old = 0;
		int done_blocks = 0;
		int percent = 0;
		int procent_ticks = 5;
		
		int total_amount_blocks = (ex-sx)*(ey-sy)*(ez-sz);
		
		sender.sendMessage("Start Import of "+total_amount_blocks+" Amount of Blocks");
		
		int amount = 0;
		int importedBlock = 0;
		
		List<Location> updateLater = new ArrayList<Location>();
		
		for(int x=sx; x<ex; x++){
			for(int y=sy; y<ey; y++){
				for(int z=sz; z<ez; z++){
					String sl = start.getWorld().getName()+"#"+x+"#"+y+"#"+z;
					Location l = LocationSerialize.serializeToLocation(sl);
					if(l.getBlock().getType() == Material.SAPLING){
						if(!Core.getInstance().isInHashMap(l)){
							Core.getInstance().addToHashMap(l, player_uuid);
							importedBlock++;
						}
					}
					else if(Core.getInstance().vaildMat(l.getBlock().getType())){
						PlaceLog(l,l.getBlock().getType(), p);
						importedBlock++;
						updateLater.add(l);
					}
					amount++;
					
					
					double multi = (100.0/(double)total_amount_blocks);
					done_blocks++;
					percent = (int) (((double)done_blocks)*multi);
					progress = (percent/procent_ticks)*procent_ticks;
//					Bukkit.broadcastMessage("Done Blocks: "+done_blocks+" of "+total_amount_blocks+" exact "+percent+"& rounded "+progress+"%");
					if(progress > progress_old){
						sender.sendMessage("Import Progress: "+progress+" %  |  Imported Blocks: "+importedBlock);
					}
					progress_old = progress;
					
				}
			}
		}
		sender.sendMessage("");
		sender.sendMessage("Updating Blocks");
		int updateAmount = updateLater.size();
		int updateNumber = 0;
		for(Location l : updateLater){
			updateBlock(l,p);
			double multi = (100.0/(double)updateAmount);
			updateNumber++;
			percent = (int) (((double)updateNumber)*multi);
			progress = (percent/procent_ticks)*procent_ticks;
//			Bukkit.broadcastMessage("Done Blocks: "+done_blocks+" of "+total_amount_blocks+" exact "+percent+"& rounded "+progress+"%");
			if(progress > progress_old){
				sender.sendMessage("Update Progress: "+progress+" % ");
			}
			progress_old = progress;
		}
		
		sender.sendMessage("Checked Blocks : "+amount+" | Found Importable Blocks: "+importedBlock);
		
	}
	
	public void PlaceLog(Location l, Material m, Player p) {
		Material mat_of_block = m;
		if (mat_of_block == Material.LOG) {
				if (Core.getInstance().getConfigurations()
						.getOnly_Walk_on_Log()) {
					if (mat_of_block == Material.LOG) {
						if (Core.getInstance().getConfigurations()
								.getDebug_Mode()) {
							Bukkit.broadcastMessage(ChatColor.GOLD
									+ "[TreeSpirit]" + ChatColor.WHITE
									+ " - Log is Chained");
						}

						if(!Core.getInstance().isInHashMap(l)){
							Core.getInstance().addToHashMap(l, p);	
						}
						
						

					}
				} else {
					if (Core.getInstance().getConfigurations().getDebug_Mode()) {
						Bukkit.broadcastMessage(ChatColor.GOLD + "[TreeSpirit]"
								+ ChatColor.WHITE + " - Block is Chained");
					}
					if(!Core.getInstance().isInHashMap(l)){
						Core.getInstance().addToHashMap(l, p);	
					}
					
				}

		}
	}
	
	public void updateBlock(Location l, Player p){
		Events.checkForKnot(l, p);
		Events.checkForConnectionBetweenKnots(l, p);
	}
}
