package mrkto.catmelon.client.animation.data.animation;


import mchorse.mclib.math.molang.expressions.MolangExpression;
import mrkto.catmelon.client.animation.data.animation.interpolation.BaseInterpolation;

public class Keyframe extends BaseKeyframe<Keyframe> {
    public BaseInterpolation interpolation;

    public MolangExpression x;
    public MolangExpression y;
    public MolangExpression z;


    public Keyframe(double time, BaseInterpolation interpolation, MolangExpression x, MolangExpression y, MolangExpression z) {
        super(time);
        this.interpolation = interpolation;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
