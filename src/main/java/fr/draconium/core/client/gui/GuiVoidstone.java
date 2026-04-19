package fr.draconium.core.client.gui;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.network.PacketVoidstone;
import fr.draconium.core.proxy.packets.server.DraconiumCorePackets;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiVoidstone extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("draconiumcore", "textures/guis/voidstone.png");

    public GuiVoidstone(Container inventorySlotsIn) {
        super(inventorySlotsIn);
        this.xSize = 196;
        this.ySize = 178;
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

        // On boucle sur tous les boutons pour voir si la souris est dessus
        for (GuiButton button : this.buttonList) {
            if (button.isMouseOver()) {
                if (button.id == 1) {
                    this.drawHoveringText("§aAméliorer la capacité (Coût: 1 Diamant)", mouseX, mouseY);
                } else if (button.id == 2) {
                    this.drawHoveringText("§cConcasser 1000 Cobblestone", mouseX, mouseY);
                }
            }
        }

        this.renderHoveredToolTip(mouseX, mouseY);
    }

    // Dans actionPerformed
    @Override
    protected void actionPerformed(GuiButton button) {
        // DraconiumNetwork.network est ton instance de SimpleNetworkWrapper
        DraconiumCorePackets.INSTANCE.sendToServer(new PacketVoidstone(button.id));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F); // 👈 important
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        // IMPORTANT : Si ta texture n'est pas un carré de 256x256,
        // il faut utiliser drawModalRectWithCustomSizedTexture
        drawModalRectWithCustomSizedTexture(
                guiLeft,
                guiTop,
                0,
                0,
                xSize,
                ySize,
                xSize,
                ySize
        );
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

