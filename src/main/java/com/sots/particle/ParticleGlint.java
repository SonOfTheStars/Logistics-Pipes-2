package com.sots.particle;

import com.sots.util.Misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ParticleGlint extends Particle implements ILP2Particle{
	public float colorR = 0;
	public float colorG = 0;
	public float colorB = 0;
	public float initScale = 0;
	public ResourceLocation texture = new ResourceLocation("lptwo:entity/particle_glint");
	
	public ParticleGlint(World worldIn, double x, double y, double z, double vx, double vy, double vz, float r, float g, float b, float scale, int lifetime) {
		super(worldIn,x,y,z,0,0,0);
		this.colorR = r;
		this.colorG = g;
		this.colorB = b;
		if (this.colorR > 1.0){
			this.colorR = this.colorR/255.0f;
		}
		if (this.colorG > 1.0){
			this.colorG = this.colorG/255.0f;
		}
		if (this.colorB > 1.0){
			this.colorB = this.colorB/255.0f;
		}
		this.setRBGColorF(colorR, colorG, colorB);
		this.particleMaxAge = (int)((float)lifetime*0.5f);
		this.particleScale = scale;
		this.initScale = scale;
		this.motionX = vx;
		this.motionY = vy;
		this.motionZ= vz;
		this.particleAngle = 2.0f*(float)Math.PI;
		TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(texture.toString());
		this.setParticleTexture(sprite);
	}
	
	@Override
	public int getBrightnessForRender(float pTicks) {
		return super.getBrightnessForRender(pTicks);
	}
	
	@Override
	public boolean isTransparent() {
		return true;
	}
	
	@Override
	public int getFXLayer() {
		return 1;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if(Misc.rand.nextInt(6)==0) {
			this.particleAge++;
		}
		float lifeCovfefe = (float)this.particleAge/(float)this.particleMaxAge;
		this.particleScale = initScale-initScale*lifeCovfefe;
		particleAngle += 1.0f;
	}

	@Override
	public boolean alive() {
		return this.particleAge < this.particleMaxAge;
	}

	@Override
	public boolean isAdditive() {
		return true;
	}

	@Override
	public boolean renderThroughBlocks() {
		return true;
	}
}
