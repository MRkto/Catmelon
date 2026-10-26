package mrkto.catmelon.client.impl.animation.actions;

import mrkto.catmelon.api.animation.IAction;

public class ParticleAction implements IAction {
    public final String particle;
    public final String locator;
    public final String preEffectScript;

    public ParticleAction(String particle, String locator, String preEffectScript) {
        this.particle = particle;
        this.locator = locator;
        this.preEffectScript = preEffectScript;
    }
}
