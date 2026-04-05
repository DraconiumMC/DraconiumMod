package fr.draconium.core.proxy;

import fr.draconium.core.DraconiumCore;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy
{
    // --- Méthodes de Cycle de Vie Forge (Base) ---
    // Ces méthodes sont appelées sur les deux côtés (Serveur et Client)

    public void preInit(FMLPreInitializationEvent event)
    {

    }

    public void init(FMLInitializationEvent event)
    {
        // Logique d'Initialisation commune au Serveur
    }

    public void postInit(FMLPostInitializationEvent event)
    {
        // Logique de Post-initialisation commune au Serveur
    }

    // --- Méthode Anti-Crash du Sided Proxy ---

    /**
     * Cette méthode est appelée par DraconiumCore, mais elle est VIDE ici.
     * Elle empêche le Serveur de crasher en essayant de charger les classes de rendu (TESR).
     * Elle est surchargée dans ClientProxy.
     */
    public void registerClientOnlyRenders()
    {
        // RIEN NE SE PASSE ICI. Le serveur est protégé.
    }
}