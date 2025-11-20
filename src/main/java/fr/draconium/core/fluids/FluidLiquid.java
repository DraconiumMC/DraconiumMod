package fr.draconium.core.fluids;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidLiquid extends Fluid
{
	
	/**
	 * @apiNote name  = fluid name
	 * @apiNote Still = location texture (.png)
	 * @apiNote Flow  = location texture (.png)
	 */
	public FluidLiquid(String name, ResourceLocation still, ResourceLocation flow)
	{
		super(name, still, flow);
		this.setUnlocalizedName(name);
	}
}
