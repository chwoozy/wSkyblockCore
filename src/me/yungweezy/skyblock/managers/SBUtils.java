package me.yungweezy.skyblock.managers;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.Skull;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.yungweezy.skyblock.main.CreateItem;
import me.yungweezy.skyblock.main.Main;
import me.yungweezy.skyblock.misc.IslandLevels;
import net.md_5.bungee.api.ChatColor;

public class SBUtils {

	public static ItemStack getInfoItem(SkyBlock block){
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Members: " + block.totalMembers() + "/" + block.getMaxMembers());
		lore.add(ChatColor.GRAY + "Type: " + block.getType().toString());
		lore.add(ChatColor.GRAY + "Level: " + block.getLevel() / IslandLevels.lvsperLevel);
		//lore.add(ChatColor.GRAY + "" + block.getID());
		ItemStack stack = CreateItem.createItem(block.getLogo(), block.getSublogo(), ChatColor.GOLD + "" + ChatColor.BOLD + block.getName(), lore);
		return stack;
	}
	
	public static Vector calculateVelocityPush(Entity from, Entity away){
		return away.getLocation().toVector().subtract(from.getLocation().toVector()).normalize().multiply(0.5);
	}
	
	public static String locToString(Location loc){
		int x = loc.getBlockX() - 500;
		int z = loc.getBlockZ() - 500;
		
		int xnr = x / 1000;
		int znr = z / 1000;
		String locstring = xnr + ":" + znr;
		return locstring;
	}
	
	public static Location stringToLoc(String string){
		String[] split = string.split(":");
		int xnr = Integer.parseInt(split[0]);
		int znr = Integer.parseInt(split[1]);
		
		int x = (xnr * 1000) + 500;
		int z = (znr * 1000) + 500;
		int y = 120;
		World world = Bukkit.getWorld("skyworld");
		
		Location loc = new Location(world, x, y, z);
		return loc;
	}
	
	public static String locToRawString(Location loc){
		int x = loc.getBlockX();
		int z = loc.getBlockZ();
		
		String locstring = x + ":" + z + ":" + loc.getYaw() + ":" + loc.getPitch() + ":" + loc.getY();
		return locstring;
	}
	
	public static Location stringToRawLoc(String string){
		String[] split = string.split(":");
		double xnr = Double.parseDouble(split[0]);
		double znr = Double.parseDouble(split[1]);
		
		int y = 120;
		World world = Bukkit.getWorld("skyworld");
		
		Location loc = new Location(world, xnr, y, znr);
		try {
			loc.setYaw((float) Double.parseDouble(split[2]));
			loc.setPitch((float) Double.parseDouble(split[3]));
			loc.setY(Double.parseDouble(split[4]));
		} catch (Exception e){
			
		}
		return loc;
	}
	
	public static Location stringToAbsoluteLoc(String string){
		string = string.replace(",", ".");
		String[] split = string.split("-");
		double xnr = Double.parseDouble(split[0].replace("q", "-"));
		double ynr = Double.parseDouble(split[1]);
		double znr = Double.parseDouble(split[2].replace("q", "-"));

		World world = Bukkit.getWorld("skyworld");
		
		if (split.length >= 6){
			world = Bukkit.getWorld(split[5]);
		}
		
		Location loc = new Location(world, xnr, ynr, znr);
		try {
			loc.setYaw((float) Double.parseDouble(split[3]));
			loc.setPitch((float) Double.parseDouble(split[4]));
		} catch (Exception e){
			
		}
		
		return loc;
	}
	
	public static String locToAbsoluteString(Location loc){
		double x = loc.getX();
		double y = loc.getY();
		double z = loc.getZ();
		
		String tx = x + "";
		String tz = z + "";
		tx = tx.replace("-", "q");
		tz = tz.replace("-", "q");
		
		try {
			String locstring = tx + "-" + y + "-" + tz + "-" + loc.getYaw() + "-" + loc.getPitch() + "-" + loc.getWorld().getName();
			locstring = locstring.replace(".", ",");
			return locstring;
		} catch (Exception e){
			return "q1-100-q1-1-1-SKYWORLD";
		}
	}
	
