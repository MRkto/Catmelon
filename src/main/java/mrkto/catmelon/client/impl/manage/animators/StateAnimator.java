package mrkto.catmelon.client.impl.manage.animators;

import mchorse.mclib.math.Operation;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.math.molang.expressions.MolangExpression;
import mrkto.catmelon.api.Pose;
import mrkto.catmelon.api.animation.IAction;
import mrkto.catmelon.api.animation.IAnimation;
import mrkto.catmelon.api.manage.IAnimator;
import mrkto.catmelon.client.CatmelonClient;
import mrkto.catmelon.client.impl.manage.animators.playbacks.AnimationPlayback;
import net.minecraft.entity.EntityLivingBase;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StateAnimator implements IAnimator<Object> {
    private final ActionLayer playback = new ActionLayer();
    private final AnimationGetter animationGetter;

    private BaseState currentState;

    public StateAnimator(BaseState mainState, AnimationGetter animationGetter){
        this.currentState = mainState;
        this.animationGetter = animationGetter;
    }
    @Override
    public void update(Object object) {
        playback.update();
        prepareMolang(object, CatmelonClient.getMolangParser());
        BaseState lastState = currentState;
        while (currentState.parent != null && currentState.canExit()) {
            currentState = currentState.parent;
        }

        AnimationState state = currentState.getAnimationState();
        prepareMolangPrev(object);
        if(state == null){
            return;
        }
        currentState = state;
        if(lastState == state){
            return;
        }

        playback.setAnimation(animationGetter.get(((AnimationState) currentState).animation));
    }

    @Override
    public void animate(Pose pose, List<IAction> actions, float partialTicks, float prevPartialTicks) {
        playback.applyAnimation(pose, partialTicks);
        playback.applyActions(actions, partialTicks, prevPartialTicks);
    }
    private boolean wasOnGround = true;
    private double prevYMotion = 0d;
    public void prepareMolang(Object object, MolangParser molangParser) {
        molangParser.setValue("query.is_on_ground.prev", wasOnGround ? 1 : 0);
        molangParser.setValue("query.vertical_velocity.prev", prevYMotion);

    }
    public void prepareMolangPrev(Object object) {
        if(object instanceof EntityLivingBase) {
            wasOnGround = ((EntityLivingBase) object).onGround;
            prevYMotion = ((EntityLivingBase) object).onGround ? 0 : ((EntityLivingBase) object).posY - ((EntityLivingBase) object).prevPosY;
        }
    }

    public static class GroupState extends BaseState {
        public List<BaseState> states = new ArrayList<>();

        @Override
        public @Nullable AnimationState getAnimationState() {
            for(BaseState state : states){
                if(!state.canEnter()){
                    continue;
                }
                return state.getAnimationState();
            }
            return null;
        }
    }
    public static class AnimationState extends BaseState{
        public String animation;
        public String getAnimation() {
            return animation;
        }
        @Override
        public @Nullable AnimationState getAnimationState() {
            return this;
        }
    }
    public abstract static class BaseState {
        public MolangExpression condition_in;
        public MolangExpression condition_out;
        public BaseState parent;
        public BaseState getParent() {
            return parent;
        }
        public boolean canExit() {
            return Operation.isTrue(condition_out.get());
        }
        public boolean canEnter() {
            return Operation.isTrue(condition_in.get());
        }
        @Nullable
        public abstract AnimationState getAnimationState();
    }

    private static class ActionLayer {
        private final AnimationPlayback animationPlayback = new AnimationPlayback();
        private Pose tempPose = new Pose();
        private Pose blendPose = new Pose();
        private float blendFactor = 0f;

        private static final float BLEND_DECAY_RATE = 0.2f;

        public void applyAnimation(Pose pose, float partialTicks) {
            animationPlayback.applyAnimation(pose, partialTicks);
            if(blendFactor != 0f) {
                pose.blend(blendPose, Math.max(0f, blendFactor - partialTicks * BLEND_DECAY_RATE));
            }
        }
        public void applyActions(List<IAction> actions, float partialTicks, float prevPartialTicks) {
            if(blendFactor == 1f) {
                return;
            }
            animationPlayback.applyActions(actions, partialTicks, prevPartialTicks);
        }

        public void setAnimation(IAnimation animation) {
            this.applyAnimation(tempPose, 0f);
            Pose buf = blendPose;
            blendPose = tempPose;
            tempPose = buf;
            tempPose.clear();
            animationPlayback.setAnimation(animation);

            this.blendFactor = 1f;
        }

        public void update() {
            animationPlayback.update();
            blendFactor = Math.max(0f, blendFactor - BLEND_DECAY_RATE);
        }
    }
    @FunctionalInterface
    public interface AnimationGetter {
        IAnimation get(String state);
    }
}
