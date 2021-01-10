package me.yungweezy.skyblock.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.yungweezy.skyblock.main.CreateItem;
import me.yungweezy.skyblock.main.FileAccessor;
import me.yungweezy.skyblock.main.Main;
import me.yungweezy.skyblock.managers.SkyBlock;
import me.yungweezy.skyblock.managers.SkyBlockType;
import me.yungweezy.skyblock.managers.SkyPlayer;
import me.yungweezy.skyblock.managers.SBUtils;
import me.yungweezy.skyblock.managers.SkyplayerManager;
import me.yungweezy.skyblock.misc.IslandLevels;

public class GUIMain {

	public String menustart = ChatColor.translateAlternateColorCodes('&', "MCTown &l»&r Skyblock");
	private Inventory keyboard; 
	private Inventory capsKeyboard;
	private HashMap<Integer, Inventory> chooseIcons = new HashMap<Integer, Inventory>();
	private Inventory typeSelect;
	public HashMap<String, ArrayList<Player>> zOnlineByID = new HashMap<String, ArrayList<Player>>();

	public HashMap<UUID, String> workingIn = new HashMap<UUID, String>(); // PlayerUUID, skyblock ID

	public HashMap<String, CachedGUI> cachedSkyblockSpecific = new HashMap<String, CachedGUI>();

	@SuppressWarnings("unchecked")
	public Inventory getMainMenu(Player player){
		Inventory inv = Bukkit.createInventory(null, 9, menustart + " Main");
		SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());

		ArrayList<String> lore = new ArrayList<String>();
		lore.add("&cClick to create");
		lore.add("&ca new skyblock!");
		ItemStack create = CreateItem.createItem(Material.GRASS, 0, "&6&lCreate Skyblock", lore);

		ItemStack locked = create.clone();
		lore.add("&4&lLocked");
		locked = CreateItem.setLore(locked, lore);

		if (p.I1 == null){
			if (player.hasPermission("skyblock.create.1")){
				inv.setItem(0, create);
			} else {
				inv.setItem(0, locked);
			}
		} else {
			SkyBlock block = Main.blockManager.skyBlocksByID.get(p.getID(1));

			String name = block.getName();
			Material logo = block.getLogo();

			ArrayList<String> blll = new ArrayList<String>();
			blll.add("&aClick here to access this");
			blll.add("&askyblock's menu");
			blll.add("");
			blll.add("&cMembers: &e" + block.totalMembers() + "/" + block.getMaxMembers() + " members");
			String biomestring = block.getType().toString().toLowerCase();
			biomestring = biomestring.substring(0, 1).toUpperCase() + biomestring.substring(1, biomestring.length());
			blll.add("&cBiome: &e" + biomestring);
			ItemStack bll = CreateItem.createItem(logo, block.getSublogo(), "&6&l" + name, blll);
			inv.setItem(0, bll);
		}

		if (p.I2 == null){
			if (player.hasPermission("skyblock.create.2")){
				inv.setItem(1, create);
			} else {
				inv.setItem(1, locked);
			}
		} else {
			SkyBlock block = Main.blockManager.skyBlocksByID.get(p.getID(2));
			String name = block.getName();
			Material logo = block.getLogo();

			ArrayList<String> blll = new ArrayList<String>();
			blll.add("&aClick here to access this");
			blll.add("&askyblock's menu");
			blll.add("");
			blll.add("&cMembers: &e" + block.totalMembers() + "/" + block.getMaxMembers() + " members");
			String biomestring = block.getType().toString().toLowerCase();
			biomestring = biomestring.substring(0, 1).toUpperCase() + biomestring.substring(1, biomestring.length());
			blll.add("&cBiome: &e" + biomestring);
			ItemStack bll = CreateItem.createItem(logo, block.getSublogo(), "&6&l" + name, blll);
			inv.setItem(1, bll);
		}

		if (p.I3 == null){
			if (player.hasPermission("skyblock.create.3")){
				inv.setItem(2, create);
			} else {
				inv.setItem(2, locked);
			}
		} else {
			SkyBlock block = Main.blockManager.skyBlocksByID.get(p.getID(3));
			String name = block.getName();
			Material logo = block.getLogo();

			ArrayList<String> blll = new ArrayList<String>();
			blll.add("&aClick here to access this");
			blll.add("&askyblock's menu");
			blll.add("");
			blll.add("&cMembers: &e" + block.totalMembers() + "/" + block.getMaxMembers() + " members");
			String biomestring = block.getType().toString().toLowerCase();
			biomestring = biomestring.substring(0, 1).toUpperCase() + biomestring.substring(1, biomestring.length());
			blll.add("&cBiome: &e" + biomestring);
			ItemStack bll = CreateItem.createItem(logo, block.getSublogo(), "&6&l" + name, blll);
			inv.setItem(2, bll);
		}

