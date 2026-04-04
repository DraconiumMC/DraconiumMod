package fr.draconium.core.items.others;

import fr.draconium.core.DraconiumCore;
import fr.draconium.core.handlers.GuiHandler;
import fr.draconium.core.init.items.ores.OresInit;
import fr.draconium.core.tabs.DraconiumCoreTab;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemVoidstone extends Item {

    public static final int GUI_ID = 3;

    public ItemVoidstone(String name) {
        this.setTranslationKey(name);
        this.setRegistryName(name);
        this.setMaxStackSize(1);
        this.setCreativeTab(DraconiumCore.DRACONIUM_TAB_OTHERS);
    }

    // --- LOGIQUE NBT ---
    private NBTTagCompound getTag(ItemStack stack) {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        return stack.getTagCompound();
    }

    public void addCobble(ItemStack stack, int amount) {
        int current = getCobbleAmount(stack);
        getTag(stack).setInteger("cobble_amount", current + amount);
    }

    public int getCobbleAmount(ItemStack stack) {
        return getTag(stack).getInteger("cobble_amount");
    }

    // --- TOOLTIP (LORE) ---
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        int currentCobble = getCobbleAmount(stack);
        int maxCobble = 100000; // Capacité max de base (à lier avec tes améliorations)

        tooltip.add("§2Structure de stockage");
        tooltip.add("§7Cobblestone: §f" + currentCobble + " §f/ " + maxCobble);

        // --- BARRE DE PROGRESSION EN COULEUR ---
        if (maxCobble > 0) {
            float percent = (float) currentCobble / (float) maxCobble;
            int barLength = 20; // Longueur de la barre (en caractères)
            int progress = Math.round(percent * barLength);

            // Couleur dynamique : Vert si < 50%, Jaune si < 80%, Rouge si > 80%
            String color = "§a"; // Vert
            if (percent > 0.50f) color = "§e"; // Jaune
            if (percent > 0.80f) color = "§c"; // Rouge

            StringBuilder bar = new StringBuilder(color + "[");
            for (int i = 0; i < barLength; i++) {
                if (i < progress) {
                    bar.append("=");
                } else {
                    bar.append("§8-"); // Partie vide en gris foncé
                }
            }
            bar.append(color + "]");
            tooltip.add(bar.toString());

            // Affichage du pourcentage précis
            tooltip.add("§7Remplissage: §f" + Math.round(percent * 100) + "%");
        }

        // Affichage des paliers d'amélioration
        int level = getTag(stack).getInteger("upgrade_level");
        tooltip.add("§6Niveau d'amelioration: §e" + level);
    }

    // --- OUVERTURE DU GUI ---
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (!worldIn.isRemote) {
            // Utilise l'ID que nous venons de définir (3)
            playerIn.openGui(DraconiumCore.instance, GUI_ID, worldIn, (int)playerIn.posX, (int)playerIn.posY, (int)playerIn.posZ);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }

    // --- LOGIQUE CONCASSAGE (STRUCTURE) ---
    public boolean canCrushCobble(ItemStack stack) {
        return getCobbleAmount(stack) >= 1000; // Exemple: 1000 cobble requis
    }

    // --- LOGIQUE DE CONCASSAGE ---

    public void crushCobble(ItemStack stack, EntityPlayer player) {
        int cost = 1000; // Coût par concassage
        if (getCobbleAmount(stack) < cost) return;

        // 1. On retire le coût
        addCobble(stack, -cost);

        // 2. On définit la table des butins (Minerai, Poids)
        Map<ItemStack, Integer> lootTable = new HashMap<>();

        // Vanilla (Poids élevés = Communs)
        lootTable.put(new ItemStack(Items.COAL), 80);
        lootTable.put(new ItemStack(Items.IRON_INGOT), 70);
        lootTable.put(new ItemStack(Items.GOLD_INGOT), 40);
        lootTable.put(new ItemStack(Items.DYE, 1, 4), 30);
        lootTable.put(new ItemStack(Items.REDSTONE), 30);

        // Rares
        lootTable.put(new ItemStack(Items.DIAMOND), 10);
        lootTable.put(new ItemStack(Items.EMERALD), 5);

        // MODDÉS (Poids très faibles = Très rares)
        lootTable.put(new ItemStack(OresInit.PYRONITE_INGOT), 3);
        lootTable.put(new ItemStack(OresInit.DRACONIUM_INGOT), 2);
        lootTable.put(new ItemStack(OresInit.FINDIUM_CRISTAL), 1);

        // 3. Calcul du tirage aléatoire
        ItemStack result = getRandomLoot(lootTable);

        // 4. On donne l'item au joueur
        if (!player.inventory.addItemStackToInventory(result)) {
            player.dropItem(result, false); // Si inventaire plein, on jette au sol
        }
    }

    private ItemStack getRandomLoot(Map<ItemStack, Integer> lootTable) {
        int totalWeight = 0;
        for (int weight : lootTable.values()) totalWeight += weight;

        int randomIndex = new java.util.Random().nextInt(totalWeight);
        int currentWeight = 0;

        for (Map.Entry<ItemStack, Integer> entry : lootTable.entrySet()) {
            currentWeight += entry.getValue();
            if (randomIndex < currentWeight) {
                return entry.getKey().copy();
            }
        }
        return new ItemStack(Blocks.COBBLESTONE); // Sécurité
    }
}