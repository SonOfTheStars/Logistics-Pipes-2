package com.sots.pipe;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.UnmodifiableIterator;
import com.sots.block.BlockTileBase;
import com.sots.tiles.TileGenericPipe;
import com.sots.tiles.TileGenericPipe.ConnectionTypes;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGenericPipe extends BlockTileBase{
	
	public boolean hasChanged=false;
	
	protected final List<String> hidden = new ArrayList<String>();
	public final IModelState state = new IModelState()
	{
		private final Optional<TRSRTransformation> value = Optional.of(TRSRTransformation.identity());
		
		@Override
		public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part)
        {
            if(part.isPresent())
            {
                // This whole thing is subject to change, but should do for now.
                UnmodifiableIterator<String> parts = Models.getParts(part.get());
                if(parts.hasNext())
                {
                    String name = parts.next();
                    // only interested in the root level
                    if(!parts.hasNext() && hidden.contains(name))
                    {
                        return value;
                    }
                }
            }
            return Optional.absent();
        }
		
	};
	
	public BlockGenericPipe(Material materialIn) {
		super(materialIn);
		setHardness(3.0f);
		setResistance(10.0f);
		setSoundType(SoundType.METAL);
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
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
	
}
