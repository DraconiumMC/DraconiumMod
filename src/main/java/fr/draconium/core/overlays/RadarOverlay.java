package fr.draconium.core.overlays;

import java.util.HashSet;
import java.util.Set;

import fr.draconium.core.items.others.ItemRadar;
import fr.draconium.core.references.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Reference.MODID, value = Side.CLIENT)
public class RadarOverlay extends Gui
{
	public static int amountTiles = 0;

	// Instance unique pour pouvoir appeler les méthodes de Gui dans un contexte static
	private static final RadarOverlay INSTANCE = new RadarOverlay();

	private static final Minecraft minecraft       = Minecraft.getMinecraft();
	private static final ResourceLocation te0      = new ResourceLocation(Reference.MODID, "textures/items/radars/radar_empty.png");
	private static final ResourceLocation te1_5    = new ResourceLocation(Reference.MODID, "textures/items/radars/radar_0-5.png");
	private static final ResourceLocation te6_10   = new ResourceLocation(Reference.MODID, "textures/items/radars/radar_6-10.png");
	private static final ResourceLocation te11_25  = new ResourceLocation(Reference.MODID, "textures/items/radars/radar_11-25.png");
	private static final ResourceLocation te26_more = new ResourceLocation(Reference.MODID, "textures/items/radars/radar_26+.png");

	// Constructeur privé pour éviter d'autres new RadarOverlay() ailleurs
	private RadarOverlay() {}

	@SubscribeEvent
	public static void onRenderPre(RenderGameOverlayEvent.Pre event)
	{
		if (event.getType() == RenderGameOverlayEvent.ElementType.HELMET)
		{
			if (minecraft.player == null || minecraft.world == null)
				return;

			ItemStack stack = ItemRadar.instance.getUsableItemStack(minecraft.player);
			if (stack.isEmpty())
			{
				amountTiles = 0;
				return;
			}

			int chunksRadius = ItemRadar.instance.getScanRadiusChunks(stack);

			Set<String> chunksVisited = new HashSet<>();
			amountTiles = 0;

			for (int dx = -chunksRadius; dx <= chunksRadius; dx++)
			{
				for (int dz = -chunksRadius; dz <= chunksRadius; dz++)
				{
					if (Math.abs(dx) + Math.abs(dz) <= chunksRadius)
					{
						int chunkX = minecraft.player.chunkCoordX + dx;
						int chunkZ = minecraft.player.chunkCoordZ + dz;
						String chunkKey = chunkX + "," + chunkZ;

						if (!chunksVisited.contains(chunkKey))
						{
							chunksVisited.add(chunkKey);
							amountTiles += minecraft.world
									.getChunk(chunkX, chunkZ)
									.getTileEntityMap()
									.values()
									.size();
						}
					}
				}
			}

			ResourceLocation texture =
					amountTiles > 25 ? te26_more :
							amountTiles > 10 ? te11_25 :
									amountTiles > 5  ? te6_10  :
											amountTiles > 0  ? te1_5   : te0;

			minecraft.getTextureManager().bindTexture(texture);

			// On utilise INSTANCE pour appeler les méthodes de Gui
			INSTANCE.drawModalRectWithCustomSizedTexture(5, 5, 0, 0, 32, 32, 32, 32);
			INSTANCE.drawCenteredString(minecraft.fontRenderer, amountTiles + "%", 23, 39, -1);
			INSTANCE.drawCenteredString(minecraft.fontRenderer, ItemRadar.instance.getTimeLeft(), 23, 48, -1);
		}
	}
}