		if (p.I4 == null){
			if (player.hasPermission("skyblock.create.4")){
				inv.setItem(3, create);
			} else {
				inv.setItem(3, locked);
			}
		} else {
			SkyBlock block = Main.blockManager.skyBlocksByID.get(p.getID(4));
			String name = block.getName();
			Material logo = block.getLogo();

			ArrayList<String> blll = new ArrayList<String>();
			blll.add("&aClick here to access this");
			blll.add("&askyblock's menu");
			blll.add("");
			blll.add("&cMembers: &e" + block.totalMembers() + "/" + block.getMaxMembers() + " members");
			String biomestring = block.getType().toString().toLowerCase();
			biomestring = biomestring.substring(0, 1).toUpperCase() + biomestring.substring(1, biomestring.length());
			blll.add("&cBiome: &e" + biomestring);
			ItemStack bll = CreateItem.createItem(logo, block.getSublogo(), "&6&l" + name, blll);
			inv.setItem(3, bll);
		}

		lore.clear(); lore.clear(); lore.clear(); // cant be sure enough ;)

		lore.add("&bJoin someone's skyblock.");
		lore.add("   ");

		ArrayList<String> noinvite = (ArrayList<String>) lore.clone();
		noinvite.add("&bInvitations will");
		noinvite.add("&bappear here!");

		ItemStack noinvitation = CreateItem.createItem(Material.PAPER, 0, "&6&lSkyblock Party", noinvite);

		if (p.I5 != null){
			SkyBlock block = Main.blockManager.skyBlocksByID.get(p.getID(5));
			String name = block.getName();
			Material logo = block.getLogo();

			ArrayList<String> blll = new ArrayList<String>();
			blll.add("&7Teleport to");
			blll.add("&7" + name);
			ItemStack bll = CreateItem.createItem(logo, block.getSublogo(), "&6&l" + name + "&7 (party)", blll);
			inv.setItem(5, bll);
		} else if (!p.hasInvites() || p.getInvitation(1) == null){
			inv.setItem(5, noinvitation);
		} else if (p.getInvitation(1) != null){
			ArrayList<String> invited = (ArrayList<String>) lore.clone();
			String ID = p.getInvitation(1);

			if (Main.blockManager.skyBlocksByID.get(ID) == null){
				p.removeInvitation(ID);
				SkyplayerManager.SkyPlayers.put(player.getUniqueId(), p);
				inv.setItem(5, noinvitation);
			} else {
				SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
				invited.add("&6Invitation to join");
				invited.add("&6&l" + block.getName());
				ItemStack invitation = CreateItem.createItem(Material.NAME_TAG, 0, "&6&lSkyblock Party", invited);
				inv.setItem(5, invitation);
			}
		}

		if (p.I6 != null){
			SkyBlock block = Main.blockManager.skyBlocksByID.get(p.getID(6));
			String name = block.getName();
			Material logo = block.getLogo();

			ArrayList<String> blll = new ArrayList<String>();
			blll.add("&7Teleport to");
			blll.add("&7" + name);
			ItemStack bll = CreateItem.createItem(logo, block.getSublogo(), "&6&l" + name + "&7 (party)", blll);
			inv.setItem(6, bll);
		} else if (!p.hasInvites() || p.getInvitation(2) == null){
			inv.setItem(6, noinvitation);
		} else if (p.getInvitation(2) != null){
			ArrayList<String> invited = (ArrayList<String>) lore.clone();
			String ID = p.getInvitation(2);

			if (Main.blockManager.skyBlocksByID.get(ID) == null){
				p.removeInvitation(ID);
				SkyplayerManager.SkyPlayers.put(player.getUniqueId(), p);
				inv.setItem(6, noinvitation);
			} else {
				SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
				invited.add("&6Invitation to join");
				invited.add("&6&l" + block.getName());
				ItemStack invitation = CreateItem.createItem(Material.NAME_TAG, 0, "&6&lSkyblock Party", invited);
				inv.setItem(6, invitation);
			}
		}

		if (p.I7 != null){
			SkyBlock block = Main.blockManager.skyBlocksByID.get(p.getID(7));
			String name = block.getName();
			Material logo = block.getLogo();

			ArrayList<String> blll = new ArrayList<String>();
			blll.add("&7Teleport to");
			blll.add("&7" + name);
			ItemStack bll = CreateItem.createItem(logo, block.getSublogo(), "&6&l" + name + "&7 (party)", blll);
			inv.setItem(7, bll);
		} else if (!p.hasInvites() || p.getInvitation(3) == null){
			inv.setItem(7, noinvitation);
		} else if (p.getInvitation(3) != null){
			ArrayList<String> invited = (ArrayList<String>) lore.clone();
			String ID = p.getInvitation(3);

			if (Main.blockManager.skyBlocksByID.get(ID) == null){
				p.removeInvitation(ID);
				SkyplayerManager.SkyPlayers.put(player.getUniqueId(), p);
				inv.setItem(7, noinvitation);
			} else {
				SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
				invited.add("&6Invitation to join");
				invited.add("&6&l" + block.getName());
				ItemStack invitation = CreateItem.createItem(Material.NAME_TAG, 0, "&6&lSkyblock Party", invited);
				inv.setItem(7, invitation);
			}
		}