	public static Location getUnusedLocation(){
		Location starter = new Location(Bukkit.getWorld("skyworld"), 500, 120, 500);
		
		Location workingon = starter.clone();
		
		boolean good = false;
		while (good == false){
			workingon.setX(workingon.getX() + 1000);
			
			if (Main.blockManager.skyBlocksByCenter.get(workingon) == null && !Main.blockManager.oldLocs.contains(workingon) && !Main.blockManager.transOldLocs.contains(SBUtils.locToString(workingon))){
				good = true;
				return workingon;
			} else {
				for (int row = 0; row <= 10; row = row + 1){
					workingon.setZ(workingon.getZ() + 1000);
					if (Main.blockManager.skyBlocksByCenter.get(workingon) == null && !Main.blockManager.oldLocs.contains(workingon) && !Main.blockManager.transOldLocs.contains(SBUtils.locToString(workingon))){
						good = true;
						return workingon;
					}
				}
				workingon.setZ(500);
			}
		}
		
		return null;
	}
	
	public static Location getUnusedLocation(int skip){
		Location starter = new Location(Bukkit.getWorld("skyworld"), 500, 120, 500);
		
		Location workingon = starter.clone();
		int toSkip = skip;
		
		boolean good = false;
		while (good == false){
			workingon.setX(workingon.getX() + 1000);
			
			if (Main.blockManager.skyBlocksByCenter.get(workingon) == null && !Main.blockManager.oldLocs.contains(workingon) && !Main.blockManager.transOldLocs.contains(SBUtils.locToString(workingon))){
				if (toSkip <= 0){
					good = true;
					return workingon;
				} 
				
				toSkip = toSkip - 1;
			} else {
				for (int row = 0; row <= 10; row = row + 1){
					workingon.setZ(workingon.getZ() + 1000);
					if (Main.blockManager.skyBlocksByCenter.get(workingon) == null && !Main.blockManager.oldLocs.contains(workingon) && !Main.blockManager.transOldLocs.contains(SBUtils.locToString(workingon))){
						if (toSkip <= 0){
							good = true;
							return workingon;
						}
						
						toSkip = toSkip - 1;
					}
				}
				workingon.setZ(500);
			}
		}
		
		return null;
	}
	
	public static String createRandomID(){
		String possibleletters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890lsajfaldjfapiowur98731hl23";
		Random rand = new Random();
		
		boolean good = false;
		
		while (good == false){
			String id = "";
			for (int place = 0; place <= 25; place = place + 1){
				if (place == 6 || place == 15 || place == 20){
					id = id + "-";
				} else {
					int randomnr = rand.nextInt(possibleletters.length());
					String r = possibleletters.substring(randomnr, randomnr + 1);
					id = id + r;
				}
			}
			
			if (Main.blockManager.skyBlocksByID.get(id) == null){
				good = true;
				return id;
			}
		}
		
		return null;
	}
	
	public static Location getCorrespondingCentLoc(Location loc){
		int x = loc.getBlockX();
		int z = loc.getBlockZ();
		
		int cx = x - 500;
		int cz = z - 500;
		
		cx = cx / 1000;
		cz = cz / 1000;
		
		cx = (cx * 1000) + 500;
		cz = (cz * 1000) + 500;
		
		if (x - cx > 500){
			cx = cx + 1000;
		} else if (x - cx < -500){
			cx = cx - 1000;
		}
		
		if (z - cz > 500){
			cz = cz + 1000;
		} else if (z - cz < -500){
			cz = cz - 1000;
		}
		
		Location translatedtocenter = new Location(loc.getWorld(), cx, 120, cz);
		return translatedtocenter;
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack getHead(String pname){
		ItemStack head = null;
		
		Location loc = new Location(Bukkit.getWorld("skyworld"), -1001, 100, -1001);
		loc.getBlock().setType(Material.SKULL);
		Skull skull = (Skull) loc.getBlock().getState();
		skull.setSkullType(SkullType.PLAYER);
		skull.update();
		skull.setOwner(pname);
		skull.update();
		skull.update(true);
		
		head = (ItemStack) loc.getBlock().getDrops().toArray()[0];
		head = CreateItem.setName(head, ChatColor.GREEN + pname);
		
		return head;
	}
	
	public static int getMaxMemberPerm(OfflinePlayer player){
		int highest = 2;
		
		if (!player.isOnline()){
			return 4;
		}
		
		for (int i = 2; i <= 64; i = i + 1){
			if (((Player) player).hasPermission("skyblock.party." + i)){
				highest = i;
			}
		}
		
		return highest;
	}
	
}
