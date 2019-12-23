package bgu.spl.mics.application.passiveObjects;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import com.google.gson.*;
/**
 *  That's where Q holds his gadget (e.g. an explosive pen was used in GoldenEye, a geiger counter in Dr. No, etc).
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {
	private List<String> gadgets = new LinkedList<>();;


	public List<String> getGadgets()
	{
		return gadgets;
	}


	/**
     * Retrieves the single instance of this class.
     */
	private static class SingletonHolder {
		private static Inventory instance = new Inventory();
	}

	public static Inventory getInstance() {
		return Inventory.SingletonHolder.instance;
	}
	/*
	private static class SingletonHolder {
		private static Inventory instance = new Inventory();
	}

	public static Inventory getInstance()
	{
		if(instance==null)
			instance = new Inventory();
		return Inventory.SingletonHolder.instance;
	}
	*/

	/**
     * Initializes the inventory. This method adds all the items given to the gadget
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (String[] inventory) {
		//this.gadgets = this.getGadgets();
		for(int i=0; i<inventory.length; i++)
		{
			this.gadgets.add(inventory[i]);
		}
	}
	
	/**
     * acquires a gadget and returns 'true' if it exists.
     * <p>
     * @param gadget 		Name of the gadget to check if available
     * @return 	‘false’ if the gadget is missing, and ‘true’ otherwise
     */
	public boolean getItem(String gadget){
		boolean flag = false;
		for(String s:gadgets)
		{
			if(s.equals(gadget))
			{
				flag = true;
			}
		}
		if(!flag)
			return false;
		else
		{
			synchronized (gadgets){ // todo: we should check if the sync should be in the begining;
				gadgets.remove(gadget);
			}
		}
		return true;
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<String> which is a
	 * list of all the of the gadgeds.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename){
		try (Writer writer = new FileWriter(filename)) {
			Gson gson = new GsonBuilder().create();
			gson.toJson(gadgets, writer);
		}
		catch(IOException e) {}

	}
}
