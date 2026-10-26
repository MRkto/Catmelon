package mrkto.catmelon.metamorph.gui;

import mchorse.mclib.client.gui.framework.elements.utils.GuiContext;
import mchorse.metamorph.client.gui.creative.GuiMorphRenderer;
import mrkto.catmelon.metamorph.CatmelonSceneMetamorphMorph;
import net.minecraft.client.Minecraft;

public class GuiCatmelonModelRenderer extends GuiMorphRenderer {
    public GuiCatmelonModelRenderer(Minecraft mc) {
        super(mc);
    }

    @Override
    protected void drawUserModel(GuiContext guiContext) {
        if (!(this.morph instanceof CatmelonSceneMetamorphMorph))
        {
            return;
        }

        CatmelonSceneMetamorphMorph morph = ((CatmelonSceneMetamorphMorph) this.morph);
        morph.render(this.entity, 0,0,0,0,guiContext.partialTicks);
    }
}
