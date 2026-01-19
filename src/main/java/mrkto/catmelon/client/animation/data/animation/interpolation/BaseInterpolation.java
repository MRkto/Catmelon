package mrkto.catmelon.client.animation.data.animation.interpolation;

import mchorse.mclib.utils.Interpolations;
import mrkto.catmelon.client.animation.data.animation.Keyframe;

import javax.vecmath.Vector3f;

public class BaseInterpolation {
    public void interpolate(Vector3f output, Keyframe keyframe, double factor) {
        output.set(
                (float) Interpolations.lerp(keyframe.getPrev().x.get(), keyframe.x.get(), factor),
                (float) Interpolations.lerp(keyframe.getPrev().y.get(), keyframe.y.get(), factor),
                (float) Interpolations.lerp(keyframe.getPrev().z.get(), keyframe.z.get(), factor)
        );
    }
}
