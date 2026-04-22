package fr.draconium.core.primal.abilities;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public interface IPrimalAbility {
    boolean execute(EntityPlayerMP player, World world);
    void onTick(EntityPlayerMP player, World world);
}
