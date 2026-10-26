package mrkto.catmelon.client.impl.animation;


import mchorse.mclib.math.molang.expressions.MolangExpression;
import mrkto.catmelon.client.impl.animation.interpolation.BaseInterpolation;

public class Keyframe extends BaseKeyframe<Keyframe> {
    public BaseInterpolation interpolation;

    public MolangExpression preX;
    public MolangExpression preY;
    public MolangExpression preZ;

    public MolangExpression x;
    public MolangExpression y;
    public MolangExpression z;


    public Keyframe(double time, BaseInterpolation interpolation, MolangExpression x, MolangExpression y, MolangExpression z) {
        super(time);
        this.interpolation = interpolation;
        this.x = x;
        this.y = y;
        this.z = z;
        this.preX = x;
        this.preY = y;
        this.preZ = z;
    }
    public Keyframe(double time, BaseInterpolation interpolation, MolangExpression x, MolangExpression y, MolangExpression z, MolangExpression preX, MolangExpression preY, MolangExpression preZ) {
        super(time);
        this.interpolation = interpolation;
        this.x = x;
        this.y = y;
        this.z = z;
        this.preX = preX;
        this.preY = preY;
        this.preZ = preZ;
    }
}
