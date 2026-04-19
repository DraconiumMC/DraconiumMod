package fr.draconium.core.worlds;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class ModConfig {
    public static boolean enableOreGeneration = true;

    /** Probabilité min (0–1) qu'une pièce d'armure Primal soit lâchée à la mort */
    public static float primalArmorDropMinChance = 0.5F;
    /** Probabilité max (0–1), tirage uniforme entre min et max pour chaque pièce */
    public static float primalArmorDropMaxChance = 0.75F;
    /** Multiplicateur de vitesse sous l'eau pour les sets « poisson » (1.0 = inchangé) */
    public static double primalFishSwimBoost = 1.35D;

    public static void loadConfig(File configFile) {
        Configuration config = new Configuration(configFile);
        config.load();

        enableOreGeneration = config.getBoolean("enableOreGeneration", "generation", false,
                "Définir sur false pour désactiver la génération des minerais moddés.");

        primalArmorDropMinChance = (float) config.get("primal_avatars", "armorDropChanceMin", 0.5D,
                "Chance minimale qu'une pièce Primal Avatars tombe au sol à la mort (0 à 1).", 0.0D, 1.0D).getDouble();
        primalArmorDropMaxChance = (float) config.get("primal_avatars", "armorDropChanceMax", 0.75D,
                "Chance maximale (chaque pièce tire un seuil entre min et max). Doit être >= min.", 0.0D, 1.0D).getDouble();
        if (primalArmorDropMaxChance < primalArmorDropMinChance) {
            primalArmorDropMaxChance = primalArmorDropMinChance;
        }
        primalFishSwimBoost = config.get("primal_avatars", "fishSwimBoost", 1.35D,
                "Bonus de nage sous l'eau pour les transformations type poisson.", 1.0D, 3.0D).getDouble();

        config.save();
    }
}