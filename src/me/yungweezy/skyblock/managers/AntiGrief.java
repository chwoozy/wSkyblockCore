package me.yungweezy.skyblock.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import me.yungweezy.skyblock.main.Main;
import me.yungweezy.skyblock.misc.IslandLevels;
import me.yungweezy.skyblock.misc.Messages;

public class AntiGrief implements Listener {

    private ArrayList<Material> blacklisted = new ArrayList<Material>();

    @EventHandler
    public void entityDamage(EntityDamageByEntityEvent event){
        if (!(event.getDamager() instanceof Player)){
            return;
        }

        Player player = (Player) event.getDamager();
        if (SkyplayerManager.SkyPlayers.get(player.getUniqueId()) == null){
            event.setCancelled(false);
            return;
        }

        if (!player.getLocation().getWorld().getName().equals("skyworld")){
            return;
        }

        Location loc = player.getLocation();
        Location centloc = SBUtils.getCorrespondingCentLoc(loc);
        String ID = Main.blockManager.skyBlocksByCenter.get(centloc);
        SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());

        if (SkyplayerManager.CanBuildAt.get(player.getUniqueId()) == null || SkyplayerManager.CanBuildAt.get(player.getUniqueId()).isEmpty()){
            if (!player.hasPermission("skyblock.protection.bypass")){
                event.setCancelled(true);
                player.sendMessage(Messages.getMessage("cant_damage_entities"));
            } else if (p.hasRemovedPerm(ID, "MOBS")){
                player.sendMessage(Messages.getMessage("cant_damage_entities"));
                event.setCancelled(true);
            }
            return;
        }

        for (Location l : SkyplayerManager.CanBuildAt.get(player.getUniqueId())){
            if (centloc == l || (centloc.getWorld().getName().equals(l.getWorld().getName()) && centloc.distance(l) <= 1)){
                if (p.hasRemovedPerm(ID, "MOBS")){
                    player.sendMessage(Messages.getMessage("cant_damage_entities"));
                    event.setCancelled(true);
                }

                return;
            }
        }

        if (!player.hasPermission("skyblock.protection.bypass")){
            event.setCancelled(true);
            player.sendMessage(Messages.getMessage("cant_damage_entities"));
        }
        return;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        Player player = (Player) event.getPlayer();

        if (!player.getLocation().getWorld().getName().equals("skyworld")){
            return;
        }

        if (SkyplayerManager.SkyPlayers.get(player.getUniqueId()) == null){
            event.setCancelled(false);
            return;
        }

        Location loc = player.getLocation();
        Location centloc = SBUtils.getCorrespondingCentLoc(loc);

        if (SkyplayerManager.CanBuildAt.get(player.getUniqueId()) == null || SkyplayerManager.CanBuildAt.get(player.getUniqueId()).isEmpty()){
            if (!player.hasPermission("skyblock.protection.bypass")){
                event.setCancelled(true);
                player.sendMessage(Messages.getMessage("cant_drop_items"));
            }

            return;
        }

        for (Location l : SkyplayerManager.CanBuildAt.get(player.getUniqueId())){
            if (centloc == l || (centloc.getWorld().getName().equals(l.getWorld().getName()) && centloc.distance(l) <= 1)){
                return;
            }
        }

        if (!player.hasPermission("skyblock.protection.bypass")){
            event.setCancelled(true);
            player.sendMessage(Messages.getMessage("cant_drop_items"));
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event){
        Player player = (Player) event.getPlayer();
        if (SkyplayerManager.SkyPlayers.get(player.getUniqueId()) == null){
            event.setCancelled(false);
            return;
        }

        if (!player.getLocation().getWorld().getName().equals("skyworld")){
            return;
        }

        Location loc = player.getLocation();
        Location centloc = SBUtils.getCorrespondingCentLoc(loc);
        String ID = Main.blockManager.skyBlocksByCenter.get(centloc);
        SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());

        if (SkyplayerManager.CanBuildAt.get(player.getUniqueId()) == null || SkyplayerManager.CanBuildAt.get(player.getUniqueId()).isEmpty()){
            if (!player.hasPermission("skyblock.protection.bypass")){
                event.setCancelled(true);
                player.sendMessage(Messages.getMessage("cant_pickup_item"));
                Vector v = SBUtils.calculateVelocityPush(event.getItem(), player);
                player.setVelocity(v);
                return;
            } else if (p.hasRemovedPerm(ID, "PICKUP")){
                event.setCancelled(true);
                player.sendMessage(Messages.getMessage("cant_pickup_item"));
                Vector v = SBUtils.calculateVelocityPush(event.getItem(), player);
                player.setVelocity(v);
            }
        }

