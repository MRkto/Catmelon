package mrkto.catmelon.client.animation.data.animation.interpolation;

import mchorse.mclib.utils.Interpolation;
import mrkto.catmelon.client.animation.data.animation.Keyframe;

import javax.vecmath.Vector3f;

public class EasingInterpolation extends BaseInterpolation {
    public final Interpolation interpolation;

    public EasingInterpolation(String name) {
        EasingInterpolationEnum easingInterpolationEnum = EasingInterpolationEnum.byName(name);
        interpolation = easingInterpolationEnum == null ? Interpolation.LINEAR : easingInterpolationEnum.interp;
    }

    @Override
    public void interpolate(Vector3f output, Keyframe keyframe, double factor) {
        super.interpolate(output, keyframe, interpolation.interpolate(0d, 1d, factor));
    }
}
