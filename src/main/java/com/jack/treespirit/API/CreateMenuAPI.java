package com.jack.treespirit.API;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.jack.treespirit.menu.IconMenu;


public interface CreateMenuAPI {
	
	/**
	 * F�gt ein IconMenu in das Hauptmenu ein
	 * @param im IconMenu
	 * @param i Slot(0-8) // Vaild if (i < 9 && i > 4)
	 * @param is ItemStack 
	 * @param name Display Name
	 * @param desc Display Description
	 * @return boolean // true if activated
	 */
	boolean SetIconMenu(IconMenu im, int i, ItemStack is, String name,
			String desc);
	public IconMenuAPI createMainMenu(Player p);
}
