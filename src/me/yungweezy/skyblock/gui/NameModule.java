package me.yungweezy.skyblock.gui;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.yungweezy.skyblock.main.CreateItem;
import me.yungweezy.skyblock.main.Main;
import me.yungweezy.skyblock.managers.SkyBlock;
import net.minecraft.server.v1_10_R1.ChatMessage;
import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.PacketPlayOutOpenWindow;

public class NameModule implements Listener {

	public ArrayList<UUID> workingInName = new ArrayList<UUID>();
	public ArrayList<UUID> workingInReName = new ArrayList<UUID>();
	
	public void openNameInv(Player player){
		CraftPlayer craftPlayer = (CraftPlayer) player;
		EntityPlayer entityPlayer = craftPlayer.getHandle();
		
		NameAnvil nameAnvil = new NameAnvil(entityPlayer);
        int containerId = entityPlayer.nextContainerCounter();
   
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, "minecraft:anvil", new ChatMessage("sjdfsld1sdofu", new Object[]{}), 0));
   
        entityPlayer.activeContainer = nameAnvil;
        entityPlayer.activeContainer.windowId = containerId;
        entityPlayer.activeContainer.addSlotListener(entityPlayer);
        entityPlayer.activeContainer = nameAnvil;
        entityPlayer.activeContainer.windowId = containerId;
   
        Inventory inv = nameAnvil.getBukkitView().getTopInventory();
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("&aType in your skyblock name");
		ItemStack nametag = CreateItem.createItem(Material.NAME_TAG, 0, " ", lore);
		
		inv.setItem(0, nametag);
		
		workingInName.add(player.getUniqueId());
	}
	
	public void openReNameInv(Player player, String ID){
		CraftPlayer craftPlayer = (CraftPlayer) player;
		EntityPlayer entityPlayer = craftPlayer.getHandle();
		
		NameAnvil nameAnvil = new NameAnvil(entityPlayer);
        int containerId = entityPlayer.nextContainerCounter();
   
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, "minecraft:anvil", new ChatMessage("sjdfsld1sdofu", new Object[]{}), 0));
   
        entityPlayer.activeContainer = nameAnvil;
        entityPlayer.activeContainer.windowId = containerId;
        entityPlayer.activeContainer.addSlotListener(entityPlayer);
        entityPlayer.activeContainer = nameAnvil;
        entityPlayer.activeContainer.windowId = containerId;
   
        Inventory inv = nameAnvil.getBukkitView().getTopInventory();
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.add("&aType in your new skyblock name");
		lore.add(ID);
		ItemStack nametag = CreateItem.createItem(Material.NAME_TAG, 0, " ", lore);
		
		inv.setItem(0, nametag);
		
		workingInReName.add(player.getUniqueId());
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent event){
		if (event.getInventory() == null){
			return;
		}
		
		Inventory inv = event.getInventory();
		Player player = (Player) event.getWhoClicked();
		
		if (!workingInName.contains(player.getUniqueId()) && !workingInReName.contains(player.getUniqueId())){
			return;
		}
		
		if (inv.getType() != InventoryType.ANVIL){
			return;
		}
		
		event.setCancelled(true);
		
		if (event.getRawSlot() != 2){
			return;
		}
		
		// slot 2 = result slot
		
		String name = CreateItem.getName(event.getCurrentItem()).toLowerCase();
		
		if (name.substring(0, 1).equals(" ")){
			name = name.substring(1, name.length());
		}

		if (workingInReName.contains(player.getUniqueId())){
			String ID = CreateItem.getLore(event.getCurrentItem()).get(1);
			SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
			block.setName(name);
			Main.blockManager.skyBlocksByID.put(ID, block);
			Main.blockManager.saveSkyBlock(ID, block);
			
			player.closeInventory();
			player.openInventory(Main.guiMain.getSkyBlockSpecific(ID));
			Main.guiMain.workingIn.put(player.getUniqueId(), ID);
			return;
		}
		
		Inventory chooseIcon = Main.guiMain.getIconInventory(1);
		Inventory pspec = Bukkit.createInventory(null, chooseIcon.getSize(), chooseIcon.getName());
		pspec.setContents(chooseIcon.getContents());
		pspec.setItem(4, CreateItem.createItem(Material.PAPER, 0, name, new ArrayList<String>()));
		player.closeInventory();
		player.openInventory(pspec);
		// end
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent event){
		Player player = (Player) event.getPlayer();
		if (workingInName.contains(player.getUniqueId()) || workingInReName.contains(player.getUniqueId())){
			Inventory inv = event.getInventory();
			inv.setItem(0, null);
			workingInName.remove(player.getUniqueId());
			workingInReName.remove(player.getUniqueId());
		}
	}
	
	public void disable(){
		for (UUID u : workingInName){
			Player player = Bukkit.getPlayer(u);
			player.closeInventory();
		}
		
		for (UUID u : workingInReName){
			Player player = Bukkit.getPlayer(u);
			player.closeInventory();
		}
	}
}
