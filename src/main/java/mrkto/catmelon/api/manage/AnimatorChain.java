package mrkto.catmelon.api.manage;

import mrkto.catmelon.api.Pose;
import mrkto.catmelon.api.animation.IAction;

import java.util.List;

public class AnimatorChain<C> {
    private final IAnimator<C>[] animators;

    public AnimatorChain(IAnimator<C>[] animators) {
        this.animators = animators;
    }

    protected void update(C object) {
        for (int i = 0; i < animators.length; i++) {
            animators[i].update(object);
        }
    }

    protected void animate(Pose pose, List<IAction> actions, float partialTicks, float prevPartialTicks) {
        for(int i = animators.length - 1; i >= 0; i--) {
            IAnimator<C> action = animators[i];
            action.animate(pose, actions, partialTicks, prevPartialTicks);
        }
    }
    public IAnimator<C> getAnimator(int i) {
        if(i < 0 || i >= animators.length)
            return null;
        return animators[i];
    }
    public <T extends IAnimator<C>> T getAnimator(int i, Class<T> expected) {
        if(i < 0 || i >= animators.length)
            return null;
        if(!expected.isInstance(animators[i]))
            return null;
        return expected.cast(animators[i]);
    }
    public <T extends IAnimator<C>> T getFirstAnimator(Class<T> expected) {
        for (int i = 0; i < animators.length; i++) {
            if(expected.isInstance(animators[i]))
                return expected.cast(animators[i]);
        }
        return null;
    }
}
