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

public class MapHash {

	
	/**
	 * Exact Save Command used by Easy Save Command
	 * @param f File of HashMap
	 * @param map HashMap
	 * @throws FileNotFoundException
	 */	
	public static void saveHashMap(File f, HashMap<String, UUID> map, int i, int max) throws FileNotFoundException{
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
				HashMap<String, UUID> copy = (HashMap<String, UUID>) map.clone();
				o.writeObject(copy);
				o.flush();
				o.close();
			} 
			catch (Exception e) {
				
				Bukkit.broadcastMessage("Couldn't serialize HashMap to file: atempt: "+i);
				saveHashMap(f,map,i+1,max);
			}
		}
	}
	
	/**
	 * Returns the HashMap file from Local place
	 * @param f
	 * @return
	 */
	public static HashMap<String, UUID> getHashMapFromFile(File f, int i, int max){
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
		      HashMap<String, UUID> map = ((HashMap<String, UUID>) objectInputStream.readObject());
		      objectInputStream.close();
		      inputFileStream.close();
		      return map;
		    }		
		    catch(Exception e){
		    	
		    	Bukkit.broadcastMessage("Deserialising of HashMap failed: atempt: "+i);
		    	return getHashMapFromFile(f,i+1,max);
		    }
		}
		return new HashMap<String, UUID>();
	}	
}
