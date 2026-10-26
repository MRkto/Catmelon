package mrkto.catmelon.client.impl.animation.interpolation;

import mchorse.mclib.utils.Interpolations;
import mrkto.catmelon.client.impl.animation.Keyframe;

import javax.vecmath.Vector3f;

public class HermiteInterpolation extends BaseInterpolation {
    @Override
    public void interpolate(Vector3f output, Keyframe keyframe, double factor) {
        double startX = keyframe.x.get();
        double startY = keyframe.y.get();
        double startZ = keyframe.z.get();

        double destinationX = keyframe.getNext().preX.get();
        double destinationY = keyframe.getNext().preY.get();
        double destinationZ = keyframe.getNext().preZ.get();

        double preX = startX;
        double preY = startY;
        double preZ = startZ;

        double postX = destinationX;
        double postY = destinationY;
        double postZ = destinationZ;

        if (keyframe.getPrev()!= null)
        {
            preX = keyframe.getPrev().x.get();
            preY = keyframe.getPrev().y.get();
            preZ = keyframe.getPrev().z.get();
        }

        if (keyframe.getNext().getNext() != null)
        {
            postX = keyframe.getNext().getNext().preX.get();
            postY = keyframe.getNext().getNext().preY.get();
            postZ = keyframe.getNext().getNext().preZ.get();
        }
        output.set(
                (float) Interpolations.cubicHermite(preX, startX, destinationX, postX, factor),
                (float) Interpolations.cubicHermite(preY, startY, destinationY, postY, factor),
                (float) Interpolations.cubicHermite(preZ, startZ, destinationZ, postZ, factor)
        );
    }
}
