package me.yungweezy.skyblock.misc;

import java.util.HashMap;
import java.util.Random;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class Hunger implements Listener {

	@EventHandler
	public void onHungerChange(FoodLevelChangeEvent event){
		if (event.getFoodLevel() > ((Player) event.getEntity()).getFoodLevel()){
			//gained food
			return;
		}
		
		Player player = (Player) event.getEntity();
		
		HashMap<String, Integer> decreasePerms = new HashMap<String, Integer>();
		decreasePerms.put("skyblock.hunger.10", 10);
		decreasePerms.put("skyblock.hunger.20", 20);
		decreasePerms.put("skyblock.hunger.30", 30);
		decreasePerms.put("skyblock.hunger.40", 40);
		decreasePerms.put("skyblock.hunger.50", 50);
		
		int highest = 0;
		
		for (String s : decreasePerms.keySet()){
			if (player.hasPermission(s)){
				highest = decreasePerms.get(s);
			}
		}
		
		if (highest == 0){
			return;
		}
		
		Random random = new Random();
		int randint = random.nextInt(100);
		if (randint > highest){
			event.setCancelled(true);
		}
	}
	
	
}
