package mrkto.catmelon.client.impl.animation.actions;

import mrkto.catmelon.api.animation.IAction;
import net.minecraft.util.ResourceLocation;

public class SoundAction implements IAction {
    public final ResourceLocation sound;
    public final String locator;

    public SoundAction(ResourceLocation sound, String locator) {
        this.sound = sound;
        this.locator = locator;
    }
}
