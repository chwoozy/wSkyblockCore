package me.yungweezy.skyblock.misc;

import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.yungweezy.skyblock.main.FileAccessor;

public class IslandLevels {

	public static int lvsperLevel = 1000;
	private static HashMap<String, Double> lvs = new HashMap<String, Double>();
	
	public static void updateLevels(){
		FileAccessor.reloadFileF("level");
		FileConfiguration f = FileAccessor.getFileF("level");
		
		for (String raw : f.getConfigurationSection("").getKeys(false)){
			String[] split = raw.split(",");
			if (split.length == 1){
				lvs.put(split[0] + ",*", f.getDouble(split[0]));
			} else if (split.length == 2){
				if (split[1].equals("*")){
					lvs.put(split[0] + ",*", f.getDouble(split[0]));
				} else {
					try {
						lvs.put(split[0] + "," + Integer.parseInt(split[1]), f.getDouble(split[0] + "," + Integer.parseInt(split[1])));
					} catch (Exception e) {
						System.out.print("Levels: " + raw + " is not configured correctly");
					}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static Double getLevel(ItemStack stack){
		if (lvs.get(stack.getTypeId() + "," + stack.getDurability()) != null){
			return lvs.get(stack.getTypeId() + "," + stack.getDurability());
		}
		
		if (lvs.get(stack.getTypeId() + ",*") != null){
			return lvs.get(stack.getTypeId() + ",*");
		}
		
		return 0.0;
	}
}
