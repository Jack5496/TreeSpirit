package com.jack.treespirit.ce;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.jack.treespirit.Core;
import com.jack.treespirit.events.Events;
import com.jack.treespirit.filemanager.PlayerMap;
import com.jack.treespirit.functions.LocationSerialize;
import com.jack.treespirit.functions.MessageGuild;
import com.jack.treespirit.knots.Knot;
import com.jack.treespirit.languages.Languages;
import com.jack.treespirit.menu.CreateMenu;
import com.jack.treespirit.menu.LanguageMenu;

public class TreeComands implements CommandExecutor {

	public Core core;
	public static Location start;
	public static Location end;
	
	public static Location nt_begin;

	public TreeComands(Core c) {
		this.core = c;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s,
			String[] args) {
		Player p = (Player)sender;
		
		try {
			if (args[0] != null) {

				if (args[0].equalsIgnoreCase("help")) {
					sendHelpMessage(sender, cmd, s, args);
					return true;
				}
				if (args[0].equalsIgnoreCase("size")) {
					sendTreeSize(sender, cmd, s, args);
					return true;
				}
				if (args[0].equalsIgnoreCase("import")) {
					ImportBlocks.execute(sender, cmd, s, args);
					return true;
				}
				if (args[0].equalsIgnoreCase("newTree")) {
					NewTreeImport.execute(sender, cmd, s, args);
					return true;
				}
				if (args[0].equalsIgnoreCase("player")) {
					PlayerMap.createPlayer(p);
					return true;
				}
				if (args[0].equalsIgnoreCase("choose")) {
					if(args.length > 1){
					TreeChoose.execute(sender,args[1]);
					}
					else{
						TreeChoose.sendUsage(p);
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("leave")) {
					leaveGuild(p);
					return true;
				}
				if (args[0].equalsIgnoreCase("filesize")) {
					fileSize(sender, cmd, s, args);
					return true;
				}
				if (args[0].equalsIgnoreCase("homepage")) {
					getTreeHomepage(sender);
					return true;
				}
				if (args[0].equalsIgnoreCase("join")) {
					acceptGuild(sender, cmd, s, args);
					return true;
				}
				if (args[0].equalsIgnoreCase("invite")) {
					if (Core.getInstance().getPlayer(args[1]) == null) {
						if (Core.getInstance().invites_pending.containsKey((Player) sender)) {
							p.sendMessage(Languages.getCantInvite2PeopleAtSameTime(p));
							return true;
						}
						p.sendMessage(Languages.getPlayerIsNotOnline(p));
						return true;
					}
					invite2Guild(sender, cmd, s, args);
					return true;
				}
				sender.sendMessage(Languages.getUsage(p)+" /tree help");
				return true;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			if (Core.getInstance().getConfigurations().getTree_menu()) {
				if(PlayerMap.getPlayerLanguage(p)==null){
					
				}
				if(!PlayerMap.getPlayerLanguage(p).equalsIgnoreCase("Default")){
					CreateMenu.getCreateMenuInstance().getMainMenu(p).open(p);
				}
				else{
					CreateMenu.getCreateMenuInstance().createLanguageMenu(p).open(p);
				}
			} else {
				p.sendMessage(Languages.getUsage(p)+" /tree help");
			}
			return true;
		}
		return false;
	}

	public boolean invite2Guild(CommandSender sender, Command cmd, String s,
			String[] args) {
		// invite <party/guild> <name>

		Player p = (Player) sender;
		if (args[1] != null) {
			PendingTimer.startPendingTimer((Player) sender, 30,
					Core.getInstance().getPlayer(args[1]));
			p.sendMessage(Languages.getInviteSend(p));
			Player tar = Core.getInstance().getPlayer(args[1]);
			tar.sendMessage(Languages.getInviteRecieved(tar)+" /tree join "+p.getName());
			Core.getInstance().invites_pending.put(tar, p);
			Core.getInstance().invite_art.put(tar, "guild");
			return true;
		}
		return false;
	}

	public boolean acceptGuild(CommandSender sender, Command cmd, String s,
			String[] args) {
		Player send = (Player) sender;
		if (args[0] != null) {
			if (args[1] != null) {
				Player p = Core.getInstance().invites_pending.get((Player) sender);
				String gg = Core.getInstance().invite_art.get((Player) sender);
				if ("guild".equalsIgnoreCase(gg)) {
					if (args[1].equalsIgnoreCase(p.getName())) {
						PlayerMap.mergeTreeToTree(send, p);
						acceptInvite(p, (Player) sender, gg);
						p.sendMessage(Languages.getInviteAccepted(p));
						if (Core.getInstance().getMessenger()
								.getInform_Guild_on_GuildJoin()) {
							String message = "<GuildNewMember>";
							UUID uuid = Core.hashmap.get(p.getUniqueId());
							MessageGuild.messageGuild(uuid, message, "");
						}
						
						
						PlayerMap.savePlayer(p);
						PlayerMap.savePlayerInTreeGroup(p);
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}

	public static boolean leaveGuild(Player p_sender) {

		String name_of_owner = Core.getInstance().getRepresentant(p_sender.getUniqueId());

		List<String> blocks_of_sender = Core.getKeysByValue(Core.hashmap,
				p_sender.getUniqueId());

		UUID owner_uuid = Bukkit.getOfflinePlayer(name_of_owner).getPlayer().getUniqueId();
		
		
		//Set Tree Group to NONE
		Core.getInstance().changeTreeTypeForPlayerMap(
				p_sender.getUniqueId(), "None");
		//Important
		

		
		if(name_of_owner.equalsIgnoreCase(p_sender.getName())){
			Location root = Core.getInstance().getLocFromRootMap(owner_uuid);
			if(root==null){
				return true;
			}
			Events.BlockBreaked(root.getBlock(), "Root broke");
		}
		
		for (String loc : blocks_of_sender) {
			Core.getInstance().removeFromHashMap(
					LocationSerialize.serializeToLocation(loc));
			if(!name_of_owner.equalsIgnoreCase(p_sender.getName())){ 
				Core.getInstance().addToHashMap(
						LocationSerialize.serializeToLocation(loc), owner_uuid);
			}
		}
		
		PlayerMap.leaveTree(p_sender);
		Core.getInstance().resetGroupForPlayer(p_sender);
		

		if (Core.getInstance().getMessenger().getInform_Guild_on_GuildLeave()) {
			MessageGuild.messageGuild(owner_uuid, "<GuildMemberLeftTree>", p_sender.getName());
		}
		p_sender.sendMessage(Languages.getYouLeftTree(p_sender));
		
		
		PlayerMap.savePlayer(p_sender);
		PlayerMap.savePlayerInTreeGroup(p_sender);
		return true;
	}

	public boolean fileSize(CommandSender sender, Command cmd, String s,
			String[] args) {
		Player p_sender = (Player) sender;
		p_sender.sendMessage(ChatColor.GOLD + "[TreeSpirit]" + ChatColor.WHITE
				+ "File Size: ");
		p_sender.sendMessage(ChatColor.GREEN + "HashMap: " + ChatColor.WHITE
				+ Core.getInstance().hashmap.size() + " Blocks");
		p_sender.sendMessage(ChatColor.GREEN + "KnotMap: " + ChatColor.WHITE
				+ Core.getInstance().knotmap.size() + " Blocks");
		p_sender.sendMessage(ChatColor.GREEN + "RootMap: " + ChatColor.WHITE
				+ Core.getInstance().rootmap.size() + " Blocks");
		p_sender.sendMessage(ChatColor.GREEN + "PlayerMap: " + ChatColor.WHITE
				+ Core.getInstance().playermap.size() + " Players");
		p_sender.sendMessage(ChatColor.GREEN + "BlocksGarbageMap: "
				+ ChatColor.WHITE + Core.getInstance().blocksgarbagemap.size()
				+ " Block");
		return true;
	}

	public static boolean getTreeHomepage(CommandSender sender) {
		Player p_sender = (Player) sender;
		File treefile = new File("plugins/BukkitHTTPD/tree.v10");

		if (treefile.exists()) {
			String serverip = Core.getInstance().getServer().getIp();
			if (!serverip.equalsIgnoreCase("")) {
				p_sender.sendMessage("Tree Homepage: " + ChatColor.BLUE
						+ ChatColor.UNDERLINE + "http://" + serverip
						+ ":8107/tree.v10");
			} else {
				p_sender.sendMessage("Tree Homepage: " + ChatColor.BLUE
						+ ChatColor.UNDERLINE
						+ "http://localhost:8107/tree.v10");
			}
		} else {
//			p_sender.sendMessage(Languages.getPleaseInstallTheAddon(p_sender, "TreeSpirit Homepage"));
			p_sender.sendMessage(Languages.getInstallAddon(p_sender));
		}

		return true;
	}



	/**
	 * Intern required method. Do not call it. This method is used for internal
	 * purposes and can lead to serious errors if it is called.
	 * 
	 * @param Player
	 *            player
	 * @param Player
	 *            target Player
	 * @param String
	 *            Guild / Party
	 */
	public void acceptInvite(Player p, Player tar, String s) {
		if (s.equalsIgnoreCase("guild")) {
			/**
			 * Put all Players from b to a
			 * 
			 * @param a
			 *            Player new Group Leader
			 * @param b
			 *            old Group Leader
			 */
			Core.getInstance().mergeGroup(p, tar);
			tar.sendMessage(Languages.getInformationTreeJoined(tar));
		}
	}

	public boolean sendTreeSize(CommandSender sender, Command cmd, String s,
			String[] args) {
		Player p_sender = (Player) sender;

		String name_of_owner = Core.getInstance().getRepresentant(p_sender.getUniqueId());

		List<UUID> tree_members = new ArrayList<UUID>();
		tree_members = Core.getKeysByValueForPlayerMap(Core.playermap, name_of_owner);

		int amount_of_members = tree_members.size();

		int total_amount_of_blocks = 0;
		int amount_of_player = 1;
		amount_of_player = Core.getKeysByValue(Core.hashmap,
				p_sender.getUniqueId()).size();

		for (UUID uuid : tree_members) {
			List<String> blocks_of_uuid = Core.getKeysByValue(Core.hashmap,
					uuid);
			total_amount_of_blocks += blocks_of_uuid.size();
		}

		p_sender.sendMessage(ChatColor.GOLD + "[TreeSpirit] Tree Size");
		p_sender.sendMessage(ChatColor.WHITE + "Tree of " + ChatColor.GREEN
				+ name_of_owner + ChatColor.WHITE + " with " + ChatColor.GREEN
				+ amount_of_members + ChatColor.WHITE + " Members placed "
				+ ChatColor.GREEN + total_amount_of_blocks + ChatColor.WHITE
				+ " Amount of Blocks");

		p_sender.sendMessage("");
		double percent = 0;
		try {
			percent = ((double) amount_of_player / (double) total_amount_of_blocks) * 100;
			DecimalFormat df2 = new DecimalFormat("###.###");
			percent = Double.valueOf(df2.format(percent));
		} catch (NumberFormatException e) {
			percent = 0;
		}

		String balken = ChatColor.GREEN + "";
		int percent_int = (int) percent;
		int rest = 100 - percent_int;
//		balken = balken + StringUtils.repeat("|", percent_int);
//		balken = balken + ChatColor.RED;
//		balken = balken + StringUtils.repeat("|", rest);

		p_sender.sendMessage(ChatColor.WHITE + "You placed " + ChatColor.GREEN
				+ amount_of_player + ChatColor.WHITE + " Blocks which are "
				+ ChatColor.GREEN + (percent) + ChatColor.WHITE
				+ " % of the Tree");
		p_sender.sendMessage(ChatColor.WHITE + "Progress: " + balken);
		return true;
	}

	public boolean sendHelpMessage(CommandSender sender, Command cmd, String s,
			String[] args) {
		String blank = "" + ChatColor.GOLD + "  ||  " + ChatColor.WHITE + "";

		Player p_sender = (Player) sender;
		p_sender.sendMessage(ChatColor.GOLD + "[TreeSpirit]" + ChatColor.RED
				+ " - Commands ");
		p_sender.sendMessage(ChatColor.GREEN + " - /tree help" + blank
				+ "Shows Help Menü");
		p_sender.sendMessage(ChatColor.GREEN + " - /tree leave" + blank
				+ "Leaves a Group Tree");
		p_sender.sendMessage(ChatColor.GREEN + " - /tree invite <Player>"
				+ blank + "Invites a Player to your Tree");
		p_sender.sendMessage(ChatColor.GREEN + " - /tree join <Tree Owner>"
				+ blank + "Joins a Tree after Request");
		p_sender.sendMessage(ChatColor.GREEN + " - /tree size" + blank
				+ "Shows Size of Tree");
		p_sender.sendMessage(ChatColor.GREEN + " - /tree homepage" + blank
				+ "Shows Tree Homepage");
		p_sender.sendMessage(ChatColor.RED + " - /tree import" + blank
				+ "Imports Area for Players");
		p_sender.sendMessage(ChatColor.RED + " - /tree filesize" + blank
				+ "Shows Information about TreeSpirit File ");

		return true;
	}

	public static Location getStartLoc() {
		return start;
	}

	public static Location getEndLoc() {
		return end;
	}

	public static void setStartLoc(Location l) {
		start = l;
	}

	public static void setEndLoc(Location l) {
		end = l;
	}
	

	public static Location getNTBegin() {
		return nt_begin;
	}
	
	public static void setNTBegin(Location l) {
		nt_begin = l;
	}

}
