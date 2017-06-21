package com.sots.particle;

import java.util.Random;

import com.sots.LogisticsPipes2;
import com.sots.proxies.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ParticleUtil {
	public static Random rand = new Random();
	public static int counter = 0;
	
	public static void spawnGlint(World world, float x, float y, float z, float vx, float vy, float vz, float r, float g, float b, float scale, int lifetime) {
		if(LogisticsPipes2.proxy instanceof ClientProxy) {
			counter += rand.nextInt(3);
			if(counter % (Minecraft.getMinecraft().gameSettings.particleSetting == 0 ? 1: 2*Minecraft.getMinecraft().gameSettings.particleSetting) == 0) {
				ClientProxy.particleRender.addParticle(new ParticleGlint(world, x, y, z, vx, vy, vz, r, g, b, scale, lifetime));
			}
		}
	}
}
