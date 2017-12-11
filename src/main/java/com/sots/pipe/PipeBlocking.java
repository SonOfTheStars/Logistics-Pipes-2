package com.sots.pipe;

import com.sots.tiles.*;
import com.sots.util.References;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.*;
import net.minecraftforge.common.property.*;
import net.minecraftforge.common.property.Properties;

public class PipeBlocking extends BlockGenericPipe {

	public PipeBlocking() {
		super(Material.IRON);
		setUnlocalizedName(References.NAME_PIPE_BLOCKING);
		setRegistryName(References.RN_PIPE_BLOCKING);
		setCreativeTab(CreativeTabs.TRANSPORTATION);
	}

	@Override
	public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {return true;}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		((TileBlockingPipe)world.getTileEntity(pos)).getAdjacentPipes(world);
		world.getTileEntity(pos).markDirty();
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		((TileBlockingPipe)world.getTileEntity(pos)).getAdjacentPipes(world);
		((TileBlockingPipe)world.getTileEntity(pos)).setIncomingRedstone(((World)world).isBlockPowered(pos));
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		((TileBlockingPipe)world.getTileEntity(pos)).getAdjacentPipes(world);
		world.getTileEntity(pos).markDirty();
		((TileBlockingPipe)world.getTileEntity(pos)).setIncomingRedstone(world.isBlockPowered(pos));
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileBlockingPipe) {
			TileBlockingPipe te = (TileBlockingPipe)world.getTileEntity(pos);
			ArrayList<String> check = te.checkConnections(world, pos);
			if(!hidden.equals(check)) {
				hidden.clear();
				hidden.addAll(check);
			}
			return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty, this.state);
		}
		return state;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {return false;}

	@Override
	public boolean isBlockNormalCube(IBlockState state) {return false;}

	@Override
	public boolean isFullCube(IBlockState state) {return false;}

	@Override
	public boolean isOpaqueCube(IBlockState state) {return false;}

	@Override
	public boolean isTopSolid(IBlockState state) {return false;}

	@Override
	public int getMetaFromState(IBlockState state) {return 0;}

	@Override
	public boolean hasTileEntity(IBlockState state) {return true;}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileBlockingPipe();
	}

	@Override
	public BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {Properties.AnimationProperty});
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		super.onBlockHarvested(world, pos, state, player);
		if (world.getTileEntity(pos.up()) instanceof TileGenericPipe) {
			((TileGenericPipe)world.getTileEntity(pos.up())).getAdjacentPipes(world);
		}
		if (world.getTileEntity(pos.down()) instanceof TileGenericPipe) {
			((TileGenericPipe)world.getTileEntity(pos.down())).getAdjacentPipes(world);
		}
		if (world.getTileEntity(pos.north()) instanceof TileGenericPipe) {
			((TileGenericPipe)world.getTileEntity(pos.north())).getAdjacentPipes(world);
		}
		if (world.getTileEntity(pos.south()) instanceof TileGenericPipe) {
			((TileGenericPipe)world.getTileEntity(pos.south())).getAdjacentPipes(world);
		}
		if (world.getTileEntity(pos.west()) instanceof TileGenericPipe) {
			((TileGenericPipe)world.getTileEntity(pos.west())).getAdjacentPipes(world);
		}
		if (world.getTileEntity(pos.east()) instanceof TileGenericPipe) {
			((TileGenericPipe)world.getTileEntity(pos.east())).getAdjacentPipes(world);
		}
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY) {
		super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY);

		return true;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return super.getBoundingBox(state, source, pos);
	}
}
