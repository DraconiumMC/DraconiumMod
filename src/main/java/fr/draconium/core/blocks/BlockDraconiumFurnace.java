package fr.draconium.core.blocks;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.blocks.tileentity.TileEntityDraconiumFurnace;
import fr.draconium.core.init.items.others.OthersInit;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BlockDraconiumFurnace extends BlockContainer
{
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    // NOUVEAU : La propriété "Est en train de brûler ?"
    public static final PropertyBool BURNING = PropertyBool.create("burning");

    public static final int GUI_ID = 1;

    public BlockDraconiumFurnace(String name)
    {
        super(Material.IRON);
        this.setTranslationKey(name);
        this.setRegistryName(name);
        this.setHardness(8.0F);
        this.setResistance(8.0F);
        this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_BLOCK);

        // Par défaut : Regarde au Nord et Eteint (false)
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(BURNING, false));
    }

    // Ajoute de la lumière quand c'est allumé (Valeur 0 à 15, 13 = assez fort)
    @Override
    public int getLightValue(IBlockState state)
    {
        return state.getValue(BURNING) ? 13 : 0;
    }

    /**
     * Méthode statique appelée par le TileEntity pour mettre à jour l'apparence
     * sans supprimer le TileEntity (sinon l'inventaire se vide !)
     */
    public static void setState(boolean active, World worldIn, BlockPos pos)
    {
        IBlockState state = worldIn.getBlockState(pos);
        TileEntity tileentity = worldIn.getTileEntity(pos);

        // On garde la rotation actuelle, on change juste le BURNING
        if (state.getBlock() instanceof BlockDraconiumFurnace) // Petite sécurité
        {
            worldIn.setBlockState(pos, state.withProperty(BURNING, active), 3);
        }

        if (tileentity != null)
        {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityDraconiumFurnace();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        // On vérifie que c'est le serveur (pour modifier les données)
        if (!worldIn.isRemote)
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileEntityDraconiumFurnace)
            {
                TileEntityDraconiumFurnace furnace = (TileEntityDraconiumFurnace) tileEntity;
                ItemStack heldItem = playerIn.getHeldItem(hand);

                // --- CAS 1 : UPGRADE VITESSE ---
                if (heldItem.getItem() == OthersInit.UPGRADE_SPEED)
                {
                    // Vérification : Pas de Fortune installée ET pas déjà au max (8)
                    if (furnace.fortuneLevel == 0 && furnace.speedLevel < 8)
                    {
                        furnace.speedLevel++; // Ajoute +1
                        heldItem.shrink(1);   // Consomme l'item
                        worldIn.playSound(null, pos, net.minecraft.init.SoundEvents.BLOCK_ANVIL_USE, net.minecraft.util.SoundCategory.BLOCKS, 0.5F, 2.0F);
                    }
                    else if (furnace.fortuneLevel > 0) {
                        // Message d'erreur : Incompatible
                        if (!worldIn.isRemote) playerIn.sendMessage(new TextComponentString("§cImpossible : Une amélioration Fortune est déjà installée !"));
                    }
                    else {
                        // Message d'erreur : Max atteint
                        if (!worldIn.isRemote) playerIn.sendMessage(new TextComponentString("§cNiveau Maximum (8) atteint !"));
                    }
                    return true; // Arrête l'action (n'ouvre pas le GUI)
                }

                // --- CAS 2 : UPGRADE FORTUNE ---
                if (heldItem.getItem() == OthersInit.UPGRADE_FORTUNE)
                {
                    // Vérification : Pas de Vitesse installée ET pas déjà au max (8)
                    if (furnace.speedLevel == 0 && furnace.fortuneLevel < 8)
                    {
                        furnace.fortuneLevel++;
                        heldItem.shrink(1);
                        worldIn.playSound(null, pos, net.minecraft.init.SoundEvents.ENTITY_PLAYER_LEVELUP, net.minecraft.util.SoundCategory.BLOCKS, 0.5F, 2.0F);
                    }
                    else if (furnace.speedLevel > 0) {
                        if (!worldIn.isRemote) playerIn.sendMessage(new TextComponentString("§cImpossible : Une amélioration Vitesse est déjà installée !"));
                    }
                    else {
                        if (!worldIn.isRemote) playerIn.sendMessage(new TextComponentString("§cNiveau Maximum (8) atteint !"));
                    }
                    return true;
                }

                // SINON : Ouvre le GUI
                playerIn.openGui(DraconiumCore.instance, GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
                // SINON : On ouvre le GUI
                playerIn.openGui(DraconiumCore.instance, GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        // On vérifie qu'on ne casse pas le bloc juste pour changer son état (burning)
        // Sinon à chaque fois que le four s'allume, il drop ses items !
        // En 1.12.2, le TileEntity est géré différemment, mais cette sécurité est bonne.
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityDraconiumFurnace)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityDraconiumFurnace)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }

    // --- ROTATION & ETAT (C'est ici que ça se corse) ---

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(BURNING, false);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    /**
     * Convertit le chiffre (Meta) en Etat (Rotation + Burning)
     * Meta a 4 bits (0000 à 1111)
     * Bit 1-2 : Direction
     * Bit 3 : Burning
     */
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing facing = EnumFacing.byHorizontalIndex(meta & 3); // Prend les 2 premiers bits
        if (facing.getAxis() == EnumFacing.Axis.Y) facing = EnumFacing.NORTH;

        boolean isBurning = (meta & 4) != 0; // Vérifie le 3ème bit (valeur 4)

        return this.getDefaultState().withProperty(FACING, facing).withProperty(BURNING, isBurning);
    }

    /**
     * Convertit l'Etat en chiffre (Meta)
     */
    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();

        if (((Boolean)state.getValue(BURNING)).booleanValue())
        {
            i |= 4; // Ajoute 4 si ça brule
        }
        return i;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, BURNING);
    }
}