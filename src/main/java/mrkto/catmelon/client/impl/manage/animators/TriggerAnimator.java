package mrkto.catmelon.client.impl.manage.animators;

import mchorse.mclib.math.Operation;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.math.molang.expressions.MolangExpression;
import mrkto.catmelon.api.Pose;
import mrkto.catmelon.api.animation.IAction;
import mrkto.catmelon.api.animation.IAnimation;
import mrkto.catmelon.api.manage.IAnimator;
import mrkto.catmelon.client.CatmelonClient;
import mrkto.catmelon.client.impl.manage.animators.playbacks.MultiAnimationPlayback;
import net.minecraft.entity.EntityLivingBase;

import java.util.List;

public class TriggerAnimator implements IAnimator<Object> {
    private final MultiAnimationPlayback animationPlayback;
    public MolangExpression expression;
    public IAnimation animation;
    public TriggerAnimator(MolangExpression expression, IAnimation animation, int max) {
        this.expression = expression;
        this.animation = animation;
        this.animationPlayback = new MultiAnimationPlayback(max);
    }

    @Override
    public void update(Object object) {
        prepareMolang(CatmelonClient.getMolangParser());
        animationPlayback.update();

        if(Operation.isTrue(expression.get())) {
            animationPlayback.addAnimation(animation);
        }
        prepareMolangPrev(object);
    }
    private boolean wasOnGround = true;
    private double prevYMotion = 0d;
    public void prepareMolang(MolangParser molangParser) {
        molangParser.setValue("query.is_on_ground.prev", wasOnGround ? 1 : 0);
        molangParser.setValue("query.vertical_velocity.prev", prevYMotion);

    }
    public void prepareMolangPrev(Object object) {
        if(object instanceof EntityLivingBase) {
            wasOnGround = ((EntityLivingBase) object).onGround;
            prevYMotion = ((EntityLivingBase) object).onGround ? 0 : ((EntityLivingBase) object).posY - ((EntityLivingBase) object).prevPosY;
        }
    }

    @Override
    public void animate(Pose pose, List<IAction> actions, float partialTicks, float prevPartialTicks) {
        animationPlayback.applyAnimation(pose, partialTicks);
        animationPlayback.applyActions(actions, partialTicks, prevPartialTicks);
    }
}
