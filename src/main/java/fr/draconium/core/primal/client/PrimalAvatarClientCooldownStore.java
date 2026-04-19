package fr.draconium.core.primal.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class PrimalAvatarClientCooldownStore {

	private static final Map<Integer, Long> NEXT_ABILITY_TICK = new ConcurrentHashMap<>();

	private PrimalAvatarClientCooldownStore() {
	}

	public static void set(int entityId, long nextWorldTick) {
		NEXT_ABILITY_TICK.put(entityId, nextWorldTick);
	}

	public static long getNextWorldTick(int entityId) {
		return NEXT_ABILITY_TICK.getOrDefault(entityId, 0L);
	}
}
