package fr.draconium.core.items.others;

import java.util.*;
import fr.draconium.core.DraconiumCore;
import fr.draconium.core.init.enchants.EnchantementsInit;
import fr.draconium.core.init.items.others.OthersInit;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ItemRadar extends Item {

    public static ItemRadar instance;

    private final int time = 90;
    private final int tickInterval = 30;
    private int secondsTimeLeft;
    private static final int BASE_CHUNK_RANGE = 1;

    // ====== CACHE PAR CHUNK (TileEntities) ======
    // key = ChunkPos.asLong(x, z), value = positions de TileEntities intéressantes
    private static final Map<Long, List<BlockPos>> TILE_CACHE = new HashMap<>();
    private static final Map<Long, Long> TILE_CACHE_TIME = new HashMap<>();
    // TTL du cache en ticks (ici 5 minutes = 5 * 60 * 20)
    private static final long CACHE_TTL = 5L * 60L * 20L;

    public ItemRadar(String name) {
        instance = this;
        this.setTranslationKey(name);
        this.setRegistryName(name);
        this.setMaxStackSize(1);
        this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_OTHERS);
    }

    // ===================== TOOLTIP =====================

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int level  = EnchantmentHelper.getEnchantmentLevel(EnchantementsInit.RANGE_ENCHANT, stack);
        int radius = getScanRadiusChunks(stack);

        tooltip.add("§b§lUnclaim Finder");
        tooltip.add("§7Détecte les coffres, fours, hoppers, shulkers,");
        tooltip.add("§7item frames, minecarts, armor stands autour de toi.");
        tooltip.add("");

        tooltip.add("§fNiveau d'extension: §a" + level);
        if (radius == 0) {
            tooltip.add("§7Portée: §aChunk actuel uniquement");
        } else {
            tooltip.add("§7Portée: §aChunk actuel + " + radius + " chunk(s) autour");
        }

        // Petit indice discret pour le niveau 3
        if (level == 3) {
            tooltip.add("");
            tooltip.add("§5Un pouvoir étrange émane de cet objet...");
        }
    }

    // ===================== CLIC DROIT =====================

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);

        if (!world.isRemote) {
            int chunkRange = getScanRadiusChunks(stack);
            RadarResult result = performScanWithCache(world, player, stack, chunkRange);
            // aucun message chat ici, résultat exploitable côté GUI / HUD si tu veux
        }

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.areItemStackTagsEqual(oldStack, newStack);
    }

    public void setSecondsTimeLeft(int ticks) {
        this.secondsTimeLeft = ticks;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return 1;
    }

    // ===================== PORTEE / NIVEAUX =====================

    /**
     * Rayon en chunks autour du joueur.
     * lvl 0 -> chunk actuel seulement
     * lvl 1 -> +1 chunk autour
     * lvl 2 -> +2
     * lvl 3 -> +3
     */
    public int getScanRadiusChunks(ItemStack stack) {
        int level = EnchantmentHelper.getEnchantmentLevel(EnchantementsInit.RANGE_ENCHANT, stack);
        if (level <= 0) return 0;
        if (level == 1) return 1;
        if (level == 2) return 2;
        return 3; // 3 et +
    }

    private String formatTimeLeft(int seconds) {
        seconds = seconds / this.tickInterval;
        int hours   = seconds / 3600;
        int minutes = seconds / 60;
        int secs    = seconds % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    public String getTimeLeft() {
        int hours   = this.secondsTimeLeft / 3600;
        int minutes = this.secondsTimeLeft / 60;
        int secs    = this.secondsTimeLeft % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    /**
     * @apiNote Récupère l'item actif (Radar) dans les mains du joueur ou fusionné dans le casque.
     */
    public ItemStack getUsableItemStack(EntityPlayer player) {
        // Radar en main ?
        for (EnumHand hand : EnumHand.values()) {
            ItemStack stack = player.getHeldItem(hand);
            if (stack.getItem() == OthersInit.RADAR) return stack;
        }

        // Radar fusionné dans casque ?
        ItemStack helmet = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (!helmet.isEmpty() && helmet.hasTagCompound()) {
            NBTTagCompound tag = helmet.getSubCompound("RadarData");
            if (tag != null && tag.getBoolean("HasRadar")) {
                return helmet; // on utilise les enchants copiés dessus
            }
        }
        return ItemStack.EMPTY;
    }

    // ===================== DETECTION / LOGIQUE =====================

    private boolean isInterestingTileEntity(TileEntity te) {
        return te instanceof TileEntityChest
                || te instanceof TileEntityFurnace
                || te instanceof TileEntityDispenser
                || te instanceof TileEntityHopper
                || te instanceof TileEntityShulkerBox
                || te instanceof TileEntityEnderChest;
    }

    private boolean isInterestingEntity(Entity entity) {
        return entity instanceof EntityItemFrame
                || entity instanceof EntityMinecart
                || entity instanceof EntityMinecartEmpty
                || entity instanceof EntityMinecartChest
                || entity instanceof EntityMinecartTNT
                || entity instanceof EntityMinecartContainer
                || entity instanceof EntityMinecartHopper
                || entity instanceof EntityMinecartFurnace
                || entity instanceof EntityArmorStand;
    }

    private String getDirectionFromOffset(double dx, double dz) {
        if (dx == 0 && dz == 0) return "Ici";

        double angle = Math.atan2(-dz, dx);
        double deg = Math.toDegrees(angle);
        if (deg < 0) deg += 360;

        if (deg >= 45 && deg < 135) return "Nord";
        if (deg >= 135 && deg < 225) return "Ouest";
        if (deg >= 225 && deg < 315) return "Sud";
        return "Est";
    }

    /**
     * Scan ultra optimisé avec cache par chunk pour les TileEntities.
     */
    private RadarResult performScanWithCache(World world, EntityPlayer player, ItemStack stack, int radiusChunks) {
        int playerChunkX = player.chunkCoordX;
        int playerChunkZ = player.chunkCoordZ;
        BlockPos playerPos = player.getPosition();

        BlockPos bestPos = null;
        double bestDistSq = Double.MAX_VALUE;
        boolean found = false;

        // === 1) Scan via cache des TileEntities par chunk ===
        for (int dx = -radiusChunks; dx <= radiusChunks; dx++) {
            for (int dz = -radiusChunks; dz <= radiusChunks; dz++) {
                int cx = playerChunkX + dx;
                int cz = playerChunkZ + dz;

                List<BlockPos> cachedPositions = getCachedTilePositions(world, cx, cz);
                if (cachedPositions.isEmpty()) continue;

                for (BlockPos pos : cachedPositions) {
                    double distSq = pos.distanceSq(playerPos);
                    if (distSq < bestDistSq) {
                        bestDistSq = distSq;
                        bestPos = pos;
                        found = true;
                    }
                }
            }
        }

        // === 2) Scan des entités intéressantes (item frames, minecarts, armor stands) ===
        int blockRange = (radiusChunks + 1) * 16;
        AxisAlignedBB box = new AxisAlignedBB(
                playerPos.getX() - blockRange, playerPos.getY() - blockRange, playerPos.getZ() - blockRange,
                playerPos.getX() + blockRange, playerPos.getY() + blockRange, playerPos.getZ() + blockRange
        );

        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, box, this::isInterestingEntity);
        for (Entity entity : entities) {
            BlockPos pos = entity.getPosition();
            double distSq = pos.distanceSq(playerPos);
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                bestPos = pos;
                found = true;
            }
        }

        if (!found) {
            return new RadarResult(false, null, 0, 0, "");
        }

        double distBlocks = Math.sqrt(bestDistSq);
        double distChunks = distBlocks / 16.0;
        String direction = getDirectionFromOffset(
                bestPos.getX() - playerPos.getX(),
                bestPos.getZ() - playerPos.getZ()
        );

        int level = EnchantmentHelper.getEnchantmentLevel(EnchantementsInit.RANGE_ENCHANT, stack);
        if (level >= 3) {
            applyLevel3Bonus(world, player, stack, bestPos);
        }

        return new RadarResult(true, bestPos, distBlocks, distChunks, direction);
    }

    /**
     * Récupère (ou construit) la liste des positions de TileEntities intéressants pour un chunk donné.
     */
    private List<BlockPos> getCachedTilePositions(World world, int chunkX, int chunkZ) {
        long key = ChunkPos.asLong(chunkX, chunkZ);
        long now = world.getTotalWorldTime();

        List<BlockPos> cached = TILE_CACHE.get(key);
        Long lastTime = TILE_CACHE_TIME.get(key);

        if (cached != null && lastTime != null && (now - lastTime) <= CACHE_TTL) {
            return cached;
        }

        if (!world.isChunkGeneratedAt(chunkX, chunkZ)) {
            return Collections.emptyList();
        }

        Chunk chunk = world.getChunk(chunkX, chunkZ);
        List<BlockPos> result = new ArrayList<>();

        for (TileEntity te : chunk.getTileEntityMap().values()) {
            if (isInterestingTileEntity(te)) {
                result.add(te.getPos());
            }
        }

        TILE_CACHE.put(key, result);
        TILE_CACHE_TIME.put(key, now);

        return result;
    }

    /**
     * Bonus niveau 3 – silencieux (aucun message chat).
     * Tu pourras y mettre des effets cachés (ex: léger cooldown réduit, légère extension invisible, etc.)
     */
    private void applyLevel3Bonus(World world, EntityPlayer player, ItemStack stack, BlockPos targetPos) {
        // Rien dans le chat, tout est caché.
        // Exemple futur: réduire un cooldown, stocker un flag NBT, etc.
    }


    // ===================== RESULTAT =====================

    public static class RadarResult {
        public final boolean found;
        public final BlockPos pos;
        public final double distBlocks;
        public final double distChunks;
        public final String direction;

        public RadarResult(boolean found, BlockPos pos, double distBlocks, double distChunks, String direction) {
            this.found = found;
            this.pos = pos;
            this.distBlocks = distBlocks;
            this.distChunks = distChunks;
            this.direction = direction;
        }
    }

    // ===================== ENCHANTEMENTS & LIVRES =====================

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        Map<Enchantment, Integer> bookEnchs = EnchantmentHelper.getEnchantments(book);
        if (bookEnchs.isEmpty()) return false;

        int rangeLevel = EnchantmentHelper.getEnchantmentLevel(EnchantementsInit.RANGE_ENCHANT, stack);

        for (Map.Entry<Enchantment, Integer> entry : bookEnchs.entrySet()) {
            Enchantment ench = entry.getKey();
            int lvl = entry.getValue();

            if (ench == EnchantementsInit.RANGE_ENCHANT) {
                return lvl >= 1 && lvl <= 3;
            }

            if (ench == Enchantments.UNBREAKING) {
                if (rangeLevel == 0) return false;
                if (rangeLevel == 1) return lvl <= 1;
                if (rangeLevel == 2) return lvl <= 2;
                if (rangeLevel >= 3) return lvl <= 3;
            }

            if (ench == Enchantments.MENDING) {
                return rangeLevel >= 3;
            }

            // Aucun autre enchant autorisé
            return false;
        }
        return false;
    }
}
