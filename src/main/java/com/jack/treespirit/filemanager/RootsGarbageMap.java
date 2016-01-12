package com.jack.treespirit.filemanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.jack.treespirit.Core;

public class RootsGarbageMap {
	/**
	 * Exat Safe Command for RootMap
	 * @param f File of RootMap
	 * @param rootmap RootMap
	 * @throws FileNotFoundException
	 */
	public static void saveRootsGarbageMap(File f, List<String> rootsgarbagemap, int i, int max) throws FileNotFoundException{
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
			List<String> copy = new ArrayList(rootsgarbagemap);
			o.writeObject(copy);
			o.flush();
			o.close();
		} 
		catch (Exception e) {
			
			Bukkit.broadcastMessage("Couldn't serialize RootsGarbageHashMap to file: atempt: "+i);
			Bukkit.broadcastMessage(e.getMessage());
			saveRootsGarbageMap(f,rootsgarbagemap, i+1,max);
		}
		}
	}
	
	/**
	 * Get RootMap from predefined File
	 * @param f File of RootMap
	 * @return
	 */
	public static List<String> getRootsGarbageMapFromFile(File f, int i, int max){
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
	      List<String> map = (List<String>) objectInputStream.readObject();
	      objectInputStream.close();
	      inputFileStream.close();
	      return map;
	    }		
	    catch(Exception e){
	    	
	    	Bukkit.broadcastMessage("Deserialising of RootsGarbageMap failed: atempt: "+i);
	    	return getRootsGarbageMapFromFile(f, i+1,max);
	    }
		}
		return new ArrayList<String>();
	}

}
