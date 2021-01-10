package me.yungweezy.skyblock.main;

import me.yungweezy.skyblock.gui.GUIEvents;
import me.yungweezy.skyblock.gui.GUIMain;
import me.yungweezy.skyblock.gui.LetterManager;
import me.yungweezy.skyblock.gui.NameModule;
import me.yungweezy.skyblock.managers.AntiGrief;
import me.yungweezy.skyblock.managers.CommandHandler;
import me.yungweezy.skyblock.managers.SkyblockManager;
import me.yungweezy.skyblock.managers.SkyblockSettings;
import me.yungweezy.skyblock.managers.SkyplayerManager;
import me.yungweezy.skyblock.managers.Top;
import me.yungweezy.skyblock.misc.PlayerMySQL;
import me.yungweezy.skyblock.misc.SkyBlockMySQL;
import me.yungweezy.skyblock.misc.Hunger;
import me.yungweezy.skyblock.misc.IslandLevels;
import me.yungweezy.skyblock.misc.Messages;
import me.yungweezy.skyblock.misc.NoLoreAnvil;
import me.yungweezy.skyblock.misc.SunCommand;
import me.yungweezy.skyblock.misc.WarpStuff;
import me.yungweezy.skyblock.shop.SellEvents;
import me.yungweezy.skyblock.shop.ShopGUI;
import me.yungweezy.skyblock.shop.ShopMain;
import me.yungweezy.skyblock.worldgen.SkyWorldCreator;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static int deleteCost = 0;
    public static int renameCost = 0;
    public static int iconChangeCost = 0;

    public static boolean debugMode = false;

    public static Plugin pl;
    public static GUIEvents guiEvents;
    public static GUIMain guiMain;
    public static LetterManager letterManager;
    public static NameModule nameModule;

    public static SkyblockManager blockManager;

    public void onEnable(){
        pl = this;

        guiEvents = new GUIEvents();
        guiMain = new GUIMain();
        letterManager = new LetterManager();
        nameModule = new NameModule();

        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new SkyplayerManager(), this);
        pm.registerEvents(guiEvents, this);
        pm.registerEvents(new AntiGrief(), this);
        pm.registerEvents(new SkyblockSettings(), this);
        pm.registerEvents(new SunCommand(), this);
        pm.registerEvents(new SellEvents(), this);
        pm.registerEvents(new WarpStuff(), this);
        pm.registerEvents(new Hunger(), this);
        pm.registerEvents(new NoLoreAnvil(), this);
        pm.registerEvents(nameModule, this);

        this.saveDefaultConfig();

        WarpStuff.buildTag();

        if (this.getServer().getWorld("skyworld") == null){
            System.out.println("[SkyBlock] Trying to create skyblock world because it wasnt found!");
            boolean worked = SkyWorldCreator.createWorld();
            System.out.println("[SkyBlock] Worked: " + worked);
            if (worked == false){
                System.out.println("[SkyBlock] Loaded: true");
            }
        }

        World world = this.getServer().getWorld("skyworld");
        if (world == null || world.getWorldType() != WorldType.FLAT){
            this.getServer().unloadWorld("skyworld", true);

            System.out.print("[SkyBlock] forcing creation of new world with old files");
            boolean success = SkyWorldCreator.createWorld();

            if (success){
                System.out.println("[SkyBlock] success!");
            } else {
                System.out.println("[SkyBlock] CRUCIAL MISTAKE!!!");
            }
        }

        boolean playerMySQLConnected = PlayerMySQL.attemptConnect();
        if (playerMySQLConnected){
            System.out.println("[SkyBlock] Player-MySQL succesfully connected");
        } else {
            for (int i = 0; i < 10; i = i + 1){
                System.out.println("[SkyBlock] Player-MySQL couldnt connect!!!");
            }
        }

        boolean blockMySQLConnected = SkyBlockMySQL.attemptConnect();
        if (blockMySQLConnected){
            System.out.println("[SkyBlock] SkyBlock-MySQL succesfully connected");
        } else {
            for (int i = 0; i < 10; i = i + 1){
                System.out.println("[SkyBlock] SkyBlock-MySQL couldnt connect!!!");
            }
        }

        blockManager = new SkyblockManager();
        blockManager.loadAll();

        for (Player player : Bukkit.getOnlinePlayers()){
            SkyplayerManager.loadForPlayer(player);
        }

        Boolean eco = 	Econ.setupEconomy();
        System.out.println("[SkyBlock] Econ hook: " + eco);
        if (eco == false){
            pm.disablePlugin(this);
        }

        FileConfiguration config = this.getConfig();
        deleteCost = config.getInt("prices.delete");
        renameCost = config.getInt("prices.rename");
        iconChangeCost = config.getInt("prices.changeicon");

        FileAccessor.saveXDefault("shop");
        ShopMain.loadAll();

        FileAccessor.reloadFileF("level");
        FileAccessor.saveXDefault("level");
        IslandLevels.updateLevels();

        FileAccessor.reloadFileF("messages");
        FileAccessor.saveXDefault("messages");
        Messages.updateMessages();

        Top.initialHeads();

        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                System.out.println("[SB] saving all data that should get saved");
                blockManager.saveAllSkyBlocksThatShould();
            }
        }, 1200, 1200);
    }

    public void onDisable(){
        for (Player player : Bukkit.getOnlinePlayers()){
            SkyplayerManager.saveForPlayer(player);
        }

        blockManager.saveAllSkyBlocksThatShould();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String Label, String[] args){
        if (cmd.getName().equalsIgnoreCase("skyblock")){
            CommandHandler.passCommand(sender, args);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("sell")){
            if (!(sender instanceof Player)){
                sender.sendMessage("no, just no.");
                return true;
            }

            Player player = (Player) sender;
            player.openInventory(ShopGUI.getShopGui(player, 1));
            return true;
        }

        return true;
    }
}
