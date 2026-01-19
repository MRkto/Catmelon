package mrkto.catmelon.client.animation.manage.animators.playbacks;

import mrkto.catmelon.client.RenderEventHandler;
import mrkto.catmelon.client.animation.data.Pose;
import mrkto.catmelon.client.animation.data.animation.Animation;
import mrkto.catmelon.client.animation.data.animation.actions.IAction;
import mrkto.catmelon.client.animation.utils.MolangHelper;

import java.util.List;
import java.util.Objects;

public class AnimationPlayback {
    protected double time = 0;
    private double speed = 1.0;
    protected Animation animation = null;

    public void update() {
        if (animation == null) {
            return;
        }
        time += speed;
        if (animation.length == 0) {
            return;
        }
        if (!animation.loop) {
            time = Math.min(Math.max(time, 0), animation.length);
        } else {
            time = ((time % animation.length) + animation.length) % animation.length;
        }
    }

    public boolean isDone() {
        return animation != null && !animation.loop && time == animation.length;
    }

    public void setAnimation(Animation animation) {
        if (Objects.equals(this.animation, animation)) {
            return;
        }
        this.animation = animation;
        this.time = 0;
    }

    public void applyAnimation(Pose pose, float partialTicks) {
        if (this.animation == null) {
            return;
        }
        double length = animation.length;
        double time = this.time + partialTicks;
        if (!animation.loop) {
            time = Math.min(Math.max(time, 0), length);
        } else if (animation.length != 0) {
            time = ((time % length) + length) % length;
        }
        MolangHelper.setMolandTime(RenderEventHandler.molangParser, (float) time);
        animation.animatePose(pose, time);
    }
    public void applyActions(List<IAction> actions, float partialTicks, float prevPartialTicks) {
        if (this.animation == null) {
            return;
        }
        double length = animation.length;
        double time = this.time + partialTicks;
        double prevTime = this.time + prevPartialTicks;
        if (!animation.loop) {
            time = Math.min(Math.max(time, 0), length);
            prevTime = Math.min(Math.max(prevTime, 0), length);
        } else if (animation.length != 0) {
            time = ((time % length) + length) % length;
            prevTime = ((prevTime % length) + length) % length;
        }
        if(time < prevTime) {
            animation.getActions(actions, prevTime, animation.length);
            animation.getActions(actions, 0, time);
            return;
        }
        animation.getActions(actions, prevTime, time);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return this.speed;
    }

    public Animation getAnimation() {
        return animation;
    }
}
