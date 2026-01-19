package mrkto.catmelon.client.animation.manage.animators;

import mrkto.catmelon.client.animation.data.Pose;
import mrkto.catmelon.client.animation.data.animation.Animation;
import mrkto.catmelon.client.animation.data.animation.actions.IAction;
import mrkto.catmelon.client.animation.manage.animators.playbacks.AnimationPlayback;
import mrkto.catmelon.client.animation.manage.animators.playbacks.MultiAnimationPlayback;
import mrkto.catmelon.client.animation.manage.commands.IAnimatorCommand;
import mrkto.catmelon.client.animation.manage.commands.MoveStateCommand;
import mrkto.catmelon.client.animation.manage.commands.TryPlayAnimationCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveAnimator implements IAnimator {
    private final Map<String, Animation> animations = new HashMap<>();

    private final ActionLayer<MoveStateCommand.State> moveLayer = new ActionLayer<>(MoveStateCommand.State.IDLE);
    private final MultiAnimationPlayback playingAnimations = new MultiAnimationPlayback();

    public MoveAnimator(List<Animation> animations) {
        for(Animation animation : animations) {
            this.animations.put(animation.id, animation);
        }
    }
    @Override
    public void update() {
        moveLayer.update();
        playingAnimations.update();
    }

    @Override
    public boolean processCommand(IAnimatorCommand command) {
        if(command instanceof MoveStateCommand) {
            moveLayer.setCurrentState(((MoveStateCommand) command).state);
            return true;
        }
        if(command instanceof TryPlayAnimationCommand) {
            Animation animation = animations.get(((TryPlayAnimationCommand) command).animation);
            if(animation != null) {
                playingAnimations.addAnimation(animation);
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public void animate(Pose pose, List<IAction> actions, float partialTicks, float prevPartialTicks) {
        moveLayer.applyAnimation(pose, partialTicks);
        playingAnimations.applyAnimation(pose, partialTicks);
        moveLayer.applyActions(actions, partialTicks, prevPartialTicks);
        playingAnimations.applyActions(actions, partialTicks, prevPartialTicks);
    }
    private class ActionLayer<C extends Enum<?>> {
        private C currentState;
        private final AnimationPlayback animationPlayback = new AnimationPlayback();
        private Pose tempPose = new Pose();
        private Pose blendPose = new Pose();
        private float blendFactor = 0f;

        private static final float BLEND_DECAY_RATE = 0.2f;

        public ActionLayer(C baseState) {
            animationPlayback.setAnimation(MoveAnimator.this.animations.get(baseState.name().toLowerCase()));
            this.currentState = baseState;
        }

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

        public void setCurrentState(C newState) {
            if (newState == this.currentState) return;
            this.applyAnimation(tempPose, 1f);
            Pose buf = blendPose;
            blendPose = tempPose;
            tempPose = buf;
            tempPose.clear();
            animationPlayback.setAnimation(MoveAnimator.this.animations.get(newState.name().toLowerCase()));

            this.currentState = newState;
            this.blendFactor = 1f;
        }

        public void update() {
            animationPlayback.update();
            blendFactor = Math.max(0f, blendFactor - BLEND_DECAY_RATE);
        }
    }


}
