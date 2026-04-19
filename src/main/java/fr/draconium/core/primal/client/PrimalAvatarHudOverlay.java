package fr.draconium.core.primal.client;

import fr.draconium.core.primal.PrimalAvatarLogic;
import fr.draconium.core.primal.PrimalAvatarType;
import fr.draconium.core.references.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Reference.MODID, value = Side.CLIENT)
public final class PrimalAvatarHudOverlay {

	private PrimalAvatarHudOverlay() {
	}

	@SubscribeEvent
	public static void onOverlay(RenderGameOverlayEvent.Post event) {
		if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		if (player == null) {
			return;
		}
		PrimalAvatarType type = PrimalAvatarLogic.getEquippedSetType(player);
		if (!type.isActive()) {
			return;
		}
		long now = player.world.getTotalWorldTime();
		long next = PrimalAvatarClientCooldownStore.getNextWorldTick(player.getEntityId());
		String line;
		if (next <= now) {
			line = "Primal : Prêt (G)";
		} else {
			int sec = (int) Math.ceil((next - now) / 20.0);
			line = "Primal : Recharge " + sec + " s";
		}
		FontRenderer fr = mc.fontRenderer;
		ScaledResolution sr = new ScaledResolution(mc);
		int x = sr.getScaledWidth() / 2 - fr.getStringWidth(line) / 2;
		int y = sr.getScaledHeight() - 59;
		fr.drawStringWithShadow(line, x, y, 0xFFFFFF);
	}
}
