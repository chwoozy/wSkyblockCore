package me.yungweezy.skyblock.shop;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.yungweezy.skyblock.main.CreateItem;

public class ShopGUI {
	
	private static ItemStack info = null;

	public static Inventory getShopGui(Player player, int page){
		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.DARK_AQUA + "SpaceSell " + ChatColor.BOLD + "� " + ChatColor.DARK_AQUA + page);
		if (page > 1){
			ItemStack back = CreateItem.createItem(Material.ARROW, 0, "&f&lBack", new ArrayList<String>());
			inv.setItem(0, back);
		}
		
		if (page < 4){
			ItemStack next = CreateItem.createItem(Material.ARROW, 0, "&f&lNext", new ArrayList<String>());
			inv.setItem(8, next);
		}
		
		if (info == null){
			ArrayList<String> infol = new ArrayList<String>();
			infol.add(ChatColor.GRAY + "Click an item to");
			infol.add(ChatColor.GRAY + "sell all items in your");
			infol.add(ChatColor.GRAY + "inventory of that item!");
			info = CreateItem.createItem(Material.PAPER, 0, "&6&lInfo", infol);
		}
		
		inv.setItem(3, info);
		inv.setItem(5, info);
		
		ArrayList<String> sellalll = new ArrayList<String>();
		sellalll.add(ChatColor.GRAY + "Click here to sell");
		sellalll.add(ChatColor.GRAY + "Everything you can");
		ItemStack sellall = CreateItem.createItem(Material.EMERALD, 0, "&6&lSell Everything", sellalll);
		inv.setItem(4, sellall);
	
		for (int slot = 0; slot <= 45; slot = slot + 1){
			int invslot = slot + 9;
			int itemnr = slot + (page * 45) - 45;
			if (ShopMain.itemslots.get(itemnr) != null){
				String item = ShopMain.itemslots.get(itemnr);

				ArrayList<String> stackl = new ArrayList<String>();
				stackl.add(ChatColor.GRAY + "Sell 1 for");
				ItemStack basicstack = ShopMain.getFromString(item);
				ItemStack stack = CreateItem.createItem(basicstack.getType(), basicstack.getDurability(), "", stackl);
				stackl.add(ChatColor.GOLD + "" + ShopMain.getPrice(stack));

				if (ShopMain.maxsell.get(item) == null){
					stackl.add(ChatColor.GRAY + "You can sell as many as you want!");
				} else {
					stackl.add(ChatColor.GRAY + "Sold " + SellLimiter.hasSoldToday(player, stack) + "/" + ShopMain.maxsell.get(item) + " for today!");
					if (SellLimiter.hasSoldToday(player, stack) >= ShopMain.getMaxSell(stack)){
						stackl.add(ChatColor.RED + "Sell more tomorrow!");
					}
				}

				stack = CreateItem.setLore(stack, stackl);

				inv.setItem(invslot, stack);
			}
		}
		
		return inv;
	}
}
