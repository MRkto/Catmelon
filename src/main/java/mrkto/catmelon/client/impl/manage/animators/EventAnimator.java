package mrkto.catmelon.client.impl.manage.animators;

import mrkto.catmelon.api.Pose;
import mrkto.catmelon.api.animation.IAction;
import mrkto.catmelon.api.animation.IAnimation;
import mrkto.catmelon.api.manage.IAnimator;
import mrkto.catmelon.client.impl.manage.animators.playbacks.SmoothAnimationPlayback;

import java.util.List;

public class EventAnimator implements IAnimator<Object> {
    public EventAnimator(IAnimation initialAnimation) {
        setAnimation(initialAnimation);
    }
    private final SmoothAnimationPlayback animationPlayback = new SmoothAnimationPlayback();
    @Override
    public void update(Object object) {
        animationPlayback.update();
        if(animationPlayback.isDone()) {
            animationPlayback.setAnimation(null);
        }
    }

    @Override
    public void animate(Pose pose, List<IAction> actions, float partialTicks, float prevPartialTicks) {
        animationPlayback.applyAnimation(pose, partialTicks);
        animationPlayback.applyActions(actions, partialTicks, prevPartialTicks);
    }
    public void setAnimation(IAnimation animation) {
        setAnimation(animation, 3f);
    }
    public void setAnimation(IAnimation animation, double blendTime) {
        animationPlayback.setBlendTime(blendTime);
        animationPlayback.setAnimation(animation);
    }
}
