package fr.draconium.core.client.gui;

import fr.draconium.core.blocks.containers.ContainerDraconiumFurnace;
import fr.draconium.core.blocks.tileentity.TileEntityDraconiumFurnace;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiDraconiumFurnace extends GuiContainer
{
    // ON POINTE VERS LA TEXTURE OFFICIELLE DE MINECRAFT !
    private static final ResourceLocation TEXTURES = new ResourceLocation("minecraft:textures/gui/container/furnace.png");

    private final TileEntityDraconiumFurnace tileEntity;
    private final InventoryPlayer playerInventory;

    public GuiDraconiumFurnace(InventoryPlayer player, TileEntityDraconiumFurnace tileEntity)
    {
        super(new ContainerDraconiumFurnace(player, tileEntity));
        this.tileEntity = tileEntity;
        this.playerInventory = player;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String tileName = this.tileEntity.hasCustomName() ? this.tileEntity.getName() : net.minecraft.client.resources.I18n.format("container.draconium_furnace");
        this.fontRenderer.drawString(tileName, (this.xSize / 2 - this.fontRenderer.getStringWidth(tileName) / 2), 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);

        // --- AFFICHAGE AMÉLIORATIONS ---
        String upgradeText = "";
        int color = 4210752;

        if (this.tileEntity.speedLevel > 0) {
            upgradeText = "Vitesse: " + this.tileEntity.speedLevel;
            color = 0x000000; // Cyan
        }
        else if (this.tileEntity.fortuneLevel > 0) {
            upgradeText = "Fortune: " + this.tileEntity.fortuneLevel;
            color = 0x000000; // Gold
        }

        if (!upgradeText.isEmpty()) {
            // POSITION PARFAITE : X=110, Y=60 (En bas à droite du feu)
            // On le dessine un peu plus petit
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.8, 0.8, 0.8);
            this.fontRenderer.drawString(upgradeText, 137, 75, color);
            GlStateManager.popMatrix();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURES);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        // Dessine le fond standard du four
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        // Dessine la flamme (Coordonnées standards de Minecraft)
        if (this.tileEntity.isBurning())
        {
            int k = getBurnLeftScaled(13);
            this.drawTexturedModalRect(i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        // Dessine la flèche (Coordonnées standards de Minecraft)
        int l = getCookProgressScaled(24);
        this.drawTexturedModalRect(i + 79, j + 34, 176, 14, l + 1, 16);
    }

    private int getCookProgressScaled(int pixels)
    {
        int i = this.tileEntity.cookTime;
        int j = this.tileEntity.totalCookTime;
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }

    private int getBurnLeftScaled(int pixels)
    {
        int i = this.tileEntity.currentBurnTime;
        if (i == 0) i = 200;
        return this.tileEntity.burnTime * pixels / i;
    }
}