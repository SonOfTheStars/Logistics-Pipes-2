package com.sots.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.common.collect.ArrayListMultimap;
import com.sots.LogisticsPipes2;
import com.sots.util.Config;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class LPWorldGen implements IWorldGenerator{
	public static class OreGen{
		String name;
		WorldGenMinable ore;
		int maxY;
		int minY;
		int chunkOccurence;
		int weight;
		
		public OreGen(String name, IBlockState state, int maxVeinSize, Block replaceTarget, int minY, int maxY, int chunkOccurence, int weight) {
			this.name = name;
			this.ore = new WorldGenMinable(state, maxVeinSize, BlockMatcher.forBlock(replaceTarget));
			this.minY = minY;
			this.maxY = maxY;
			this.chunkOccurence = chunkOccurence;
			this. weight = weight;
		}
		
		public void generate(World world, Random rand, int x, int z) {
			BlockPos pos;
			for(int i = 0; i<chunkOccurence; i++) {
				if(rand.nextInt(100)<weight) {
					pos = new BlockPos(x + rand.nextInt(16), minY + rand.nextInt(maxY - minY), z + rand.nextInt(16));
					ore.generate(world, rand, pos);
				}
			}
		}
	}
	
	public static ArrayList<OreGen> generators = new ArrayList<OreGen>();
	public static ArrayList<Integer> oreDimBlacklist = new ArrayList<Integer>();
	public static HashMap<String, Boolean> retrogenMap = new HashMap<String, Boolean>();
	
	public static OreGen addOreGen(String name, IBlockState state, int maxVeinSize, int minY, int maxY, int chunkOccurence, int weight) {
		OreGen gen = new OreGen(name, state, maxVeinSize, Blocks.STONE, minY, maxY, chunkOccurence, weight);
		generators.add(gen);
		return gen;
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		this.generateOres(random, chunkX, chunkZ, world, true);
	}
	
	public void generateOres(Random rand, int chunkX, int chunkZ, World world, boolean newGeneration) {
		if(!oreDimBlacklist.contains(world.provider.getDimension())) {
			for(OreGen gen : generators) {
				if(newGeneration || retrogenMap.get("retrogen_"+gen.name)) {
					gen.generate(world, rand, chunkX*16, chunkZ*16);
				}
			
			}
		}
	}
	
	@SubscribeEvent
	public void chunkSave(ChunkDataEvent.Save event)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		event.getData().setTag("LPTwo", nbt);
		nbt.setBoolean("LPRetroGen", true);
	}
	
	@SubscribeEvent
	public void chunkLoad(ChunkDataEvent.Load event)
	{
		int dimension = event.getWorld().provider.getDimension();
		if((!event.getData().getCompoundTag("LPTwo").hasKey("LPRetroGen") && Config.retroGen))
		{
			if(Config.retroGenFlagLog)
				LogisticsPipes2.logger.info("Chunk "+event.getChunk().getPos()+" has been flagged for Ore RetroGeneration by LP2.");
			retrogenChunks.put(dimension, event.getChunk().getPos());
		}
	}
	
	public static ArrayListMultimap<Integer, ChunkPos> retrogenChunks = ArrayListMultimap.create();
	@SubscribeEvent
	public void serverWorldTick(TickEvent.WorldTickEvent event) {
		if(event.side==Side.CLIENT || event.phase==TickEvent.Phase.START) {
			return;
		}
		int dimension = event.world.provider.getDimension();
		int counter = 0;
		List<ChunkPos> chunks = retrogenChunks.get(dimension);
		if(chunks!=null && !chunks.isEmpty()) {
			for(int i=0; i<2; i++) {
				chunks = retrogenChunks.get(dimension);
				if(chunks == null || chunks.isEmpty()) {
					break;
				}
				counter++;
				
				ChunkPos loc = chunks.get(0);
				long worldSeed = event.world.getSeed();
				Random fmlRandom = new Random(worldSeed);
				
				long xSeed = (fmlRandom.nextLong()>>3);
				long zSeed = (fmlRandom.nextLong()>>3);
				
				fmlRandom.setSeed(xSeed * loc.x + zSeed * loc.z ^ worldSeed);
				this.generateOres(fmlRandom, loc.x, loc.z, event.world, false);
				chunks.remove(0);
			}
		}
		if(counter>0 && Config.retrogenRemaining) {
			LogisticsPipes2.logger.info("Retrogen was performed on "+counter+" Chunks, "+Math.max(0, chunks.size())+" chunks remaining");
		}
	}
}
