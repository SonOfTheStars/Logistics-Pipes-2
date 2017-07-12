package com.sots.pipe;

import java.util.ArrayList;
import java.util.List;

import com.sots.particle.ParticleUtil;
import com.sots.tiles.TileBasicPipe;
import com.sots.tiles.TileGenericPipe;
import com.sots.tiles.TileRoutedPipe;
import com.sots.tiles.TileGenericPipe.ConnectionTypes;
import com.sots.util.References;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
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
	public boolean isFullyOpaque(IBlockState state) {return false;}
	
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
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		
		double x1 = 0.275;
		double y1 = 0.275;
		double z1 = 0.275;
		double x2 = 0.725;
		double y2 = 0.725;
		double z2 = 0.725;
		
		if(source.getTileEntity(pos) instanceof TileGenericPipe){
			TileGenericPipe pipe = (TileGenericPipe) source.getTileEntity(pos);
			if(pipe.down != ConnectionTypes.NONE) {
				y1=0;
			}
			if(pipe.up != ConnectionTypes.NONE) {
				y2=1;
			}
			if(pipe.north != ConnectionTypes.NONE) {
				z1=0;
			}
			if(pipe.south != ConnectionTypes.NONE) {
				z2=1;
			}
			if(pipe.west != ConnectionTypes.NONE) {
				x1=0;
			}
			if(pipe.east != ConnectionTypes.NONE) {
				x2=1;
			}
		}
		return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
	}
	
	/*@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean p_185477_7_) {
		
		double x1 = 0.275;
		double y1 = 0.275;
		double z1 = 0.275;
		double x2 = 0.725;
		double y2 = 0.725;
		double z2 = 0.725;
		
		if(worldIn.getTileEntity(pos) instanceof TileGenericPipe){
			TileGenericPipe pipe = (TileGenericPipe) worldIn.getTileEntity(pos);
			if(pipe.down != ConnectionTypes.NONE) {
				collidingBoxes.add(new AxisAlignedBB(x1, 0, z1, x2, y2, z2));
			}
			if(pipe.up != ConnectionTypes.NONE) {
				collidingBoxes.add(new AxisAlignedBB(x1, y2, z1, x2, 1, z2));
			}
			if(pipe.north != ConnectionTypes.NONE) {
				collidingBoxes.add(new AxisAlignedBB(x1, y1, 0, x2, y2, z2));
			}
			if(pipe.south != ConnectionTypes.NONE) {
				collidingBoxes.add(new AxisAlignedBB(x1, y1, z2, x2, y2, 1));
			}
			if(pipe.west != ConnectionTypes.NONE) {
				collidingBoxes.add(new AxisAlignedBB(0, y1, z1, x2, y2, z2));
			}
			if(pipe.east != ConnectionTypes.NONE) {
				collidingBoxes.add(new AxisAlignedBB(x2, y1, z1, 1, y2, z2));
			}
		}
		
	}*/
}
