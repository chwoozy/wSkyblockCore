package me.yungweezy.skyblock.shop;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import me.yungweezy.skyblock.main.FileAccessor;

public class ShopMain {

	public static HashMap<String, Double> prices = new HashMap<String, Double>();
	public static HashMap<Integer, String> itemslots = new HashMap<Integer, String>();
	public static HashMap<String, Integer> maxsell = new HashMap<String, Integer>();
	
	public static void loadAll(){
		FileAccessor.reloadFileF("shop");
		FileConfiguration file = FileAccessor.getFileF("shop");
		
		int slot = 0;
		for (String uc : file.getConfigurationSection("prices").getKeys(false)){
			String[] c = uc.split(",");
			if (c.length == 1){
				try {
					String item = uc + ",0";
					itemslots.put(slot, item);
					slot = slot + 1;
					prices.put(item, file.getDouble("prices." + uc));
				} catch (Exception e){
					slot = slot + 1;
					System.out.print("SkyBlock: Item " + uc + " is not defined correcly! (price)");
				}
			} else if (c.length == 2) {
				try {
					String item = uc + "";
					itemslots.put(slot, item);
					slot = slot + 1;
					prices.put(item, file.getDouble("prices." + uc));
				} catch (Exception e){
					slot = slot + 1;
					System.out.print("SkyBlock: Item " + uc + " is not defined correcly! (price)");
				}
			}
		}
		
		for (String uc : file.getConfigurationSection("maxsell").getKeys(false)){
			String[] c = uc.split(",");
			if (c.length == 1){
				try {
					String item = uc + ",0";
					maxsell.put(item, file.getInt("maxsell." + uc));
				} catch (Exception e){
					System.out.print("SkyBlock: Item " + uc + " is not defined correcly! (maxsell)");
				}
			} else if (c.length == 2) {
				try {
					String item = uc + "";
					maxsell.put(item, file.getInt("maxsell." + uc));
				} catch (Exception e){
					System.out.print("SkyBlock: Item " + uc + " is not defined correcly! (maxsell)");
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static Double getPrice(ItemStack stack){
		String item = stack.getTypeId() + "," + stack.getDurability();
		
		if (prices.get(item) == null){
			return (double) -1000;
		}
		
		return (prices.get(item));
	}
	
	@SuppressWarnings("deprecation")
	public static Integer getMaxSell(ItemStack stack){
		String item = stack.getTypeId() + "," + stack.getDurability();
		if (maxsell.get(item) == null){
			return -1000;
		}
		
		return maxsell.get(item);
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getFromString(String string){
		String[] split = string.split(",");
		ItemStack stack = new ItemStack(Material.getMaterial(Integer.parseInt(split[0])), 1, (byte) Integer.parseInt(split[1]));
		return stack;
	}
}
