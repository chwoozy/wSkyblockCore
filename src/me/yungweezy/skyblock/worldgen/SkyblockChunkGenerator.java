package me.yungweezy.skyblock.worldgen;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

public class SkyblockChunkGenerator extends ChunkGenerator {
	
	SkyWorldCreator plugin;
	
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid grid) {
        byte[][] result = new byte[world.getMaxHeight() / 16][];
        
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {            
                for (int y = 0; y < 20; y++) {
                    setBlock(result, x, y, z, 0);
                }
            }
        }

        return result;
    }
	
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return Arrays.asList(new BlockPopulator[0]);
	}

    private void setBlock(byte[][] result, int x, int y, int z, int blockID){
    	if (result[y >> 4] == null){
    		result [y >> 4] = new byte [4096];
    	}
    	result[y>>4][((y & 0xF) << 8) | (z << 4) | x] = (byte) blockID;
    }
    
}
