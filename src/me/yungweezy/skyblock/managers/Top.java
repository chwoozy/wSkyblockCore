package me.yungweezy.skyblock.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import me.yungweezy.skyblock.main.FileAccessor;
import me.yungweezy.skyblock.main.Main;
import me.yungweezy.skyblock.misc.IslandLevels;

public class Top {

	private static long lastTime = 0;
	public static HashMap<Integer, String> skyblocks = new HashMap<Integer, String>(); // POSISTION, SKYBLOCK ID
	private static HashMap<String, Double> lvs = new HashMap<String, Double>();
	private static HashMap<Double, ArrayList<String>> reLvs = new HashMap<Double, ArrayList<String>>();
	private static TreeMap<Double, ArrayList<String>> sortedReLvs;
	private static HashMap<Location, Integer> headLocs = new HashMap<Location, Integer>();
	
	public static void sort(){
		if (System.currentTimeMillis() - lastTime < 1800000){ // 30 minutes
			return;
		}
		
		skyblocks = new HashMap<Integer, String>();
		lvs = new HashMap<String, Double>();
		reLvs = new HashMap<Double, ArrayList<String>>();
		
		for (String ID : Main.blockManager.skyBlocksByID.keySet()){
			if (Main.blockManager.skyBlocksByID.get(ID).getLevel() / IslandLevels.lvsperLevel < 1){
				lvs.put(ID, 1.0);
			} else {
				lvs.put(ID, Main.blockManager.skyBlocksByID.get(ID).getLevel());
			}
		}
		
		for (String ID : lvs.keySet()){
			Double lv = lvs.get(ID);
			ArrayList<String> put = new ArrayList<String>();
			if (reLvs.get(lv) != null){
				put = reLvs.get(lv);
			}
			
			put.add(ID);
			reLvs.put(lv, put);
		}
		
		sortedReLvs = new TreeMap<Double, ArrayList<String>>(reLvs);
		
		int place = Main.blockManager.skyBlocksByID.size();
		for (double lv : sortedReLvs.keySet()){
			ArrayList<String> get = sortedReLvs.get(lv);
			for (String ID : get){
				skyblocks.put(place, ID);
				place = place - 1;
			}
		}
		
		lastTime = System.currentTimeMillis();
	}

	public static void initialHeads(){
		FileAccessor.reloadFileF("heads");
		FileConfiguration file = FileAccessor.getFileF("heads");
		for (String s : file.getConfigurationSection("").getKeys(false)){
			Location loc = SBUtils.stringToAbsoluteLoc(s);
			int position = file.getInt(s);
			headLocs.put(loc, position);
		}
		
		updateHeads();
		
		System.out.println("Got headLocs filled: " + headLocs);
	}
	
	public static void updateHeads(){
		sort();
		Plugin pl = Main.getPlugin(Main.class);
		pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				for (Location loc : headLocs.keySet()){
					Block block = loc.getBlock();
					
					if (block.getType() == null || block.getType() == Material.AIR || (block.getType() != Material.SKULL && block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN)){
						System.out.println("No head at " + loc + " replacing without rotation");
						block.setType(Material.SKULL);
					}
					
					if (block.getType() == Material.SKULL){
						Skull state = (Skull) block.getState();
						state.setSkullType(SkullType.PLAYER);
						
						state.update();
						
						String ID = skyblocks.get(headLocs.get(loc));
						
						if (ID != null){
							SkyBlock sblock = Main.blockManager.skyBlocksByID.get(ID);
							try {
								state.setOwner(Bukkit.getOfflinePlayer(sblock.getOwner()).getName());
								System.out.println("Updated head at " + loc);
							} catch (Exception e){
								System.out.println("Couldnt update head at " + loc);
							}
							
							state.update();
						}
					} else {
						String ID = skyblocks.get(headLocs.get(loc));
						SkyBlock sblock = Main.blockManager.skyBlocksByID.get(ID);
						
						if (ID != null){
							Sign state = (Sign) block.getState();
							state.setLine(1, Bukkit.getOfflinePlayer(sblock.getOwner()).getName());
							state.setLine(2, ChatColor.BOLD + "LVL " + ChatColor.RESET + sblock.getLevel() / IslandLevels.lvsperLevel);
							state.update();
						}
					}
				}
				
				Top.updateHeads();
			}
		}, 60 * 30 * 20L);
	}
}
