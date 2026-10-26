package mrkto.catmelon.metamorph.gui;

import mchorse.mclib.client.gui.framework.elements.buttons.GuiButtonElement;
import mchorse.mclib.client.gui.framework.elements.input.GuiTexturePicker;
import mchorse.metamorph.client.gui.editor.GuiMorphPanel;
import mrkto.catmelon.metamorph.CatmelonSceneMetamorphMorph;
import net.minecraft.client.Minecraft;

public class GuiCatmelonMainPanel extends GuiMorphPanel<CatmelonSceneMetamorphMorph, GuiCatmelonMorph> {

    public GuiTexturePicker picker;
    public GuiButtonElement skin;

    public GuiCatmelonMainPanel(Minecraft mc, GuiCatmelonMorph editor) {
        super(mc, editor);

//        this.skin = new GuiButtonElement(mc, IKey.lang("chameleon.gui.editor.pick_skin"), (b) ->
//        {
//            this.picker.refresh();
////            this.picker.fill(this.morph.texture);
//            this.add(this.picker);
//            this.picker.resize();
//        });
////        this.picker = new GuiTexturePicker(mc, (rl) -> this.morph.texture = RLUtils.clone(rl));
//
//        this.skin.flex().relative(this).set(10, 10, 110, 20);
//        this.picker.flex().relative(this).wh(1F, 1F);
//
//        this.add(this.skin);
    }
    @Override
    public void finishEditing() {
        this.picker.close();
    }
}
