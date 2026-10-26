package mrkto.catmelon.client.impl.animation.interpolation;

import mrkto.catmelon.client.impl.animation.Keyframe;

import javax.vecmath.Vector3f;

public class StepInterpolation extends BaseInterpolation {
    @Override
    public void interpolate(Vector3f output, Keyframe keyframe, double factor) {
        output.set((float) keyframe.x.get(), (float) keyframe.y.get(), (float) keyframe.z.get());
    }
}
