package me.yungweezy.skyblock.managers;

import org.bukkit.block.Biome;

public enum SkyBlockType {

	PLAINS (Biome.FOREST), //
	JUNGLE (Biome.JUNGLE), //
	NETHER (Biome.HELL),//
	MESA (Biome.MESA), //
	DESERT (Biome.DESERT), //
	SWAMP (Biome.SWAMPLAND), //
	MUSHROOM (Biome.MUSHROOM_ISLAND), //
	TAIGA (Biome.TAIGA), //
	CAVE (Biome.DEEP_OCEAN),
	OLDSCHOOL (Biome.FOREST),
	HEAVENLY (Biome.EXTREME_HILLS),
	SLIMY (Biome.FOREST),
	ICY (Biome.TAIGA); //
	
	private final Biome biome;
	
	SkyBlockType(Biome bio){
		this.biome = bio; 
	} 
	
	public Biome getBiome(){
		return this.biome;
	}
}
