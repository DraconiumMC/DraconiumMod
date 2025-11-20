package fr.draconium.core.init.items.liquids;

import fr.draconium.core.fluids.FluidLiquid;
import fr.draconium.core.references.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class FluidInit
{
	public static Fluid FAKE_WATER_FLUID = new FluidLiquid("fake_water_fluid",
			new ResourceLocation(Reference.MODID, "fluids/fake_water_fluid"),
			new ResourceLocation(Reference.MODID, "fluids/fake_water_fluid"));
	
	public static void registerFluids()
	{
		registerFluid(FAKE_WATER_FLUID);
	}
	
	public static void registerFluid(Fluid fluid)
	{
		FluidRegistry.registerFluid(fluid);
		FluidRegistry.addBucketForFluid(fluid);
	}
}
