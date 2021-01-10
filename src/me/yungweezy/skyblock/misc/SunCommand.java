package me.yungweezy.skyblock.misc;

import java.util.ArrayList;
import java.util.UUID;

import me.yungweezy.skyblock.main.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

public class SunCommand implements Listener {

	private int votes = 0;
	private ArrayList<UUID> voted = new ArrayList<UUID>();
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event){
		if (!event.getMessage().toLowerCase().startsWith("/sun")){
			return;
		}
		
		event.setCancelled(true);
		
		World world = event.getPlayer().getLocation().getWorld();
		Player player = event.getPlayer();
		
		if (!world.hasStorm() && !world.isThundering()){
			player.sendMessage(ChatColor.RED + "It is not raining right now!");
			return;
		}
		
		if (voted.contains(player.getUniqueId())){
			player.sendMessage(ChatColor.RED + "You already voted!");
			return;
		}
		
		if (votes == 0 || voted.isEmpty()){
			clearScheduler(world.getThunderDuration());
		}
		
		votes = votes + 1;
		voted.add(player.getUniqueId());
		
		int needed = Bukkit.getOnlinePlayers().size() / 3;
		
		if (votes >= needed){
			world.setThundering(false);
			world.setStorm(false);
			votes = 0;
			voted.clear();
			Bukkit.broadcastMessage(Main.guiMain.menustart + ChatColor.GOLD + " Vote succesful! Removing rain!");
			return;
		}
		
		Bukkit.broadcastMessage(Main.guiMain.menustart + " " + ChatColor.YELLOW + player.getName() + " voted to clear the weather " + ChatColor.RED + "[" + ChatColor.GREEN + votes + ChatColor.BLUE + "/" + needed + ChatColor.RED + "]");
		Bukkit.broadcastMessage(Main.guiMain.menustart + ChatColor.GRAY + " " + ChatColor.BOLD + " Use /sun to vote!");
		return;
	}
	
	public void clearScheduler(int delay){
		Plugin pl = Main.getPlugin(Main.class);
		pl.getServer().getScheduler().scheduleSyncDelayedTask(pl, new Runnable() {
			public void run() {
				voted.clear();
				votes = 0;
			}
		}, delay);
	}
	
}
