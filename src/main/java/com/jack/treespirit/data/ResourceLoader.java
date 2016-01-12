package com.jack.treespirit.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.jack.treespirit.Core;
import com.jack.treespirit.filemanager.CustomTree;

public class ResourceLoader {
	
	public static File Common = new File(Core.getInstance().getPlugindirDataTrees()+"/Common");
	
	/**
	 * Oak Trees
	 */
	public static File AcaciaTrees = new File(Common+"/AcaciaTrees");
		
	public static File Acacia = new File(AcaciaTrees+"/Acacia.ser");
	public static int AcaciaChance = 100;
	
	/**
	 * Oak Trees
	 */
	public static File OakTrees = new File(Common+"/OakTrees");
	
	public static File BigOak = new File(OakTrees+"/BigOak.ser");
	public static int BigOakChance = 10;
	public static File Oak = new File(OakTrees+"/Oak.ser");
	public static int OakChance = 80;
	public static File PineOak = new File(OakTrees+"/PineOak.ser");
	public static int PineOakChance = 10;
	
	/**
	 * Jungle Trees
	 */
	public static File JungleTrees = new File(Common+"/JungleTrees");
	
	public static File Jungle = new File(JungleTrees+"/Jungle.ser");
	public static int JungleChance = 0;
	public static File SmallJungle = new File(JungleTrees+"/SmallJungle.ser");
	public static int SmallJungleChance = 95;
	public static File JungleBush = new File(JungleTrees+"/JungleBush.ser");
	public static int JungleBushChance = 5;
	
	/**
	 * Birch Trees
	 */
	public static File BirchTrees = new File(Common+"/BirchTrees");
	
	public static File Birch = new File(BirchTrees+"/Birch.ser");
	public static int BirchChance = 95;
	public static File TallBirch = new File(BirchTrees+"/TallBirch.ser");
	public static int TallBirchChance = 5;

	/**
	 * Sequoia Trees
	 * Tanne
	 */
	public static File SequoiaTrees = new File(Common+"/SequoiaTrees");
	
	public static File Sequoia = new File(SequoiaTrees+"/Sequoia.ser");
	public static int SequoiaChance = 95;
	public static File TallSequoia = new File(SequoiaTrees+"/TallSequoia.ser");
	public static int TallSequoiaChance = 5;
	public static File MegaSequoia = new File(SequoiaTrees+"/MegaSequoia.ser");
	public static int MegaSequoiaChance = 0;
	
	/**
	 * Dark Trees
	 */
	public static File DarkTrees = new File(Common+"/DarkTrees");
	
	public static File DarkOak = new File(DarkTrees+"/DarkOak.ser");
	public static int DarkOakChance = 100;

	
	/**
	 * Sequoia Trees
	 * Tanne
	 */
	public static File SwampTrees = new File(Common+"/SwampTrees");
	
	public static File Swamp = new File(SwampTrees+"/Swamp.ser");
	public static int SwampChance = 100;
	
	
	public ResourceLoader(){
		if(!Common.exists()){
			Common.mkdir();
		}
		loadOakTrees();
		loadAcaciaTrees();
		loadBirchTrees();
		loadJungleTrees();
		loadSequoiaTrees();
		loadDarkTrees();
		loadSwampTrees();
	}
	
	public static void loadOakTrees(){
		if(!OakTrees.exists()){
			OakTrees.mkdir();
		}
		CustomTree.loadFromResource(BigOak);
		CustomTree.loadFromResource(Oak);
		CustomTree.loadFromResource(PineOak);
	}
	
	public static void loadAcaciaTrees(){
		if(!AcaciaTrees.exists()){
			AcaciaTrees.mkdir();
		}
		CustomTree.loadFromResource(Acacia);
	}
	
