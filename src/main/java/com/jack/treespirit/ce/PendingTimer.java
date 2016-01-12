package com.jack.treespirit.ce;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.jack.treespirit.Core;

public class PendingTimer {

	public static void startPendingTimer(final Player p, int delay,
			final Player tar) {
		int d = delay * 20;
		Core.getInstance().getServer().getScheduler()
				.scheduleSyncDelayedTask(Core.getInstance(), new Runnable() {

					public void run() {
						Bukkit.broadcastMessage("Remove both");
						Core.getInstance().invites_pending.remove(p);
						Core.getInstance().invite_art.remove(tar);
					}
				}, d);
	}
}
