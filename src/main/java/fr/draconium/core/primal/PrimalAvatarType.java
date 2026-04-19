package fr.draconium.core.primal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.boss.EntityWither;

/**
 * Types de transformation Primal Avatars.
 * Les dimensions (width, height) sont basées sur les valeurs vanilla 1.12.2.
 */
public enum PrimalAvatarType {
    NONE(0, 0.6F, 1.8F, null, false, false, false),

    // --- Mobs Hostiles ---
    BLAZE(400, 0.6F, 1.8F, EntityBlaze.class, false, false, true),
    CREEPER(1200, 0.6F, 1.7F, EntityCreeper.class, false, false, false),
    ENDERMAN(300, 0.6F, 2.9F, EntityEnderman.class, false, false, false),
    GHAST(160, 4.0F, 4.0F, EntityGhast.class, true, false, true),
    SKELETON(100, 0.6F, 1.99F, EntitySkeleton.class, false, false, false),
    WITHER_SKELETON(400, 0.7F, 2.4F, EntityWitherSkeleton.class, false, false, false),
    SPIDER(300, 1.4F, 0.9F, EntitySpider.class, false, false, false),
    CAVE_SPIDER(300, 0.7F, 0.5F, EntityCaveSpider.class, false, false, false),
    WITCH(600, 0.6F, 1.95F, EntityWitch.class, false, false, false),
    ZOMBIE(1200, 0.6F, 1.95F, EntityZombie.class, false, false, false),
    PIG_ZOMBIE(600, 0.6F, 1.95F, EntityPigZombie.class, false, false, false),

    // --- Mobs Passifs ---
    COW(200, 0.9F, 1.4F, EntityCow.class, false, false, false),
    PIG(200, 0.9F, 0.9F, EntityPig.class, true, false, false),
    SHEEP(200, 0.9F, 1.3F, EntitySheep.class, false, false, false),
    CHICKEN(100, 0.4F, 0.7F, EntityChicken.class, false, false, false),
    SQUID(160, 0.8F, 0.8F, EntitySquid.class, false, true, false),

    // --- Golems ---
    IRON_GOLEM(200, 1.4F, 2.7F, EntityIronGolem.class, false, false, false),
    SNOW_GOLEM(100, 0.7F, 1.9F, EntitySnowman.class, false, false, false),

    // --- Poissons (1.12.2 simule via Squid ou classes custom si existantes) ---
    SALMON(160, 0.4F, 0.4F, EntitySquid.class, false, true, false),
    COD(160, 0.4F, 0.3F, EntitySquid.class, false, true, false),
    PUFFERFISH(200, 0.35F, 0.35F, EntitySquid.class, false, true, false);

    public final int defaultAbilityCooldownTicks;
    public final float width;
    public final float height;
    public final Class<? extends Entity> renderEntityClass;
    public final boolean mountableBySaddle;
    public final boolean fishBiology;
    public final boolean grantsFlight;

    PrimalAvatarType(int abilityCooldownTicks, float width, float height, Class<? extends Entity> renderEntityClass,
                     boolean mountableBySaddle, boolean fishBiology, boolean grantsFlight) {
        this.defaultAbilityCooldownTicks = abilityCooldownTicks;
        this.width = width;
        this.height = height;
        this.renderEntityClass = renderEntityClass;
        this.mountableBySaddle = mountableBySaddle;
        this.fishBiology = fishBiology;
        this.grantsFlight = grantsFlight;
    }

    public static PrimalAvatarType byOrdinal(int o) {
        if (o < 0 || o >= values().length) {
            return NONE;
        }
        return values()[o];
    }

    public boolean isActive() {
        return this != NONE;
    }
}