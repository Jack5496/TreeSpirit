package com.jack.treespirit.threads;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.jack.treespirit.Core;
import com.jack.treespirit.functions.LocationSerialize;

public class DamageController implements Runnable {

	private double dps = 3.0;
	private static int delay = 1000;
	private double damageAmount = dps / (1000 / delay);

	private boolean activ = true;
	private List<Player> lp;

	public DamageController() {
		lp = new ArrayList<Player>();
		lp = new ArrayList<Player>(Core.getInstance().getDamageList()); // Kopie
																		// der
																		// Liste
	}

	@Override
	public void run() {
		while (activ) {
			checkAllPlayers();
			lp = new ArrayList<Player>(Core.getInstance().getDamageList()); // Kopie
																			// der
																			// Liste,
																			// da
																			// sonst
																			// ConcurrentModifikation
			for (Player p : lp) {
				if (p.isOnline()) {
					double damage = damageAmount;
					if (Core.getInstance().containsPlayerReduceDamage(p)) {
						Integer reduce = Core.getInstance()
								.getReduceDamageMap().get(p);
						damage -= reduce;
					}
					try {
						
						if (damage > 0) {
							if (!Core.getInstance()
									.getTreeForPlayer(p.getUniqueId())
									.equalsIgnoreCase("None")) {
							p.damage(damage);
							}
						}
					} catch (Exception e) {
						if (!Core.getInstance()
								.getTreeForPlayer(p.getUniqueId())
								.equalsIgnoreCase("None")) {
							p.damage((int) damage);
						}
					}
				} else {
					Core.getInstance().removePlayer(p);
				}
			}

			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void stopMe() {
		this.activ = false;
	}

	public void checkAllPlayers() {
		// Bukkit.broadcastMessage("PlayerListLength: "+Core.getInstance().getServer().getOnlinePlayers().length);
		for (Player p : Core.getInstance().getServer().getOnlinePlayers()) {

			checkPlayer(p);
		}
	}

	public void checkPlayer(Player p) {

		boolean near = false;
		Location adjusted = new Location(p.getWorld(), p.getLocation()
				.getBlockX(), p.getLocation().getBlockY(), p.getLocation()
				.getBlockZ());

		List<String> ls = getNeighbors26(adjusted);
		String s = LocationSerialize.serializeFromLocation(adjusted);
		ls.add(s);
		ls = addAbouve(ls, adjusted);
		try {
			if (!p.isOnGround()) {
				ls = addUnder(ls, adjusted);
			}
		} catch (Exception e) {
			Location l = p.getLocation();
			l.add(0, -1, 0);
			Location high = p.getWorld().getHighestBlockAt(l).getLocation();
			if (!(l.distance(high) > 0.5)) {
				ls = addUnder(ls, adjusted);
			}
		}

		for (String sa : ls) {
			Location l = LocationSerialize.serializeToLocation(sa);
			if (Core.getInstance().vaildMat(l.getBlock().getType())) {
				if (Core.getInstance().isInHashmapForPlayer(p, l)) {
					near = true;
				}
			}

		}

		if (!near) {
			if (Core.getInstance().getConfigurations().getDebug_Mode()) {
				// Bukkit.broadcastMessage("Add Player to List: "+p.getName());
			}
			Core.getInstance().addPlayer(p);
		} else {
			try {
				if (Core.getInstance().getConfigurations().getDebug_Mode()) {
					// Bukkit.broadcastMessage("Remove Player from List");
				}
				Core.getInstance().removePlayer(p);
			} catch (Exception ex) {

			}
		}
	}

	public static List<String> getNeighbors6(Location l) {
		List<String> back = new ArrayList<String>();
		int x = (int) l.getX();
		int y = (int) l.getY();
		int z = (int) l.getZ();

		String a = l.getWorld().getName() + "#" + (x - 1) + "#" + (y) + "#"
				+ (z);
		String b = l.getWorld().getName() + "#" + (x + 1) + "#" + (y) + "#"
				+ (z);
		String c = l.getWorld().getName() + "#" + (x) + "#" + (y) + "#"
				+ (z - 1);
		String d = l.getWorld().getName() + "#" + (x) + "#" + (y) + "#"
				+ (z + 1);
		String e = l.getWorld().getName() + "#" + (x) + "#" + (y + 1) + "#"
				+ (z);
		String f = l.getWorld().getName() + "#" + (x) + "#" + (y - 1) + "#"
				+ (z);

		back.add(a);
		back.add(b);
		back.add(c);
		back.add(d);

		back.add(e);
		back.add(f);
		return back;
	}

	public static List<String> getNeighbors10(Location l) {
		List<String> back = getNeighbors6(l);
		int x = (int) l.getX();
		int y = (int) l.getY();
		int z = (int) l.getZ();

		String a = l.getWorld().getName() + "#" + (x - 1) + "#" + (y) + "#"
				+ (z - 1);
		String b = l.getWorld().getName() + "#" + (x - 1) + "#" + (y) + "#"
				+ (z + 1);
		String c = l.getWorld().getName() + "#" + (x + 1) + "#" + (y) + "#"
				+ (z - 1);
		String d = l.getWorld().getName() + "#" + (x + 1) + "#" + (y) + "#"
				+ (z + 1);

		back.add(a);
		back.add(b);
		back.add(c);
		back.add(d);

		return back;
	}

	public static List<String> getNeighbors26(Location l) {
		List<String> back = new ArrayList<String>();

		int x = (int) l.getX();
		int y = (int) l.getY();
		int z = (int) l.getZ();

		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				for (int w = -1; w < 2; w++) {
					if (!(i == 0 && j == 0 && w == 0)) {
						String s = l.getWorld().getName() + "#" + (i + x) + "#"
								+ (j + y) + "#" + (z + w);
						back.add(s);
					}
				}
			}
		}

		return back;
	}

	public static List<String> addAbouve(List<String> back, Location l) {

		int x = (int) l.getX();
		int y = (int) l.getY();
		int z = (int) l.getZ();

		for (int i = -1; i < 2; i++) {
			for (int w = -1; w < 2; w++) {
				String s = l.getWorld().getName() + "#" + (i + x) + "#"
						+ (2 + y) + "#" + (z + w);
				back.add(s);
			}
		}

		return back;
	}

	public static List<String> addUnder(List<String> back, Location l) {

		int x = (int) l.getX();
		int y = (int) l.getY();
		int z = (int) l.getZ();

		for (int i = -1; i < 2; i++) {
			for (int w = -1; w < 2; w++) {
				String s = l.getWorld().getName() + "#" + (i + x) + "#"
						+ (y - 2) + "#" + (z + w);
				back.add(s);
			}
		}

		return back;
	}

	public double getDamageAmount() {
		return damageAmount;
	}
}
