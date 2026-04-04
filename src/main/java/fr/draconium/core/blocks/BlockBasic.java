package fr.draconium.core.blocks;

import fr.draconium.core.DraconiumCore;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;

public class BlockBasic extends Block
{
    private String requiredTool;       // type d'outil requis, ex: "pickaxe"
    private int requiredHarvestLevel;  // niveau minimum de l’outil requis

    public BlockBasic(String name, Material materialIn)
    {
        super(materialIn);
        this.setTranslationKey(name);
        this.setRegistryName(name);
        this.setHardness(8.0F);
        this.setResistance(8.0F);
        this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_BLOCK);
        this.setHarvestLevel("pickaxe", 0);

        // Par défaut : minable avec une pioche de niveau 0 (bois)
        this.requiredTool = "pickaxe";
        this.requiredHarvestLevel = 0;
    }

    // Méthode pour définir l’outil requis et son niveau
    public BlockBasic setRequiredTool(String tool, int level)
    {
        this.requiredTool = tool;
        this.requiredHarvestLevel = level;
        this.setHarvestLevel(tool, level); // Forge utilise ça aussi
        return this;
    }

    // Override pour forcer que seules les bonnes pioches minent le bloc
    public boolean canHarvestBlock(IBlockState state, EntityPlayer player)
    {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty()) return false;

        // Vérifie si l’item est une pioche et si son harvest level est suffisant
        if (stack.getItem() instanceof ItemPickaxe)
        {
            int toolLevel = stack.getItem().getHarvestLevel(stack, requiredTool, player, state);
            return toolLevel >= this.requiredHarvestLevel;
        }
        return false;
    }
}
