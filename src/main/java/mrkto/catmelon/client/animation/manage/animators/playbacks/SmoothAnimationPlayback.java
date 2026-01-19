package mrkto.catmelon.client.animation.manage.animators.playbacks;

import mrkto.catmelon.client.animation.data.Pose;
import mrkto.catmelon.client.animation.data.animation.Animation;

public class SmoothAnimationPlayback extends AnimationPlayback {
    private final double blendTime = 5d;
    private final Pose blendPose = new Pose();

    public float getBlendFactor(float partialTicks) {
        if (animation == null) {
            return 0f;
        }
        double timePartial = time + partialTicks;
        if (timePartial < blendTime) {
            return (float) (timePartial / blendTime);
        }
        if (timePartial > animation.length - blendTime) {
            return (float) ((animation.length - timePartial) / blendTime);
        }
        return 1f;
    }

    @Override
    public void setAnimation(Animation animation) {
        super.setAnimation(animation);
        blendPose.clear();
    }

    @Override
    public void applyAnimation(Pose pose, float partialTicks) {
        super.applyAnimation(blendPose, partialTicks);
        pose.blend(blendPose, getBlendFactor(partialTicks));
    }
}
