package fr.draconium.core.worlds.generation;

import fr.draconium.core.init.blocks.ores.BlocksOresInit;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.*;

public class WorldGenCustomOres implements IWorldGenerator {

    // =======================================================================
    // 🪨 Liste de tous les minerais à générer dans l'Overworld
    // =======================================================================
    private static final List<OreGenParams> OVERWORLD_ORES = Arrays.asList(
            // 🔥 Pyronite — Génération spéciale près des lacs de lave
            new OreGenParams(4, 3, 1, 15, BlocksOresInit.PYRONITE_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), WorldGenCustomOres::isNearLava),

            // 💣 ExplosiveOre — Génération normale
            new OreGenParams(2,2,1,30, BlocksOresInit.EXPLOSIVE_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), (w,r,p) -> true),

            // 💎 Draconium — Génération normale
            new OreGenParams(3, 2, 1, 12, BlocksOresInit.DRACONIUM_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), (w, r, p) -> true),

            // ☠️ Findium — Génération normale
            new OreGenParams(2, 2, 1, 7, BlocksOresInit.FINDIUM_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), (w, r, p) -> true),

            // 🌍 RandomOre — Génération classique aléatoire
            new OreGenParams(2, 3, 3, 50, BlocksOresInit.RANDOM_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), (w, r, p) -> true)
    );



    // =======================================================================
    // 🌋 Liste des minerais du Nether
    // =======================================================================
    private static final List<OreGenParams> NETHER_ORES = Arrays.asList(
            // 🔥 Pyronite du Nether — Génération unique ment près des lacs de lave
           new OreGenParams(6, 4, 10, 110, BlocksOresInit.NETHER_PYRONITE_ORE.getDefaultState(), Blocks.NETHERRACK.getDefaultState(), (w, r, p) -> true)
    );


    // =======================================================================
    // ⚙️ Méthode principale : appelée à chaque chunk
    // =======================================================================
    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator chunkGen, IChunkProvider chunkProvider) {
        if (world.isRemote) return;

        int dim = world.provider.getDimension();

        switch (dim) {
            case 0: // Overworld
                for (OreGenParams ore : OVERWORLD_ORES) {
                    ore.generate(world, rand, chunkX, chunkZ);
                }
                break;

            case -1: // Nether
                for (OreGenParams ore : NETHER_ORES) {
                    ore.generate(world, rand, chunkX, chunkZ);
                }
                break;

            default:
                // ❌ Pas d'autres dimensions
                break;
        }
    }

    // =======================================================================
    // 🔥 Condition spéciale : Pyronite autour de la lave
    // =======================================================================
    private static boolean isNearLava(World world, Random rand, BlockPos pos) {
        // Vérifie une petite zone autour du bloc pour détecter la lave
        for (int dx = -3; dx <= 3; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -3; dz <= 3; dz++) {
                    BlockPos checkPos = pos.add(dx, dy, dz);
                    if (world.getBlockState(checkPos).getBlock() == Blocks.LAVA ||
                            world.getBlockState(checkPos).getBlock() == Blocks.FLOWING_LAVA) {
                        return true; // ✅ Génère seulement si de la lave est proche
                    }
                }
            }
        }
        return false;
    }

    // =======================================================================
    // 🧱 Classe interne : paramètres d’un minerai
    // =======================================================================
    private static class OreGenParams {
        private final WorldGenMinable generator;
        private final int veinCount;
        private final int minY;
        private final int maxY;
        private final OreCondition condition;

        public OreGenParams(int veinSize, int veinCount, int minY, int maxY,
                            IBlockState oreBlock, IBlockState targetBlock, OreCondition condition) {
            this.generator = new WorldGenMinable(oreBlock, veinSize);
            this.veinCount = veinCount;
            this.minY = minY;
            this.maxY = maxY;
            this.condition = condition;
        }

        public void generate(World world, Random rand, int chunkX, int chunkZ) {
            final int heightRange = maxY - minY + 1;
            final int baseX = chunkX << 4;
            final int baseZ = chunkZ << 4;

            for (int i = 0; i < veinCount; i++) {
                int x = baseX + rand.nextInt(16);
                int y = minY + rand.nextInt(heightRange);
                int z = baseZ + rand.nextInt(16);
                BlockPos pos = new BlockPos(x, y, z);

                // Vérifie la condition spécifique (Pyronite ou autre)
                if (condition.test(world, rand, pos)) {
                    generator.generate(world, rand, pos);
                }
            }
        }
    }

    // =======================================================================
    // 🧩 Interface fonctionnelle (condition personnalisée de génération)
    // =======================================================================
    @FunctionalInterface
    private interface OreCondition {
        boolean test(World world, Random rand, BlockPos pos);
    }
}
