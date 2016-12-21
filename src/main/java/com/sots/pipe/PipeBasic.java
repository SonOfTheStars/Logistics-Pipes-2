package com.sots.pipe;

import java.util.List;

import com.google.common.collect.Lists;
import com.sots.tiles.TileBasicPipe;
import com.sots.util.References;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PipeBasic extends BlockGenericPipe implements ITileEntityProvider{

	public PipeBasic() {
		super(Material.IRON);
		setUnlocalizedName(References.NAME_PIPE_BASIC);
		setRegistryName(References.RN_PIPE_BASIC);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
		
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		List<String> connections = Lists.newArrayList("CENTER");
		TileBasicPipe te = (TileBasicPipe)world.getTileEntity(pos);
		OBJModel.OBJState objState = null;
		if(te != null) {
			objState = (new OBJModel.OBJState(te.checkConnections(world, pos),  true));
		}
		else {
			objState = (new OBJModel.OBJState(connections, true));
		}
		return ((IExtendedBlockState) this.getDefaultState().withProperty(OBJModel., objState));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {return false;}
	
	@Override
	public boolean isBlockNormalCube(IBlockState state) {return false;}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {return false;}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileBasicPipe();
	}
	
}
