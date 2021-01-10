package me.yungweezy.skyblock.misc;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;


public class NoLoreAnvil implements Listener {

	@EventHandler
	public void onInvClick(InventoryClickEvent event){
		if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || event.getCurrentItem().getType() == Material.AIR){
			return;
		}
		
		if (event.getInventory() == null){
			return;
		}
		
		if (event.getInventory().getType() == null || event.getInventory().getType() != InventoryType.ANVIL){
			return;
		}
		
		if (event.getRawSlot() != 2){
			return;
		}
		
		ItemStack stack = event.getCurrentItem();
		
		if (stack.getType() == Material.TRIPWIRE_HOOK){
			event.setCancelled(true);
			((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "You cant use this item in an anvil!");
			return;
		}
		
		if (stack.getItemMeta() == null || stack.getItemMeta().getLore() == null){
			return;
		}
		
		ArrayList<String> lore = (ArrayList<String>) stack.getItemMeta().getLore();
		if (lore.contains(ChatColor.translateAlternateColorCodes('&', "&7Donor Kits Exclusive"))){
			event.setCancelled(true);
			((Player) event.getWhoClicked()).sendMessage(ChatColor.RED + "You cant use this item in an anvil!");
		}
	}
	
}