		if (p.I8 != null){
			SkyBlock block = Main.blockManager.skyBlocksByID.get(p.getID(8));
			String name = block.getName();
			Material logo = block.getLogo();

			ArrayList<String> blll = new ArrayList<String>();
			blll.add("&7Teleport to");
			blll.add("&7" + name);
			ItemStack bll = CreateItem.createItem(logo, block.getSublogo(), "&6&l" + name + "&7 (party)", blll);
			inv.setItem(8, bll);
		} else if (!p.hasInvites() || p.getInvitation(4) == null){
			inv.setItem(8, noinvitation);
		} else if (p.getInvitation(4) != null){
			ArrayList<String> invited = (ArrayList<String>) lore.clone();
			String ID = p.getInvitation(4);

			if (Main.blockManager.skyBlocksByID.get(ID) == null){
				p.removeInvitation(ID);
				SkyplayerManager.SkyPlayers.put(player.getUniqueId(), p);
				inv.setItem(8, noinvitation);
			} else {
				SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
				invited.add("&6Invitation to join");
				invited.add("&6&l" + block.getName());
				ItemStack invitation = CreateItem.createItem(Material.NAME_TAG, 0, "&6&lSkyblock Party", invited);
				inv.setItem(8, invitation);
			}
		}

		if (Main.debugMode){
			player.sendMessage("[Debug] ID (1): " + p.getID(1));
			player.sendMessage("[Debug] ID (2): " + p.getID(2));
			player.sendMessage("[Debug] ID (3): " + p.getID(3));
			player.sendMessage("[Debug] ID (4): " + p.getID(4));
			player.sendMessage("[Debug] ID (5): " + p.getID(5));
			player.sendMessage("[Debug] ID (6): " + p.getID(6));
			player.sendMessage("[Debug] ID (7): " + p.getID(7));
			player.sendMessage("[Debug] ID (8): " + p.getID(8));

			String debugInvs = "";
			for (int i = 1; i <= 8; i = i + 1){
				debugInvs = debugInvs + "| I: " + i + " " + p.getInvitation(i) + " ";
			}
			player.sendMessage("[Debug] Inv1|8: " + debugInvs);
		}

