package fr.draconium.core.blocks;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.blocks.tileentity.TileEntityDisenchanter;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDisenchanter extends BlockContainer
{
    // ID unique pour le GUI (Le four était 1, celui-là sera 2).
    public static final int GUI_ID = 2;

    public BlockDisenchanter(String name)
    {
        super(Material.ROCK);
        this.setRegistryName(name);
        this.setTranslationKey(name);
        this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_BLOCK);
        this.setHardness(8.0F);
        this.setResistance(12.0F);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDisenchanter();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote) {
            // On ouvre le GUI numéro 2
            playerIn.openGui(DraconiumCore.instance, GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityDisenchanter) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityDisenchanter)tile);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}