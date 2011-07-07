package me.simplex.buildr;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import me.simplex.buildr.util.Buildr_ItemStackSaveContainer;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Buildr_InventoryManager {
	Buildr plugin;

	public Buildr_InventoryManager(Buildr plugin) {
		this.plugin = plugin;
	}
	
	public void switchInventory(Player player){
		checkBackupFile(player);
		ItemStack[] currInv = loadCurrentInventory(player);
		ItemStack[] newInv = loadBackupInventory(player);
		
		saveBackupInventory(player, currInv);
			
		changeCurrentInventory(player, newInv);
		
	}

	private ItemStack[] loadCurrentInventory(Player player){
		return  player.getInventory().getContents();
	}
	
	private void changeCurrentInventory(Player player, ItemStack[] newInv){
		for (int i = 0; i < player.getInventory().getSize(); i++) {
			player.getInventory().setItem(i, newInv[i]);
		}
	}
	
	
	/**
	 * @param invtype true= normal, false=build
	 * @return fullinv with Armor
	 */

	private ItemStack[] loadBackupInventory(Player player){
		ObjectInputStream objctInStrm;
		Buildr_ItemStackSaveContainer[] FileContainer =null;

		try {
			objctInStrm = new ObjectInputStream(new FileInputStream(plugin.getPluginDirectory()+File.separator+"inv_data"+File.separator+player.getName()+".inv"));
			FileContainer = (Buildr_ItemStackSaveContainer[])objctInStrm.readObject();
			objctInStrm.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		ItemStack[] inventory= new ItemStack[FileContainer.length];
		for (int i = 0; i < FileContainer.length; i++) {
			if (FileContainer[i]!=null) {
				inventory[i] = new ItemStack(FileContainer[i].getType(), FileContainer[i].getAmount(), FileContainer[i].getDurability(), FileContainer[i].getMaterial_data());
			}
		}
		return inventory;
	}
	
	private void saveBackupInventory(Player player,ItemStack[] inv){
		Buildr_ItemStackSaveContainer[] FileContainer =new Buildr_ItemStackSaveContainer[inv.length];
		for (int i = 0; i < inv.length; i++) {
			if (inv[i]!= null) {
				byte savedata = 0;
				if (inv[i].getData() != null) {
					savedata = inv[i].getData().getData();
				}
				FileContainer[i] = new Buildr_ItemStackSaveContainer(inv[i].getTypeId(), inv[i].getAmount(), inv[i].getDurability(), savedata);
			}
		}
		
		try {
			ObjectOutputStream objctOutStrm = new ObjectOutputStream(new FileOutputStream(plugin.getPluginDirectory()+File.separator+"inv_data"+File.separator+player.getName()+".inv"));
			objctOutStrm.writeObject(FileContainer);
			objctOutStrm.flush();
			objctOutStrm.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean startupCheck(){
		return new File(plugin.getPluginDirectory()+File.separator+"inv_data").mkdir();
	}
	
	private void checkBackupFile(Player player) {
		if (new File(plugin.getPluginDirectory()+File.separator+"inv_data"+File.separator+player.getName()+".inv").exists()) {
			plugin.log("InvBackup for "+player.getName()+" found");
			return;
		}
		createNewBuildInv(player);
		plugin.log("InvBackup for "+player.getName()+" created");
	}

	private void createNewBuildInv(Player player) {
		ItemStack[] newinv = new ItemStack[40];
		
		newinv[0]  = new ItemStack(278, 1); //Diamond Pickaxe
		newinv[1]  = new ItemStack(279, 1); //Diamond axe
		newinv[2]  = new ItemStack(280, 1); //Stick
		newinv[3]  = new ItemStack(50,64); //Torch
		
		newinv[5]  = new ItemStack(20, 64); //Stone
		newinv[6]  = new ItemStack(1, 64); //Stone
		newinv[7]  = new ItemStack(4, 64); //Cobblestone
		newinv[8]  = new ItemStack(5, 64); //Planks
		
		newinv[9]  = new ItemStack(53, 64); //Woodenstairs
		newinv[10] = new ItemStack(67, 64); //Cobblestonestairs
		
		saveBackupInventory(player, newinv);
	}
	
	public void updateInventoryStateFile(ArrayList<Player> builders){
		ArrayList<String> playernames= new ArrayList<String>();
		for (Player player : builders) {
			playernames.add(player.getName());
		}

		System.out.println("StateFile update. size:"+builders.size());
		
		if (playernames == null) {
			deleteInventoryStateFile();
			return;
		}
		
		try {
			ObjectOutputStream objctOutStrm = new ObjectOutputStream(new FileOutputStream(plugin.getPluginDirectory()+File.separator+"inv_data"+File.separator+"InventoryState.dat"));
			objctOutStrm.writeObject(playernames);
			objctOutStrm.flush();
			objctOutStrm.close();
		} catch (Exception e) {
			plugin.log("Failed to write to InventoryStateFile");
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> loadInventoryStateFile(){
		ArrayList<String> toHandleName = new ArrayList<String>();
		try {
			ObjectInputStream objctInStrm = new ObjectInputStream(new FileInputStream(plugin.getPluginDirectory()+File.separator+"inv_data"+File.separator+"InventoryState.dat"));
			toHandleName = (ArrayList<String>)objctInStrm.readObject();
			objctInStrm.close();

		} catch (ClassNotFoundException e) {
			// TODO: handle exception
		}catch (Exception e) {
			plugin.log("Failed to load InventoryStateFile");
			e.printStackTrace();
		}
		return toHandleName;
	}
	
	public boolean checkInventoryStateFile(){
		return new File(plugin.getPluginDirectory()+File.separator+"inv_data"+File.separator+"InventoryState.dat").exists();
	}
	
	public void deleteInventoryStateFile(){
		File inventoryState = new File(plugin.getPluginDirectory()+File.separator+"inv_data"+File.separator+"InventoryState.dat");
		inventoryState.delete();
	}
	
}
