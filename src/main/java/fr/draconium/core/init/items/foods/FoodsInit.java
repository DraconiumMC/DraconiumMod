package fr.draconium.core.init.items.foods;

import java.util.ArrayList;
import java.util.List;

import fr.draconium.core.items.foods.FoodEffect;
import fr.draconium.core.messages.Console;
import fr.draconium.core.references.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class FoodsInit
{
	
	private static List<Item> foods = new ArrayList<>();
	
	public static Item DRACONIUM_APPLE;
	
	public static void init()
	{
		foods.add(DRACONIUM_APPLE = new FoodEffect("draconium_apple", 8));
	}
	
	@SubscribeEvent
	protected static void registerItems(RegistryEvent.Register<Item> event)
	{
		Console.debug("- Enregistrement des nourritures:");
		for (Item food : foods)
		{
			event.getRegistry().registerAll(food);
			Console.debug("  - #6FF7D0" + food.getRegistryName());
		}
	}
	
	@SubscribeEvent
	protected static void registerRenders(ModelRegistryEvent event)
	{
		Console.debug("- Enregistrement du rendu des nourritures:");
		for (Item food : foods)
		{
			registerRender(food);
			Console.debug("  - #6FF794" + food.getRegistryName());
		}
	}
	
	private static void registerRender(Item item)
	{
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
