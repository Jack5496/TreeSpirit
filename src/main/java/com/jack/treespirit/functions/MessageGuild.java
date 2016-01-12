package com.jack.treespirit.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jack.treespirit.Core;
import com.jack.treespirit.languages.Languages;

public class MessageGuild {

	public static void messageGuild(Player p, String message) {
		messageGuild(p.getUniqueId(), message);
	}
	public static void messageGuild(UUID p_uuid, String message) {
		messageGuild(p_uuid, message, "");
	}
	
	public static void messageGuild(Player p, String message, String args) {
		messageGuild(p.getUniqueId(), message, args);
	}
	public static void messageGuild(UUID uuid, String message, String args) {
		messageGuild(uuid, message, args, "");
	}
	
	public static void messageGuild(Player p, String message, String args, String args2) {
		messageGuild(p.getUniqueId(), message, args, args2);
	}	
	public static void messageGuild(UUID p_uuid, String message, String args, String args2) {
		List<UUID> guild_members = getAllGuildMembers(p_uuid);
		
		for(UUID uuid : guild_members){
			try{
				OfflinePlayer p = Bukkit.getOfflinePlayer(uuid);
				if(p.isOnline()){
					Player p_online = (Player) p;
					String languagemsg = Languages.getGuildMessage(message, p_online, args);
					((Player) p).sendMessage(languagemsg);
				}
			}
			catch(Exception e){
				Bukkit.broadcastMessage("Message Guild Members failed");
			}
		}
	}
	
	public static List<UUID> getAllGuildMembers(Player p){
		return getAllGuildMembers(p.getUniqueId());
	}
	
	public static List<UUID> getAllGuildMembers(UUID uuid){
		String owner_name = Core.getInstance().getRepresentant(uuid);
		
		List<UUID> guild_members = new ArrayList<UUID>();
		try{
		   		   guild_members = Core.getKeysByValueForPlayerMap(Core.playermap, owner_name);
		}
		catch(NullPointerException e){
			// MERGING EMPTY GROUPS
		}
		return guild_members;
	}
}
