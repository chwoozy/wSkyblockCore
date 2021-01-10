package me.yungweezy.skyblock.shop;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellLimiter {

	public static HashMap<UUID, HashMap<String, Integer>> soldToday = new HashMap<UUID, HashMap<String, Integer>>();

	@SuppressWarnings("deprecation")
	public static void sold(Player player, ItemStack stack){
		HashMap<String, Integer> pspec;
		if (soldToday.get(player.getUniqueId()) == null){
			pspec = new HashMap<String, Integer>();
		} else {
			pspec = soldToday.get(player.getUniqueId());
		}
		
		String item = stack.getTypeId() + "," + stack.getDurability();
		int putamount = stack.getAmount();
		
		if (pspec.get(item) != null){
			putamount = putamount + pspec.get(item);
		}
		
		pspec.put(item, putamount);
		soldToday.put(player.getUniqueId(), pspec);
	}
	
	@SuppressWarnings("deprecation")
	public static void sold(Player player, ItemStack stack, Integer amount){
		HashMap<String, Integer> pspec;
		if (soldToday.get(player.getUniqueId()) == null){
			pspec = new HashMap<String, Integer>();
		} else {
			pspec = soldToday.get(player.getUniqueId());
		}
		
		String item = stack.getTypeId() + "," + stack.getDurability();
		int putamount = amount;
		
		if (pspec.get(item) != null){
			putamount = putamount + pspec.get(item);
		}
		
		pspec.put(item, putamount);
		soldToday.put(player.getUniqueId(), pspec);
	}
	
	@SuppressWarnings("deprecation")
	public static Integer hasSoldToday(Player player, ItemStack stack){
		HashMap<String, Integer> pspec;
		if (soldToday.get(player.getUniqueId()) == null){
			return 0;
		} else {
			pspec = soldToday.get(player.getUniqueId());
		}
		
		String item = stack.getTypeId() + "," + stack.getDurability();
		
		if (pspec.get(item) != null){
			return pspec.get(item);
		}
		
		return 0;
	}
}
