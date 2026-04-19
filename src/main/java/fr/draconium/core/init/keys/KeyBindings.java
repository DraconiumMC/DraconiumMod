package fr.draconium.core.init.keys;

import fr.draconium.core.primal.network.PrimalNetwork;
import org.lwjgl.input.Keyboard;

import fr.draconium.core.references.Reference;
import fr.draconium.core.proxy.packets.server.DraconiumCorePackets;
import fr.draconium.core.proxy.packets.server.EnergyShieldPacket;
import fr.draconium.core.proxy.packets.server.SpawnWolfPacket;
import fr.draconium.core.proxy.packets.server.TeleportPacket;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Reference.MODID, value = Side.CLIENT)
public class KeyBindings
{
	public static final KeyBinding TELEPORT_KEY      = new KeyBinding("key.teleport", Keyboard.KEY_T, "key.categories.draconiummod");
	public static final KeyBinding ENERGY_SHIELD_KEY = new KeyBinding("key.energy_shield", Keyboard.KEY_Y, "key.categories.draconiummod");
	public static final KeyBinding SPAWN_ALLIES_KEY  = new KeyBinding("key.spawn_allies", Keyboard.KEY_U, "key.categories.draconiummod");
	public static final KeyBinding PRIMAL_ABILITY_KEY = new KeyBinding("key.primal_ability", Keyboard.KEY_G, "key.categories.draconiummod");

	public static void init()
	{
		ClientRegistry.registerKeyBinding(TELEPORT_KEY);
		ClientRegistry.registerKeyBinding(ENERGY_SHIELD_KEY);
		ClientRegistry.registerKeyBinding(SPAWN_ALLIES_KEY);
		ClientRegistry.registerKeyBinding(PRIMAL_ABILITY_KEY);
	}

	@SubscribeEvent
	public static void onKeyInput(InputEvent.KeyInputEvent event)
	{
		if (TELEPORT_KEY.isPressed())
			DraconiumCorePackets.INSTANCE.sendToServer(new TeleportPacket());
		else if (ENERGY_SHIELD_KEY.isPressed())
			DraconiumCorePackets.INSTANCE.sendToServer(new EnergyShieldPacket());
		else if (SPAWN_ALLIES_KEY.isPressed())
			DraconiumCorePackets.INSTANCE.sendToServer(new SpawnWolfPacket());
        else if (PRIMAL_ABILITY_KEY.isPressed())
            DraconiumCorePackets.INSTANCE.sendToServer(new PrimalNetwork.PacketRequest());
	}
}
