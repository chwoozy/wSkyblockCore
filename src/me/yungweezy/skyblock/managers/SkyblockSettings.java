package me.yungweezy.skyblock.managers;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import me.yungweezy.skyblock.main.Main;
import me.yungweezy.skyblock.misc.IslandLevels;
import me.yungweezy.skyblock.misc.Messages;

@SuppressWarnings("deprecation")
public class SkyblockSettings implements Listener {

	@EventHandler
	public void onPvP(EntityDamageByEntityEvent event){
		if (!(event.getEntity() instanceof Player)){
			return;
		}
		
		if (!(event.getDamager() instanceof Player)){
			if (event.getDamager() instanceof Projectile){
				if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)){
					return;
				}
			} else {
				return;
			}
		}
		
		Player player = (Player) event.getEntity();
		Player damager;
		if (event.getDamager() instanceof Player){
			damager = (Player) event.getDamager();
		} else {
			damager = (Player) ((Projectile) event.getDamager()).getShooter();
		}
		
		Location loc = player.getLocation();
		Location translated = SBUtils.getCorrespondingCentLoc(loc);
		
		if (Main.blockManager.skyBlocksByCenter.get(translated) == null){
			return;
		}
		
		String ID = Main.blockManager.skyBlocksByCenter.get(translated);
		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
		if (!block.pvpEnabled()){
			event.setCancelled(true);
			damager.sendMessage(Messages.getMessage("cant_pvp"));
		}
		
		if (!block.isMember(player) || !block.isMember(damager)){
			event.setCancelled(true);
			damager.sendMessage(Messages.getMessage("cant_pvp"));
		}
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent event){
		Location loc = event.getEntity().getLocation();
		Location translated = SBUtils.getCorrespondingCentLoc(loc);
		if (Main.blockManager.skyBlocksByCenter.get(translated) == null){
			return;
		}
		
		String ID = Main.blockManager.skyBlocksByCenter.get(translated);
		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
		if (block.doExplode()){
			return;
		}
		
		event.setCancelled(true);
		loc.getWorld().createExplosion(loc, 0);
	}
	
	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event){
		Location loc = event.getBlock().getLocation();
		Location translated = SBUtils.getCorrespondingCentLoc(loc);
		if (Main.blockManager.skyBlocksByCenter.get(translated) == null){
			return;
		}
		
		double level = IslandLevels.getLevel(new ItemStack(event.getBlock().getType(), event.getBlock().getData()));
		
		if (level == 0){
			return;
		}
		
		String ID = Main.blockManager.skyBlocksByCenter.get(translated);
		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
		
		if (ID == null){
			return;
		}
		
		block.removeFromLevel(level);
		Main.blockManager.skyBlocksByID.put(ID, block);
		Main.blockManager.saveSkyBlock(ID, block);
		return;
		
	}
	
	@EventHandler
	public void onMobSpawn(EntitySpawnEvent event){
		ArrayList<EntityType> workfor = new ArrayList<EntityType>();
		workfor.add(EntityType.BAT);
		workfor.add(EntityType.CAVE_SPIDER);
		workfor.add(EntityType.CREEPER);
		workfor.add(EntityType.ENDERMAN);
		workfor.add(EntityType.GHAST);
		workfor.add(EntityType.MAGMA_CUBE);
		workfor.add(EntityType.PIG_ZOMBIE);
		workfor.add(EntityType.SILVERFISH);
		workfor.add(EntityType.SKELETON);
		workfor.add(EntityType.SLIME);
		workfor.add(EntityType.SPIDER);
		workfor.add(EntityType.WITCH);
		workfor.add(EntityType.WOLF);
		workfor.add(EntityType.ZOMBIE);
		workfor.add(EntityType.WITHER);
		if (!workfor.contains(event.getEntityType())){
			return;
		}
		
		Location centerloc = SBUtils.getCorrespondingCentLoc(event.getLocation());
		if (Main.blockManager.skyBlocksByCenter.get(centerloc) == null){
			return;
		}
		
		String ID = Main.blockManager.skyBlocksByCenter.get(centerloc);
		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
		
		if (block == null){
			return;
		}
		
		if (block.spawnMobs()){
			return;
		}
		
		event.getEntity().remove();
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event){
		if (!event.getTo().getWorld().getName().equals("skyworld")){
			return;
		}
		
		Location loc = event.getTo();
		Location translated = SBUtils.getCorrespondingCentLoc(loc);
		if (Main.blockManager.skyBlocksByCenter.get(translated) == null){
			return;
		}
		
		String ID = Main.blockManager.skyBlocksByCenter.get(translated);
		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
		if (block.isLocked() == false || event.getPlayer().hasPermission("skyblock.protection.bypass") || event.getPlayer().hasPermission("skyblock.entry.bypass")){
			if (!loc.getWorld().getName().equals(event.getFrom().getWorld().getName()) || !translated.equals(SBUtils.getCorrespondingCentLoc(event.getFrom()))){
				fixLoc(loc, event.getPlayer());
				String msg = Messages.getMessage("island_entry");
				msg = msg.replace("<island>", block.getName());
				event.getPlayer().sendMessage(msg);
			}
			
			return;
		}
		
		if (!block.getAllMembers().contains(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("skyblock.protection.bypass") && !event.getPlayer().hasPermission("skyblock.entry.bypass")){
			event.setCancelled(true);
			
			event.getPlayer().sendMessage(Messages.getMessage("cant_enter_area"));
			
			if (event.getTo().getWorld().equals(event.getFrom().getWorld()) && event.getTo().distance(event.getFrom()) < 1){
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + event.getPlayer().getName());
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + event.getPlayer().getName());
				event.getPlayer().sendMessage(ChatColor.RED + "Teleported to spawn for safety!");
			}
			
			return;
		}
		
		if (block.getAllMembers().contains(event.getPlayer().getUniqueId())){
			if (!loc.getWorld().getName().equals(event.getFrom().getWorld().getName()) || !translated.equals(SBUtils.getCorrespondingCentLoc(event.getFrom()))){
				fixLoc(loc, event.getPlayer());
				String msg = Messages.getMessage("island_entry");
				msg = msg.replace("<island>", block.getName());
				event.getPlayer().sendMessage(msg);
			}
			return;
		}
	}
	
	public static void fixLoc(Location loc, Player player){
		if (loc.getWorld().getHighestBlockAt(loc.getBlockX(), loc.getBlockZ()).getY() != 0){
			return;
		}
		
		Location edit = loc.clone();
		edit.setY(edit.getY() - 1);
		edit.getBlock().setType(Material.COBBLESTONE);
	}
	
	public Location getSafeLoc(Location loc){
		if (loc.getWorld().getHighestBlockAt(loc.getBlockX(), loc.getBlockZ()).getY() == 0){
			Location startSearch = loc.clone();
			startSearch.setX(startSearch.getX() + 15);
			startSearch.setZ(startSearch.getZ() + 15);
			
			ArrayList<Location> possible = new ArrayList<Location>();
			
			for (int i = 0; i < 30; i = i + 1){
				startSearch.setX(startSearch.getX() - 1);
				
				if (loc.getWorld().getHighestBlockAt(startSearch.getBlockX(), startSearch.getBlockZ()).getY() != 0){
					possible.add(startSearch.clone());
				}
				
				for (int j = 0; j < 30; j = j + 1){
					startSearch.setZ(startSearch.getZ() - 1);
					
					if (loc.getWorld().getHighestBlockAt(startSearch.getBlockX(), startSearch.getBlockZ()).getY() != 0){
						possible.add(startSearch.clone());
					}
					
					if (j >= 29){
						startSearch.setZ(loc.getZ() + 15);
					}
				}
			}
			
			double closestdis = 10000;
			Location closest = loc;
			
			for (Location pos : possible){
				if (pos.distance(loc) < closestdis){
					closest = pos;
					closestdis = pos.distance(loc);
				}
			}
			
			if (closest.distance(loc) < 0.5){
				loc.setY(loc.getY() - 1);
				loc.getBlock().setType(Material.COBBLESTONE);
			}
			
			closest.setY(loc.getWorld().getHighestBlockYAt(closest) + 2);
			
			return closest;
		} 
		
		return null;
	}
	
	@EventHandler
	public void onTeleportAway(PlayerTeleportEvent event){
		if (!event.getFrom().getWorld().getName().equals("skyworld")){
			return;
		}
		
		Location loc = event.getFrom();
		Location loc2 = event.getTo();
		Location translated2 = SBUtils.getCorrespondingCentLoc(loc2);
		Location translated = SBUtils.getCorrespondingCentLoc(loc);
		if (Main.blockManager.skyBlocksByCenter.get(translated) == null){
			return;
		}
		
		String ID = Main.blockManager.skyBlocksByCenter.get(translated);
		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
		
		if (!loc.getWorld().getName().equals("skyworld") || (loc2.getWorld().getName().equals("skyworld") && translated2.distance(translated) < 10)){
			return;
		}
		
		String msg = Messages.getMessage("island_leave");
		msg = msg.replace("<island>", block.getName());
		event.getPlayer().sendMessage(msg);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event){		
		if (!event.getTo().getWorld().getName().equals("skyworld")){
			return;
		}
		
		Location loc = event.getTo();
		Location translated = SBUtils.getCorrespondingCentLoc(loc);
		if (Main.blockManager.skyBlocksByCenter.get(translated) == null){
			return;
		}
		
		String ID = Main.blockManager.skyBlocksByCenter.get(translated);
		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
		if (block.isLocked() == false){
			entryMessage(event.getPlayer(), event.getFrom(), event.getTo(), translated);
			return;
		}
		
		if (block.getAllMembers().contains(event.getPlayer().getUniqueId())){
			entryMessage(event.getPlayer(), event.getFrom(), event.getTo(), translated);
			return;
		}
		
		if (event.getPlayer().hasPermission("skyblock.protection.bypass") || event.getPlayer().hasPermission("skyblock.entry.bypass")){
			return;
		}
		
		event.setCancelled(true);
		
		event.getPlayer().sendMessage(Messages.getMessage("cant_enter_area_teleport"));
		
		if (loc.distance(translated) < 1){
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + event.getPlayer().getName());
			Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "spawn " + event.getPlayer().getName());
			event.getPlayer().sendMessage(ChatColor.RED + "Teleported to spawn for safety!");
		}
	}
	
	public void entryMessage(Player player, Location from, Location to, Location cent){
		if (from.distance(to) < 0.1){
			return;
		}
		
		if (to.distance(cent) > 400){
			return;
		}
		
		if (to.distance(cent) < 399.7){
			return;
		}
		
		if (to.getX() - cent.getX() < 0){
			// greater x = entry
			if (to.getX() < from.getX()){
				if (to.getZ() - cent.getZ() < 0){
					if (to.getZ() < from.getZ()){
						leaveMessage(player, from, to, cent);
						return; 
					}
				} else {
					if (to.getZ() > from.getZ()){
						leaveMessage(player, from, to, cent);
						return; 
					}
				}
			}
		} else {
			// smaller x = entry
			if (to.getX() > from.getX()){
				if (to.getZ() - cent.getZ() < 0){
					if (to.getZ() < from.getZ()){
						leaveMessage(player, from, to, cent);
						return; 
					}
				} else {
					if (to.getZ() > from.getZ()){
						leaveMessage(player, from, to, cent);
						return; 
					}
				}
			}
		}
		
		String msg = Messages.getMessage("island_entry");
		SkyBlock block = Main.blockManager.skyBlocksByID.get(Main.blockManager.skyBlocksByCenter.get(cent));
		msg = msg.replace("<island>", block.getName());
		player.sendMessage(msg);
		
		if (!block.getSpecialJoin().equals("")){
			player.sendMessage(block.getSpecialJoin());
		}
	}
	
	public void leaveMessage(Player player, Location from, Location to, Location cent){
		SkyBlock block = Main.blockManager.skyBlocksByID.get(Main.blockManager.skyBlocksByCenter.get(cent));
		String msg = Messages.getMessage("island_leave");
		msg = msg.replace("<island>", block.getName());
		player.sendMessage(msg);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onChat(PlayerChatEvent event){
		String format = event.getFormat();
		
		if (SkyplayerManager.SkyPlayers.get(event.getPlayer().getUniqueId()) == null){
			format = format.replace("{ilvl}", "0");
			event.setFormat(format);
			return;
		}
		
		SkyPlayer p = SkyplayerManager.SkyPlayers.get(event.getPlayer().getUniqueId());
		
		if (p.getMain() == null){
			format = format.replace("{ilvl}", "0");
			event.setFormat(format);
			return;
		}
		
		String mainid = p.getMain();
		SkyBlock main = Main.blockManager.skyBlocksByID.get(mainid);
		
		int lvl = (int) (main.getLevel() / IslandLevels.lvsperLevel);
		
		if (lvl < 0){
			lvl = 0;
		}
		
		format = format.replace("{ilvl}",  lvl + "");
		
		event.setFormat(format);
	}
	
}
