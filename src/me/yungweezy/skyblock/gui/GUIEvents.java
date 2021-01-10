package me.yungweezy.skyblock.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.yungweezy.skyblock.main.CreateItem;
import me.yungweezy.skyblock.main.Econ;
import me.yungweezy.skyblock.main.Main;
import me.yungweezy.skyblock.managers.SBUtils;
import me.yungweezy.skyblock.managers.SkyBlock;
import me.yungweezy.skyblock.managers.SkyBlockType;
import me.yungweezy.skyblock.managers.SkyPlayer;
import me.yungweezy.skyblock.managers.SkyplayerManager;
import me.yungweezy.skyblock.misc.Messages;

public class GUIEvents implements Listener {

    private HashMap<UUID, Long> smallCooldown = new HashMap<UUID, Long>();

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        if (!event.getInventory().getName().startsWith(Main.guiMain.menustart)){
            return;
        }

        Main.guiMain.workingIn.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent event){
        Main.guiMain.workingIn.remove(event.getPlayer().getUniqueId());
    }

    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.LOW)
    public void onClick(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();

        if (inv == null){
            return;
        }

        if (event.getCurrentItem() == null){
            return;
        }

        if (inv.getName() == null || !inv.getName().startsWith(Main.guiMain.menustart)){
            return;
        }

        event.setCancelled(true);

        if (smallCooldown.containsKey(player.getUniqueId())){
            if (System.currentTimeMillis() - smallCooldown.get(player.getUniqueId()) > 5000){
                smallCooldown.remove(player.getUniqueId());
            } else {
                return;
            }
        }

        String strippedName = ChatColor.stripColor(inv.getName());

        if (strippedName.endsWith("Main")){
            SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
            int sbslot = event.getRawSlot() + 1;
            String rawID = p.getID(sbslot);

            if (rawID == null && sbslot <= 4){
                if (!player.hasPermission("skyblock.create." + sbslot)){
                    player.sendMessage(Messages.getMessage("no_perm_new_skyblock"));
                    smallCooldown.put(player.getUniqueId(), System.currentTimeMillis() - 2000); // Can only do it every 3 seconds
                    return;
                }

                player.sendMessage(Messages.getMessage("starting_creation"));

                //Main.blockManager.startCreation(player);
                Main.nameModule.openNameInv(player);
                return;
            }

            if (event.getRawSlot() == 4){
                return;
            }

            if (sbslot > 5){
                sbslot = sbslot - 1;
                rawID = p.getID(sbslot);
            }

            if (rawID == null){
                if (p.hasInvites() && p.getInvitation(sbslot - 4) != null){
                    String InvitedTo = p.getInvitation(sbslot - 4);
                    player.closeInventory();
                    player.openInventory(Main.guiMain.acceptDenyInvite(InvitedTo));
                    Main.guiMain.workingIn.put(player.getUniqueId(), InvitedTo);
                }
                return;
            }

            String ID = p.getID(sbslot);

            if (sbslot <= 4){
                Inventory menu = Main.guiMain.getSkyBlockSpecific(ID);
                player.openInventory(menu);
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
            } else {
                player.closeInventory();
                player.openInventory(Main.guiMain.leaveOrTP(ID));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
            }

            return;
        }

        if (strippedName.endsWith("PartyOwned")){
            String ID = Main.guiMain.workingIn.get(player.getUniqueId());
            SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);

            if (event.getRawSlot() == 2){
                player.teleport(block.getCenter());
                String teleporting = Messages.getMessage("teleporting_to");
                teleporting = teleporting.replace("<skyblockname>", block.getName());
                player.sendMessage(teleporting);
                return;
            }

            if (event.getRawSlot() == 6){
                Main.blockManager.removeFromSkyBlock(player, ID);
                player.closeInventory();
                player.openInventory(Main.guiMain.getMainMenu(player));

                SkyplayerManager.saveForPlayer(player);
            }

            return;
        }

        if (strippedName.endsWith("Invitation")){
            String ID = Main.guiMain.workingIn.get(player.getUniqueId());

            if (event.getRawSlot() == 2){
                // accept invitation
                SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
                p.removeInvitation(ID);
                SkyplayerManager.SkyPlayers.put(player.getUniqueId(), p);

                Main.blockManager.addToSkyBlock(player, ID);

                player.closeInventory();
                player.openInventory(Main.guiMain.getMainMenu(player));
                return;
            }

            if (event.getRawSlot() == 6){
                SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
                p.removeInvitation(ID);
                SkyplayerManager.SkyPlayers.put(player.getUniqueId(), p);
                player.closeInventory();
                player.openInventory(Main.guiMain.getMainMenu(player));
                player.sendMessage(ChatColor.RED + "Denied invitation");
                return;
            }

            return;
        }

        if (strippedName.contains("Icon") && !strippedName.contains("ReIcon")){
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || event.getCurrentItem().getType() == Material.AIR){
                return;
            }

            if (event.getRawSlot() == 0){
                int currentpage = Integer.parseInt(strippedName.substring(strippedName.length() - 2, strippedName.length()).replace(" ", ""));
                player.closeInventory();
                Inventory fresh = Main.guiMain.getIconInventory(currentpage - 1);
                Inventory clone = Bukkit.createInventory(null, fresh.getSize(), fresh.getName());
                clone.setContents(fresh.getContents());
                clone.setItem(4, inv.getItem(4));
                player.openInventory(clone);
                // prev page
            }

            if (event.getRawSlot() == 8){
                int currentpage = Integer.parseInt(strippedName.substring(strippedName.length() - 2, strippedName.length()).replace(" ", ""));
                //player.closeInventory();
                Inventory fresh = Main.guiMain.getIconInventory(currentpage + 1);
                Inventory clone = Bukkit.createInventory(null, fresh.getSize(), fresh.getName());
                clone.setContents(fresh.getContents());
                clone.setItem(4, inv.getItem(4));
                player.openInventory(clone);
                // next page
            }

            if (event.getRawSlot() > 9){
                Inventory type = Main.guiMain.getTypeSelect();
                Inventory clone = Bukkit.createInventory(null, type.getSize(), type.getName());
                clone.setContents(type.getContents());
                ItemStack stack = inv.getItem(4);
                stack.setType(event.getCurrentItem().getType());
                stack.setDurability(event.getCurrentItem().getDurability());
                clone.setItem(4, inv.getItem(4));
                player.closeInventory();
                player.openInventory(clone);
                return;
            }

            return;
        }

        if (strippedName.endsWith("Type")){
            if (event.getRawSlot() <= 8){
                return;
            }

            if (event.getRawSlot() > inv.getSize()){
                return;
            }

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || event.getCurrentItem().getItemMeta() == null){
                return;
            }

            if (smallCooldown.get(player.getUniqueId()) != null){
                if (System.currentTimeMillis() - smallCooldown.get(player.getUniqueId()) < 5000){
                    player.sendMessage(ChatColor.RED + "Please try again in a few seconds!");
                    return;
                }
            }

            SkyBlockType type = Main.blockManager.nameType.get(event.getCurrentItem().getItemMeta().getDisplayName());//SkyBlockType.valueOf(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).toUpperCase());
            SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());
            int sbslot = 0;
            if (p.I1 == null){
                sbslot = 1;
            } else if (p.I2 == null){
                sbslot = 2;
            } else if (p.I3 == null){
                sbslot = 3;
            } else if (p.I4 == null){
                sbslot = 4;
            }
			/*
			if (p.getFirst() == null){
				sbslot = 1;
			} else if (p.getSecond() == null){
				sbslot = 2;
			} else if (p.getThird() == null){
				sbslot = 3;
			} else if (p.getFourth() == null){
				sbslot = 4;
			}*/

            if (sbslot == 0){
                player.sendMessage(ChatColor.RED + "Something went wrong, there's no free island for you, please inform an admin!");
                return;
            }

            smallCooldown.put(player.getUniqueId(), System.currentTimeMillis());

            String name = ChatColor.stripColor(inv.getItem(4).getItemMeta().getDisplayName());

            player.sendMessage(ChatColor.GREEN + "Your skyblock " + name + " is being created! Please allow a few seconds of waiting time!");

            String ID = Main.blockManager.createSkyBlock(player, name, type, sbslot, inv.getItem(4).getType(), inv.getItem(4).getDurability());

            if (ID == null || ID.length() <= 3){
                return;
            }
            //player.teleport(Main.blockManager.skyBlocksByID.get(ID).getCenter());

            return;
        }

        if (strippedName.endsWith("Block")){
            if (event.getRawSlot() > inv.getSize()){
                return;
            }

            //String ID = ChatColor.stripColor(inv.getItem(6).getItemMeta().getLore().get(3));
            String ID = Main.guiMain.workingIn.get(player.getUniqueId());
            SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);

            if (event.getRawSlot() == 6){
                player.closeInventory();
                player.openInventory(Main.guiMain.getSettings(ID));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                return;
            }

            if (event.getRawSlot() == 0){
                player.teleport(block.getHome());
                return;
            }

            if (event.getRawSlot() == 13){
                player.closeInventory();
                player.openInventory(Main.guiMain.getMainMenu(player));
                return;
            }

            if (event.getRawSlot() == 2){
                player.closeInventory();
                ArrayList<Player> online = new ArrayList<Player>();
                for (Player pl : Bukkit.getOnlinePlayers()){
                    online.add(pl);
                }

                player.openInventory(Main.guiMain.getInvite(ID, 0, online));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                return;
            }

            if (event.getRawSlot() == 4){
                player.closeInventory();
                player.openInventory(Main.guiMain.getMembers(ID, 1));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                return;
            }

            if (event.getRawSlot() == 8){
                if (Econ.economy.getBalance(player) < me.yungweezy.skyblock.main.Main.deleteCost){
                    player.sendMessage(Messages.getMessage("not_enough_money"));
                    return;
                }

                player.closeInventory();
                player.openInventory(Main.guiMain.getConfirmDelete(ID));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                return;
            }

            return;
        }

        if (strippedName.endsWith("Invite")){
            ItemStack info = inv.getItem(4);
            String ID = Main.guiMain.workingIn.get(player.getUniqueId());
            //String ID = ChatColor.stripColor(info.getItemMeta().getLore().get(3));

            if (event.getRawSlot() == 3 || event.getRawSlot() == 5){
                player.closeInventory();
                player.openInventory(Main.guiMain.getSkyBlockSpecific(ID));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                return;
            }

            if (event.getRawSlot() == 8){
                String pagestring = info.getItemMeta().getLore().get(3);
                pagestring = ChatColor.stripColor(pagestring);
                pagestring = pagestring.replace("Page: ", "");
                int page = Integer.parseInt(pagestring);
                player.closeInventory();
                player.openInventory(Main.guiMain.getInvite(ID, page + 1, Main.guiMain.zOnlineByID.get(ID)));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                return;
            }

            if (event.getRawSlot() == 0 && event.getCurrentItem() != null && event.getCurrentItem().getType() != null && event.getCurrentItem().getType() == Material.ARROW){
                String pagestring = info.getItemMeta().getLore().get(3);
                pagestring = ChatColor.stripColor(pagestring);
                pagestring = pagestring.replace("Page: ", "");
                int page = Integer.parseInt(pagestring);
                player.closeInventory();
                player.openInventory(Main.guiMain.getInvite(ID, page - 1, Main.guiMain.zOnlineByID.get(ID)));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                return;
            }

            if (event.getRawSlot() < 9){
                return;
            }

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || event.getCurrentItem().getItemMeta() == null){
                return;
            }

            String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

            if (Main.debugMode){
                player.sendMessage("[Debug] Trying to invite: " + name);
            }

            if (name.equals(player.getName())){
                player.sendMessage(ChatColor.RED + "Dont be silly!");
                return;
            }

            Player target = Bukkit.getPlayer(name);
            if (target == null || !target.isOnline()){
                player.sendMessage(Messages.getMessage("couldnt_invite_offline"));
                return;
            }

            boolean invited = Main.blockManager.inviteToSkyblock(target, ID);

            if (Main.debugMode){
                player.sendMessage("[Debug] Used method Main.blockManager.inviteToSkyblock(PlayerTarget, ID): " + invited);
            }

            if (invited){
                String invited1 = Messages.getMessage("invited");
                invited1 = invited1.replace("<invited>", target.getName());
                player.sendMessage(invited1);
            } else {
                player.sendMessage(Messages.getMessage("couldnt_invite"));
            }

            return;
        }

        if (strippedName.contains("Members")){
            //ItemStack info = inv.getItem(4);
            String ID = Main.guiMain.workingIn.get(player.getUniqueId());
            //String ID = ChatColor.stripColor(info.getItemMeta().getLore().get(3));

            if (event.getRawSlot() == 3 || event.getRawSlot() == 5){
                player.openInventory(Main.guiMain.getSkyBlockSpecific(ID));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
            }

            if (event.getRawSlot() == 8 && event.getCurrentItem() != null){
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || event.getCurrentItem().getItemMeta() == null){
                    return;
                }

                player.openInventory(Main.guiMain.getMembers(ID, 2));

                Main.guiMain.workingIn.put(player.getUniqueId(), ID);

                return;
            }

            if (event.getRawSlot() == 0 && event.getCurrentItem() != null){
                if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || event.getCurrentItem().getItemMeta() == null){
                    return;
                }

                player.openInventory(Main.guiMain.getMembers(ID, 1));

                Main.guiMain.workingIn.put(player.getUniqueId(), ID);

                return;
            }

            if (event.getRawSlot() < 9 || event.getCurrentItem() == null){
                return;
            }

            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || event.getCurrentItem().getItemMeta() == null){
                return;
            }

            String name = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
            UUID u = UUID.fromString(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(2)));

            if (name.equals(player.getName())){
                player.sendMessage(ChatColor.RED + "Dont be silly!");
                return;
            }

            Player target = Bukkit.getPlayer(name);
            if (target == null){
                player.closeInventory();
                player.openInventory(Main.guiMain.getPlayerInventory(ID, u));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                return;
            }

            player.closeInventory();
            player.openInventory(Main.guiMain.getPlayerInventory(ID, u));
            Main.guiMain.workingIn.put(player.getUniqueId(), ID);

            return;
        }

        if (strippedName.endsWith("Delete")){
            //ItemStack info = inv.getItem(4);
            String ID = Main.guiMain.workingIn.get(player.getUniqueId());
            //String ID = ChatColor.stripColor(info.getItemMeta().getLore().get(3));

            if (event.getRawSlot() > 4){
                player.closeInventory();
                player.openInventory(Main.guiMain.getSkyBlockSpecific(ID));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                return;
            }

            if (event.getRawSlot() < 4){
                Econ.economy.withdrawPlayer(player, me.yungweezy.skyblock.main.Main.deleteCost);
                player.closeInventory();
                player.performCommand("spawn");

                SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
                ArrayList<UUID> members = (ArrayList<UUID>) block.getAllMembers().clone();

                for (UUID u : members){
                    if (Bukkit.getPlayer(u) != null && Bukkit.getPlayer(u).isOnline()){
                        Player lsd = Bukkit.getPlayer(u);
                        lsd.performCommand("spawn");
                    }
                    Main.blockManager.removeFromSkyBlock(Bukkit.getOfflinePlayer(u), ID);
                }

                Main.blockManager.deleteSkyBlock(ID);
            }

            return;
        }

        if (strippedName.contains("ReIcon")){
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == null || event.getCurrentItem().getType() == Material.AIR){
                return;
            }

            String ID = Main.guiMain.workingIn.get(player.getUniqueId());

            if (event.getRawSlot() == 0){
                int currentpage = Integer.parseInt(strippedName.substring(strippedName.length() - 2, strippedName.length()).replace(" ", ""));
                player.closeInventory();
                Inventory fresh = Main.guiMain.getIconInventory(currentpage - 1);
                Inventory clone = Bukkit.createInventory(null, fresh.getSize(), Main.guiMain.menustart + " ReIcon " + (currentpage - 1));
                clone.setContents(fresh.getContents());
                clone.setItem(4, inv.getItem(4));
                player.openInventory(clone);
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                // prev page
            }

            if (event.getRawSlot() == 8){
                int currentpage = Integer.parseInt(strippedName.substring(strippedName.length() - 2, strippedName.length()).replace(" ", ""));
                player.closeInventory();
                Inventory fresh = Main.guiMain.getIconInventory(currentpage + 1);
                Inventory clone = Bukkit.createInventory(null, fresh.getSize(), Main.guiMain.menustart +  " ReIcon " + (currentpage + 1));
                clone.setContents(fresh.getContents());
                clone.setItem(4, inv.getItem(4));
                player.openInventory(clone);
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                // next page
            }

            if (event.getRawSlot() > 9){
                //String ID = ChatColor.stripColor(inv.getItem(4).getItemMeta().getLore().get(0));
                SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
                block.setLogo(event.getCurrentItem().getType());
                block.setSublogo(event.getCurrentItem().getDurability());

                Main.blockManager.skyBlocksByID.put(ID, block);
                Main.blockManager.saveSkyBlock(ID, block);

                player.closeInventory();
                player.openInventory(Main.guiMain.getSkyBlockSpecific(ID));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                return;
            }

            return;
        }

        if (strippedName.endsWith("Player")){
            String ID = Main.guiMain.workingIn.get(player.getUniqueId());
            //String ID = ChatColor.stripColor(inv.getItem(8).getItemMeta().getLore().get(1));

            if (event.getRawSlot() == 8){
                // return to menu
                player.closeInventory();
                player.openInventory(Main.guiMain.getSkyBlockSpecific(ID));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                return;
            }

            UUID u = UUID.fromString(ChatColor.stripColor(inv.getItem(8).getItemMeta().getLore().get(0)));

            if (SkyplayerManager.SkyPlayers.get(u) == null){
                SkyplayerManager.loadForPlayer(Bukkit.getOfflinePlayer(u));
            }

            SkyPlayer p = SkyplayerManager.SkyPlayers.get(u);

            if (event.getRawSlot() == 0){
                // INTERACT
                if (event.getCurrentItem().getDurability() == 8){
                    p.addPerm(ID, "INTERACT");
                } else {
                    p.removePerm(ID, "INTERACT");
                }
            }

            if (event.getRawSlot() == 1){
                // BREAK
                if (event.getCurrentItem().getDurability() == 8){
                    p.addPerm(ID, "BREAK");
                } else {
                    p.removePerm(ID, "BREAK");
                }
            }

            if (event.getRawSlot() == 2){
                // PLACE
                if (event.getCurrentItem().getDurability() == 8){
                    p.addPerm(ID, "PLACE");
                } else {
                    p.removePerm(ID, "PLACE");
                }
            }

            if (event.getRawSlot() == 3){
                // MOBS
                if (event.getCurrentItem().getDurability() == 8){
                    p.addPerm(ID, "MOBS");
                } else {
                    p.removePerm(ID, "MOBS");
                }
            }

            if (event.getRawSlot() == 4){
                // PICKUP
                if (event.getCurrentItem().getDurability() == 8){
                    p.addPerm(ID, "PICKUP");
                } else {
                    p.removePerm(ID, "PICKUP");
                }
            }

            if (event.getRawSlot() == 6){
                // KICK PLAYER FROM SKYBLOCK
                Main.blockManager.removeFromSkyBlock(Bukkit.getOfflinePlayer(u), ID);
                player.closeInventory();
                player.openInventory(Main.guiMain.getMembers(ID, 1));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                SkyplayerManager.SkyPlayers.put(u, p);

                if (Bukkit.getPlayer(u) == null){
                    SkyplayerManager.saveForPlayer(Bukkit.getOfflinePlayer(u));
                }

                if (Bukkit.getPlayer(u) != null && Bukkit.getPlayer(u).isOnline()){
                    SkyplayerManager.saveForPlayer(Bukkit.getPlayer(u));
                }

                return;
            }

            SkyplayerManager.SkyPlayers.put(u, p);
            if (Bukkit.getPlayer(u) == null){
                SkyplayerManager.saveForPlayer(Bukkit.getOfflinePlayer(u));
            }

            if (Bukkit.getPlayer(u) != null && Bukkit.getPlayer(u).isOnline()){
                SkyplayerManager.saveForPlayer(Bukkit.getPlayer(u));
            }

            player.closeInventory();
            player.openInventory(Main.guiMain.getPlayerInventory(ID, u));
            Main.guiMain.workingIn.put(player.getUniqueId(), ID);

            return;
        }

        if (strippedName.endsWith("Settings")){
            String ID = Main.guiMain.workingIn.get(player.getUniqueId());
            //String ID = ChatColor.stripColor(inv.getItem(8).getItemMeta().getLore().get(2));

            if (event.getRawSlot() == 22){
                player.closeInventory();
                player.openInventory(Main.guiMain.getSkyBlockSpecific(ID));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                return;
            }

            SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);

            if (event.getRawSlot() == 15){
                // set main island
                UUID pl = block.getOwner();
                SkyPlayer p = SkyplayerManager.SkyPlayers.get(pl);
                if (p.getMain() != null && p.getMain().equals(ID)){
                    player.sendMessage(ChatColor.RED + "This island is already set as your main island!");
                    return;
                }

                p.setMain(ID);
                SkyplayerManager.SkyPlayers.put(pl, p);
                player.sendMessage(ChatColor.GREEN + "Set new main island!");
            } else if (event.getRawSlot() == 14){
                // set home
                if (player.hasPermission("skyblock.sethome")){
                    Location translated = SBUtils.getCorrespondingCentLoc(player.getLocation());
                    String transID = Main.blockManager.skyBlocksByCenter.get(translated);

                    if (!ID.equals(transID)){
                        player.sendMessage(ChatColor.RED + "You cant set your skyblock's home here!");
                        return;
                    }

                    block.setHome(player.getLocation());
                    Main.blockManager.skyBlocksByID.put(ID, block);
                    player.sendMessage(Messages.getMessage("home_set"));
                    Main.blockManager.saveSkyBlock(ID, block);
                } else {
                    player.sendMessage(Messages.getMessage("feature_no_perm"));
                }
                return;
            } else if (event.getRawSlot() == 13){
                // change name
                if (player.hasPermission("skyblock.changename")){
                    if (Econ.economy.getBalance(player) < me.yungweezy.skyblock.main.Main.renameCost){
                        player.sendMessage(Messages.getMessage("not_enough_money"));
                        return;
                    }

                    Econ.economy.withdrawPlayer(player, me.yungweezy.skyblock.main.Main.renameCost);

                    Main.nameModule.openReNameInv(player, ID);

                    Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                    return;
                } else {
                    player.sendMessage(Messages.getMessage("feature_no_perm"));
                }
            } else if (event.getRawSlot() == 12){
                // change icon
                if (player.hasPermission("skyblock.changeicon")){
                    if (Econ.economy.getBalance(player) < me.yungweezy.skyblock.main.Main.iconChangeCost){
                        player.sendMessage(Messages.getMessage("not_enough_money"));
                        return;
                    }

                    Econ.economy.withdrawPlayer(player, me.yungweezy.skyblock.main.Main.iconChangeCost);

                    Inventory icon = Main.guiMain.getIconInventory(1);
                    Inventory reicon = Bukkit.createInventory(null, icon.getSize(), Main.guiMain.menustart + " ReIcon 1");
                    reicon.setContents(icon.getContents());
                    ArrayList<String> blockiteml = new ArrayList<String>();
                    blockiteml.add(ChatColor.GRAY + ID);
                    ItemStack blockitem = CreateItem.createItem(block.getLogo(), block.getSublogo(), ChatColor.GOLD + block.getName(), blockiteml);

                    reicon.setItem(4, blockitem);

                    player.closeInventory();
                    player.openInventory(reicon);
                    Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                    return;
                } else {
                    player.sendMessage(Messages.getMessage("feature_no_perm"));
                }
                return;
            } else if (event.getRawSlot() == 5){
                // Toggle Explosions
                if (player.hasPermission("skyblock.explosions")){
                    if (block.doExplode() == true){
                        block.setExplosions(false);
                        player.sendMessage(ChatColor.GREEN + "Explosions will no longer do damage to your island!");
                    } else {
                        block.setExplosions(true);
                        player.sendMessage(ChatColor.RED + "Explosions will now do damage to your island again!");
                    }
                    Main.blockManager.skyBlocksByID.put(ID, block);
                    Main.blockManager.saveSkyBlock(ID, block);
                } else {
                    player.sendMessage(Messages.getMessage("feature_no_perm"));
                    return;
                }
            } else if (event.getRawSlot() == 4){
                // Toggle MobSpawning
                if (player.hasPermission("skyblock.mobspawning")){
                    if (block.spawnMobs() == true){
                        block.setMobSpawning(false);
                        player.sendMessage(ChatColor.GREEN + "Mobs will no longer spawn on your island");
                    } else {
                        block.setMobSpawning(true);
                        player.sendMessage(ChatColor.RED + "Mobs will now spawn on your island again");
                    }
                    Main.blockManager.skyBlocksByID.put(ID, block);
                    Main.blockManager.saveSkyBlock(ID, block);
                } else {
                    player.sendMessage(Messages.getMessage("feature_no_perm"));
                    return;
                }
            } else if (event.getRawSlot() == 3){
                // Toggle Lock
                if (player.hasPermission("skyblock.togglelock")){
                    if (block.isLocked() == true){
                        block.setLock(false);
                        player.sendMessage(ChatColor.RED + "Everyone can enter your island now!");
                    } else {
                        block.setLock(true);
                        player.sendMessage(ChatColor.RED + "Players can no longer enter your islands protected region!");
                    }
                    Main.blockManager.skyBlocksByID.put(ID, block);
                    Main.blockManager.saveSkyBlock(ID, block);
                } else {
                    player.sendMessage(Messages.getMessage("feature_no_perm"));
                    return;
                }
            } else if (event.getRawSlot() == 2){
                // Toggle PvP
                if (player.hasPermission("skyblock.pvp")){
                    if (block.pvpEnabled() == true){
                        block.setPvp(false);
                        player.sendMessage(ChatColor.GREEN + "You can no longer PvP on your island!");
                    } else {
                        block.setPvp(true);
                        player.sendMessage(ChatColor.RED + "PvP is now enabled on your island!");
                    }
                    Main.blockManager.skyBlocksByID.put(ID, block);
                    Main.blockManager.saveSkyBlock(ID, block);
                } else {
                    player.sendMessage(Messages.getMessage("feature_no_perm"));
                    return;
                }
            } else if (event.getRawSlot() == 11){
                // Clear mobs
                player.closeInventory();
                player.openInventory(Main.guiMain.chooseMobTypeButcher());
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
                return;
            } else if (event.getRawSlot() == 6){
                player.sendMessage(ChatColor.GOLD + "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                player.sendMessage(ChatColor.BLUE + "Start debug information!");
                player.sendMessage(ChatColor.GOLD + "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                player.sendMessage(ChatColor.GRAY + "PN: " + player.getName());
                player.sendMessage(ChatColor.GRAY + "PU: " + player.getUniqueId().toString());
                player.sendMessage(ChatColor.GRAY + "TS: " + System.currentTimeMillis());
                player.sendMessage(ChatColor.GRAY + "SBN: " + block.getName());
                player.sendMessage(ChatColor.GRAY + "SBU: " + block.getID());
                player.sendMessage(ChatColor.GRAY + "SBR: " + block.getMaxMembers() + "|" + block.getAllMembers().size() + "|" + block.getLevel());
                player.sendMessage(ChatColor.GRAY + "SBS: L:" + block.isLocked() + " E:" +block.doExplode() + " P:" + block.pvpEnabled() + " M:" + block.spawnMobs());
                player.sendMessage(ChatColor.GOLD + "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                player.sendMessage(ChatColor.BLUE + "End debug information!");
                player.sendMessage(ChatColor.GOLD + "=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                return;
            }

            Main.blockManager.saveSkyBlock(ID, block);
            player.closeInventory();
            player.openInventory(Main.guiMain.getSettings(ID));
            Main.guiMain.workingIn.put(player.getUniqueId(), ID);

            return;
        }

        if (strippedName.endsWith("Butcher")){
            String ID = Main.guiMain.workingIn.get(player.getUniqueId());

            if (event.getRawSlot() == 4){
                player.closeInventory();
                player.openInventory(Main.guiMain.getSettings(ID));
                Main.guiMain.workingIn.put(player.getUniqueId(), ID);
            }

            SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);

            if (event.getRawSlot() == 2){
                if (player.hasPermission("skyblock.clearmobs.hostile")){
                    player.sendMessage(ChatColor.GREEN + "Killed all hostile mobs on your island!");

                    ArmorStand as = block.getCenter().getWorld().spawn(block.getCenter(), ArmorStand.class);
                    as.setVisible(false);
                    as.setGravity(false);

                    ArrayList<EntityType> mobs = new ArrayList<EntityType>();
                    mobs.add(EntityType.BAT);
                    mobs.add(EntityType.CREEPER);
                    mobs.add(EntityType.SKELETON);
                    mobs.add(EntityType.GHAST);
                    mobs.add(EntityType.PIG_ZOMBIE);
                    mobs.add(EntityType.ZOMBIE);
                    mobs.add(EntityType.CAVE_SPIDER);
                    mobs.add(EntityType.SPIDER);
                    mobs.add(EntityType.ENDER_DRAGON);
                    mobs.add(EntityType.MAGMA_CUBE);
                    mobs.add(EntityType.SLIME);
                    mobs.add(EntityType.WITCH);
                    mobs.add(EntityType.ENDERMAN);

                    for (Entity e : as.getNearbyEntities(200, 200, 200)){
                        if (mobs.contains(e.getType())){
                            e.remove();
                        }
                    }

                    as.remove();
                } else {
                    player.sendMessage(Messages.getMessage("feature_no_perm"));
                    return;
                }
            } else if (event.getRawSlot() == 6){
                if (player.hasPermission("skyblock.clearmobs.passive")){
                    player.sendMessage(ChatColor.GREEN + "Killed all passive mobs on your island!");

                    ArmorStand as = block.getCenter().getWorld().spawn(block.getCenter(), ArmorStand.class);
                    as.setVisible(false);
                    as.setGravity(false);

                    ArrayList<EntityType> mobs = new ArrayList<EntityType>();
                    mobs.add(EntityType.BOAT);
                    mobs.add(EntityType.COW);
                    mobs.add(EntityType.CHICKEN);
                    mobs.add(EntityType.MUSHROOM_COW);
                    mobs.add(EntityType.OCELOT);
                    mobs.add(EntityType.PIG);
                    mobs.add(EntityType.SHEEP);
                    mobs.add(EntityType.SQUID);
                    mobs.add(EntityType.SNOWMAN);
                    mobs.add(EntityType.VILLAGER);
                    mobs.add(EntityType.WOLF);

                    for (Entity e : as.getNearbyEntities(200, 200, 200)){
                        if (mobs.contains(e.getType())){
                            e.remove();
                        }
                    }

                    as.remove();
                } else {
                    player.sendMessage(Messages.getMessage("feature_no_perm"));
                    return;
                }
            }

            return;
        }
    }
}