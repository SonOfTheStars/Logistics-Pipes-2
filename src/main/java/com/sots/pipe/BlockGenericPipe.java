package com.sots.pipe;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.UnmodifiableIterator;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.Models;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGenericPipe extends Block{
	
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
}
