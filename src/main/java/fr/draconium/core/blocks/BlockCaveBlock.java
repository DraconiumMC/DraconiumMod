package fr.draconium.core.blocks;

import fr.draconium.core.DraconiumCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCaveBlock extends Block
{

	public BlockCaveBlock(String name, Material materialIn)
	{
		super(materialIn);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setHardness(5.0f);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_BLOCK);
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT_MIPPED;
	}
}
