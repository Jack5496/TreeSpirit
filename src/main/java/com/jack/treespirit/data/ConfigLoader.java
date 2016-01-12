package com.jack.treespirit.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.jack.treespirit.Core;

public class ConfigLoader {

	public static void loadAllConfigs(){
		loadConfig();
		loadMessages();
		loadSignConfigs();
		getMatList();
		loadScoreboardConfig();
	}
	
	
	public static void loadConfig() {
		File adr = Core.configfile;
		if (!adr.exists()) {
			try {
				adr.createNewFile();
				YamlConfiguration temp = YamlConfiguration
						.loadConfiguration(adr);
				temp.addDefault("Decay_Tree", "true");
				temp.addDefault("Tree_Menu", "true");
				temp.addDefault("Tree_PvP", "false");
				temp.addDefault("Damage_All_Blocks_on_Explosion", "true");
				temp.addDefault("Only_Damage_Tree_on_Explosion", "false");
				temp.addDefault("Sapling_must_Set_Next_To_Log", "true");
				temp.addDefault("Debug_Mode", "false");
				temp.addDefault("Only_Walk_on_Log", "true");
				temp.addDefault("Create_Sign_On_Knot", "false");
				temp.options().copyDefaults(true);
				temp.save(adr);
			} catch (IOException ex) {

			}
		}
		YamlConfiguration ycf = YamlConfiguration.loadConfiguration(adr);
		for (String name : ycf.getKeys(false)) {

			try {
				if (name.equalsIgnoreCase("Decay_Tree")) {
					Core.configs.setDecay_tree(ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Tree_Menu")) {
					Core.configs.setTree_menu(ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Tree_PvP")) {
					Core.configs.setTree_Pvp(ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Damage_All_Blocks_on_Explosion")) {
					Core.configs.setDamage_All_Blocks_on_Explosion(ycf
							.getString(name));
				}
				if (name.equalsIgnoreCase("Only_Damage_Tree_on_Explosion")) {
					Core.configs.setOnly_Damage_Tree_on_Explosion(ycf
							.getString(name));
				}
				if (name.equalsIgnoreCase("Only_Walk_on_Log")) {
					Core.configs.setOnly_Walk_on_Log(ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Sapling_must_Set_Next_To_Log")) {
					Core.configs.setSapling_must_Set_Next_To_Log(ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Debug_Mode")) {
					Core.configs.setDebug_Mode(ycf.getString(name));
				}
				if(name.equalsIgnoreCase("Create_Sign_On_Knot")){
					Core.configs.setCreate_Sign_On_Knot(ycf.getString(name));
				}

			} catch (Exception ex) {
				ycf.set(name, "INVAILD");
			}
		}
	}
	
	public static void setConfigsInYML(String name, String value){
		File adr = Core.configfile;
		if (adr.exists()) {
			try {
				YamlConfiguration temp = YamlConfiguration
						.loadConfiguration(adr);
				temp.set(name, value);
				temp.options().copyDefaults(true);
				temp.save(adr);
			} catch (IOException ex) {

			}
		}
		else{
			loadConfig();
		}
	}
	
	public static void loadSignConfigs() {
		File adr = Core.signsfile;
		if (!adr.exists()) {
			try {
				adr.createNewFile();
				YamlConfiguration temp = YamlConfiguration
						.loadConfiguration(adr);
				temp.addDefault("Tree Text", "Tree");
				temp.addDefault("Tree Player Text", "T Player");
				temp.addDefault("To Root Text", "To Root");
				temp.addDefault("Total Tree's", "Roots");

				temp.options().copyDefaults(true);
				temp.save(adr);
			} catch (IOException ex) {

			}
		}
		YamlConfiguration ycf = YamlConfiguration.loadConfiguration(adr);
		for (String name : ycf.getKeys(false)) {

			try {
				if (name.equalsIgnoreCase("Tree Text")) {
					Core.signconfigs.set_tree_text(ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Tree Player Text")) {
					Core.signconfigs.set_tree_player_text(ycf.getString(name));
				}
				if (name.equalsIgnoreCase("To Root Text")) {
					Core.signconfigs.set_to_root_text(ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Total Tree's")) {
					Core.signconfigs.set_roots_text(ycf.getString(name));
				}

			} catch (Exception ex) {
				ycf.set(name, "INVAILD");
			}
		}
	}

	public static void loadMessages() {
		File adr = Core.messagefile;
		if (!adr.exists()) {
			try {
				adr.createNewFile();
				YamlConfiguration temp = YamlConfiguration
						.loadConfiguration(adr);
				temp.addDefault("Inform_Guild_on_Respawn", "true");
				temp.addDefault("Inform_Guild_on_PlayerJoin", "true");
				temp.addDefault("Inform_Player_of_Non_Tree_PvP", "true");
				temp.addDefault("Inform_Guild_on_Fire", "true");
				temp.addDefault("Inform_Guild_on_Enemy_Grief", "true");
				temp.addDefault("Inform_Guild_on_Explosion", "true");
				temp.addDefault("Inform_Guild_on_TreeGrow", "true");
				temp.addDefault("Inform_Guild_on_RootDestroy", "true");
				temp.addDefault("Inform_Guild_on_GuildJoin", "true");
				temp.addDefault("Inform_Guild_on_GuildLeave", "true");

				temp.addDefault("Inform_Player_on_RootPlace", "true");
				temp.addDefault(
						"Inform_Player_on_Sapling_must_Set_Next_To_Log", "true");
				temp.addDefault("Inform_Player_on_Log_must_Set_Next_To_Log",
						"false");

				temp.options().copyDefaults(true);
				temp.save(adr);
			} catch (IOException ex) {

			}
		}
		YamlConfiguration ycf = YamlConfiguration.loadConfiguration(adr);
		for (String name : ycf.getKeys(false)) {

			try {
				if (name.equalsIgnoreCase("Inform_Guild_on_Respawn")) {
					Core.getMessenger().setInform_Guild_on_Respawn(
							ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Inform_Guild_on_PlayerJoin")) {
					Core.getMessenger().setInform_Guild_on_PlayerJoin(
							ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Inform_Player_of_Non_Tree_PvP")) {
					Core.getMessenger().setInform_Player_of_Non_Tree_PvP(
							ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Inform_Guild_on_Fire")) {
					Core.getMessenger().setInform_Guild_on_Fire(ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Inform_Guild_on_Enemy_Grief")) {
					Core.getMessenger().setInform_Guild_on_Enemy_Grief(
							ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Inform_Guild_on_Explosion")) {
					Core.getMessenger().setInform_Guild_on_Explosion(
							ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Inform_Guild_on_TreeGrow")) {
					Core.getMessenger().setInform_Guild_on_TreeGrow(
							ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Inform_Guild_on_RootDestroy")) {
					Core.getMessenger().setInform_Guild_on_RootDestroy(
							ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Inform_Guild_on_GuildJoin")) {
					Core.getMessenger().setInform_Guild_on_GuildJoin(
							ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Inform_Guild_on_GuildLeave")) {
					Core.getMessenger().setInform_Guild_on_GuildLeave(
							ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Inform_Player_on_RootPlace")) {
					Core.getMessenger().setInform_Player_on_RootPlace(
							ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Inform_Player_on_Sapling_must_Set_Next_To_Log")) {
					Core.getMessenger()
							.setInform_Player_on_Sapling_must_Set_Next_To_Log(
									ycf.getString(name));
				}
				if (name.equalsIgnoreCase("Inform_Player_on_Log_must_Set_Next_To_Log")) {
					Core.getMessenger()
							.setInform_Player_on_Log_must_Set_Next_To_Log(
									ycf.getString(name));
				}

			} catch (Exception ex) {
				ycf.set(name, "INVAILD");
			}
		}
	}
	
	/**
	 * Läd die Aktuellen erlauben Materialien
	 * 
	 * @return
	 */
	public static void getMatList() {
		List<Material> toRet = new ArrayList<Material>();
		File adr = Core.materialsfile;
		if (!adr.exists()) {
			try {
				adr.createNewFile();
				YamlConfiguration temp = YamlConfiguration
						.loadConfiguration(adr);
				temp.addDefault(Material.LOG.name(),
						("" + Material.LOG.getId()));
				temp.addDefault(Material.LOG_2.name(),
						("" + Material.LOG_2.getId()));
				temp.addDefault(Material.LEAVES.name(),
						("" + Material.LEAVES.getId()));
				temp.addDefault(Material.LEAVES_2.name(),
						("" + Material.LEAVES_2.getId()));
				temp.addDefault(Material.GLOWSTONE.name(),
						("" + Material.GLOWSTONE.getId()));
				temp.options().copyDefaults(true);
				temp.save(adr);
			} catch (IOException ex) {

			}
		}
		YamlConfiguration ycf = YamlConfiguration.loadConfiguration(adr);
		for (String name : ycf.getKeys(false)) {
			try {
				toRet.add(Material.getMaterial(name));
			} catch (Exception e) {
				try {
					int id = Integer.parseInt(ycf.getString(name));
					toRet.add(Material.getMaterial(id));
				} catch (Exception ex) {
					ycf.set(name, "INVAILD ID OR NAME");
				}
			}

			// toRet.add(new Material(name, ycf.getString(name)));
		}

		Core.vaildMats = toRet;
	}
	
	public static void loadScoreboardConfig() {
		File adr = Core.scoreboardsfile;
		if (!adr.exists()) {
			try {
				adr.createNewFile();
				YamlConfiguration temp = YamlConfiguration
						.loadConfiguration(adr);
				
				temp.addDefault("showPlayersScoreboard", "true");
				temp.addDefault("showTreeScoreboard", "true");
				temp.addDefault("showTreeTopsScoreboard", "true");
				temp.addDefault("showGlobalTreeScoreboard", "true");
				temp.addDefault("showGlobalPlayerScoreboard", "true");

				temp.options().copyDefaults(true);
				temp.save(adr);
			} catch (IOException ex) {

			}
		}
		YamlConfiguration ycf = YamlConfiguration.loadConfiguration(adr);
		for (String name : ycf.getKeys(false)) {

			try {
				if (name.equalsIgnoreCase("showPlayersScoreboard")) {
					Core.sbc.setshowPlayersScoreboard(ycf.getString(name));
				}
				if (name.equalsIgnoreCase("showTreeScoreboard")) {
					Core.sbc.setshowTreeScoreboard(ycf.getString(name));
				}
				if (name.equalsIgnoreCase("showTreeTopsScoreboard")) {
					Core.sbc.setshowTreeTopsScoreboard(ycf.getString(name));
				}
				if (name.equalsIgnoreCase("showGlobalTreeScoreboard")) {
					Core.sbc.setshowGlobalTreeScoreboard(ycf.getString(name));
				}
				if (name.equalsIgnoreCase("showGlobalPlayerScoreboard")) {
					Core.sbc.setshowGlobalPlayerScoreboard(ycf.getString(name));
				}

			} catch (Exception ex) {
				ycf.set(name, "INVAILD");
			}
		}
	}
	
	public static void setScoreboardInYML(String name, String value){
		File adr = Core.scoreboardsfile;
		if (adr.exists()) {
			try {
				YamlConfiguration temp = YamlConfiguration
						.loadConfiguration(adr);
				temp.set(name, value);
				temp.options().copyDefaults(true);
				temp.save(adr);
			} catch (IOException ex) {
				Bukkit.broadcastMessage("Fehler");
			}
		}
		else{
			loadConfig();
		}
	}
}
