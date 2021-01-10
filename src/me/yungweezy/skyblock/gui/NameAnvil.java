package me.yungweezy.skyblock.gui;

import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.ContainerAnvil;
import net.minecraft.server.v1_10_R1.EntityHuman;

public final class NameAnvil extends ContainerAnvil {

	public NameAnvil(EntityHuman entityHuman) {    
		super(entityHuman.inventory, entityHuman.world, new BlockPosition(69,1,69), entityHuman);
	}

	@Override
	public boolean a(EntityHuman entityHuman) {    
		return true;
	}
}
