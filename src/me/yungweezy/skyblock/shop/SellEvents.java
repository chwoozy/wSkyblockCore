package me.yungweezy.skyblock.shop;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.yungweezy.skyblock.main.Econ;
import me.yungweezy.skyblock.misc.Messages;

public class SellEvents implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(InventoryClickEvent event){
		if (event.getInventory() == null){
			return;
		}
		
		if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null){
			return;
		}
		
		Inventory inv = event.getInventory();
		if (!inv.getName().startsWith(ChatColor.DARK_AQUA + "SpaceSell " + ChatColor.BOLD + "� ")){
			return;
		}
		
		event.setCancelled(true);
		
		Player player = (Player) event.getWhoClicked();
		
		if (event.getRawSlot() == 4){
			HashMap<String, Integer> amntsold = new HashMap<String, Integer>();
			
			double giveCash = 0;
			
			for (int i = 0; i < player.getInventory().getSize(); i = i + 1){
				ItemStack stack = player.getInventory().getItem(i);
				if (stack != null && stack.getType() != null){
					double price = ShopMain.getPrice(stack);
					int amountsold = 0;
					int canStillSell = ShopMain.getMaxSell(stack) - SellLimiter.hasSoldToday(player, stack);
					
					if (canStillSell > 0){
						if (amntsold.get(stack.getTypeId() + "," + stack.getDurability()) != null){
							amountsold = amntsold.get(stack.getTypeId() + "," + stack.getDurability());
						}
						
						canStillSell = canStillSell - amountsold;
						
						if (price > -100){
							if (canStillSell > 0){
								if (canStillSell >= stack.getAmount()){
									amountsold = amountsold + stack.getAmount();
									canStillSell = canStillSell - stack.getAmount();
									player.getInventory().setItem(i, null);
									amntsold.put(stack.getTypeId() + "," + stack.getDurability(), amountsold);
								} else {
									int canSellOfThisStack = stack.getAmount() - (stack.getAmount() - canStillSell);
									stack.setAmount(stack.getAmount() - canSellOfThisStack);
									player.getInventory().setItem(i, stack);
									amountsold = amountsold + canSellOfThisStack;
									canStillSell = canStillSell - canSellOfThisStack;
									amntsold.put(stack.getTypeId() + "," + stack.getDurability(), amountsold);
								}
							}
						}
					}
				}
			}
			
			for (String s : amntsold.keySet()){
				ItemStack stack = new ItemStack(Material.getMaterial(Integer.parseInt(s.split(",")[0])));
				stack.setDurability((short) Integer.parseInt(s.split(",")[1]));
				double price = ShopMain.getPrice(stack);
				giveCash = (price * amntsold.get(s));
				SellLimiter.sold(player, stack, amntsold.get(s));
				
				Econ.economy.depositPlayer(player, giveCash);
				String sms = Messages.getMessage("sold_items");
				sms = sms.replace("<amount>", amntsold.get(s) + "");
				sms = sms.replace("<type>", stack.getType().toString().toLowerCase());
				sms = sms.replace("<price>", amntsold.get(s) * price + "");
				player.sendMessage(sms);
			}
			
			Inventory openNext = ShopGUI.getShopGui(player, 1);
			player.closeInventory();
			player.openInventory(openNext);
			
			return;
		}
		
		if (event.getRawSlot() == 8){
			String invname = ChatColor.stripColor(inv.getName());
			String pagestring = invname.substring(invname.length() - 2, invname.length());
			pagestring = pagestring.replace(" ", "");
			int page = Integer.parseInt(pagestring);
			Inventory toOpen = ShopGUI.getShopGui(player, page + 1);
			player.closeInventory();
			player.openInventory(toOpen);
			return;
		}
		
		if (event.getRawSlot() == 0){
			String invname = ChatColor.stripColor(inv.getName());
			String pagestring = invname.substring(invname.length() - 2, invname.length());
			pagestring = pagestring.replace(" ", "");
			int page = Integer.parseInt(pagestring);
			Inventory toOpen = ShopGUI.getShopGui(player, page - 1);
			player.closeInventory();
			player.openInventory(toOpen);
			return;
		}
		
		if (event.getRawSlot() >= event.getInventory().getSize() - 1){
			return;
		}
		
		ItemStack current = event.getCurrentItem();
		
		if (ShopMain.getPrice(current) < -100){
			player.sendMessage(Messages.getMessage("cant_sell_this"));
			return;
		}
		
		int hasSold = SellLimiter.hasSoldToday(player, current);
		
		if (hasSold >= ShopMain.getMaxSell(current) && ShopMain.getMaxSell(current) > -100){
			player.sendMessage(Messages.getMessage("sold_max_today"));
			return;
		}
		
		double price = ShopMain.getPrice(current);
		
		int canStillSell = ShopMain.getMaxSell(current) - hasSold;
		int amountsold = 0;
		
		for (int i = 0; i < player.getInventory().getSize(); i = i + 1){
			ItemStack stack = player.getInventory().getItem(i);
			if (stack != null && stack.getType() != null && stack.getType() == current.getType() && stack.getDurability() == current.getDurability()){
				if (canStillSell > 0){
					if (canStillSell >= stack.getAmount()){
						amountsold = amountsold + stack.getAmount();
						canStillSell = canStillSell - stack.getAmount();
						player.getInventory().setItem(i, null);
					} else {
						int canSellOfThisStack = stack.getAmount() - (stack.getAmount() - canStillSell);
						stack.setAmount(stack.getAmount() - canSellOfThisStack);
						player.getInventory().setItem(i, stack);
						amountsold = amountsold + canSellOfThisStack;
						canStillSell = canStillSell - canSellOfThisStack;
					}
				}
			}
		}
		
		if (amountsold == 0){
			player.sendMessage(Messages.getMessage("dont_have_any"));
			return;
		}

		
		Econ.economy.depositPlayer(player, amountsold * price);
		
		String sms = Messages.getMessage("sold_items");
		sms = sms.replace("<amount>", amountsold + "");
		sms = sms.replace("<type>", current.getType().toString().toLowerCase());
		sms = sms.replace("<price>", amountsold * price + "");
		player.sendMessage(sms);
		
		ItemStack sold = current.clone();
		sold.setAmount(amountsold);
		SellLimiter.sold(player, sold);
		
		Inventory openNext = ShopGUI.getShopGui(player, 1);
		player.closeInventory();
		player.openInventory(openNext);
	}
}
