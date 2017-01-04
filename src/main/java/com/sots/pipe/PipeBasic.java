package com.sots.pipe;

import com.sots.tiles.TileBasicPipe;
import com.sots.util.References;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
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

public class PipeBasic extends BlockGenericPipe implements ITileEntityProvider{
	
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
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if(world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileBasicPipe) {
			TileBasicPipe te = (TileBasicPipe)world.getTileEntity(pos);
			te.checkConnections(world, pos);
			return ((IExtendedBlockState) state).withProperty(Properties.AnimationProperty, te.state);
		}
		return state;
	}
	
	@Override
	public boolean canRenderInLayer(BlockRenderLayer layer) {
		return layer == BlockRenderLayer.SOLID || layer == BlockRenderLayer.TRANSLUCENT;
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
    public boolean isVisuallyOpaque() { return false; }
	
	@Override
	public int getMetaFromState(IBlockState state) {return 0;}
	
	@Override
	public boolean hasTileEntity() {return true;}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileBasicPipe();
	}
	
	@Override
	public BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {Properties.AnimationProperty});
	}
	
}
