package mrkto.catmelon.client.animation.data.animation.interpolation;

import mchorse.mclib.utils.Interpolations;
import mrkto.catmelon.client.animation.data.animation.Keyframe;

import javax.vecmath.Vector3f;

public class HermiteInterpolation extends BaseInterpolation {
    @Override
    public void interpolate(Vector3f output, Keyframe keyframe, double factor) {
        double startX = keyframe.getPrev().x.get();
        double startY = keyframe.getPrev().y.get();
        double startZ = keyframe.getPrev().z.get();

        double destinationX = keyframe.x.get();
        double destinationY = keyframe.y.get();
        double destinationZ = keyframe.z.get();

        double preX = startX;
        double preY = startY;
        double preZ = startZ;

        double postX = destinationX;
        double postY = destinationY;
        double postZ = destinationZ;

        if (keyframe.getPrev().getPrev() != null)
        {
            preX = keyframe.getPrev().getPrev().x.get();
            preY = keyframe.getPrev().getPrev().y.get();
            preZ = keyframe.getPrev().getPrev().z.get();
        }

        if (keyframe.getNext() != null)
        {
            postX = keyframe.getNext().x.get();
            postY = keyframe.getNext().y.get();
            postZ = keyframe.getNext().z.get();
        }
        output.set(
                (float) Interpolations.cubicHermite(preX, startX, destinationX, postX, factor),
                (float) Interpolations.cubicHermite(preY, startY, destinationY, postY, factor),
                (float) Interpolations.cubicHermite(preZ, startZ, destinationZ, postZ, factor)
        );
    }
}
