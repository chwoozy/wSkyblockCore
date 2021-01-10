package me.yungweezy.skyblock.managers;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.primesoft.asyncworldedit.AsyncWorldEditMain;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;

import me.yungweezy.skyblock.main.Main;

@SuppressWarnings("deprecation")
public class SchematicPaster {
	
    public static WorldEditPlugin WE() {
        Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (p instanceof WorldEditPlugin) return (WorldEditPlugin) p;
        else return null;
    }
    
    public static AsyncWorldEditMain getAWE(){
    	Plugin p = Bukkit.getServer().getPluginManager().getPlugin("AsyncWorldEditInjector");
    	return (AsyncWorldEditMain) p;
    }
    
    public static boolean exists(String schematicname){
		File schematic = new File(Main.getPlugin(Main.class).getDataFolder() + "/schematics/", schematicname + ".schematic");
		if (!schematic.exists()){
			return false;
		}
		
		return true;
    }
    
	public static boolean paste(Location loc, String schematicname){
		File schematic = new File(Main.getPlugin(Main.class).getDataFolder() + "/schematics/", schematicname + ".schematic");
		if (!schematic.exists()){
			return false;
		}
		
		EditSession session = WE().getWorldEdit().getEditSessionFactory().getEditSession(new BukkitWorld(loc.getWorld()), 1000000);
		
		try {
			MCEditSchematicFormat.getFormat(schematic).load(schematic).paste(session, new Vector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), false);
			return true;
		} catch (MaxChangedBlocksException | com.sk89q.worldedit.data.DataException | IOException e) {
			return false;
		}
	}
}
