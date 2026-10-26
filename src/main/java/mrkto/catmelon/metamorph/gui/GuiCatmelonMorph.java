package mrkto.catmelon.metamorph.gui;

import mchorse.mclib.client.gui.utils.Icons;
import mchorse.mclib.client.gui.utils.keys.IKey;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mrkto.catmelon.metamorph.CatmelonSceneMetamorphMorph;
import net.minecraft.client.Minecraft;

public class GuiCatmelonMorph extends GuiAbstractMorph<CatmelonSceneMetamorphMorph> {
    private final GuiCatmelonMainPanel mainPanel;

    public GuiCatmelonMorph(Minecraft mc) {
        super(mc);

        this.mainPanel = new GuiCatmelonMainPanel(mc, this);
        this.registerPanel(this.mainPanel, IKey.lang("chameleon.gui.editor.main"), Icons.GEAR);
    }
    @Override
    public boolean canEdit(AbstractMorph morph)
    {
        return morph instanceof CatmelonSceneMetamorphMorph;
    }
}
