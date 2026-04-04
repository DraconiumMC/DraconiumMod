package fr.draconium.core.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiVoidstone extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("draconiumcore", "textures/guis/voidstone.png");

    public GuiVoidstone(Container inventorySlotsIn) {
        super(inventorySlotsIn);
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        // Bouton Améliorer
        this.buttonList.add(new GuiInvisibleButton(1, i + 38, j + 35, 22, 22));
        this.buttonList.add(new GuiInvisibleButton(2, i + 116, j + 35, 22, 22));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        // Si la souris est sur le bouton 1 (Améliorer)
        if (mouseX >= i + 38 && mouseX <= i + 60 && mouseY >= j + 35 && mouseY <= j + 57) {
            this.drawHoveringText("§aAméliorer la capacité (Coût: 1 Diamant)", mouseX, mouseY);
        }

        // Si la souris est sur le bouton 2 (Concasser)
        if (mouseX >= i + 116 && mouseX <= i + 138 && mouseY >= j + 35 && mouseY <= j + 57) {
            this.drawHoveringText("§cConcasser 1000 Cobblestone", mouseX, mouseY);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 2) {// Envoyer un Packet au serveur pour dire "Le joueur veut améliorer"
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 2);
        } else if (button.id == 2) {
            // Envoyer un Packet pour le concassage
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    // Une classe de bouton qui n'affiche pas le rectangle gris de Minecraft
    public class GuiInvisibleButton extends GuiButton {
        public GuiInvisibleButton(int buttonId, int x, int y, int widthIn, int heightIn) {
            super(buttonId, x, y, widthIn, heightIn, "");
        }

        @Override
        public void drawButton(net.minecraft.client.Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            // On ne dessine rien ici, donc le bouton est invisible !
            // Mais il reste cliquable.
        }
    }
}

