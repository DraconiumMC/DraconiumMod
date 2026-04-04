package fr.draconium.core.handlers;

import fr.draconium.core.blocks.BlockDisenchanter;
import fr.draconium.core.blocks.BlockDraconiumFurnace;
import fr.draconium.core.blocks.containers.ContainerDisenchanter;
import fr.draconium.core.blocks.containers.ContainerDraconiumFurnace;
import fr.draconium.core.blocks.containers.ContainerVoidstone;
import fr.draconium.core.blocks.tileentity.TileEntityDisenchanter;
import fr.draconium.core.blocks.tileentity.TileEntityDraconiumFurnace;
import fr.draconium.core.client.gui.GuiDisenchanter;
import fr.draconium.core.client.gui.GuiDraconiumFurnace;
import fr.draconium.core.client.gui.GuiVoidstone;
import fr.draconium.core.items.others.ItemVoidstone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

        // ID 1 : Le Four
        if (ID == BlockDraconiumFurnace.GUI_ID)
        {
            if (tile instanceof TileEntityDraconiumFurnace) {
                return new ContainerDraconiumFurnace(player.inventory, (TileEntityDraconiumFurnace)tile);
            }
        }

        // ID 2 : Le Désenchanteur (AJOUTÉ ICI)
        else if (ID == BlockDisenchanter.GUI_ID)
        {
            if (tile instanceof TileEntityDisenchanter) {
                return new ContainerDisenchanter(player.inventory, (TileEntityDisenchanter)tile);
            }
        }

        if (ID == ItemVoidstone.GUI_ID) {
            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
            // On vérifie que le joueur tient bien la Voidstone
            if (!stack.isEmpty() && stack.getItem() instanceof ItemVoidstone) {
                return new ContainerVoidstone(player.inventory, stack);
            }
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));

        // ID 1 : Le Four
        if (ID == BlockDraconiumFurnace.GUI_ID)
        {
            if (tile instanceof TileEntityDraconiumFurnace) {
                return new GuiDraconiumFurnace(player.inventory, (TileEntityDraconiumFurnace)tile);
            }
        }

        // ID 2 : Le Désenchanteur (AJOUTÉ ICI)
        else if (ID == BlockDisenchanter.GUI_ID)
        {
            if (tile instanceof TileEntityDisenchanter) {
                return new GuiDisenchanter(player.inventory, (TileEntityDisenchanter)tile);
            }
        }

        if (ID == ItemVoidstone.GUI_ID) {
            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
            if (!stack.isEmpty() && stack.getItem() instanceof ItemVoidstone) {
                // On cast le container pour l'affichage client
                return new GuiVoidstone(new ContainerVoidstone(player.inventory, stack));
            }
        }

        return null;
    }
}