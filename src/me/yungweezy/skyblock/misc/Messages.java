package me.yungweezy.skyblock.misc;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import me.yungweezy.skyblock.main.FileAccessor;

public class Messages {

	public static HashMap<String, String> messagez = new HashMap<String, String>();
	
	public static void updateMessages(){
		FileConfiguration f = FileAccessor.getFileF("messages");
		
		for (String s : f.getConfigurationSection("").getKeys(false)){
			messagez.put(s, ChatColor.translateAlternateColorCodes('&', f.getString(s)));
		}
	}
	
	public static String getMessage(String time){
		if (messagez.get(time) != null){
			return messagez.get(time);
		}
		
		return "<" + time + "> (not configured #BlameCloudWaffles)"; 
	}
}
