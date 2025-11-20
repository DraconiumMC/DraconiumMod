package fr.draconium.core.handlers;


import fr.draconium.core.client.gui.MainMenuGui;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiMainMenu;

@SideOnly(Side.CLIENT)
public class GuiEventHandler {

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiMainMenu) {
            event.setGui(new MainMenuGui()); // 👈 Remplace le menu vanilla
        }
    }
}
