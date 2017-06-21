package com.sots.pipe;

import org.apache.logging.log4j.Level;

import com.sots.LogisticsPipes2;
import com.sots.tiles.TileNetworkCore;
import com.sots.util.AccessHelper;
import com.sots.util.References;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NetworkCore extends BlockGenericPipe{

	public NetworkCore() {
		super(Material.IRON);
		setUnlocalizedName(References.NAME_NETWORK_CORE);
		setRegistryName(References.RN_NETWORK_CORE);
		setCreativeTab(CreativeTabs.TRANSPORTATION);
		setHardness(3.0f);
		setResistance(15.0f);
		setSoundType(SoundType.METAL);
		setHarvestLevel("pickaxe",3);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
		
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {

		return true;
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		TileNetworkCore te = new TileNetworkCore();
		return te;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY) {
		super.onBlockActivated(worldIn, pos, state, playerIn, hand, heldItem, side, hitX, hitY);
		if(!worldIn.isRemote) {
			TileEntity self =  AccessHelper.getTileSafe(worldIn, pos);
			LogisticsPipes2.logger.log(Level.DEBUG, (self!=null ? "Tile is present!" : "Tile is absent!"));
		}
		
		return true;
	}
	
	

}
