package com.sots.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sots.util.References;
import com.sots.util.registries.ItemRegistry;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockOreRutile extends LPBlockBase{
	public BlockOreRutile(){
		super(Material.ROCK);
		setUnlocalizedName(References.NAME_ORE_RUTILE);
		setRegistryName(References.RN_ORE_RUTILE);
		setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
		setHardness(3.0f);
		setResistance(15.0f);
		setSoundType(SoundType.STONE);
		setHarvestLevel("pickaxe",3);
		setOreName("oreTitanium");
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel(){
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		
		for(int i = 0; i<3+fortune; i++) {
			ret.add(new ItemStack(ItemRegistry.shard_rutile,1,0));
		}
		return ret;
	}
}
