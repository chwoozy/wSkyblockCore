package me.yungweezy.skyblock.misc;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.yungweezy.skyblock.main.CreateItem;
import me.yungweezy.skyblock.main.Main;
import me.yungweezy.skyblock.managers.SkyBlock;
import me.yungweezy.skyblock.managers.SkyPlayer;
import me.yungweezy.skyblock.managers.SBUtils;
import me.yungweezy.skyblock.managers.SkyplayerManager;
import net.md_5.bungee.api.ChatColor;

public class WarpStuff implements Listener {

	public static ItemStack warpTag;
	
	public static void buildTag(){
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.AQUA + "Right click on one");
		lore.add(ChatColor.AQUA + "of  your islands to");
		lore.add(ChatColor.AQUA + "set its warp!");
		warpTag = CreateItem.createItem(Material.NAME_TAG, 0, "&6Warp Ticket &7(single use)", lore);
	}
	
	public static boolean isTag(ItemStack stack){
		if (stack == null){
			return false;
		}
		
		if (stack.getType() == null || stack.getType() == Material.AIR){
			return false;
		}
		
		if (stack.getType() != warpTag.getType()){
			return false;
		}
		
		if (stack.getItemMeta() == null || stack.getItemMeta().getDisplayName() == null || stack.getItemMeta().getDisplayName() == null){
			return false;
		}
		
		ArrayList<String> lore = (ArrayList<String>) stack.getItemMeta().getLore();
		String name = stack.getItemMeta().getDisplayName();
		
		if (!name.equals(warpTag.getItemMeta().getDisplayName())){
			return false;
		}
		
		if (lore.size() != warpTag.getItemMeta().getLore().size()){
			return false;
		}
		
		for (int i = 0; i < lore.size(); i = i + 1){
			if (!lore.get(i).equals(warpTag.getItemMeta().getLore().get(i))){
				return false;
			}
		}
		
		return true;
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event){
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK){
			return;
		}
		
		Player player = event.getPlayer();

		ItemStack hand = player.getInventory().getItemInMainHand();
		if (hand == null){
			return;
		}
		
		if (!isTag(hand)){
			return;
		}
		
		if (hand.getAmount() == 1){
			player.getInventory().setItemInMainHand(null);
		} else {
			hand.setAmount(hand.getAmount() - 1);
			player.getInventory().setItemInMainHand(null);
		}
		
		player.openInventory(WarpStuff.getSetupWarpInv(player));
		player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "Click the island you want to enable /is setwarp on!");
	}

	@SuppressWarnings("deprecation")
	public static Inventory getWarpInv(Player player){
		Inventory inv = Bukkit.createInventory(null, 9, Main.guiMain.menustart + " Player Warps");
		ItemStack head = SBUtils.getHead(player.getName());
		inv.setItem(8, head);
		
		if (SkyplayerManager.SkyPlayers.get(player.getUniqueId()) == null){
			return inv;
		}
		
		SkyPlayer pl = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
		for (int i = 0; i <= 3; i = i + 1){
			if (pl.getID(i + 1) != null){
				SkyBlock block = Main.blockManager.skyBlocksByID.get(pl.getID(i + 1));
				if (block.getWarp() != null && block.isLocked() == false){
					ItemStack info = SBUtils.getInfoItem(block);
					ArrayList<String> lore = (ArrayList<String>) info.getItemMeta().getLore();
					lore.add(ChatColor.GRAY + block.getID());
					info = CreateItem.setLore(info, lore);
					inv.setItem(i, info);
				} else {
					ItemStack stack = CreateItem.createItem(Material.getMaterial(166), 0, "&4&lX", new ArrayList<String>());
					inv.setItem(i, stack);
				}
			}
		}
		
		
		return inv;
	}
	
	@SuppressWarnings("deprecation")
	public static Inventory getWarpInv(OfflinePlayer player){
		Inventory inv = Bukkit.createInventory(null, 9, Main.guiMain.menustart + " Player Warps");
		ItemStack head = SBUtils.getHead(player.getName());
		inv.setItem(8, head);
		
		if (SkyplayerManager.SkyPlayers.get(player.getUniqueId()) == null){
			SkyplayerManager.loadForPlayer(player);
		}
		
		SkyPlayer pl = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
		for (int i = 0; i <= 3; i = i + 1){
			if (pl.getID(i + 1) != null){
				SkyBlock block = Main.blockManager.skyBlocksByID.get(pl.getID(i + 1));
				if (block.getWarp() != null && block.isLocked() == false){
					ItemStack info = SBUtils.getInfoItem(block);
					ArrayList<String> lore = (ArrayList<String>) info.getItemMeta().getLore();
					lore.add(ChatColor.GRAY + block.getID());
					info = CreateItem.setLore(info, lore);
					inv.setItem(i, info);
				} else {
					ItemStack stack = CreateItem.createItem(Material.getMaterial(166), 0, "&4&lX", new ArrayList<String>());
					inv.setItem(i, stack);
				}
			}
		}
		
		
		return inv;
	}
	
	public static Inventory getOPWarpInv(Player player){
		Inventory inv = Bukkit.createInventory(null, 9, Main.guiMain.menustart + " PlayerWarps");
		ItemStack head = SBUtils.getHead(player.getName());
		inv.setItem(8, head);
		
		if (SkyplayerManager.SkyPlayers.get(player.getUniqueId()) == null){
			return inv;
		}
		
		SkyPlayer pl = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
		for (int i = 0; i <= 3; i = i + 1){
			if (pl.getID(i + 1) != null){
				SkyBlock block = Main.blockManager.skyBlocksByID.get(pl.getID(i + 1));
				ItemStack info = SBUtils.getInfoItem(block);
				ArrayList<String> lore = (ArrayList<String>) info.getItemMeta().getLore();
				lore.add(ChatColor.GRAY + block.getID());
				info = CreateItem.setLore(info, lore);
				inv.setItem(i, info);
			}
		}
		
		
		return inv;
	}
	
	public static Inventory getOPWarpInv(OfflinePlayer player){
		Inventory inv = Bukkit.createInventory(null, 9, Main.guiMain.menustart + " PlayerWarps");
		ItemStack head = SBUtils.getHead(player.getName());
		inv.setItem(8, head);
		
		if (SkyplayerManager.SkyPlayers.get(player.getUniqueId()) == null){
			SkyplayerManager.loadForPlayer(player);
		}
		
		SkyPlayer pl = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
		for (int i = 0; i <= 3; i = i + 1){
			if (pl.getID(i + 1) != null){
				SkyBlock block = Main.blockManager.skyBlocksByID.get(pl.getID(i + 1));
				ItemStack info = SBUtils.getInfoItem(block);
				ArrayList<String> lore = (ArrayList<String>) info.getItemMeta().getLore();
				lore.add(ChatColor.GRAY + block.getID());
				info = CreateItem.setLore(info, lore);
				inv.setItem(i, info);
			}
		}
		
		
		return inv;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInvClick(InventoryClickEvent event){
		if (event.getInventory() == null || event.getInventory().getName() == null){
			return;
		}
		
		if (event.getRawSlot() != event.getSlot()){
			return;
		}
		
		if (!event.getInventory().getName().equals(Main.guiMain.menustart + " Player Warps") && !event.getInventory().getName().equals(Main.guiMain.menustart + " PlayerWarps")){
			return;
		}
		
		if (event.getRawSlot() > 3){
			return;
		}
		
		if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null){
			return;
		}
		
		if (event.getCurrentItem().getTypeId() == 166 && (event.getCurrentItem().getItemMeta().getLore() == null || event.getCurrentItem().getItemMeta().getLore().isEmpty())){
			return;
		}
		
		String ID = event.getCurrentItem().getItemMeta().getLore().get(3);
		ID = ChatColor.stripColor(ID);
		
		if (Main.blockManager.skyBlocksByID.get(ID) == null){
			return;
		}
		
		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
		if (block.getWarp() != null){
			event.getWhoClicked().teleport(block.getWarp());
			return;
		}
		
		if (event.getWhoClicked().hasPermission("skyblock.forcewarp")){
			event.getWhoClicked().teleport(block.getHome());
			((Player) event.getWhoClicked()).sendMessage(ChatColor.AQUA + "Forced warp!");
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onChooseInvClick(InventoryClickEvent event){
		if (event.getInventory() == null || event.getInventory().getName() == null){
			return;
		}
		
		if (event.getRawSlot() != event.getSlot()){
			return;
		}
		
		Inventory inv = event.getInventory();
		
		if (!inv.getName().equals(Main.guiMain.menustart + " EnableWarp")){
			return;
		}
		
		if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || event.getCurrentItem().getType() == Material.AIR){
			return;
		}
		
		if (event.getCurrentItem().getTypeId() == 166 && (event.getCurrentItem().getItemMeta().getLore() == null || event.getCurrentItem().getItemMeta().getLore().isEmpty())){
			return;
		}
		
		ItemStack stack = event.getCurrentItem();
		
		if (stack.getItemMeta().getDisplayName().equals(ChatColor.RED + "" + ChatColor.BOLD + "X")){
			return;
		}
		
		String ID = stack.getItemMeta().getLore().get(3);
		ID = ChatColor.stripColor(ID);
		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
		block.setWarp(block.getCenter());
		Main.blockManager.skyBlocksByID.put(ID, block);
		Main.blockManager.saveSkyBlock(ID, block);
		((Player) event.getWhoClicked()).sendMessage(ChatColor.GREEN + "You can now use /is setwarp for this island!");
		inv.setItem(8, null);
		event.getWhoClicked().closeInventory();
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent event){
		if (event.getInventory() == null || event.getInventory().getName() == null){
			return;
		}
		
		Inventory inv = event.getInventory();
		
		if (!inv.getName().equals(Main.guiMain.menustart + " EnableWarp")){
			return;
		}
		
		if (inv.getItem(8) != null){
			event.getPlayer().getInventory().addItem(WarpStuff.warpTag);
		}
	}

	@SuppressWarnings("deprecation")
	public static Inventory getSetupWarpInv(Player player){
		Inventory inv = Bukkit.createInventory(null, 9, Main.guiMain.menustart + " EnableWarp");
		ItemStack head = SBUtils.getHead(player.getName());
		inv.setItem(8, head);
		
		if (SkyplayerManager.SkyPlayers.get(player.getUniqueId()) == null){
			return inv;
		}
		
		SkyPlayer pl = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
		for (int i = 0; i <= 3; i = i + 1){
			if (pl.getID(i + 1) != null){
				SkyBlock block = Main.blockManager.skyBlocksByID.get(pl.getID(i + 1));
				if (block.getWarp() != null){
					ItemStack stack = CreateItem.createItem(Material.getMaterial(166), 0, "&4&lX", new ArrayList<String>());
					inv.setItem(i, stack);
				} else {
					ItemStack info = SBUtils.getInfoItem(block);
					ArrayList<String> lore = (ArrayList<String>) info.getItemMeta().getLore();
					lore.add(ChatColor.GRAY + block.getID());
					info = CreateItem.setLore(info, lore);
					inv.setItem(i, info);
				}
			}
		}
		
		return inv;
	}
}
