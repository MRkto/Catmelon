package mrkto.catmelon.client.animation.manage.animators.playbacks;

import mrkto.catmelon.client.animation.data.Pose;
import mrkto.catmelon.client.animation.data.animation.Animation;
import mrkto.catmelon.client.animation.data.animation.actions.IAction;

import java.util.ArrayList;
import java.util.List;

public class MultiAnimationPlayback {
    private final List<AnimationPlayback> playbacks = new ArrayList<>();
    private final Pose temp = new Pose();

    public void addAnimation(Animation animation) {
        AnimationPlayback p = new SmoothAnimationPlayback();
        p.setAnimation(animation);
        playbacks.add(p);
    }

    public void removeAnimation(Animation animation) {
        playbacks.removeIf(p -> p.getAnimation() == animation);
    }

    public void update() {
        playbacks.removeIf(AnimationPlayback::isDone);
        playbacks.forEach(AnimationPlayback::update);
    }

    public void applyAnimation(Pose pose, float partialTicks) {
        for (AnimationPlayback p : playbacks) {
            temp.clear();
            p.applyAnimation(temp, partialTicks);
            pose.add(temp);
        }
    }
    public void applyActions(List<IAction> actions, float partialTicks, float prevPartialTicks) {
        for (AnimationPlayback p : playbacks) {
            p.applyActions(actions, partialTicks, prevPartialTicks);
        }
    }
}
