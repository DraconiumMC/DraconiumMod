package fr.draconium.core.client.gui;

import fr.draconium.core.blocks.containers.ContainerDisenchanter;
import fr.draconium.core.blocks.tileentity.TileEntityDisenchanter;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiDisenchanter extends GuiContainer
{
    // On utilise la texture de l'entonnoir (Hopper)
    private static final ResourceLocation TEXTURE_GUI = new ResourceLocation("minecraft:textures/gui/container/hopper.png");

    // Texture pour la barre de progression custom

    private final TileEntityDisenchanter tileEntity;

    // FIX : La variable est bien déclarée ici
    private final InventoryPlayer playerInventory;

    public GuiDisenchanter(InventoryPlayer player, TileEntityDisenchanter tileEntity)
    {
        super(new ContainerDisenchanter(player, tileEntity));
        this.playerInventory = player; // La variable est initialisée
        this.tileEntity = tileEntity;

        this.xSize = 176; // Taille standard
        this.ySize = 133; // Hauteur du GUI Hopper
    }

    // --- RENDU STANDARD ---

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    // --- RENDU DU TEXTE (PREMIER PLAN) ---
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String tileName = this.tileEntity.hasCustomName() ? this.tileEntity.getName() : I18n.format("container.disenchanter");
        this.fontRenderer.drawString(tileName, (this.xSize / 2 - this.fontRenderer.getStringWidth(tileName) / 2), 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        // 1. Dessin du fond (Hopper Texture)
        this.mc.getTextureManager().bindTexture(TEXTURE_GUI);
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);


    }
}