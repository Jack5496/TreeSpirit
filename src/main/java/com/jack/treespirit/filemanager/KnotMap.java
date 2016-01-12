package com.jack.treespirit.filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.jack.treespirit.Core;
import com.jack.treespirit.knots.Knot;

public class KnotMap {
	/**
	 * Exat Safe Command for RootMap
	 * @param f File of RootMap
	 * @param rootmap RootMap
	 * @throws FileNotFoundException
	 */
	public static void saveKnotMap(File f, HashMap<String, Knot> knotmap, int i, int max) throws FileNotFoundException{
		Core.getInstance().checkIfPluginDirsExist();
		
		if(i<max){
			try {
				f.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try(FileOutputStream fis = new FileOutputStream(f.getPath());
				ObjectOutputStream o = new ObjectOutputStream(fis)){
				HashMap<String, Knot> copy = (HashMap<String, Knot>) knotmap.clone();
				o.writeObject(copy);
				o.flush();
				o.close();
			} 
			catch (Exception e) {
				Bukkit.broadcastMessage("Couldn't serialize KnotMap to file: atempt: "+i);
				Bukkit.broadcastMessage(e.getMessage());
				
				saveKnotMap(f,knotmap, i+1,max);
			}
		}
	}
	
	/**
	 * Get RootMap from predefined File
	 * @param f File of RootMap
	 * @return
	 */
	public static HashMap<String, Knot> getKnotMapFromFile(File f, int i, int max){
		if(i<max){
			try {
				f.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try(FileInputStream inputFileStream = new FileInputStream(f);
		      ObjectInputStream objectInputStream = new ObjectInputStream(inputFileStream);)
		    {
		      HashMap<String, Knot> map = ((HashMap<String, Knot>) objectInputStream.readObject());
		      objectInputStream.close();
		      inputFileStream.close();
		      return map;
		    }		
		    catch(Exception e){
		    	
		    	Bukkit.broadcastMessage("Deserialising of KnotMap failed, atempt: "+i);
		    	return getKnotMapFromFile(f,i+1,max);	    	
		    }
		}
		return new HashMap<String, Knot>();
	}	
}
