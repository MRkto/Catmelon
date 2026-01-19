package mrkto.catmelon.client.animation.data.animation.actions;

import net.minecraft.util.ResourceLocation;

public class SoundAction implements IAction {
    public final ResourceLocation sound;
    public final String locator;

    public SoundAction(ResourceLocation sound, String locator) {
        this.sound = sound;
        this.locator = locator;
    }
}
