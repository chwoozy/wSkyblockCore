package me.yungweezy.skyblock.managers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.yungweezy.skyblock.main.CreateItem;
import me.yungweezy.skyblock.main.FileAccessor;
import me.yungweezy.skyblock.main.Main;
import me.yungweezy.skyblock.misc.Messages;
import me.yungweezy.skyblock.misc.PlayerMySQL;
import me.yungweezy.skyblock.misc.SkyBlockMySQL;

public class SkyblockManager {

	public HashMap<String, SkyBlock> skyBlocksByID = new HashMap<String, SkyBlock>(); // ID, SkyBlock
	public HashMap<Location, String> skyBlocksByCenter = new HashMap<Location, String>(); // Location, ID
	public ArrayList<Location> oldLocs = new ArrayList<Location>(); // used before, dont replace
	public ArrayList<String> transOldLocs = new ArrayList<String>(); // translated to the small string
	
	public ItemStack[] contents;
	public HashMap<String, SkyBlockType> nameType = new HashMap<String, SkyBlockType>();
	
	public int workingNow = 0;
	
	public ArrayList<String> shouldGetSaved = new ArrayList<String>();
	
	public void startCreation(final Player player){
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl, new Runnable() {
			@Override
			public void run() {
				Main.nameModule.openNameInv(player);
			}
		}, 2);
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void loadAll(){
		System.out.println("[SB] Starting to load all from MySQL");
		Connection con = SkyBlockMySQL.getConnection();
		Statement state = null;
		
		try {
			state = con.createStatement();
		} catch (Exception e){
			e.printStackTrace();
			return;
		}
		
		ResultSet result = null;
		
		try {
			result = state.executeQuery("SELECT * FROM " + SkyBlockMySQL.table);
		} catch (Exception e){
			e.printStackTrace();
			return;
		}
		
		try {
			while (result.next()){
				try {
					SkyBlock block = new SkyBlock();
					String ID = result.getString("ID");

					block.setID(ID);
					block.setOwner(UUID.fromString(result.getString("OWNER").replace("|", "-")));
					block.setName(result.getString("NAME"));

					Location center = SBUtils.stringToLoc(result.getString("CENTER").replace("Q", ":"));
					block.setCenter(center);

					ArrayList<UUID> members = new ArrayList<UUID>();
					for (String s : result.getString("MEMBERS").split("!")){
						String raw = s + "";
						raw = raw.replace("|", "-");
						members.add(UUID.fromString(raw));
					}
					block.setAllMembers(members);

					block.setType(SkyBlockType.valueOf(result.getString("TYPE")));
					block.setLogo(Material.valueOf(result.getString("LOGO")));
					block.setSublogo(result.getInt("SUBLOGO"));
					block.level = result.getDouble("LEVEL");
					block.setLock(result.getInt("LOCKED") == 1);
					block.setExplosions(result.getInt("EXPLOSIONS") == 1);
					block.setPvp(result.getInt("PVP") == 1);
					block.setMobSpawning(result.getInt("MOBSPAWNING") == 1);

					block.setMaxMembers(result.getInt("MAXMEMBERS"));

					String warpRaw = result.getString("WARP");
					if (!warpRaw.equalsIgnoreCase("NULL")){
						block.setWarp(SBUtils.stringToRawLoc(warpRaw));
					}

					String homeRaw = result.getString("HOME");
					if (!homeRaw.equalsIgnoreCase("NULL")){
						block.setHome(SBUtils.stringToRawLoc(homeRaw));
					}

					skyBlocksByCenter.put(center, ID);
					skyBlocksByID.put(ID, block);
				} catch (Exception e){
					System.out.println("Loading of skyblock " + result.getString("ID") + " went wrong!");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (skyBlocksByCenter.size() <= 1){
			System.out.println("[SB] None found, converting server!");
			convertAll();
			loadAll();
			saveAllSkyBlocksThatShould();
		}
		
		FileConfiguration f = FileAccessor.getFileF("oldSkyblockLocs");
		
		if (f.get("OldLocs") != null){
			for (String s : (ArrayList<String>) f.get("OldLocs")){
				Location translated = SBUtils.stringToLoc(s);
				if (!oldLocs.contains(translated)){
					oldLocs.add(translated);
				}
				transOldLocs.add(s);
			}
		}
		
		FileAccessor.reloadFileF("chestcontents");
		FileConfiguration c = FileAccessor.getFileF("chestcontents");
		Inventory dummy = Bukkit.createInventory(null, 27);
		for (String s : c.getConfigurationSection("").getKeys(false)){
			int slot = Integer.parseInt(s);
			String raw = c.getString(s + ".item");
			String[] split = raw.split(",");

			int amount = c.getInt(s + ".amount");
			
			int id = 0;
			int sub = 0;
			
			id = Integer.parseInt(split[0]);
			
			if (split.length >= 2){
				sub = Integer.parseInt(split[1]);
			}
			
			ItemStack stack = CreateItem.createItem(Material.getMaterial(id), sub, "", new ArrayList<String>());
			stack.setAmount(amount);
			dummy.setItem(slot, stack);
		}
		
		contents = dummy.getContents();
		
		System.out.println("[SB] Done loading in skyblockmanager, loaded a total of " + skyBlocksByID.size() + " skyblocks!");
	}
	
	public void saveSkyBlock(String ID, SkyBlock block){
		System.out.println("[SBMysql] Starting to push skyblock data for skyblock " + ID + " " + System.currentTimeMillis());
		
		Connection con = SkyBlockMySQL.getConnection();

		String saveQuery = "insert into " + SkyBlockMySQL.table + " (ID, OWNER, NAME, CENTER, MEMBERS, TYPE, LOGO, SUBLOGO, LEVEL, LOCKED, EXPLOSIONS, PVP, MOBSPAWNING, MAXMEMBERS, WARP, HOME) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			PreparedStatement checkstate = con.prepareStatement("SELECT * FROM " + SkyBlockMySQL.table + " where ID=" + "'" + ID + "'");
			ResultSet result = checkstate.executeQuery();
			
			if (result.next()){
				saveQuery = saveQuery.replace("insert", "replace");
			}
		} catch (Exception e){
			System.out.println("[SBMysql] Something went wrong while checking if having to update or insert");
			e.printStackTrace();
			return;
		}
		
		try {
			PreparedStatement saveState = PlayerMySQL.getConnection().prepareStatement(saveQuery);
			saveState.setString(1, ID);
			saveState.setString(2, block.getOwner().toString().replace("-", "|"));
			saveState.setString(3, block.getName());
			saveState.setString(4, SBUtils.locToString(block.getCenter()).replace(":", "Q"));
			
			String memberString = "";
			for (UUID u : block.getAllMembers()){
				memberString = memberString + u.toString().replace("-", "|") + "!";
			}
			if (memberString.endsWith("!")){
				memberString = memberString.substring(0, memberString.length() - 1);
			}
			
			saveState.setString(5, memberString);
			saveState.setString(6, block.getType().toString());
			saveState.setString(7, block.getLogo().toString());
			saveState.setInt(8, block.getSublogo());
			saveState.setDouble(9, block.level);
			saveState.setInt(10, toBit(block.isLocked()));
			saveState.setInt(11, toBit(block.explosions));
			saveState.setInt(12, toBit(block.PvP));
			saveState.setInt(13, toBit(block.mobSpawning));
			saveState.setInt(14, block.getMaxMembers());
			
			if (block.getWarp() != null){
				saveState.setString(15, SBUtils.locToRawString(block.getWarp()));
			} else {
				saveState.setString(15, "NULL");
			}
			
			if (block.getHome() != null && block.getHome().getWorld() == block.getCenter().getWorld() && block.getHome().distanceSquared(block.getCenter()) > 0.01){
				saveState.setString(16, SBUtils.locToRawString(block.getHome()));
			} else {
				saveState.setString(16, "NULL");
			}
			
			saveState.executeUpdate();
			System.out.println("[SBMysql] Succesfully pushed data for " + ID + " " + System.currentTimeMillis());
		} catch (SQLException e1) {
			System.out.println("[SBMysql] Error while pushing data for " + ID);
			e1.printStackTrace();
			return;
		}
	}
	
	public Integer toBit(Boolean bool){
		if (bool){
			return 1;
		}
		
		return 0;
	}
	
	public void saveAllSkyBlocksThatShould(){
		for (String ID : shouldGetSaved){
			saveSkyBlock(ID, skyBlocksByID.get(ID));
		}
		
		shouldGetSaved.clear();
		
		FileAccessor.reloadFileF("oldSkyblockLocs");
		FileConfiguration f = FileAccessor.getFileF("oldSkyblockLocs");
		
		ArrayList<String> oldlocs = new ArrayList<String>();
		for (Location loc : oldLocs){
			String trans = SBUtils.locToString(loc);
			if (!oldlocs.contains(trans)){
				oldlocs.add(trans);
			}
		}
		
		f.set("OldLocs", null);
		
		f.set("OldLocs", oldlocs);
		
		FileAccessor.saveFile("oldSkyblockLocs");
	}

	@SuppressWarnings({ "unchecked" })
	public void convertAll(){
		System.out.println("[SB] Starting conversion of old Skyblocks.yml");
		File file = new File(Main.getPlugin(Main.class).getDataFolder().getAbsolutePath() + "/skyblocks.yml");
		if (file.exists()){
			FileConfiguration c = YamlConfiguration.loadConfiguration(file);
			try {
				c.save(new File(Main.getPlugin(Main.class).getDataFolder().getAbsolutePath() + "/skyblocks" + System.currentTimeMillis() + ".yml"));
				System.out.println("[SkyBlock] Backed up the skyblocks file");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		FileAccessor.reloadFileF("skyblocks");
		FileConfiguration f = FileAccessor.getFileF("skyblocks");
		for (String sid : f.getConfigurationSection("").getKeys(false)){
			if (!sid.equals("OldLocs")){
				SkyBlock block = new SkyBlock();
				block.setID(sid);
				block.setOwner(UUID.fromString(f.getString(sid + ".owner")));

				ArrayList<String> members = (ArrayList<String>) f.get(sid + ".members");
				for (String u : members){
					UUID uuid = UUID.fromString(u);
					block.addMember(uuid);
				}

				block.setName(f.getString(sid + ".name"));

				String locstring = f.getString(sid + ".center");
				Location center = SBUtils.stringToLoc(locstring);
				block.setCenter(center);

				block.setType(SkyBlockType.valueOf(f.getString(sid + ".type")));

				Material mat = Material.valueOf(f.getString(sid + ".logo"));
				block.setLogo(mat);
				block.setSublogo(f.getInt(sid + ".sublogo"));

				block.setExplosions(f.getBoolean(sid + ".explosions"));
				block.setPvp(f.getBoolean(sid + ".pvp"));
				block.setMobSpawning(f.getBoolean(sid + ".mobspawning"));
				block.setLock(f.getBoolean(sid + ".lock"));
				block.addToLevel(f.getDouble(sid + ".level"));
				block.setMaxMembers(f.getInt(sid + ".maxMembers"));

				if (f.get(sid + ".home") != null){
					Location loc = SBUtils.stringToRawLoc(f.getString(sid + ".home"));
					block.setHome(loc);
				}
				
				if (f.get(sid + ".warp") != null){
					Location loc = SBUtils.stringToRawLoc(f.getString(sid + ".warp"));
					block.setWarp(loc);
				}

				skyBlocksByCenter.put(center, sid);
				skyBlocksByID.put(sid, block);
				
				saveSkyBlock(sid, block);
			}
		}
	}
	
	public String createSkyBlock(final Player player, final String name, final SkyBlockType type, final int slot, final Material logo, final int sublogo){
		System.out.println("Starting creation of skyblock " + System.currentTimeMillis());
		
		final String ID = SBUtils.createRandomID();
		final SkyBlock block = new SkyBlock();
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl, new Runnable() {
			@Override
			public void run() {
				block.setOwner(player);
				block.setID(ID);
				block.setName(name);
				Location loc;

				if (workingNow == 0){
					workingNow = 1;
					loc = SBUtils.getUnusedLocation();
				} else {
					workingNow = workingNow + 1;
					loc = SBUtils.getUnusedLocation(workingNow);
				}

				block.setCenter(loc);
				block.addMember(player);
				block.setType(type);
				block.setLogo(logo);
				block.setSublogo(sublogo);

				block.setExplosions(true);
				block.setMobSpawning(true);
				block.setPvp(false);
				block.setLock(false);
				block.addToLevel(1);
				block.setMaxMembers(SBUtils.getMaxMemberPerm(player));

				if (SchematicPaster.exists(type.toString())){
					skyBlocksByID.put(ID, block);
					skyBlocksByCenter.put(loc, ID);
				}

				Boolean pasted = SchematicPaster.paste(block.getCenter(), type.toString());
				if (!pasted){
					player.sendMessage(Messages.getMessage("choose_other_biome"));
					return;
				}

				skyBlocksByID.put(ID, block);
				skyBlocksByCenter.put(loc, ID);

				SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
				switch (slot){
				case 1: p.I1 = ID; break;
				case 2: p.I2 = ID; break;
				case 3: p.I3 = ID; break;
				case 4: p.I4 = ID; break;
				case 5: p.I5 = ID; break;
				case 6: p.I6 = ID; break;
				case 7: p.I7 = ID; break;
				case 8: p.I8 = ID; break;
				}

				if (slot <= 4 && p.getMain() == null){
					p.setMain(ID);
				}

				SkyplayerManager.SkyPlayers.put(player.getUniqueId(), p);

				final Biome bio = block.getType().getBiome();
				final int offset = 80;
				final Location center = block.getCenter();
				Location working = center.clone();
				working.setX(working.getBlockX() + offset);
				working.setZ(working.getBlockZ() - offset);

				System.out.println("Starting rendering chunks " + System.currentTimeMillis());

				double tickOffset = 1;
				for (int x = 0; x <= offset * 2; x = x + 16){
					for (int z = 0; z <= offset * 2; z = z + 16){
						Location tempLoc = working.clone();
						tempLoc.setX(tempLoc.getX() + x);
						tempLoc.setZ(tempLoc.getZ() + z);
						final Chunk chunk = tempLoc.getChunk();

						Main.pl.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl, new Runnable() {
							public void run() {
								chunk.load(false);

								for (int ix = 0; ix <= 16; ix = ix + 1){
									for (int iz = 0; iz <= 16; iz = iz + 1){
										Block b = chunk.getBlock(ix, 10, iz);
										b.setBiome(bio);
									}
								}
							}
						}, (int) tickOffset);

						tickOffset = tickOffset + 2;
					}
				}

				working.getWorld().save();

				Main.pl.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl, new Runnable() {
					public void run() {
						System.out.println("Last saving part " + System.currentTimeMillis());

						SkyplayerManager.saveForPlayer(player);

						Main.pl.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl, new Runnable() {
							public void run() {
								SkyplayerManager.loadForPlayer(player);
							}
						}, 1);

						Location chest = block.getCenter().clone();
						chest.setZ(chest.getZ() + 2);

						Block ches = chest.getBlock();
						ches.setType(Material.CHEST);
						ches = chest.getBlock();
						Chest state = (Chest) ches.getState();
						state.getInventory().setContents(contents);
						state.update();
						state.update(true);

						workingNow = workingNow - 1;

						Main.pl.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl, new Runnable() {
							public void run() {
								player.sendMessage(ChatColor.GREEN + "Skyblock " + name + " created!");

								player.teleport(skyBlocksByID.get(ID).getCenter());
								
								saveSkyBlock(ID, block);
							}
						}, 10);

						System.out.println("All done " + System.currentTimeMillis());
					}
				}, ((int) tickOffset) + 10);

				System.out.println("All done final message " + System.currentTimeMillis());
			}
		}, 20);
		return ID;
	}
	
	public void deleteSkyBlock(String ID){
		SkyBlock block = skyBlocksByID.get(ID);
		oldLocs.add(block.getCenter());
		transOldLocs.add(SBUtils.locToString(block.getCenter()));
		
		skyBlocksByCenter.remove(block.getCenter());
		skyBlocksByID.remove(ID);
		
		FileAccessor.reloadFileF("skyblocks");
		FileConfiguration f = FileAccessor.getFileF("skyblocks");
		f.set(ID, null);
		FileAccessor.saveFile("skyblocks");
	}
	
	@Deprecated
	public void deprecated_saveSkyBlock(String ID, SkyBlock block){
		System.out.println("[SkyBlock] saveSkyblock was called, but cancelled for performance testing!");
		/*FileAccessor.reloadFileF("skyblocks");
		FileConfiguration f = FileAccessor.getFileF("skyblocks");
		f.set(ID + ".owner", block.getOwner().toString());
		f.set(ID + ".name", block.getName());
		f.set(ID + ".center", SBUtils.locToString(block.getCenter()));
		ArrayList<String> convertedUUIDS = new ArrayList<String>();
		for (UUID u : block.getAllMembers()){
			convertedUUIDS.add(u.toString());
		}
		f.set(ID + ".members", convertedUUIDS);
		f.set(ID + ".type", block.getType().toString());
		f.set(ID + ".logo", block.getLogo().toString());
		f.set(ID + ".sublogo", block.getSublogo());
		
		f.set(ID + ".explosions", block.doExplode());
		f.set(ID + ".pvp", block.pvpEnabled());
		f.set(ID + ".mobspawning", block.spawnMobs());
		f.set(ID + ".lock", block.isLocked());
		f.set(ID + ".level", block.getLevel());
		f.set(ID + ".maxMembers", block.getMaxMembers());
		
		if (block.getHome().distance(block.getCenter()) >= 0.1){
			f.set(ID + ".home", SBUtils.locToRawString(block.getHome()));
		}
		
		if (block.getWarp() != null){
			f.set(ID + ".warp", SBUtils.locToRawString(block.getWarp()));
		}
		
		FileAccessor.saveFile("skyblocks");*/
	}
	
	@Deprecated
	public void deprecated_saveAllSkyBlocks(){
		FileAccessor.reloadFileF("skyblocks");
		FileConfiguration f = FileAccessor.getFileF("skyblocks");
		for (String ID : skyBlocksByID.keySet()){
			SkyBlock block = skyBlocksByID.get(ID);
			
			f.set(ID + ".owner", block.getOwner().toString());
			f.set(ID + ".name", block.getName());
			f.set(ID + ".center", SBUtils.locToString(block.getCenter()));
			ArrayList<String> convertedUUIDS = new ArrayList<String>();
			for (UUID u : block.getAllMembers()){
				convertedUUIDS.add(u.toString());
			}
			f.set(ID + ".members", convertedUUIDS);
			f.set(ID + ".type", block.getType().toString());
			f.set(ID + ".logo", block.getLogo().toString());
			f.set(ID + ".sublogo", block.getSublogo());

			f.set(ID + ".explosions", block.doExplode());
			f.set(ID + ".pvp", block.pvpEnabled());
			f.set(ID + ".mobspawning", block.spawnMobs());
			f.set(ID + ".lock", block.isLocked());
			f.set(ID + ".level", block.getLevel());
			f.set(ID + ".maxMembers", block.getMaxMembers());

			if (block.getHome().distance(block.getCenter()) >= 0.1){
				f.set(ID + ".home", SBUtils.locToRawString(block.getHome()));
			}
			
			if (block.getWarp() != null){
				f.set(ID + ".warp", SBUtils.locToRawString(block.getWarp()));
			}
		}
		
		ArrayList<String> oldlocs = new ArrayList<String>();
		for (Location loc : oldLocs){
			String trans = SBUtils.locToString(loc);
			if (!oldlocs.contains(trans)){
				oldlocs.add(trans);
			}
		}
		
		f.set("OldLocs", null);
		
		f.set("OldLocs", oldlocs);
		
		FileAccessor.saveFile("skyblocks");
	}
	
	public void addToSkyBlock(Player player, String ID){
		SkyBlock block = skyBlocksByID.get(ID);
		
		if (block.getAllMembers().size() >= block.getMaxMembers()){
			player.sendMessage(ChatColor.RED + "Reached max party size!");
			return;
		}
		
		block.addMember(player);
		
		player.sendMessage(ChatColor.GREEN + "You were added to " + block.getName());
		
		for (UUID u : block.getAllMembers()){
			if (Bukkit.getPlayer(u) != null && Bukkit.getPlayer(u).isOnline()){
				String added = Messages.getMessage("added_to_skyblock");
				added = added.replace("<player>", player.getName());
				added = added.replace("<skyblockname>", block.getName());
				Bukkit.getPlayer(u).sendMessage(added);
			}
		}
		
		skyBlocksByID.put(ID, block);
		skyBlocksByCenter.put(block.getCenter(), ID);
		
		saveSkyBlock(ID, block);
		
		SkyPlayer pl = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
		for (int i = 1; i <= 8; i = i + 1){
			if (pl.getID(i) == null){
				if (pl.getInvitation(i - 4) == null || pl.getInvitation(i - 4).equals(ID)){
					if (i == 5){
						pl.I5 = ID; break;
					} else if (i == 6){
						pl.I6 = ID; break;
					} else if (i == 7){
						pl.I7 = ID; break;
					} else if (i == 8){
						pl.I8 = ID; break;
					}
				}
			}
		}
		
		SkyplayerManager.SkyPlayers.put(player.getUniqueId(), pl);
		SkyplayerManager.saveForPlayer(player);
		SkyplayerManager.loadForPlayer(player);
	}
	
	public void removeFromSkyBlock(Player player, String ID){
		SkyBlock block = skyBlocksByID.get(ID);
		block.removeMember(player);

		String removed = Messages.getMessage("removed_from_skyblock");
		removed = removed.replace("<skyblockname>", block.getName());
		player.sendMessage(removed);
		
		for (UUID u : block.getAllMembers()){
			if (Bukkit.getPlayer(u) != null && Bukkit.getPlayer(u).isOnline()){
				Bukkit.getPlayer(u).sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.RED + " was removed from " + block.getName());
			}
		}
		
		skyBlocksByID.put(ID, block);
		skyBlocksByCenter.put(block.getCenter(), ID);
		
		saveSkyBlock(ID, block);
		
		SkyPlayer pl = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
		
		if (pl.getMain() != null && pl.getMain().equals(ID)){
			pl.setMain(null);
		}
		
		for (int i = 1; i <= 8; i = i + 1){
			if (pl.getID(i) != null && pl.getID(i).equals(ID)){
				if (i == 1){
					pl.I1 = null;
				} else if (i == 2){
					pl.I2 = null;
				} else if (i == 3){
					pl.I3 = null;
				} else if (i == 4){
					pl.I4 = null;
				} else if (i == 5){
					pl.I5 = null;
				} else if (i == 6){
					pl.I6 = null;
				} else if (i == 7){
					pl.I7 = null;
				} else if (i == 8){
					pl.I8 = null;
				}
			}
		}
		
		SkyplayerManager.SkyPlayers.put(player.getUniqueId(), pl);
		SkyplayerManager.saveForPlayer(player);
		SkyplayerManager.loadForPlayer(player);
		SkyplayerManager.loadForPlayer(player);
	}
	
	public void removeFromSkyBlock(OfflinePlayer player, String ID){
		SkyBlock block = skyBlocksByID.get(ID);
		block.removeMember(player.getUniqueId());
		
		for (UUID u : block.getAllMembers()){
			if (Bukkit.getPlayer(u) != null && Bukkit.getPlayer(u).isOnline()){
				Bukkit.getPlayer(u).sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.RED + " was removed from " + block.getName());
			}
		}
		
		skyBlocksByID.put(ID, block);
		skyBlocksByCenter.put(block.getCenter(), ID);
		
		saveSkyBlock(ID, block);
		
		SkyPlayer pl = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
		
		if (pl == null){
			SkyplayerManager.loadForPlayer(player);
		}
		
		pl = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
		
		if (pl.getMain() != null && pl.getMain().equals(ID)){
			pl.setMain(null);
		}
		
		for (int i = 1; i <= 8; i = i + 1){
			if (pl.getID(i) != null && pl.getID(i).equals(ID)){
				if (i == 1){
					pl.I1 = null;
				} else if (i == 2){
					pl.I2 = null;
				} else if (i == 3){
					pl.I3 = null;
				} else if (i == 4){
					pl.I4 = null;
				} else if (i == 5){
					pl.I5 = null;
				} else if (i == 6){
					pl.I6 = null;
				} else if (i == 7){
					pl.I7 = null;
				} else if (i == 8){
					pl.I8 = null;
				}
			}
		}
		
		SkyplayerManager.SkyPlayers.put(player.getUniqueId(), pl);
		SkyplayerManager.saveForPlayer(player);
		SkyplayerManager.loadForPlayer(player);
		SkyplayerManager.loadForPlayer(player);
	}
	
	public boolean inviteToSkyblock(Player player, String ID){
		SkyBlock block = skyBlocksByID.get(ID);
		if (block.totalMembers() >= block.getMaxMembers()){
			return false;
		}
		
		SkyPlayer sp = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
		boolean invitation = sp.addInvitation(ID);
		
		if (Main.debugMode){
			player.sendMessage("[Debug] Fired SkyPlayer.addInvitation(ID): " + invitation);
		}
		
		SkyplayerManager.SkyPlayers.put(player.getUniqueId(), sp);
		SkyplayerManager.saveForPlayer(player);
		SkyplayerManager.loadForPlayer(player);
		
		return invitation;
	}
}
