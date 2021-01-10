package me.yungweezy.skyblock.managers;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.yungweezy.skyblock.main.FileAccessor;
import me.yungweezy.skyblock.main.Main;
import me.yungweezy.skyblock.misc.IslandLevels;
import me.yungweezy.skyblock.misc.Messages;
import me.yungweezy.skyblock.misc.WarpStuff;
import me.yungweezy.skyblock.worldgen.SkyWorldCreator;

public class CommandHandler {

	@SuppressWarnings("deprecation")
	public static void passCommand(CommandSender sender, String[] args){
		if (args.length == 1 && args[0].equalsIgnoreCase("debug")){
			if (!sender.hasPermission("skyblock.debugmode")){
				sender.sendMessage("Not for you!");
				return;
			}
			
			if (Main.debugMode == true){
				sender.sendMessage(ChatColor.GREEN + "Debug mode toggled off!");
				Main.debugMode = false;
			} else {
				sender.sendMessage(ChatColor.GREEN + "Debug mode toggled on! Turn off again with /is debug");
				Main.debugMode = true;
			}
			
			sender.sendMessage(ChatColor.GOLD + "debugMode: " + Main.debugMode);
			
			return;
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("cancelrender")){
			if (!sender.hasPermission("skyblock.cancelrender")){
				sender.sendMessage("Not for you!");
				return;
			}
			
			sender.sendMessage(ChatColor.GREEN + "Canceling the renderer");
			Main.pl.getServer().getScheduler().cancelTask(SkyWorldCreator.taskID);
			return;
		}
		
		if (!(sender instanceof Player)){ // /is giveticket <player> <amount>
			if (args.length != 3){
				sender.sendMessage(ChatColor.RED + "/is giveticket <player> <amount>");
				return;
			}
			
			Player target = Bukkit.getPlayer(args[1]);
			int amount = 0;
			
			try {
				amount = Integer.parseInt(args[2]);
			} catch (Exception e){
				sender.sendMessage(ChatColor.RED + "/is giveticket <player> <amount>");
				return;
			}
			
			ItemStack giveTags = WarpStuff.warpTag.clone();
			giveTags.setAmount(amount);
			
			target.getInventory().addItem(giveTags);
			sender.sendMessage(ChatColor.GREEN + "Gave " + target.getName() + " " + amount + " tickets!");
			
			
			return;
		}
		
		Player player = (Player) sender;
		
		if (args.length == 0){
			Inventory menu = Main.guiMain.getMainMenu(player);
			player.openInventory(menu);
			return;
		} else if (args.length == 1){ 
			if (args[0].equalsIgnoreCase("top")){
				for (int nr = 1; nr <= 10; nr = nr + 1){
					if (Top.skyblocks.get(nr) != null){
						String ID = Top.skyblocks.get(nr);
						SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
						
						String members = "";
						for (UUID u : block.getAllMembers()){
							String name;
							if (Bukkit.getPlayer(u) != null){
								name = Bukkit.getPlayer(u).getName();
							} else {
								name = Bukkit.getOfflinePlayer(u).getName();
							}
							
							if (members.length() < 2){
								members = name;
							} else {
								members = members + ", " + name;
							}
						}
						
						String leader;
						
						if (Bukkit.getPlayer(block.getOwner()) != null){
							leader = Bukkit.getPlayer(block.getOwner()).getName();
						} else {
							leader = Bukkit.getOfflinePlayer(block.getOwner()).getName();
						}
						
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', nr + ". " + block.getName() + "&b - " + leader + " - " + members + " - " + block.getLevel() / IslandLevels.lvsperLevel));
					}
				} 
			} else if (args[0].equalsIgnoreCase("setwarp")){
				if (!player.getLocation().getWorld().getName().equals("skyworld")){
					player.sendMessage(ChatColor.RED + "You can only use this command on your island!");
					return;
				}
				
				Location center = SBUtils.getCorrespondingCentLoc(player.getLocation());
				if (Main.blockManager.skyBlocksByCenter.get(center) == null){
					player.sendMessage(ChatColor.RED + "You can only use this command on your island!");
					return;
				}
				
				String ID = Main.blockManager.skyBlocksByCenter.get(center);
				SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
				
				if (!block.getOwner().equals(player.getUniqueId())){
					player.sendMessage(ChatColor.RED + "You can only use this command on your own island!");
					return;
				}
				
				if (block.getWarp() == null){
					player.sendMessage(ChatColor.RED + "You must first use a warp-ticket before you are able to use this command!");
					return;
				}
				
				block.setWarp(player.getLocation());
				Main.blockManager.skyBlocksByID.put(ID, block);
				Main.blockManager.saveSkyBlock(ID, block);
				player.sendMessage(ChatColor.GREEN + "Changed warp location!");
			} else if (args[0].equalsIgnoreCase("info")){
				if (player.hasPermission("skyblock.inspect")){
					Location loc = player.getLocation();
					if (!loc.getWorld().getName().equals("skyworld")){
						player.sendMessage(ChatColor.RED + "Y u so dumb?");
						return;
					}
					
					Location translated = SBUtils.getCorrespondingCentLoc(loc);
					String ID = Main.blockManager.skyBlocksByCenter.get(translated);
					SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
					
					player.sendMessage(ChatColor.GREEN + "SB info: ");
					player.sendMessage(ChatColor.GREEN + "Coords: " + translated.getX() + " " + translated.getY() + " " + translated.getZ());
					player.sendMessage(ChatColor.GREEN + "Level: " + block.getLevel() / IslandLevels.lvsperLevel);
					
					String members = "";
					for (UUID u : block.getAllMembers()){
						String name;
						if (Bukkit.getPlayer(u) != null){
							name = Bukkit.getPlayer(u).getName();
						} else {
							name = Bukkit.getOfflinePlayer(u).getName();
						}
						
						if (members.length() < 2){
							members = name;
						} else {
							members = members + ", " + name;
						}
					}
					
					String leader;
					
					if (Bukkit.getPlayer(block.getOwner()) != null){
						leader = Bukkit.getPlayer(block.getOwner()).getName();
					} else {
						leader = Bukkit.getOfflinePlayer(block.getOwner()).getName();
					}
					
					player.sendMessage(ChatColor.GREEN + "Owner: " + leader);
					player.sendMessage(ChatColor.GREEN + "Members: " + members);
				} else {
					player.sendMessage(Messages.getMessage("cmd_usage"));
				}
			} else {
				player.sendMessage(Messages.getMessage("cmd_usage"));
			}
		} else if (args.length == 2){
			if (args[0].equalsIgnoreCase("w") || args[0].equalsIgnoreCase("warp")){
				if (Bukkit.getPlayer(args[1]) != null){
					Player target = Bukkit.getPlayer(args[1]);
					Inventory inv = WarpStuff.getWarpInv(target);
					
					if (player.hasPermission("skyblock.forcewarp")){
						inv = WarpStuff.getOPWarpInv(target);
					}
		
					player.openInventory(inv);
				} else if (Bukkit.getOfflinePlayer(args[1]) != null){
					OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
					Inventory inv = WarpStuff.getWarpInv(target);
					
					if (player.hasPermission("skyblock.forcewarp")){
						inv = WarpStuff.getOPWarpInv(target);
					}
					
					player.openInventory(inv);
				} else {
					player.sendMessage(Messages.getMessage("cmd_usage"));
				}
			} else if (args[0].equalsIgnoreCase("head") || args[0].equalsIgnoreCase("top")){
				if (player.hasPermission("skyblock.createhad")){
					Block lookingAt = player.getTargetBlock((HashSet<Byte>) null, 100);
					
					if (lookingAt.getType() != Material.SKULL && lookingAt.getType() != Material.SIGN_POST && lookingAt.getType() != Material.WALL_SIGN){
						player.sendMessage(ChatColor.RED + "Place a skull/sign and look at it, you goof!");
						return;
					}
					
					int nr = 1;
					try {
						nr = Integer.parseInt(args[1]);	
					} catch (Exception e){
						player.sendMessage(ChatColor.RED + "/is head <nr>");
						return;
					}
					
					player.sendMessage(ChatColor.GREEN + "configuring head/sign!");
					
					FileAccessor.reloadFileF("heads");
					FileConfiguration file = FileAccessor.getFileF("heads");
					file.set(SBUtils.locToAbsoluteString(lookingAt.getLocation()), nr);
					FileAccessor.saveFile("heads");
				} else {
					player.sendMessage(Messages.getMessage("cmd_usage"));
				}
			} else {
				player.sendMessage(Messages.getMessage("cmd_usage"));
			}
		} else {
			player.sendMessage(Messages.getMessage("cmd_usage"));
		}
	}
}
