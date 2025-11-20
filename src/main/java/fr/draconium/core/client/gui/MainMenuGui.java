package fr.draconium.core.client.gui;

import fr.draconium.core.messages.Console;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.launchwrapper.Launch;

public class MainMenuGui extends GuiScreen {

    private static final String ADMIN_USERNAME = "FrozerYTB";
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("custommainmenu", "textures/gui/background.png");

    // true en dev (IntelliJ / Gradle), false en prod (jar dans .minecraft/mods)
    private static final boolean IS_DEV_ENV = Boolean.TRUE.equals(Launch.blackboard.get("fml.deobfuscatedEnvironment"));

    // Référence directe au bouton solo (peut être null en prod)
    private GuiButton soloButton;

    @Override
    @SideOnly(Side.CLIENT)
    public void initGui() {
        this.buttonList.clear();

        int centerX = this.width / 2;
        int startY = this.height / 3;

        int y = startY + 15;

        // Bouton "Jouer en solo" visible uniquement en dev
        if (IS_DEV_ENV) {
            soloButton = new GuiButton(0, centerX - 100, y, 200, 20, "Jouer en solo");
            this.buttonList.add(soloButton);
            y += 25;
        } else {
            soloButton = null; // pas de bouton en prod
        }

        // Bouton DraconiumMC
        this.buttonList.add(new GuiButton(1, centerX - 100, y, 200, 20, "Jouer à DraconiumMC !"));
        y += 25;

        // Bouton Mods
        this.buttonList.add(new GuiButton(2, centerX - 100, y, 200, 20, "Voir les mods de DraconiumMC"));
        y += 25;

        // Options et Quitter
        this.buttonList.add(new GuiButton(3, centerX - 100, y, 98, 20, "Options"));
        this.buttonList.add(new GuiButton(4, centerX + 2,  y, 98, 20, "Quitter le jeu"));
        y += 25;

        // Bouton admin custom si tu veux le garder
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null && player.getName().equals(ADMIN_USERNAME) && IS_DEV_ENV) {
            this.buttonList.add(new GuiButton(6, centerX - 100, y + 24, 200, 20, "Edit Menu"));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                // Solo - uniquement en dev
                Minecraft.getMinecraft().displayGuiScreen(new GuiWorldSelection(this));
                break;

            case 1:
                connectToServer("65.21.131.103", 25503);
                break;

            case 2:
                Minecraft.getMinecraft().displayGuiScreen(new GuiModList(this));
                break;

            case 3:
                Minecraft.getMinecraft().displayGuiScreen(new GuiOptions(this, Minecraft.getMinecraft().gameSettings));
                break;

            case 4:
                Minecraft.getMinecraft().shutdown();
                break;

            case 5:
                Minecraft.getMinecraft().displayGuiScreen(
                        new GuiLanguage(this, Minecraft.getMinecraft().gameSettings, Minecraft.getMinecraft().getLanguageManager())
                );
                break;

            case 6:
                Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Editing menu..."));
                break;
        }
    }

    private void connectToServer(String ip, int port) {
        try {
            ServerData serverData = new ServerData("DraconiumMC", ip + ":" + port, false);
            Minecraft.getMinecraft().displayGuiScreen(new GuiConnecting(this, Minecraft.getMinecraft(), serverData));
        } catch (Exception exception) {
            Console.error("[DraconiumMC] Erreur lors de la tentative de connexion: " + exception.getMessage());
            exception.printStackTrace();

            Minecraft.getMinecraft().displayGuiScreen(new GuiDisconnected(
                    this,
                    "Connexion échouée",
                    new TextComponentString("Impossible de se connecter au serveur DraconiumMC.\nErreur : " + exception.getMessage())
            ));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        this.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, this.width, this.height, this.width, this.height);

        super.drawScreen(mouseX, mouseY, partialTicks);

        // Tooltip pour le bouton solo si un jour tu veux le désactiver en dev
        if (soloButton != null && !soloButton.enabled && isMouseOverButton(soloButton, mouseX, mouseY)) {
            this.drawHoveringText("Le mode solo est désactivé sur DraconiumMC.", mouseX, mouseY);
        }
    }

    private boolean isMouseOverButton(GuiButton button, int mouseX, int mouseY) {
        return button != null
                && mouseX >= button.x && mouseX <= button.x + button.width
                && mouseY >= button.y && mouseY <= button.y + button.height;
    }
}
