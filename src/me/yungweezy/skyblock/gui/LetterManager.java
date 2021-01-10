package me.yungweezy.skyblock.gui;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LetterManager {

	public HashMap<Integer, ItemStack> cachedLetters = new HashMap<Integer, ItemStack>();
	
	public ItemStack getLetter(int alphabet){
		ItemStack cached = cachedLetters.get(alphabet); 
		if (cached != null){
			return cached;
		}
		
		Location loc = new Location(Bukkit.getWorld("skyworld"), -800, 100, -800);
		loc.setX(loc.getX() + alphabet);
		if (loc.getBlock() != null && loc.getBlock().getType() == Material.SKULL){
			ItemStack stack = (ItemStack) loc.getBlock().getDrops().toArray()[0];
			stack.setDurability((short) 3);
			cachedLetters.put(alphabet, stack);
			return stack;
		} else {
			loc.getBlock().setType(Material.WOOL);
			return (new ItemStack(Material.DIRT));
		}
	}
	
}
