package fr.draconium.core.primal;

import fr.draconium.core.primal.abilities.*; // Importe ton nouveau dossier d'abilities
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.world.World;
import net.minecraft.entity.EntityLiving;
import net.minecraft.network.datasync.DataParameter;
import java.lang.reflect.Field;

public enum PrimalAvatarType {
    // --- Mobs Hostiles ---
    // On ajoute "new MobAbility()" pour chaque ligne
    NONE(0, 0.6F, 1.8F, null, false, false, false, null),
    BLAZE(400, 0.6F, 1.8F, EntityBlaze.class, false, false, true, new BlazeAbility()),
    CREEPER(1200, 0.6F, 1.7F, EntityCreeper.class, false, false, false, new CreeperAbility(false)),
    CREEPER_CHARGED(1200, 0.6F, 1.7F, EntityCreeper.class, false, false, false, new CreeperAbility(true)),
    WITHER(2400, 0.9F, 3.5F, EntityWither.class, false, false, false, new WitherAbility()),
    WITHER_SKELETON(400, 0.7F, 2.4F, EntityWitherSkeleton.class, false, false, false, new WitherSkeletonAbility()),
    SKELETON(400, 0.7F, 2.4F, EntitySkeleton.class, false, false, false, new SkeletonAbility()),
    WITCH(600, 0.6F, 1.95F, EntityWitch.class, false, false, false, new WitchAbility()),
    ENDER_DRAGON(1200, 16.0F, 8.0F, EntityDragon.class, false, false, false, new EnderDragonAbility()),
    ENDERMAN(300, 0.6F, 2.9F, EntityEnderman.class, false, false, false, new EndermanAbility()),
    GHAST(160, 4.0F, 4.0F, EntityGhast.class, true, false, true, new GhastAbility()),
    ZOMBIE(1200, 0.6F, 1.95F, EntityZombie.class, false, false, false, new ZombieAbility()),
    PIG_ZOMBIE(400, 0.6F, 1.95F, EntityPigZombie.class, false, false, false, new PigZombieAbility()),
    SPIDER(100, 0.7F, 0.5F, EntitySpider.class, false, false, false, new SpiderAbility()),
    CAVE_SPIDER(100, 0.7F, 0.5F, EntityCaveSpider.class, false, false, false, new CaveSpiderAbility()),
    IRON_GOLEM(200, 1.4F, 2.7F, EntityIronGolem.class, false, false, false, new IronGolemAbility()),
    SNOW_GOLEM(100, 0.7F, 1.9F, net.minecraft.entity.monster.EntitySnowman.class, false, false, false, new SnowGolemAbility()),
    PIG(200, 0.9F, 0.9F, EntityPig.class, true, false, false, new PigAbility()),
    SALMON(160, 0.4F, 0.4F, EntitySquid.class, false, true, false, new SalmonAbility());

    // On peut continuer la liste plus tard...

    public final int defaultAbilityCooldownTicks;
    public final float width;
    public final float height;
    public final Class<? extends Entity> renderEntityClass;
    public final boolean mountableBySaddle;
    public final boolean fishBiology;
    public final boolean grantsFlight;
    private final IPrimalAbility ability; // Le lien vers le fichier de capacité

    private EntityLivingBase ghostEntity;

    PrimalAvatarType(int abilityCooldownTicks, float width, float height, Class<? extends Entity> renderEntityClass,
                     boolean mountableBySaddle, boolean fishBiology, boolean grantsFlight, IPrimalAbility ability) {
        this.defaultAbilityCooldownTicks = abilityCooldownTicks;
        this.width = width;
        this.height = height;
        this.renderEntityClass = renderEntityClass;
        this.mountableBySaddle = mountableBySaddle;
        this.fishBiology = fishBiology;
        this.grantsFlight = grantsFlight;
        this.ability = ability;
    }

    public IPrimalAbility getAbility() {
        return this.ability;
    }

    public boolean isActive() {
        return this != NONE;
    }

    public EntityLivingBase getGhostEntity(World world) {
        if (renderEntityClass == null) return null;
        if (ghostEntity == null || ghostEntity.world != world) {
            try {
                ghostEntity = (EntityLivingBase) this.renderEntityClass.getConstructor(World.class).newInstance(world);
                if (ghostEntity instanceof EntityLiving) {
                    ((EntityLiving) ghostEntity).setNoAI(true);
                }
                ghostEntity.setSilent(true);

                // CREEPER CHARGÉ (Aura bleue)
                if (this == CREEPER_CHARGED && ghostEntity instanceof EntityCreeper) {
                    try {
                        Field powerField = EntityCreeper.class.getDeclaredField("POWERED");
                        powerField.setAccessible(true);
                        DataParameter<Boolean> POWERED = (DataParameter<Boolean>) powerField.get(null);
                        ghostEntity.getDataManager().set(POWERED, true);
                    } catch (Exception ignored) {}
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
        return ghostEntity;
    }

    public static PrimalAvatarType byOrdinal(int o) {
        if (o < 0 || o >= values().length) return NONE;
        return values()[o];
    }
}