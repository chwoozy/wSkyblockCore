package me.yungweezy.skyblock.worldgen;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import me.yungweezy.skyblock.main.Main;

public class SkyWorldCreator {

	public static ConstantRenderer renderer = null;
	public static Integer taskID = -1;
	
	public static boolean createWorld(){
		WorldCreator options = new WorldCreator("skyworld");
		options = options.generateStructures(false);
		options = options.type(WorldType.FLAT);
		options = options.generator(new SkyblockChunkGenerator());
		World skyworld = Bukkit.getServer().createWorld(options);
		skyworld.setDifficulty(Difficulty.NORMAL);
		
		if (renderer == null || taskID == -1){
			renderer = new ConstantRenderer();
			taskID = Main.pl.getServer().getScheduler().scheduleSyncRepeatingTask(Main.pl, renderer, 400, 100);
			System.out.println("Started constantrenderer");
		}
		return true;
	}
}
