package mrkto.catmelon.client.animation.manage.animators;

import mrkto.catmelon.client.RenderEventHandler;
import mrkto.catmelon.client.animation.data.Pose;
import mrkto.catmelon.client.animation.data.animation.Animation;
import mrkto.catmelon.client.animation.utils.MolangHelper;
import net.minecraft.client.entity.AbstractClientPlayer;

import java.util.*;

//public class PlayerAnimator implements IAnimator<AbstractClientPlayer> {
//    private final Map<String, Animation> animations = new HashMap<>();
//
//    private final ActionLayer<MoveState> moveLayer = new ActionLayer<>(MoveState.IDLE);
//    private final ActionLayer<HandState> handLayer = new ActionLayer<>(HandState.NONE);
//
//
//    private final MultiAnimationPlayback playingAnimations = new MultiAnimationPlayback();
//
//    public PlayerAnimator(List<Animation> animations) {
//        for(Animation animation : animations) {
//            this.animations.put(animation.id, animation);
//        }
//    }
//
//    private boolean wasOnGround;
//    private double prevMY;
//    @Override
//    public void update(AbstractClientPlayer player) {
//        updateMoveLayer(player);
//        updateHandLayer(player);
//        moveLayer.update();
//        handLayer.update();
//        playingAnimations.update();
//        this.wasOnGround = player.onGround;
//        this.prevMY = player.posY - player.prevPosY;
//    }
//    private void updateMoveLayer(AbstractClientPlayer player) {
//        double dx = player.posX - player.prevPosX;
//        double dz = player.posZ - player.prevPosZ;
//        double dy = player.posY - player.prevPosY;
//        boolean creativeFlying = player.capabilities.isFlying;
//        boolean wet = player.isInWater();
//        final float threshold = creativeFlying ? 0.3F : (wet ? 0.025F : 0.02F);
//        boolean moves = Math.abs(dx) > threshold || Math.abs(dz) > threshold;
//        if (player.hurtTime == player.maxHurtTime - 1) {
//            tryPlayAnimation("hurt");
//        }
//        if(creativeFlying) {
//            moveLayer.setCurrentState(moves ? MoveState.FLYING : MoveState.FLYING_IDLE);
//            return;
//        }
//        if (!player.onGround && dy < -0.4) {
////            moveLayer.setCurrentState(MoveState.FALLING);
//            return;
//        }
//        if(wet) {
//            moveLayer.setCurrentState(moves ? MoveState.SWIMMING : MoveState.SWIMMING_IDLE);
//            return;
//        }
//        if (!player.onGround && this.wasOnGround && Math.abs(player.motionY) > 0.2F) {
//            tryPlayAnimation("jump");
//        }
//        if (player.onGround && !this.wasOnGround && this.prevMY < -0.5) {
//            tryPlayAnimation("land");
//        }
//        if(player.isSneaking()) {
//            moveLayer.setCurrentState(moves ? MoveState.CROUCHING : MoveState.CROUCHING_IDLE);
//            return;
//        }
//        if(moves) {
//            double body_yaw = player.renderYawOffset / 180 * Math.PI;
//            double forward = (dz * Math.cos(body_yaw) - dx * Math.sin(body_yaw));
//            moveLayer.setCurrentState(player.isSprinting() ? MoveState.SPRINTING : forward < -0.01 ? MoveState.RUNNING_BACKWARD : MoveState.RUNNING_FORWARD);
//            return;
//        }
//        moveLayer.setCurrentState(MoveState.IDLE);
//    }
//    private int raisedTimer = 0;
//    private void updateHandLayer(AbstractClientPlayer player) {
//        if (player.isSwingInProgress) {
//            handLayer.setCurrentState(HandState.SWIPE);
//            raisedTimer = Math.min(raisedTimer + 1, 200);
//            return;
//        } else {
//            raisedTimer = Math.max(raisedTimer - 2, 0);
//        }
//        if(!player.getHeldItemMainhand().isEmpty()) {
//            handLayer.setCurrentState(raisedTimer > 60 ? HandState.RAISED : HandState.HOLDING);
//            return;
//        }
//        handLayer.setCurrentState(HandState.NONE);
//    }
//
//    @Override
//    public boolean tryPlayAnimation(String animationName) {
//        if(animations.containsKey(animationName)) {
//            playingAnimations.addAnimation(animations.get(animationName));
//            return true;
//        }
//        return false;
//    }
//    @Override
//    public boolean tryStopAnimation(String animationName) {
//        if(!animations.containsKey(animationName)) {
//            return false;
//        }
//        Animation animation = animations.get(animationName);
//        playingAnimations.removeAnimation(animation);
//        return true;
//    }
//
//    @Override
//    public Pose getPose(float partialTicks) {
//
//        Pose movePose = moveLayer.getBlendedPose(partialTicks);
//        Pose handPose = handLayer.getBlendedPose(partialTicks);
//        if(handLayer.animationPlayback.animation == null || handLayer.animationPlayback.animation.override) {
//            movePose.overwrite(handPose);
//        } else {
//            movePose.add(handPose);
//        }
//        playingAnimations.apply(movePose, partialTicks);
//        return movePose;
//    }
//    private enum MoveState {
//        IDLE,
//        CROUCHING_IDLE,
//        CROUCHING,
//        FLYING_IDLE,
//        FLYING,
//        SWIMMING_IDLE,
//        SWIMMING,
//        RUNNING_FORWARD,
//        RUNNING_BACKWARD,
//        SPRINTING,
//        FALLING;
//    }
//    private enum HandState {
//        NONE,
//        HOLDING,
//        RAISED,
//        SWIPE;
//    }
//    private class ActionLayer<C extends Enum<?>> {
//        private C currentState;
//        private final AnimationPlayback animationPlayback = new AnimationPlayback();
//        private Pose finalePose = new Pose();
//        private Pose blendPose = new Pose();
//        private float blendFactor = 0f;
//
//        private static final float BLEND_DECAY_RATE = 0.2f;
//
//        public ActionLayer(C baseState) {
//            animationPlayback.setAnimation(PlayerAnimator.this.animations.get(baseState.name().toLowerCase()));
//            this.currentState = baseState;
//        }
//
//        public Pose getBlendedPose(float partialTicks) {
//            finalePose.reset();
//            animationPlayback.applyAnimation(finalePose, partialTicks);
//            if(blendFactor != 0f) {
//                finalePose.blend(blendPose, Math.max(0f, blendFactor - partialTicks * BLEND_DECAY_RATE));
//            }
//            return finalePose;
//        }
//
//        public void setCurrentState(C newState) {
//            if (newState == this.currentState) return;
//            Pose buf = getBlendedPose(1f);
//            finalePose = blendPose;
//            blendPose = buf;
//            finalePose.clear();
//            animationPlayback.setAnimation(PlayerAnimator.this.animations.get(newState.name().toLowerCase()));
//
//            this.currentState = newState;
//            this.blendFactor = 1f;
//        }
//
//        public void update() {
//            animationPlayback.update();
//            blendFactor = Math.max(0f, blendFactor - BLEND_DECAY_RATE);
//        }
//    }
//    private static class AnimationPlayback {
//        protected double time = 0;
//        private double speed = 1.0;
//        protected Animation animation = null;
//        public void update() {
//            if(animation == null) {
//                return;
//            }
//            time += speed;
//            if(animation.length == 0) {
//                return;
//            }
//            if (!animation.loop) {
//                time = Math.min(Math.max(time, 0), animation.length);
//            } else {
//                time = ((time % animation.length) + animation.length) % animation.length;
//            }
//        }
//        public boolean isDone() {
//            return animation != null && !animation.loop && time == animation.length;
//        }
//        public void setAnimation(Animation animation) {
//            if(Objects.equals(this.animation, animation)) {
//                return;
//            }
//            this.animation = animation;
//            this.time = 0;
//        }
//        public void applyAnimation(Pose pose, float partialTicks) {
//            if(this.animation == null) {
//                return;
//            }
//            double length = animation.length;
//            double time = this.time + partialTicks;
//            if (!animation.loop) {
//                time = Math.min(Math.max(time, 0), length);
//            } else if(animation.length != 0) {
//                time = ((time % length) + length) % length;
//            }
//            MolangHelper.setMolandTime(RenderEventHandler.molangParser, (float) time);
//            animation.animatePose(pose, time);
//        }
//        public void setSpeed(double speed) {
//            this.speed = speed;
//        }
//        public double getSpeed() {
//            return this.speed;
//        }
//
//        public Animation getAnimation() {
//            return animation;
//        }
//    }
//    private static class SmoothAnimationPlayback extends AnimationPlayback {
//        private final double blendTime = 3d;
//        private final Pose blendPose = new Pose();
//        public float getBlendFactor(float partialTicks) {
//            if(animation == null) {
//                return 0f;
//            }
//            double timePartial = time + partialTicks;
//            if(timePartial < blendTime) {
//                return (float) (timePartial / blendTime);
//            }
//            if(timePartial > animation.length - blendTime) {
//                return (float) ((animation.length - timePartial) / blendTime);
//            }
//            return 1f;
//        }
//
//        @Override
//        public void setAnimation(Animation animation) {
//            super.setAnimation(animation);
//            blendPose.clear();
//        }
//
//        @Override
//        public void applyAnimation(Pose pose, float partialTicks) {
//            super.applyAnimation(blendPose, partialTicks);
//            pose.blend(blendPose, getBlendFactor(partialTicks));
//        }
//    }
//    private static class MultiAnimationPlayback {
//        private final List<AnimationPlayback> playbacks = new ArrayList<>();
//        private final Pose temp = new Pose();
//
//        public void addAnimation(Animation animation) {
//            AnimationPlayback p = new SmoothAnimationPlayback();
//            p.setAnimation(animation);
//            playbacks.add(p);
//        }
//        public void removeAnimation(Animation animation) {
//            playbacks.removeIf(p -> p.getAnimation() == animation);
//        }
//
//        public void update() {
//            playbacks.removeIf(AnimationPlayback::isDone);
//            playbacks.forEach(AnimationPlayback::update);
//        }
//
//        public void apply(Pose pose, float partialTicks) {
//            for (AnimationPlayback p : playbacks) {
//                temp.clear();
//                p.applyAnimation(temp, partialTicks);
//                pose.add(temp);
//            }
//        }
//    }
//}