	public static void loadJungleTrees(){
		if(!JungleTrees.exists()){
			JungleTrees.mkdir();
		}
		CustomTree.loadFromResource(Jungle);
		CustomTree.loadFromResource(SmallJungle);
		CustomTree.loadFromResource(JungleBush);
	}
	
	public static void loadBirchTrees(){
		if(!BirchTrees.exists()){
			BirchTrees.mkdir();
		}
		CustomTree.loadFromResource(Birch);
		CustomTree.loadFromResource(TallBirch);
	}
	
	public static void loadSequoiaTrees(){
		if(!SequoiaTrees.exists()){
			SequoiaTrees.mkdir();
		}
		CustomTree.loadFromResource(Sequoia);
		CustomTree.loadFromResource(TallSequoia);
		CustomTree.loadFromResource(MegaSequoia);
	}
	
	public static void loadDarkTrees(){
		if(!DarkTrees.exists()){
			DarkTrees.mkdir();
		}
		CustomTree.loadFromResource(DarkOak);
	}
	
	public static void loadSwampTrees(){
		if(!SwampTrees.exists()){
			SwampTrees.mkdir();
		}
		CustomTree.loadFromResource(Swamp);
	}
	
	
	
	public static File getFileByName(String s){
		if(s.equalsIgnoreCase("Oak")){
			return getFileFromOak();
		}
		if(s.equalsIgnoreCase("Acacia")){
			return getFileFromOak();
		}
		if(s.equalsIgnoreCase("Birch")){
			return getFileFromBirch();
		}
		if(s.equalsIgnoreCase("Jungle")){
			return getFileFromJungle();
		}
		if(s.equalsIgnoreCase("Sequoia")){
			return getFileFromSequoia();
		}
		if(s.equalsIgnoreCase("Dark")){
			return getFileFromDark();
		}
		if(s.equalsIgnoreCase("Swamp")){
			return getFileFromSwamp();
		}
		return Oak;
	}
	
	public static File getFileFromOak(){
		double d = Math.random() * 100;
		if ((d -= OakChance) < 0) return Oak;
		if ((d -= BigOakChance) < 0) return BigOak;
		if ((d -= 45) < PineOakChance) return PineOak;
		return Oak;
	}
	
	public File getFileFromAcacia(){
		double d = Math.random() * 100;
		if ((d -= AcaciaChance) < 0) return Acacia;
		return Acacia;
	}
	
	public static File getFileFromJungle(){
		double d = Math.random() * 100;
		if ((d -= SmallJungleChance) < 0) return SmallJungle;
		if ((d -= JungleBushChance) < 0) return JungleBush;
		if ((d -= JungleChance) < 0) return Jungle;
		return SmallJungle;
	}
	
	public static File getFileFromSequoia(){
		double d = Math.random() * 100;
		if ((d -= SequoiaChance) < 0) return Sequoia;
		if ((d -= TallSequoiaChance) < 0) return TallSequoia;
		if ((d -= MegaSequoiaChance) < 0) return MegaSequoia;
		return Sequoia;
	}
	
	public static File getFileFromBirch(){
		double d = Math.random() * 100;
		if ((d -= BirchChance) < 0) return Birch;
		if ((d -= TallBirchChance) < 0) return TallBirch;
		return Birch;
	}
	
	public static File getFileFromDark(){
		double d = Math.random() * 100;
		if ((d -= DarkOakChance) < 0) return DarkOak;
		return DarkOak;
	}
	
	public static File getFileFromSwamp(){
		double d = Math.random() * 100;
		if ((d -= SwampChance) < 0) return Swamp;
		return Swamp;
	}
	
	public static String getNameBySapling(Block b){
		if(b.getType()==Material.SAPLING){
			byte data = b.getData();
			if(data==0) return "Oak";
			if(data==1) return "Sequoia";
			if(data==2) return "Birch";
			if(data==3) return "Jungle";
			if(data==4) return "Acacia";
			if(data==5) return "Dark";
			return "Oak";
		}
		return "Oak";
	}
}