        for (Location l : SkyplayerManager.CanBuildAt.get(player.getUniqueId())){
            if (centloc == l || (centloc.getWorld().getName().equals(l.getWorld().getName()) && centloc.distance(l) <= 1)){
                if (p.hasRemovedPerm(ID, "PICKUP")){
                    event.setCancelled(true);
                    player.sendMessage(Messages.getMessage("cant_pickup_item"));
                    Vector v = SBUtils.calculateVelocityPush(event.getItem(), player);
                    player.setVelocity(v);
                }
                return;
            }
        }

        if (!player.hasPermission("skyblock.protection.bypass")){
            event.setCancelled(true);
            player.sendMessage(Messages.getMessage("cant_pickup_item"));
            Vector v = SBUtils.calculateVelocityPush(event.getItem(), player);
            player.setVelocity(v);
            return;
        }
    }

    @EventHandler
    public void onInteractWithEntity(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        if (SkyplayerManager.SkyPlayers.get(player.getUniqueId()) == null){
            event.setCancelled(false);
            return;
        }

        if (!player.getLocation().getWorld().getName().equals("skyworld")){
            return;
        }

        Location loc = player.getLocation();
        Location centloc = SBUtils.getCorrespondingCentLoc(loc);
        String ID = Main.blockManager.skyBlocksByCenter.get(centloc);
        SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());

        if (SkyplayerManager.CanBuildAt.get(player.getUniqueId()) == null || SkyplayerManager.CanBuildAt.get(player.getUniqueId()).isEmpty()){
            if (!player.hasPermission("skyblock.protection.bypass")){
                event.setCancelled(true);
                player.sendMessage(Messages.getMessage("cant_interact_block"));
                return;
            } else if (p.hasRemovedPerm(ID, "MOBS")){
                event.setCancelled(true);
                player.sendMessage(Messages.getMessage("cant_interact_block"));
            }
        }

        for (Location l : SkyplayerManager.CanBuildAt.get(player.getUniqueId())){
            if (centloc == l || (centloc.getWorld().getName().equals(l.getWorld().getName()) && centloc.distance(l) <= 1)){
                if (p.hasRemovedPerm(ID, "MOBS")){
                    event.setCancelled(true);
                    player.sendMessage(Messages.getMessage("cant_interact_block"));
                }
                return;
            }
        }

        if (!player.hasPermission("skyblock.protection.bypass")){
            event.setCancelled(true);
            player.sendMessage(Messages.getMessage("cant_interact_block"));
            return;
        }
    }

    @EventHandler
    public void onInteractWithArmorstand(PlayerArmorStandManipulateEvent event){
        Player player = event.getPlayer();
        if (SkyplayerManager.SkyPlayers.get(player.getUniqueId()) == null){
            event.setCancelled(false);
            return;
        }

        if (!player.getLocation().getWorld().getName().equals("skyworld")){
            return;
        }

        Location loc = player.getLocation();
        Location centloc = SBUtils.getCorrespondingCentLoc(loc);
        String ID = Main.blockManager.skyBlocksByCenter.get(centloc);
        SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());

        if (SkyplayerManager.CanBuildAt.get(player.getUniqueId()) == null || SkyplayerManager.CanBuildAt.get(player.getUniqueId()).isEmpty()){
            if (!player.hasPermission("skyblock.protection.bypass")){
                event.setCancelled(true);
                player.sendMessage(Messages.getMessage("cant_interact_block"));
                return;
            } else if (p.hasRemovedPerm(ID, "MOBS")){
                event.setCancelled(true);
                player.sendMessage(Messages.getMessage("cant_interact_block"));
            }
        }

        for (Location l : SkyplayerManager.CanBuildAt.get(player.getUniqueId())){
            if (centloc == l || (centloc.getWorld().getName().equals(l.getWorld().getName()) && centloc.distance(l) <= 1)){
                if (p.hasRemovedPerm(ID, "MOBS")){
                    event.setCancelled(true);
                    player.sendMessage(Messages.getMessage("cant_interact_block"));
                }
                return;
            }
        }

        if (!player.hasPermission("skyblock.protection.bypass")){
            event.setCancelled(true);
            player.sendMessage(Messages.getMessage("cant_interact_block"));
            return;
        }
    }

    @EventHandler
    public void onProjectile(ProjectileLaunchEvent event){
        if (event.getEntity().getShooter() == null || !(event.getEntity().getShooter() instanceof Player)){
            return;
        }

        Player shooter = (Player) event.getEntity().getShooter();
        Location loc = event.getEntity().getLocation();
        Location centloc = SBUtils.getCorrespondingCentLoc(loc);
        String ID = Main.blockManager.skyBlocksByCenter.get(centloc);
        SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
        if (block.isMember(shooter)){
            return;
        }

        event.setCancelled(true);
        event.getEntity().remove();
    }

    public HashMap<UUID, Long> lastSpam = new HashMap<UUID, Long>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        if (event.getAction() == Action.PHYSICAL){
            Player player = event.getPlayer();
            Location loc = player.getLocation();
            Location centloc = SBUtils.getCorrespondingCentLoc(loc);
            String ID = Main.blockManager.skyBlocksByCenter.get(centloc);
            SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);

            if (block == null){
                return;
            }

            if (block.isMember(player)){
                return;
            }

            event.setCancelled(true);

            if (lastSpam.get(player.getUniqueId()) == null || System.currentTimeMillis() - lastSpam.get(player.getUniqueId()) > 1000){
                player.sendMessage(Messages.getMessage("cant_interact_block"));
                lastSpam.put(player.getUniqueId(), System.currentTimeMillis());
            }
            return;
        }

        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (!player.getLocation().getWorld().getName().equals("skyworld")){
            return;
        }

        if (hand != null && hand.getType().toString().contains("BUCKET")){
            Location loc = player.getLocation();
            Location centloc = SBUtils.getCorrespondingCentLoc(loc);
            String ID = Main.blockManager.skyBlocksByCenter.get(centloc);
            SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());

            if (SkyplayerManager.CanBuildAt.get(player.getUniqueId()) == null || SkyplayerManager.CanBuildAt.get(player.getUniqueId()).isEmpty()){
                if (!player.hasPermission("skyblock.protection.bypass")){
                    event.setCancelled(true);
                    player.sendMessage(Messages.getMessage("cant_interact_block"));
                    return;
                } else if (p.hasRemovedPerm(ID, "INTERACT")){
                    event.setCancelled(true);
                    player.sendMessage(Messages.getMessage("cant_interact_block"));
                    return;
                }
            }
        }

        if (blacklisted.isEmpty()){
            blacklisted.add(Material.CHEST);
            blacklisted.add(Material.TRAPPED_CHEST);
            blacklisted.add(Material.TRAP_DOOR);
            blacklisted.add(Material.STONE_BUTTON);
            blacklisted.add(Material.WOOD_BUTTON);
            blacklisted.add(Material.WOODEN_DOOR);
            blacklisted.add(Material.IRON_DOOR);
            blacklisted.add(Material.ANVIL);
            blacklisted.add(Material.BED);
            blacklisted.add(Material.BOAT);
            blacklisted.add(Material.DROPPER);
            blacklisted.add(Material.FURNACE);
            blacklisted.add(Material.BEACON);
            blacklisted.add(Material.BREWING_STAND);
            blacklisted.add(Material.BURNING_FURNACE);
            blacklisted.add(Material.DAYLIGHT_DETECTOR);
            blacklisted.add(Material.DISPENSER);
            blacklisted.add(Material.DIODE_BLOCK_ON);
            blacklisted.add(Material.DIODE_BLOCK_OFF);
            blacklisted.add(Material.FENCE_GATE);
            blacklisted.add(Material.HOPPER);
            blacklisted.add(Material.ITEM_FRAME);
            blacklisted.add(Material.LEVER);
            blacklisted.add(Material.PAINTING);
            blacklisted.add(Material.BUCKET);
            blacklisted.add(Material.LAVA_BUCKET);
            blacklisted.add(Material.WATER_BUCKET);
            blacklisted.add(Material.BOAT);
            blacklisted.add(Material.MINECART);
            blacklisted.add(Material.EXPLOSIVE_MINECART);
            blacklisted.add(Material.HOPPER_MINECART);
        }

        if (blacklisted.contains(event.getPlayer().getInventory().getItemInMainHand().getType())){
            if (SkyplayerManager.SkyPlayers.get(player.getUniqueId()) == null){
                event.setCancelled(false);
                return;
            }

            Location loc = player.getLocation();
            Location centloc = SBUtils.getCorrespondingCentLoc(loc);
            String ID = Main.blockManager.skyBlocksByCenter.get(centloc);
            SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());

            if (SkyplayerManager.CanBuildAt.get(player.getUniqueId()) == null || SkyplayerManager.CanBuildAt.get(player.getUniqueId()).isEmpty()){
                if (!player.hasPermission("skyblock.protection.bypass")){
                    event.setCancelled(true);
                    player.sendMessage(Messages.getMessage("cant_interact_block"));
                    return;
                } else if (p.hasRemovedPerm(ID, "INTERACT")){
                    event.setCancelled(true);
                    player.sendMessage(Messages.getMessage("cant_interact_block"));
                    return;
                }
            }

            for (Location l : SkyplayerManager.CanBuildAt.get(player.getUniqueId())){
                if (centloc == l || (centloc.getWorld().getName().equals(l.getWorld().getName()) && centloc.distance(l) <= 1)){
                    if (p.hasRemovedPerm(ID, "INTERACT")){
                        event.setCancelled(true);
                        player.sendMessage(Messages.getMessage("cant_interact_block"));
                        return;
                    }
                    return;
                }
            }

            if (!player.hasPermission("skyblock.protection.bypass")){
                event.setCancelled(true);
                player.sendMessage(Messages.getMessage("cant_interact_block"));
                return;
            }
        }

        if (event.getClickedBlock() == null || event.getClickedBlock().getType() == null){
            return;
        }

        if (blacklisted.contains(event.getClickedBlock().getType()) || blacklisted.contains(event.getMaterial()) || blacklisted.contains(event.getPlayer().getInventory().getItemInMainHand().getType())){
            if (SkyplayerManager.SkyPlayers.get(player.getUniqueId()) == null){
                event.setCancelled(false);
                return;
            }

            Location loc = player.getLocation();
            Location centloc = SBUtils.getCorrespondingCentLoc(loc);
            String ID = Main.blockManager.skyBlocksByCenter.get(centloc);
            SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());

            if (SkyplayerManager.CanBuildAt.get(player.getUniqueId()) == null || SkyplayerManager.CanBuildAt.get(player.getUniqueId()).isEmpty()){
                if (!player.hasPermission("skyblock.protection.bypass")){
                    event.setCancelled(true);
                    player.sendMessage(Messages.getMessage("cant_interact_block"));
                    return;
                } else if (p.hasRemovedPerm(ID, "INTERACT")){
                    event.setCancelled(true);
                    player.sendMessage(Messages.getMessage("cant_interact_block"));
                    return;
                }
            }

            for (Location l : SkyplayerManager.CanBuildAt.get(player.getUniqueId())){
                if (centloc == l || (centloc.getWorld().getName().equals(l.getWorld().getName()) && centloc.distance(l) <= 1)){
                    if (p.hasRemovedPerm(ID, "INTERACT")){
                        event.setCancelled(true);
                        player.sendMessage(Messages.getMessage("cant_interact_block"));
                        return;
                    }
                    return;
                }
            }

            if (!player.hasPermission("skyblock.protection.bypass")){
                event.setCancelled(true);
                player.sendMessage(Messages.getMessage("cant_interact_block"));
                return;
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onBuild(BlockPlaceEvent event){
        Player player = event.getPlayer();

        if (!player.getLocation().getWorld().getName().equals("skyworld")){
            return;
        }

        if (SkyplayerManager.SkyPlayers.get(player.getUniqueId()) == null){
            event.setCancelled(false);
            return;
        }

        Location loc = player.getLocation();
        Location centloc = SBUtils.getCorrespondingCentLoc(loc);
        String ID = Main.blockManager.skyBlocksByCenter.get(centloc);
        SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());

        if (SkyplayerManager.CanBuildAt.get(player.getUniqueId()) == null || SkyplayerManager.CanBuildAt.get(player.getUniqueId()).isEmpty()){
            if (!player.hasPermission("skyblock.protection.bypass")){
                event.setCancelled(true);
                player.sendMessage(Messages.getMessage("cant_build"));
                return;
            } else if (p.hasRemovedPerm(ID, "PLACE")){
                event.setCancelled(true);
                player.sendMessage(Messages.getMessage("cant_build"));
                return;
            }
        }

        for (Location l : SkyplayerManager.CanBuildAt.get(player.getUniqueId())){
            if (centloc == l || (centloc.getWorld().getName().equals(l.getWorld().getName()) && centloc.distance(l) <= 1)){
                if (p.hasRemovedPerm(ID, "PLACE")){
                    event.setCancelled(true);
                    player.sendMessage(Messages.getMessage("cant_build"));
                    return;
                }

                double level = IslandLevels.getLevel(new ItemStack(event.getBlock().getType(), event.getBlock().getData()));

                if (level == 0){
                    return;
                }

                if (ID == null){
                    return;
                }

                SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
                block.addToLevel(level);
                Main.blockManager.skyBlocksByID.put(ID, block);
                if (!Main.blockManager.shouldGetSaved.contains(ID)){
                    Main.blockManager.shouldGetSaved.add(ID);
                }
                return;
            }
        }

        if (!player.hasPermission("skyblock.protection.bypass")){
            event.setCancelled(true);
            player.sendMessage(Messages.getMessage("cant_build"));
            return;
        }

        double level = IslandLevels.getLevel(new ItemStack(event.getBlock().getType(), event.getBlock().getData()));

        if (level == 0){
            return;
        }

        if (ID == null){
            return;
        }

        SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
        block.addToLevel(level);
        Main.blockManager.skyBlocksByID.put(ID, block);
        if (!Main.blockManager.shouldGetSaved.contains(ID)){
            Main.blockManager.shouldGetSaved.add(ID);
        }
        // fine with building, add to level
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        if (SkyplayerManager.SkyPlayers.get(player.getUniqueId()) == null){
            event.setCancelled(false);
            return;
        }

        if (!player.getLocation().getWorld().getName().equals("skyworld")){
            return;
        }

        Location loc = player.getLocation();
        Location centloc = SBUtils.getCorrespondingCentLoc(loc);
        String ID = Main.blockManager.skyBlocksByCenter.get(centloc);
        SkyPlayer p = SkyplayerManager.SkyPlayers.get(player.getUniqueId());

        if (SkyplayerManager.CanBuildAt.get(player.getUniqueId()) == null || SkyplayerManager.CanBuildAt.get(player.getUniqueId()).isEmpty()){
            if (!player.hasPermission("skyblock.protection.bypass")){
                event.setCancelled(true);
                player.sendMessage(Messages.getMessage("cant_build"));
                return;
            } else if (p.hasRemovedPerm(ID, "BREAK")){
                event.setCancelled(true);
                player.sendMessage(Messages.getMessage("cant_build"));
                return;
            }
        }

        for (Location l : SkyplayerManager.CanBuildAt.get(player.getUniqueId())){
            if (centloc == l || (centloc.getWorld().getName().equals(l.getWorld().getName()) && centloc.distance(l) <= 1)){
                if (p.hasRemovedPerm(ID, "BREAK")){
                    event.setCancelled(true);
                    player.sendMessage(Messages.getMessage("cant_build"));
                    return;
                }

                double level = IslandLevels.getLevel(new ItemStack(event.getBlock().getType(), event.getBlock().getData()));

                if (level == 0){
                    return;
                }

                if (ID == null){
                    return;
                }

                SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
                block.removeFromLevel(level);
                Main.blockManager.skyBlocksByID.put(ID, block);
                if (!Main.blockManager.shouldGetSaved.contains(ID)){
                    Main.blockManager.shouldGetSaved.add(ID);
                }
                return;
            }
        }

        if (!player.hasPermission("skyblock.protection.bypass")){
            event.setCancelled(true);
            player.sendMessage(Messages.getMessage("cant_build"));
            return;
        }


        double level = IslandLevels.getLevel(new ItemStack(event.getBlock().getType(), event.getBlock().getData()));

        if (level == 0){
            return;
        }

        if (ID == null){
            return;
        }

        SkyBlock block = Main.blockManager.skyBlocksByID.get(ID);
        block.removeFromLevel(level);
        Main.blockManager.skyBlocksByID.put(ID, block);
        if (!Main.blockManager.shouldGetSaved.contains(ID)){
            Main.blockManager.shouldGetSaved.add(ID);
        }
        // fine with breaking, remove from level
    }

    @EventHandler
    public void onAggro(EntityTargetEvent event){
        if (!(event.getTarget() instanceof Player)){
            return;
        }

        Player player = (Player) event.getTarget();

        if (!player.getLocation().getWorld().getName().equals("skyworld")){
            return;
        }

        Location loc = event.getEntity().getLocation();
        Location centloc = SBUtils.getCorrespondingCentLoc(loc);

        if (SkyplayerManager.CanBuildAt.get(player.getUniqueId()) == null || SkyplayerManager.CanBuildAt.get(player.getUniqueId()).isEmpty()){
            if (!player.hasPermission("skyblock.protection.bypass")){
                event.setCancelled(true);
                return;
            }
        }

        for (Location l : SkyplayerManager.CanBuildAt.get(player.getUniqueId())){
            if (centloc == l || (centloc.getWorld().getName().equals(l.getWorld().getName()) && centloc.distance(l) <= 1)){
                return;
            }
        }

        if (!player.hasPermission("skyblock.protection.bypass")){
            event.setCancelled(true);
            return;
        }
    }
}
