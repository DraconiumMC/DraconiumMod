package fr.draconium.core.blocks;

import java.util.Random;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.init.blocks.ores.BlocksOresInit;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * 🧱 Bloc qui lâche un minerai aléatoire à la destruction.
 * Gère Fortune + Silk Touch.
 */
public class BlockRandomOre extends Block {

    public BlockRandomOre(String name, Material materialIn) {
        super(materialIn);
        this.setRegistryName(name);
        this.setTranslationKey(name);
        this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_BLOCK);
        this.setHardness(8.0F);
        this.setResistance(5.0F);
    }

    /**
     * Appelé lorsqu’un joueur casse le bloc.
     */
    @Override
    public void harvestBlock(World worldIn,
                             net.minecraft.entity.player.EntityPlayer player,
                             BlockPos pos,
                             IBlockState state,
                             TileEntity te,
                             ItemStack stack) {

        if (worldIn.isRemote) return;

        boolean hasSilkTouch =
                EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0;

        if (hasSilkTouch) {
            // Silk Touch → le bloc lui-même
            spawnAsEntity(worldIn, pos, new ItemStack(this));
        } else {
            int fortune = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
            ItemStack drop = getRandomOre(worldIn.rand, fortune);

            if (!drop.isEmpty()) {
                spawnAsEntity(worldIn, pos, drop);
            }
        }
    }

    /**
     * 🎲 Retourne un minerai aléatoire pondéré.
     * Les chances varient selon le niveau de Fortune.
     */
    private ItemStack getRandomOre(Random rand, int fortune) {
        Item[] ores = {
                Item.getItemFromBlock(Blocks.COAL_ORE),
                Item.getItemFromBlock(Blocks.IRON_ORE),
                Item.getItemFromBlock(Blocks.GOLD_ORE),
                Item.getItemFromBlock(Blocks.DIAMOND_ORE),
                Item.getItemFromBlock(Blocks.LAPIS_ORE),
                Item.getItemFromBlock(Blocks.REDSTONE_ORE),
                Item.getItemFromBlock(BlocksOresInit.PYRONITE_ORE),
                Item.getItemFromBlock(BlocksOresInit.DRACONIUM_ORE),
                Item.getItemFromBlock(BlocksOresInit.EXPLOSIVE_ORE),
                Item.getItemFromBlock(BlocksOresInit.FINDIUM_ORE),
        };

        double[] baseWeights = {
                40.0,  // CHARBON
                30.0,  // FER
                20.0,  // OR
                10.0,  // DIAMANT
                25.0,  // LAPIS
                25.0,  // REDSTONE
                6.0,   // PYRONITE
                3.0,   // DRACONIUM
                2.0,   // EXPLOSIVE
                0.8    // FINDIUM
        };

        double[] adjustedWeights = new double[baseWeights.length];
        for (int i = 0; i < baseWeights.length; i++) {
            adjustedWeights[i] = baseWeights[i];
        }

        // 📈 Bonus Fortune uniquement sur les minerais modés
        double fortuneBonus = 0.007 * fortune;

        adjustedWeights[6] += baseWeights[6] * fortuneBonus; // PYRONITE
        adjustedWeights[7] += baseWeights[7] * fortuneBonus; // DRACONIUM
        adjustedWeights[8] += baseWeights[8] * fortuneBonus; // EXPLOSIVE_ORE
        adjustedWeights[9] += baseWeights[9] * fortuneBonus; // FINDFIUM

        double totalWeight = 0;
        for (double w : adjustedWeights) totalWeight += w;

        double randomWeight = rand.nextDouble() * totalWeight;
        double cumulativeWeight = 0;

        for (int i = 0; i < adjustedWeights.length; i++) {
            cumulativeWeight += adjustedWeights[i];
            if (randomWeight <= cumulativeWeight) {
                return new ItemStack(ores[i]);
            }
        }

        return ItemStack.EMPTY;
    }
}
