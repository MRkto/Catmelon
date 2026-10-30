package mrkto.catmelon.api.manage;

import mrkto.catmelon.api.Pose;
import mrkto.catmelon.api.animation.IAction;

import java.util.List;

public class AnimationController<C> {
    private final AnimatorChain<C> animatorChain;
    private final Class<C> type;

    float prevPartialTicks = 0f;
    public AnimationController(IAnimator<C>[] animators, Class<C> type) {
        animatorChain = new AnimatorChain<>(animators);
        this.type = type;
    }
    //TODO rewrite how prevPartialTicks calculates
    public void update(C object) {
        animatorChain.update(object);
        prevPartialTicks -= 1f;
    }
    public void animate(Pose pose, List<IAction> actionList, float partialTicks) {
        animatorChain.animate(pose, actionList, partialTicks, prevPartialTicks);
        prevPartialTicks = partialTicks;
    }
    public AnimatorChain<C> getAnimatorChain() {
        return animatorChain;
    }
    public boolean isSupportedType(Class<?> clazz) {
        return type.isAssignableFrom(clazz);
    }
}
