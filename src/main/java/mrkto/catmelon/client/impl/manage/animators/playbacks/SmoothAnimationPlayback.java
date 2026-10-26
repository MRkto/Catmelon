package mrkto.catmelon.client.impl.manage.animators.playbacks;

import mrkto.catmelon.api.Pose;
import mrkto.catmelon.api.animation.IAnimation;

public class SmoothAnimationPlayback extends AnimationPlayback {
    private double blendTime = 5d;
    private final Pose blendPose = new Pose();

    public float getBlendFactor(float partialTicks) {
        if (animation == null) {
            return 0f;
        }
        if(animation.isLooping()) {
            return 1f;
        }
        double timePartial = time + partialTicks;
        if (timePartial < blendTime) {
            return (float) (timePartial / blendTime);
        }
        if (timePartial > animation.getLength() - blendTime) {
            return (float) ((animation.getLength() - timePartial) / blendTime);
        }
        return 1f;
    }
    public boolean isFadingOut(float partialTicks) {
        return animation != null && time + partialTicks > animation.getLength() - blendTime;
    }
    public boolean isFadingIn(float partialTicks) {
        return animation != null && time + partialTicks < blendTime;
    }
    @Override
    public void setAnimation(IAnimation animation) {
        super.setAnimation(animation);
        blendPose.clear();
    }
    public void setBlendTime(double blendTime) {
        this.blendTime = blendTime;
    }

    @Override
    public void applyAnimation(Pose pose, float partialTicks) {
        super.applyAnimation(blendPose, partialTicks);
        pose.blend(blendPose, getBlendFactor(partialTicks));
    }
}