		return inv;
	}

	public Inventory getKeyboard(boolean caps){
		if (keyboard != null){
			if (caps == true){
				return capsKeyboard;
			} else {
				return keyboard;
			}
		}

		keyboard = Bukkit.createInventory(null, 36, menustart + " Name");
		capsKeyboard = Bukkit.createInventory(null, 36, menustart + " Name [shift]");
		String letters = "abcdefghijklmnopqrstuvwxyz";

		for (int i = 0; i <= 25; i = i + 1){
			int slotnr = i + 9;
			ItemStack head = Main.letterManager.getLetter(i);
			ItemStack stack = CreateItem.createItem(Material.WOOL, 5, ChatColor.WHITE + letters.substring(i, i + 1), new ArrayList<String>());

			if (head.getType() != Material.DIRT){
				stack = CreateItem.setName(head, ChatColor.WHITE + letters.substring(i, i + 1));
				stack = CreateItem.setLore(stack, new ArrayList<String>());
			}

			keyboard.setItem(slotnr, stack);
			stack = CreateItem.setName(stack, letters.substring(i, i + 1).toUpperCase());
			capsKeyboard.setItem(slotnr, stack);
		}

		ItemStack shift = CreateItem.createItem(Material.ANVIL, 0, "shift", new ArrayList<String>());
		keyboard.setItem(0, shift);
		capsKeyboard.setItem(0, shift);
		ItemStack name = CreateItem.createItem(Material.PAPER, 0, "_ _ _ _ _ _ _", new ArrayList<String>());
		keyboard.setItem(4, name);
		capsKeyboard.setItem(4, shift);

		ItemStack back = CreateItem.createItem(Material.WOOL, 14, ChatColor.RED + "" + ChatColor.BOLD + "Backspace", new ArrayList<String>());
		keyboard.setItem(8, back);
		capsKeyboard.setItem(8, back);

		ItemStack finished = CreateItem.createItem(Material.EMERALD, 0, ChatColor.GREEN + "" + ChatColor.BOLD + "Finished", new ArrayList<String>());
		keyboard.setItem(3, finished);
		capsKeyboard.setItem(3, finished);
		keyboard.setItem(5, finished);
		capsKeyboard.setItem(5, finished);

		if (caps == true){
			return capsKeyboard;
		} else {
			return keyboard;
		}
	}

	@SuppressWarnings("deprecation")
	public Inventory getIconInventory(int page){
		if (chooseIcons.get(page) != null){
			return chooseIcons.get(page);
		}

		Inventory inv = Bukkit.createInventory(null, 54, menustart + " Icon " + page);
		FileAccessor.reloadFileF("config");
		FileConfiguration config = FileAccessor.getFileF("config");

		if (page < 12){
			ItemStack next = CreateItem.createItem(Material.ARROW, 0, "&f&lNext page", new ArrayList<String>());
			inv.setItem(8, next);
		}

		if (page > 1){
			ItemStack prev = CreateItem.createItem(Material.ARROW, 0, "&f&lPrevious page", new ArrayList<String>());
			inv.setItem(0, prev);
		}

		for (int i = 0; i <= 44; i = i + 1){
			int converted = i + (44 * (page - 1));
			if (config.get("icons." + converted + "") != null){
				String un = config.getString("icons." + converted + "");
				String[] split = un.split(",");
				Material mat = Material.AIR;
				int sub = 0;
				try {
					mat = Material.getMaterial(Integer.parseInt(split[0]));
					sub = Integer.parseInt(split[1]);
				} catch (Exception e){
					System.out.println("Incorrectly configured item: " + converted);
				}

				ItemStack stack = CreateItem.createItem(mat, sub, "", new ArrayList<String>());
				inv.setItem(i + 9, stack);
			}
		}

		chooseIcons.put(page, inv);
		return chooseIcons.get(page);
	}

	public Inventory getTypeSelect(){
		if (typeSelect != null){
			return typeSelect;
		}

		typeSelect = Bukkit.createInventory(null, 45, menustart + " Type");

		FileAccessor.reloadFileF("config");
		FileConfiguration config = FileAccessor.getFileF("config");
		int slot = 0;
		for (String n : config.getConfigurationSection("types").getKeys(false)){
			Material mat = Material.valueOf(config.getString("types." + n + ".mat"));
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("&aClick here to select this");
			lore.add("&abiome theme.");

			String name = "";
			if (config.getString("types." + n + ".name") != null){
				name = config.getString("types." + n + ".name");
			} else {
				String biomestring = n;
				biomestring = ChatColor.AQUA + biomestring.substring(0, 1).toUpperCase() + biomestring.substring(1, biomestring.length()).toLowerCase();
				name = biomestring;
			}

			name = ChatColor.translateAlternateColorCodes('&', name);
			Main.blockManager.nameType.put(name, SkyBlockType.valueOf(n));

			ItemStack stack = CreateItem.createItem(mat, config.getInt("types." + n + ".sub"), name, lore);
			typeSelect.setItem(slot + 9, stack);
			slot = slot + 1;
		} 

		return typeSelect;
	}

	public Inventory getSkyBlockSpecific(String ID){
		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);

		Inventory inv = null;
		CachedGUI cached = cachedSkyblockSpecific.get(ID);
		
		if (cached == null || System.currentTimeMillis() - cached.created >= 30000 || cached.inv == null){
			if (cached == null || cached.inv == null){
				inv = Bukkit.createInventory(null, 18, menustart + " Block");
			} else {
				inv = cached.inv;
			}
			
			cached = new CachedGUI();
			cached.created = System.currentTimeMillis();
			cached.inv = inv;
			cachedSkyblockSpecific.put(ID, cached);
		} else {
			inv = cached.inv;
			
			ArrayList<String> lore = new ArrayList<String>();
			lore.add("&aClick here to modify");
			lore.add("&askyblock settings");
			lore.add("");
			lore.add("&cMembers: &e" + block.totalMembers() + "/" + block.getMaxMembers());
			lore.add("&cLevel: &e" + block.getLevel() / IslandLevels.lvsperLevel);
			ItemStack sbinfo = CreateItem.createItem(Material.FLINT, 0, "&6&lSkyblock Settings", lore);
			inv.setItem(6, sbinfo);
			
			return inv;
		}

		ArrayList<String> lore = new ArrayList<String>();
		lore.add("&aClick here to modify");
		lore.add("&askyblock settings");
		lore.add("");
		lore.add("&cMembers: &e" + block.totalMembers() + "/" + block.getMaxMembers());
		lore.add("&cLevel: &e" + block.getLevel() / IslandLevels.lvsperLevel);
		ItemStack sbinfo = CreateItem.createItem(Material.FLINT, 0, "&6&lSkyblock Settings", lore);
		inv.setItem(6, sbinfo);

		ArrayList<String> backl = new ArrayList<String>();
		backl.add("&aClick here to return");
		backl.add("&ato the previous menu");
		ItemStack back = CreateItem.createItem(Material.SIGN, 0, "&6&lReturn to Menu", backl);
		inv.setItem(13, back);

		ArrayList<String> deletel = new ArrayList<String>();
		deletel.add("&aClick here to delete");
		deletel.add("&ayour skyblock entirely!");
		deletel.add("");
		deletel.add("&cWarning: this is not");
		deletel.add("&creversable and removes");
		deletel.add("&cyour skyblock completely!");
		deletel.add("");
		if (me.yungweezy.skyblock.main.Main.deleteCost > 0){
			deletel.add("&3Price: &e" + me.yungweezy.skyblock.main.Main.deleteCost + " Sky Bucks");
		}
		ItemStack delete = CreateItem.createItem(Material.TNT, 0, "&6&lDelete Skyblock", deletel);
		inv.setItem(8, delete);

		ArrayList<String> invitel = new ArrayList<String>();
		invitel.add("&aClick here to invite players");
		invitel.add("&ainto your skyblock party");
		ItemStack invite = CreateItem.createItem(Material.BOOK_AND_QUILL, 0, "&6&lInvite", invitel);
		inv.setItem(2, invite);

		ArrayList<String> teleportl = new ArrayList<String>();
		teleportl.add(ChatColor.GREEN + "Click here to teleport");
		teleportl.add(ChatColor.GREEN + "to your skyblock");
		ItemStack teleport = CreateItem.createItem(block.getLogo(), block.getSublogo(), "&6&lTeleport", teleportl);
		inv.setItem(0, teleport);

		ArrayList<String> membersl = new ArrayList<String>();
		membersl.add("&aClick here to view and kick");
		membersl.add("&akick party members");
		ItemStack members = CreateItem.createItem(Material.SKULL_ITEM, 3, "&6&lParty Members", membersl);
		inv.setItem(4, members);

		return inv;
	}

	public Inventory getInvite(String ID, int page, ArrayList<Player> online){
		Inventory inv = Bukkit.createInventory(null, 45, menustart + " Invite");

		ItemStack info = SBUtils.getInfoItem(Main.blockManager.skyBlocksByID.get(ID));
		ArrayList<String> lre = (ArrayList<String>) info.getItemMeta().getLore();
		lre.add(ChatColor.GRAY + "Page: " + page);
		info = CreateItem.setLore(info, lre);
		inv.setItem(4, info);

		ArrayList<String> backl = new ArrayList<String>();
		backl.add("&aClick here to return");
		backl.add("&ato the previous menu");
		ItemStack back = CreateItem.createItem(Material.SIGN, 0, "&6&lReturn to Menu", backl);
		inv.setItem(3, back);
		inv.setItem(5, back);

		zOnlineByID.put(ID, online);

		if (online == null || online.size() == 0){
			return inv;
		}

		if (online.size() > 36){
			ItemStack next = CreateItem.createItem(Material.ARROW, 0, "&f&lNext page", new ArrayList<String>());
			inv.setItem(8, next);
		}

		if (page > 0){
			ItemStack prev = CreateItem.createItem(Material.ARROW, 0, "&f&lPrevious page", new ArrayList<String>());
			inv.setItem(0, prev);
		}

		for (int i = 0; i <= 35; i = i + 1){
			if (online.size() <= (i + (page * 36))){
				return inv;
			}

			if (online.get(i) == null){
				return inv;
			}

			if (online.get(i + (page * 36)) == null){
				return inv;
			}

			Player p =  online.get((i + (page * 36)));
			String pname = p.getName();

			ItemStack head = SBUtils.getHead(pname);

			if (p.getCustomName() != null){
				if (!p.getCustomName().equals(pname)){
					ArrayList<String> lore = new ArrayList<String>();
					lore.add(p.getCustomName());
					head = CreateItem.setLore(head, lore);
				}
			}

			inv.setItem(i + 9, head);
		}

		return inv;
	}

	public Inventory getMembers(String ID, int page){
		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
		ArrayList<UUID> members = block.getAllMembers();

		Inventory inv;

		if (members.size() < 34){
			inv = Bukkit.createInventory(null, 45, menustart + " Members");
		} else {
			inv = Bukkit.createInventory(null, 45, menustart + " Members " + page);

			ItemStack next = CreateItem.createItem(Material.ARROW, 0, "&f&lNext page", new ArrayList<String>());
			inv.setItem(8, next);

			if (page > 1){
				ItemStack prev = CreateItem.createItem(Material.ARROW, 0, "&f&lPrev page", new ArrayList<String>());
				inv.setItem(0, prev);
			}
		}

		ItemStack info = SBUtils.getInfoItem(Main.blockManager.skyBlocksByID.get(ID));
		inv.setItem(4, info);


		ArrayList<String> backl = new ArrayList<String>();
		backl.add("&aClick here to return");
		backl.add("&ato the previous menu");
		ItemStack back = CreateItem.createItem(Material.SIGN, 0, "&6&lReturn to Menu", backl);
		inv.setItem(3, back);
		inv.setItem(5, back);

		int slot = 9;

		HashMap<Integer, UUID> memberz = new HashMap<Integer, UUID>();

		for (UUID u : members){
			memberz.put(slot, u);
			slot = slot + 1;
		}

		int start = 9;

		if (page > 1){
			start = inv.getSize();
			start = start * (page - 1);
		}

		int rawslot = 9;

		for (int i = start; i < inv.getSize(); i = i + 1){
			String playername = "";
			if (memberz.get(i) != null){
				UUID u = memberz.get(i);

				if (Bukkit.getPlayer(u) != null && Bukkit.getPlayer(u).isOnline()){
					Player pl = Bukkit.getPlayer(u);
					playername = pl.getName();
				} else if (Bukkit.getOfflinePlayer(u) != null){
					playername = Bukkit.getOfflinePlayer(u).getName();
				} else {
					playername = "Error!";
				}

				ItemStack head = SBUtils.getHead(playername);
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("&cClick to open");
				lore.add("&cplayer settings!");
				lore.add(ChatColor.GRAY + u.toString());
				head = CreateItem.setLore(head, lore);

				inv.setItem(rawslot, head);

				rawslot = rawslot + 1;
			}
		}

		return inv;
	}

	public Inventory getConfirmDelete(String ID){
		Inventory inv = Bukkit.createInventory(null, 9, menustart + " Delete");
		ItemStack info = SBUtils.getInfoItem(Main.blockManager.skyBlocksByID.get(ID));
		inv.setItem(4, info);

		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);

		ArrayList<String> deletel = new ArrayList<String>();
		deletel.add(ChatColor.RED + "Confirm you want");
		deletel.add(ChatColor.RED + "to " + ChatColor.DARK_RED + "permanently delete");
		deletel.add(ChatColor.RED + block.getName());
		deletel.add(ChatColor.RED + "Costs: " + me.yungweezy.skyblock.main.Main.deleteCost);

		ItemStack delete = CreateItem.createItem(Material.WOOL, 5, "&4Permanently DELETE", deletel);
		inv.setItem(0, delete);
		inv.setItem(1, delete);
		inv.setItem(2, delete);
		inv.setItem(3, delete);

		ArrayList<String> cancell = new ArrayList<String>();
		cancell.add(ChatColor.GREEN + "I dont want to");
		cancell.add(ChatColor.GREEN + "delete my skyblock!");

		ItemStack cancel = CreateItem.createItem(Material.WOOL, 14, ChatColor.GREEN + "Cancel", cancell);
		inv.setItem(5, cancel);
		inv.setItem(6, cancel);
		inv.setItem(7, cancel);
		inv.setItem(8, cancel);
		return inv;
	}

	@SuppressWarnings("deprecation")
	public Inventory getSettings(String ID){
		Inventory inv = Bukkit.createInventory(null, 27, menustart + " Settings");
		ArrayList<String> backl = new ArrayList<String>();
		backl.add("&aClick here to return");
		backl.add("&ato the previous menu");
		ItemStack back = CreateItem.createItem(Material.SIGN, 0, "&6&lReturn to Menu", backl);
		inv.setItem(22, back);

		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);

		ArrayList<String> pvpl = new ArrayList<String>(); // PVP
		if (block.pvpEnabled()){
			pvpl.add("&b● &a&lENABLED &b●");
		} else {
			pvpl.add("&b● &c&lDISABLED &b●");
		}
		pvpl.add("&aClick to toggle combat");
		pvpl.add("&ain your skyblock!");

		ItemStack pvp = CreateItem.createItem(Material.DIAMOND_HELMET, 0, "&6&lPlayer Combat", pvpl);
		inv.setItem(2, pvp); // END PVP

		ArrayList<String> lockl = new ArrayList<String>(); // LOCK
		if (block.isLocked()){
			lockl.add("&b● &a&lENABLED &b●");
		} else {
			lockl.add("&b● &c&lDISABLED &b●");
		}
		lockl.add("&aClick to lock or unlock");
		lockl.add("&ayour skyblock");
		lockl.add("");
		lockl.add("&aLocking your skyblock");
		lockl.add("&aprevents players not in");
		lockl.add("&ayour party from entering");
		lockl.add("&ayour skyblock");

		ItemStack lock = CreateItem.createItem(Material.IRON_FENCE, 0, "&6&lSkyblock Lock", lockl);
		inv.setItem(3, lock); // END LOCK

		ArrayList<String> mobsl = new ArrayList<String>(); // MOBSPAWNING
		if (block.spawnMobs()){
			mobsl.add("&b● &a&lENABLED &b●");
		} else {
			mobsl.add("&b● &c&lDISABLED &b●");
		}
		mobsl.add("&aClick here to toggle toggle mob");
		mobsl.add("&aspawning in your skyblock");

		ItemStack mobs = CreateItem.createItem(Material.MOB_SPAWNER, 0, "&6&lMonster Spawning", mobsl);
		inv.setItem(4, mobs); // END MOBSPAWNING

		ArrayList<String> explosionsl = new ArrayList<String>(); // EXPLOSIONS
		if (block.doExplode()){
			explosionsl.add("&b● &a&lENABLED &b●");
		} else {
			explosionsl.add("&b● &c&lDISABLED &b●");
		}
		explosionsl.add("&aClick here to toggle explosions");
		explosionsl.add("&ain your skyblock");

		ItemStack explosions = CreateItem.createItem(Material.TNT, 0, "&6&lExplosions", explosionsl);
		inv.setItem(5, explosions); // END EXPLOSIONS

		ArrayList<String> iconl = new ArrayList<String>(); // ICON
		iconl.add("&aClick here to change the");
		iconl.add("&aicon of this skyblock");
		iconl.add("");
		iconl.add("&3Price: &e" + me.yungweezy.skyblock.main.Main.iconChangeCost + " Sky Bucks");

		ItemStack icon = CreateItem.createItem(Material.ITEM_FRAME, 0, "&6&lChange Skyblock Icon", iconl);
		inv.setItem(12, icon); // END ICON

		ArrayList<String> namel = new ArrayList<String>(); // NAME
		namel.add("&aClick here to change the");
		namel.add("&aname of this skyblock");
		namel.add("");
		namel.add("&3Price: &e" + me.yungweezy.skyblock.main.Main.renameCost + " Sky Bucks");

		ItemStack name = CreateItem.createItem(Material.NAME_TAG, 0, "&6&lChange Skyblock Name", namel);
		inv.setItem(13, name); // END NAME

		ArrayList<String> homel = new ArrayList<String>(); // HOME
		homel.add("&aClick here to set the home");
		homel.add("&ateleport location of your");
		homel.add("&askyblock to where you are");
		homel.add("&acurrently standing");

		ItemStack home = CreateItem.createItem(Material.COMPASS, 0, "&6&lSet Skyblock Home Location", homel);
		inv.setItem(14, home); // END HOME

		// MAIN	
		UUID owner = block.getOwner();
		SkyPlayer p = SkyplayerManager.SkyPlayers.get(owner);

		if (p.getMain() == null || !p.getMain().equals(ID)){
			// grey dye & not main
			ArrayList<String> mainl = new ArrayList<String>();
			mainl.add("&aClick here to activate this");
			mainl.add("&askyblock as your main skyblock");
			ItemStack mainstack = CreateItem.createItem(Material.INK_SACK, 8, "&6&lSet Main skyblock", mainl);
			inv.setItem(15, mainstack);
		} else {
			// lime dye & main
			ArrayList<String> mainl = new ArrayList<String>();
			mainl.add("&aThis skyblock is activated");
			mainl.add("&aas your main skyblock!");
			ItemStack mainstack = CreateItem.createItem(Material.INK_SACK, 10, "&6&lMain skyblock", mainl);
			inv.setItem(15, mainstack);
		}
		// END MAIN

		// CLEAR MOBS
		ArrayList<String> clearl = new ArrayList<String>();
		clearl.add("&aClick here to butcher types");
		clearl.add("&aof mobs in your skyblock.");
		ItemStack clear = CreateItem.createItem(Material.getMaterial(166), 0, "&6&lButcher Mobs", clearl);
		inv.setItem(11, clear);
		// END CLEAR MOBS

		// DEBUG MESSAGE
		ArrayList<String> debugl = new ArrayList<String>();
		debugl.add("&aClick here to output debug");
		debugl.add("&ainformation of your skyblock");
		ItemStack debug = CreateItem.createItem(Material.WATCH, 0, "&6&lDebug Information", debugl);
		inv.setItem(6, debug);
		// END DEBUG MESSAGE
		return inv;
	}

	@SuppressWarnings("deprecation")
	public Inventory getPlayerInventory(String ID, UUID u){
		Inventory inv = Bukkit.createInventory(null, 9, menustart + " Player");

		String playername = "";

		if (Bukkit.getPlayer(u) != null && Bukkit.getPlayer(u).isOnline()){
			Player pl = Bukkit.getPlayer(u);
			playername = pl.getName();
		} else if (Bukkit.getOfflinePlayer(u) != null){
			playername = Bukkit.getOfflinePlayer(u).getName();
		} else {
			playername = "Error!";
		}

		ItemStack head = SBUtils.getHead(playername);
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + u.toString());
		lore.add(ChatColor.GRAY + ID);
		head = CreateItem.setLore(head, lore);
		inv.setItem(8, head);

		if (SkyplayerManager.SkyPlayers.get(u) == null){
			if (Bukkit.getPlayer(u) == null){
				SkyplayerManager.loadForPlayer(Bukkit.getOfflinePlayer(u));
			} else {
				SkyplayerManager.loadForPlayer(Bukkit.getPlayer(u));
			}
		}

		SkyPlayer p = SkyplayerManager.SkyPlayers.get(u);

		if (p == null){
			SkyplayerManager.loadForPlayer(Bukkit.getOfflinePlayer(u));
			p = SkyplayerManager.SkyPlayers.get(u);
		}

		if (p == null){
			System.out.print("p is still null");
		}

		if (ID == null){
			System.out.print("ID is null");
		}

		// interact
		ArrayList<String> interactl = new ArrayList<String>();
		if (p.hasRemovedPerm(ID, "INTERACT")){
			interactl.add("&b● &c&lDISABLED &b●");
		} else {
			interactl.add("&b● &a&lENABLED &b●");
		}

		interactl.add("&7If player can");
		interactl.add("&7interact with blocks");
		interactl.add("&7(Doors, chests, etc)");

		if (p.hasRemovedPerm(ID, "INTERACT")){
			ItemStack interact = CreateItem.createItem(Material.INK_SACK, 8, "&6&lInteract", interactl);
			inv.setItem(0, interact);
		} else {
			ItemStack interact = CreateItem.createItem(Material.INK_SACK, 10, "&6&lInteract", interactl);
			inv.setItem(0, interact);
		}
		// end interact

		// break
		ArrayList<String> breakl = new ArrayList<String>();
		breakl.add("&7If player can");
		breakl.add("&7break blocks");
		breakl.add("&7on your skyblock");

		if (p.hasRemovedPerm(ID, "BREAK")){
			breakl.add("&b● &c&lDISABLED &b●");
			ItemStack breaks = CreateItem.createItem(Material.INK_SACK, 8, "&6&lBreak", breakl);
			inv.setItem(1, breaks);
		} else {
			breakl.add("&b● &a&lENABLED &b●");
			ItemStack breaks = CreateItem.createItem(Material.INK_SACK, 10, "&6&lBreak", breakl);
			inv.setItem(1, breaks);
		}
		// end break

		// place
		ArrayList<String> placel = new ArrayList<String>();
		if (p.hasRemovedPerm(ID, "PLACE")){
			placel.add("&b● &c&lDISABLED &b●");
		} else {
			placel.add("&b● &a&lENABLED &b●");
		}
		placel.add("&7If player can");
		placel.add("&7place blocks");
		placel.add("&7on your skyblock");

		if (p.hasRemovedPerm(ID, "PLACE")){
			ItemStack place = CreateItem.createItem(Material.INK_SACK, 8, "&6&lPlace", placel);
			inv.setItem(2, place);
		} else {
			ItemStack place = CreateItem.createItem(Material.INK_SACK, 10, "&6&lPlace", placel);
			inv.setItem(2, place);
		}
		// end place

		// mobs
		ArrayList<String> mobsl = new ArrayList<String>();
		if (p.hasRemovedPerm(ID, "MOBS")){
			mobsl.add("&b● &c&lDISABLED &b●");
		} else {
			mobsl.add("&b● &a&lENABLED &b●");
		}

		mobsl.add("&7If player can");
		mobsl.add("&7interact with mobs");
		mobsl.add("&7(Creepers, Itemframes, etc)");

		if (p.hasRemovedPerm(ID, "MOBS")){
			ItemStack mobs = CreateItem.createItem(Material.INK_SACK, 8, "&6&lMobs", mobsl);
			inv.setItem(3, mobs);
		} else {
			ItemStack mobs = CreateItem.createItem(Material.INK_SACK, 10, "&6&lMobs", mobsl);
			inv.setItem(3, mobs);
		}
		// end mobs

		// pickup 
		ArrayList<String> pickupl = new ArrayList<String>();
		if (p.hasRemovedPerm(ID, "PICKUP")){
			pickupl.add("&b● &c&lDISABLED &b●");
		} else {
			pickupl.add("&b● &a&lENABLED &b●");
		}

		pickupl.add("&7If player can");
		pickupl.add("&7pickup items");
		pickupl.add("&7on your skyblock");

		if (p.hasRemovedPerm(ID, "PICKUP")){
			ItemStack pickup = CreateItem.createItem(Material.INK_SACK, 8, "&6&lPickup", pickupl);
			inv.setItem(4, pickup);
		} else {
			ItemStack pickup = CreateItem.createItem(Material.INK_SACK, 10, "&6&lPickup", pickupl);
			inv.setItem(4, pickup);
		}
		// end pickup

		// kick
		ArrayList<String> kickl = new ArrayList<String>();
		kickl.add("&cClick to kick");
		kickl.add("&c" + playername);
		kickl.add("&cfrom your skyblock");
		ItemStack kick = CreateItem.createItem(Material.getMaterial(166), 0, "&4&lKick Player", kickl);
		inv.setItem(6, kick);
		// end kick

		return inv;
	}

	public Inventory chooseMobTypeButcher(){
		Inventory inv = Bukkit.createInventory(null, 9, menustart + " Butcher");

		ArrayList<String> hostilel = new ArrayList<String>();
		hostilel.add("&7Click to kill all");
		hostilel.add("&7hostile mobs on your skyblock");
		ItemStack hostile = CreateItem.createItem(Material.MONSTER_EGG, 50, "&6&lKill all hostile mobs", hostilel);
		inv.setItem(2, hostile);

		ArrayList<String> passivel = new ArrayList<String>();
		passivel.add("&7Click to kill all");
		passivel.add("&7passive mobs on your skyblock");
		ItemStack passive = CreateItem.createItem(Material.MONSTER_EGG, 90, "&6&lKill all passive mobs", passivel);
		inv.setItem(6, passive);

		ArrayList<String> backl = new ArrayList<String>();
		backl.add("&aClick here to return");
		backl.add("&ato the previous menu");
		ItemStack back = CreateItem.createItem(Material.SIGN, 0, "&6&lReturn to Menu", backl);
		inv.setItem(4, back);

		return inv;
	}

	public Inventory acceptDenyInvite(String ID){
		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
		Inventory inv = Bukkit.createInventory(null, 9, menustart + " Invitation");
		inv.setItem(4, SBUtils.getInfoItem(block));

		ArrayList<String> acceptl = new ArrayList<String>();
		acceptl.add("&aClick here to accept");
		acceptl.add("&athe invitation");
		ItemStack accept = CreateItem.createItem(Material.WOOL, 5, "&6&lAccept", acceptl);
		inv.setItem(2, accept);

		ArrayList<String> denyl = new ArrayList<String>();
		denyl.add("&cClick here to deny");
		denyl.add("&cthe invitation");
		ItemStack deny = CreateItem.createItem(Material.WOOL, 14, "&6&lDeny", denyl);
		inv.setItem(6, deny);

		return inv;
	}

	@SuppressWarnings("deprecation")
	public Inventory leaveOrTP(String ID){
		SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
		Inventory inv = Bukkit.createInventory(null, 9, menustart + " PartyOwned");

		ArrayList<String> tpl = new ArrayList<String>();
		tpl.add("&aClick here to teleport");
		tpl.add("&ato the skyblock");
		ItemStack tp = CreateItem.createItem(Material.COMPASS, 0, "&6&lTeleport", tpl);
		inv.setItem(2, tp);

		ArrayList<String> leavel = new ArrayList<String>();
		leavel.add("&cClick here to leave");
		leavel.add("&cthis skyblock's party");
		ItemStack leave = CreateItem.createItem(Material.getMaterial(166), 0, "&6&lLeave", leavel);
		inv.setItem(6, leave);

		inv.setItem(4, SBUtils.getInfoItem(block));

		return inv;
	}
}
