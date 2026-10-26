package mrkto.catmelon.client.impl.manage.animators.playbacks;

import mrkto.catmelon.api.Pose;
import mrkto.catmelon.api.animation.IAnimation;
import mrkto.catmelon.client.CatmelonClient;
import mrkto.catmelon.api.animation.IAction;
import mrkto.catmelon.client.utils.MolangHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class AnimationPlayback {
    protected double time = 0;
    private double speed = 1.0;
    @Nullable
    protected IAnimation animation = null;

    public void update() {
        if (animation == null) {
            return;
        }
        time += speed;
        if (animation.getLength() == 0) {
            return;
        }
        if (!animation.isLooping()) {
            time = Math.min(Math.max(time, 0), animation.getLength());
        } else {
            time = ((time % animation.getLength()) + animation.getLength()) % animation.getLength();
        }
    }

    public boolean isDone() {
        return animation != null && !animation.isLooping() && time == animation.getLength();
    }

    public void setAnimation(IAnimation animation) {
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
        double length = animation.getLength();
        double time = this.time + partialTicks * this.speed;
        if (!animation.isLooping()) {
            time = Math.min(Math.max(time, 0), length);
        } else if (animation.getLength() != 0) {
            time = ((time % length) + length) % length;
        }
        MolangHelper.setMolandTime(CatmelonClient.getMolangParser(), (float) time);
        animation.animatePose(pose, time);
    }
    public void applyActions(List<IAction> actions, float partialTicks, float prevPartialTicks) {
        if (this.animation == null) {
            return;
        }
        double length = animation.getLength();
        double time = this.time + partialTicks * this.speed;
        double prevTime = this.time + prevPartialTicks * this.speed;
        if (!animation.isLooping()) {
            time = Math.min(Math.max(time, 0), length);
            prevTime = Math.min(Math.max(prevTime, 0), length);
        } else if (animation.getLength() != 0) {
            time = ((time % length) + length) % length;
            prevTime = ((prevTime % length) + length) % length;
        }
        if(time < prevTime) {
            animation.collectEvents(prevTime, animation.getLength(), actions);
            animation.collectEvents(0, time, actions);
            return;
        }
        animation.collectEvents(prevTime, time, actions);
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return this.speed;
    }

    public double getTime() {
        return this.time;
    }

    @Nullable
    public IAnimation getAnimation() {
        return animation;
    }
}
