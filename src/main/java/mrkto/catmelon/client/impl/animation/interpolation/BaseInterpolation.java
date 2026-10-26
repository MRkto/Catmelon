package mrkto.catmelon.client.impl.animation.interpolation;

import mchorse.mclib.utils.Interpolations;
import mrkto.catmelon.client.impl.animation.Keyframe;

import javax.vecmath.Vector3f;

public class BaseInterpolation {
    public void interpolate(Vector3f output, Keyframe keyframe, double factor) {
        output.set(
                (float) Interpolations.lerp(keyframe.x.get(), keyframe.getNext().preX.get(), factor),
                (float) Interpolations.lerp(keyframe.y.get(), keyframe.getNext().preY.get(), factor),
                (float) Interpolations.lerp(keyframe.z.get(), keyframe.getNext().preZ.get(), factor)
        );
    }
}
