package fr.draconium.core.client.render;

import fr.draconium.core.blocks.tileentity.TileEntityDisenchanter;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDisenchanter extends TileEntitySpecialRenderer<TileEntityDisenchanter> {
    private static final ResourceLocation BOOK_TEXTURE = new ResourceLocation("minecraft:textures/entity/enchanting_table_book.png");
    private final ModelBook bookModel = new ModelBook();

    @Override
    public void render(TileEntityDisenchanter te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();

        // center + lift a bit
        GlStateManager.translate((float)x + 0.5F, (float)y + 0.85F, (float)z + 0.5F);

        // interpolation (prev + (cur - prev) * partial)
        float f = (float)te.ticks + partialTicks;
        float bookSpreadFinal = te.bookSpreadPrev + (te.bookSpread - te.bookSpreadPrev) * partialTicks;
        float bookRotationFinal = te.bookRotationPrev + (te.bookRotation - te.bookRotationPrev) * partialTicks;
        float pageFlipFinal = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks;

        // bobbing
        GlStateManager.translate(0.0F, 0.1F + MathHelper.sin(f * 0.1F) * 0.01F, 0.0F);

        // rotation Y
        GlStateManager.rotate(-bookRotationFinal * 57.295776F, 0.0F, 1.0F, 0.0F);

        // tilt same as vanilla (gives character to the book)
        GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);

        this.bindTexture(BOOK_TEXTURE);

        // compute page flips like vanilla (two values offset)
        float f3 = pageFlipFinal + 0.25F;
        float f4 = pageFlipFinal + 0.75F;
        f3 = (f3 - (float)MathHelper.fastFloor((double)f3)) * 1.6F - 0.3F;
        f4 = (f4 - (float)MathHelper.fastFloor((double)f4)) * 1.6F - 0.3F;
        if (f3 < 0.0F) f3 = 0.0F;
        if (f4 < 0.0F) f4 = 0.0F;
        if (f3 > 1.0F) f3 = 1.0F;
        if (f4 > 1.0F) f4 = 1.0F;

        float bookSpread = bookSpreadFinal;

        GlStateManager.enableCull();
        this.bookModel.render((Entity)null, f, f3, f4, bookSpread, 0.0F, 0.0625F);

        GlStateManager.popMatrix();
    }
}
