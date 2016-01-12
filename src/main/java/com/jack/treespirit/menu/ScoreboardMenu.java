package com.jack.treespirit.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.jack.treespirit.Core;
import com.jack.treespirit.API.IconMenuAPI;
import com.jack.treespirit.data.ConfigLoader;

public class ScoreboardMenu {
	
	private static com.jack.treespirit.API.IconMenuAPI adminOptionScoreboardMenu;

	public static IconMenuAPI createAdminOptionScoreboardMenu(Player p) {
		final ItemStack greenwool = new ItemStack(Material.WOOL, 1,
				DyeColor.LIME.getData());

		final ItemStack redwool = new ItemStack(Material.WOOL, 1,
				DyeColor.RED.getData());

		adminOptionScoreboardMenu = new IconMenu(p,
				CreateMenu.findMiddle(ChatColor.DARK_PURPLE + "    Admin Scoreboard Menu"),
				18, new IconMenu.OptionClickEventHandler() {
					@Override
					public void onOptionClick(IconMenu.OptionClickEvent event) {

						boolean on;

						switch (event.getName()) {
						case "Back":
							CreateMenu.getCreateMenuInstance().createAdminOptionMenu(event.getPlayer()).open(event.getPlayer());
							event.setWillClose(false);
							break;

						case "Players Scoreboard":
							on = Core.getInstance().getScoreboardController()
									.getshowPlayersScoreboard();
							Core.getInstance().getScoreboardController()
									.setshowPlayersScoreboard("" + !on);

							if (!on) {
								event.getInstance().setOption(event.getPosition(),
										new ItemStack(greenwool),
										"Players Scoreboard",
										"Show Players Scoreboard");
							} else {
								event.getInstance().setOption(event.getPosition(),
										new ItemStack(redwool),
										"Players Scoreboard",
										"Hide Players Scoreboard");
							}
							ConfigLoader.setScoreboardInYML(
									"showPlayersScoreboard", "" + !on);

							event.setWillClose(false);
							break;

						case "Tree Scoreboard":
							on = Core.getInstance().getScoreboardController()
									.getshowTreeScoreboard();
							Core.getInstance().getScoreboardController()
									.setshowTreeScoreboard("" + !on);

							if (!on) {
								event.getInstance().setOption(event.getPosition(),
										new ItemStack(greenwool),
										"Tree Scoreboard",
										"Show Tree Scoreboard");
							} else {
								event.getInstance().setOption(event.getPosition(),
										new ItemStack(redwool),
										"Tree Scoreboard",
										"Hide Tree Scoreboard");
							}
							ConfigLoader.setScoreboardInYML(
									"showTreeScoreboard", "" + !on);

							
							event.setWillClose(false);
							break;

						case "Tree Top Scoreboard":
							on = Core.getInstance().getScoreboardController()
									.getshowTreeTopsScoreboard();
							Core.getInstance().getScoreboardController()
									.setshowTreeTopsScoreboard("" + !on);

							if (!on) {
								event.getInstance().setOption(event.getPosition(),
										new ItemStack(greenwool),
										"Tree Top Scoreboard",
										"Show Tree Top Scoreboard");
							} else {
								event.getInstance().setOption(event.getPosition(),
										new ItemStack(redwool),
										"Tree Top Scoreboard",
										"Hide Tree Top Scoreboard");
							}
							ConfigLoader.setScoreboardInYML(
									"showTreeTopsScoreboard", "" + !on);

							
							event.setWillClose(false);
							break;

						case "Global Tree Scoreboard":
							on = Core.getInstance().getScoreboardController()
									.getshowGlobalTreeScoreboard();
							Core.getInstance().getScoreboardController()
									.setshowGlobalTreeScoreboard("" + !on);

							if (!on) {
								event.getInstance().setOption(event.getPosition(),
										new ItemStack(greenwool),
										"Global Tree Scoreboard",
										"Show Global Tree Scoreboard");
							} else {
								event.getInstance().setOption(3,
										new ItemStack(redwool),
										"Global Tree Scoreboard",
										"Hide Global Tree Scoreboard");
							}
							ConfigLoader.setScoreboardInYML(
									"showGlobalTreeScoreboard", "" + !on);

							event.setWillClose(false);
							break;

						case "Global Player Scoreboard":
							on = Core.getInstance().getScoreboardController()
									.getshowGlobalPlayerScoreboard();
							Core.getInstance().getScoreboardController()
									.setshowGlobalPlayerScoreboard("" + !on);

							if (!on) {
								event.getInstance().setOption(event.getPosition(),
										new ItemStack(greenwool),
										"Global Player Scoreboard",
										"Show Global Player Scoreboard");
							} else {
								event.getInstance().setOption(event.getPosition(),
										new ItemStack(redwool),
										"Global Player Scoreboard",
										"Hide Global Player Scoreboard");
							}
							ConfigLoader.setScoreboardInYML(
									"showGlobalPlayerScoreboard", "" + !on);

							event.setWillClose(false);
							break;

						default:
							event.setWillClose(false);
							break;
						}
						
						event.getInstance().updateIcon(event.getPosition());
					}
				}).setOption(17, new ItemStack(
				Material.REDSTONE_BLOCK), "Back", "to Admin Menu"); // <--
		// ";" ACHTUNG

		if (Core.getInstance().getScoreboardController()
				.getshowPlayersScoreboard()) {
			adminOptionScoreboardMenu.setOption(0, new ItemStack(greenwool),
					"Players Scoreboard", "Show Players Scoreboard");
		} else {
			adminOptionScoreboardMenu.setOption(0, new ItemStack(redwool),
					"Players Scoreboard", "Hide Players Scoreboard");
		}
		
		if (Core.getInstance().getScoreboardController()
				.getshowTreeScoreboard()) {
			adminOptionScoreboardMenu.setOption(1, new ItemStack(greenwool),
					"Tree Scoreboard", "Show Tree Scoreboard");
		} else {
			adminOptionScoreboardMenu.setOption(1, new ItemStack(redwool),
					"Tree Scoreboard", "Hide Tree Scoreboard");
		}
		
		if (Core.getInstance().getScoreboardController()
				.getshowTreeTopsScoreboard()) {
			adminOptionScoreboardMenu.setOption(2, new ItemStack(greenwool),
					"Tree Top Scoreboard", "Show Tree Top Scoreboard");
		} else {
			adminOptionScoreboardMenu.setOption(2, new ItemStack(redwool),
					"Tree Top Scoreboard", "Hide Tree Top Scoreboard");
		}

		if (Core.getInstance().getScoreboardController()
				.getshowGlobalTreeScoreboard()) {
			adminOptionScoreboardMenu.setOption(3, new ItemStack(greenwool),
					"Global Tree Scoreboard", "Show Global Tree Scoreboard");
		} else {
			adminOptionScoreboardMenu.setOption(3, new ItemStack(redwool),
					"Global Tree Scoreboard", "Hide Global Tree Scoreboard");
		}
		
		if (Core.getInstance().getScoreboardController()
				.getshowGlobalPlayerScoreboard()) {
			adminOptionScoreboardMenu.setOption(4, new ItemStack(greenwool),
					"Global Player Scoreboard", "Show Global Player Scoreboard");
		} else {
			adminOptionScoreboardMenu.setOption(4, new ItemStack(redwool),
					"Global Player Scoreboard", "Hide Global Player Scoreboard");
		}

		return adminOptionScoreboardMenu;
	}
}
