package me.yungweezy.skyblock.worldgen;

import org.bukkit.Chunk;
import org.bukkit.Location;

import me.yungweezy.skyblock.managers.SBUtils;

public class ConstantRenderer implements Runnable{

	public Location currentlyAt = null;
	public Long waitUntil = null;
	
	@Override
	public void run(){
		if (waitUntil != null){
			if (System.currentTimeMillis() >= waitUntil){
				if (currentlyAt.getX() < SBUtils.getUnusedLocation().getX() + 10000){
					waitUntil = null;
				} else {
					waitUntil = System.currentTimeMillis() + 30000;
				}
			} else {
				System.out.println("Paused the render");
				return;
			}
		}
		
		if (currentlyAt == null){
			currentlyAt = SBUtils.getUnusedLocation();
			currentlyAt.setX(currentlyAt.getX() - 500);
			currentlyAt.setZ(currentlyAt.getZ() - 500);
		}
		
		if (currentlyAt.getZ() >= 10600){
			currentlyAt.setZ(500);
			currentlyAt.setX(currentlyAt.getX() + 16);
			currentlyAt.getWorld().save();
			
			if (currentlyAt.getX() > SBUtils.getUnusedLocation().getX() + 10000){
				waitUntil = System.currentTimeMillis() + 300000;
			}
		}
		
		Chunk chunk = currentlyAt.getChunk();
		if (!chunk.isLoaded()){
			chunk.load();
			System.out.println("Prerendered " + chunk.getX() + ";" + chunk.getZ());
		}
		
		currentlyAt.setZ(currentlyAt.getZ() + 14);
		
	}
	
}
