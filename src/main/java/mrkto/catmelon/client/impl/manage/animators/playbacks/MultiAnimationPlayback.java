package mrkto.catmelon.client.impl.manage.animators.playbacks;

import mrkto.catmelon.api.Pose;
import mrkto.catmelon.api.animation.IAction;
import mrkto.catmelon.api.animation.IAnimation;

import java.util.ArrayList;
import java.util.List;

public class MultiAnimationPlayback {
    private final List<AnimationPlayback> playbacks = new ArrayList<>();
    private final int max;

    public MultiAnimationPlayback(int max) {
        this.max = max;
    }

    public void addAnimation(IAnimation animation) {
        if(playbacks.size() >= max) {
            return;
        }
        AnimationPlayback p = new SmoothAnimationPlayback();
        p.setAnimation(animation);
        playbacks.add(p);
    }

    public void removeAnimation(IAnimation animation) {
        playbacks.removeIf(p -> p.getAnimation() == animation);
    }

    public void update() {
        playbacks.removeIf(AnimationPlayback::isDone);
        playbacks.forEach(AnimationPlayback::update);
    }

    public void applyAnimation(Pose pose, float partialTicks) {
        for (AnimationPlayback p : playbacks) {
            p.applyAnimation(pose, partialTicks);
        }
    }
    public void applyActions(List<IAction> actions, float partialTicks, float prevPartialTicks) {
        for (AnimationPlayback p : playbacks) {
            p.applyActions(actions, partialTicks, prevPartialTicks);
        }
    }
}
