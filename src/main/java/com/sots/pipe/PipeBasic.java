package com.sots.pipe;

import java.util.ArrayList;

import com.sots.particle.ParticleUtil;
import com.sots.tiles.TileBasicPipe;
import com.sots.tiles.TileGenericPipe;
import com.sots.tiles.TileRoutedPipe;
import com.sots.util.References;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PipeBasic extends BlockGenericPipe{
	
	public PipeBasic() {
		super(Material.IRON);
		setUnlocalizedName(References.NAME_PIPE_BASIC);
		setRegistryName(References.RN_PIPE_BASIC);
		setCreativeTab(CreativeTabs.TRANSPORTATION);
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
		
	}
	
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos){
		((TileBasicPipe)world.getTileEntity(pos)).getAdjacentPipes(world);
		world.getTileEntity(pos).markDirty();
	}
	
	
	 
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		((TileBasicPipe)world.getTileEntity(pos)).getAdjacentPipes(world);
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		((TileBasicPipe)world.getTileEntity(pos)).getAdjacentPipes(world);
		world.getTileEntity(pos).markDirty();
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if(world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileBasicPipe) {
			TileBasicPipe te = (TileBasicPipe)world.getTileEntity(pos);
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
    public boolean isFullCube(IBlockState state) { return false; }
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {return false;}
	
	@Override
	public boolean isVisuallyOpaque(IBlockState p_176214_1_) {return false;}
	
	@Override
	public int getMetaFromState(IBlockState state) {return 0;}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {return true;}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileBasicPipe();
	}
	
	@Override
	public BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {Properties.AnimationProperty});
	}
	
	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player){
		super.onBlockHarvested(world, pos, state, player);
		if (world.getTileEntity(pos.up()) instanceof TileGenericPipe){
			((TileGenericPipe)world.getTileEntity(pos.up())).getAdjacentPipes(world);
		}
		if (world.getTileEntity(pos.down()) instanceof TileGenericPipe){
			((TileGenericPipe)world.getTileEntity(pos.down())).getAdjacentPipes(world);
		}
		if (world.getTileEntity(pos.north()) instanceof TileGenericPipe){
			((TileGenericPipe)world.getTileEntity(pos.north())).getAdjacentPipes(world);
		}
		if (world.getTileEntity(pos.south()) instanceof TileGenericPipe){
			((TileGenericPipe)world.getTileEntity(pos.south())).getAdjacentPipes(world);
		}
		if (world.getTileEntity(pos.west()) instanceof TileGenericPipe){
			((TileGenericPipe)world.getTileEntity(pos.west())).getAdjacentPipes(world);
		}
		if (world.getTileEntity(pos.east()) instanceof TileGenericPipe){
			((TileGenericPipe)world.getTileEntity(pos.east())).getAdjacentPipes(world);
		}
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY) {
		super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY);
		if(playerIn.getHeldItemMainhand()==null) {
			TileEntity target = worldIn.getTileEntity(pos);
			if(target instanceof TileBasicPipe) {
				if(((TileBasicPipe) target).hasNetwork()) {
					//Add Debug routing on Activation
					return true;
				}
			}
		}
		
		return false;
	}
	
}
