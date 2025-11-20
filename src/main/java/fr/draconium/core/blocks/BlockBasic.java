package fr.draconium.core.blocks;

import fr.draconium.core.DraconiumCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BlockBasic extends Block
{
	/**
	 * @apiNote name 		= item name + json name + texture name, ect
	 * @apiNote material 	= sound step
	 */
	public BlockBasic(String name, Material materialIn)
	{
		super(materialIn);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_BLOCK);
	}
	
	protected static void test()
	{
		
	}
}
