package me.yungweezy.skyblock.main;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CreateItem {

    @SuppressWarnings("deprecation")
    public static ItemStack fromCommaString(String s){
        String[] split = s.split(",");
        int id = Integer.parseInt(split[0]);
        int sub = Integer.parseInt(split[1]);
        ItemStack stack = createItem(Material.getMaterial(id), sub, "", new ArrayList<String>());
        return stack;
    }

    public static ArrayList<String> getLore(ItemStack stack){
        if (stack.getItemMeta() == null){
            return new ArrayList<String>();
        }

        if (stack.getItemMeta().getLore() == null){
            return new ArrayList<String>();
        }

        return (ArrayList<String>) stack.getItemMeta().getLore();
    }

    public static String getName(ItemStack stack){
        if (!stack.hasItemMeta()){
            return stack.getType().toString().toLowerCase();
        }

        if (!stack.getItemMeta().hasDisplayName()){
            return stack.getType().toString().toLowerCase();
        }

        return stack.getItemMeta().getDisplayName();
    }

    public static ItemStack createItem(Material mat, int sub, String name, ArrayList<String> lore){
        ItemStack stack = new ItemStack(mat, 1, (byte) sub);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        ArrayList<String> formattedlore = new ArrayList<String>();
        if (!lore.isEmpty()){
            for (String s : lore){
                String b = ChatColor.translateAlternateColorCodes('&', s);
                formattedlore.add(b);
            }
        }

        meta.setLore(formattedlore);
        stack.setItemMeta(meta);

        return stack;
    }

    public static ItemStack setName(ItemStack stack, String name){
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        stack.setItemMeta(meta);

        return stack;
    }

    public static ItemStack setLore(ItemStack stack, ArrayList<String> lore){
        ItemMeta meta = stack.getItemMeta();

        ArrayList<String> formattedlore = new ArrayList<String>();
        if (!lore.isEmpty()){
            for (String s : lore){
                String b = ChatColor.translateAlternateColorCodes('&', s);
                formattedlore.add(b);
            }
        }

        meta.setLore(formattedlore);

        stack.setItemMeta(meta);

        return stack;
    }
}