package me.yungweezy.skyblock.main;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Econ {

    public static Economy economy = null;

    static boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = Main.getPlugin(Main.class).getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    static void giveCash(Player player, Double amount){
        if (amount == 0) {
            return;
        }

        economy.depositPlayer(player, amount);
    }

}
