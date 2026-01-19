//package mrkto.catmelon.client.animation.manage.animators;
//
//import mchorse.chameleon.animation.ActionConfig;
//import mchorse.chameleon.animation.ActionPlayback;
//import mchorse.chameleon.lib.data.animation.Animation;
//import mchorse.chameleon.lib.data.animation.Animations;
//import mchorse.chameleon.lib.data.model.Model;
//import net.minecraft.entity.EntityLivingBase;
//import net.minecraft.entity.player.EntityPlayer;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//
//public class HandsAnimationController {
//    public ActionPlayback idle;
//    public ActionPlayback running;
//    public ActionPlayback sprinting;
//    public ActionPlayback crouching;
//    public ActionPlayback crouchingIdle;
//    public ActionPlayback swimming;
//    public ActionPlayback swimmingIdle;
//    public ActionPlayback flying;
//    public ActionPlayback flyingIdle;
//    public ActionPlayback falling;
//    public ActionPlayback land;
//    public ActionPlayback jump;
//    public ActionPlayback hurt;
//
//    public ActionPlayback equip;
//    public ActionPlayback staticIdle;
//    public ActionPlayback swipe;
//    public ActionPlayback use;
//    public ActionPlayback reload;
//    public ActionPlayback shoot;
//    public ActionPlayback inspect;
//
//
//
//    /* Action pipeline properties */
//    public ActionPlayback action;
//    public ActionPlayback lastAction;
//    public List<ActionPlayback> actions = new ArrayList<ActionPlayback>();
//
//    public ActionPlayback base;
//
//    public double prevX = Float.MAX_VALUE;
//    public double prevZ = Float.MAX_VALUE;
//    public double prevMY;
//
//    /* States */
//    public boolean wasOnGround = true;
//
//    public Animations animations;
//
//    public void setAnimations(Animations animations) {
//        this.animations = animations;
//        this.reset();
//    }
//
//    public void reset()
//    {
//        this.idle = this.createAction("idle", true);
//        this.running = this.createAction("running", true);
//        this.sprinting = this.createAction("sprinting", true);
//        this.crouching = this.createAction("crouching", true);
//        this.crouchingIdle = this.createAction("crouching_idle", true);
//        this.swimming = this.createAction("swimming", true);
//        this.swimmingIdle = this.createAction("swimming_idle", true);
//        this.flying = this.createAction("flying", true);
//        this.flyingIdle = this.createAction("flying_idle", true);
//        this.falling = this.createAction("falling", true);
//        this.land = this.createAction("land", false);
//        this.jump = this.createAction("jump", false);
//        this.hurt = this.createAction("hurt", false);
//
//        this.equip = this.createAction("equip", false);
//        this.staticIdle = this.createAction("static", true);
//        this.swipe = this.createAction("swipe", false);
//        this.use = this.createAction("use", false);
//        this.reload = this.createAction("reload", false);
//        this.shoot = this.createAction("shoot", false);
//        this.inspect = this.createAction("inspect", false);
//
//        action = null;
//        lastAction = null;
//
//        base = null;
//    }
//
//
//    public ActionPlayback createAction(String animationName, boolean looping)
//    {
//        return this.createAction(animationName, looping, 1);
//    }
//
//
//    public ActionPlayback createAction(String animationName, boolean looping, int priority)
//    {
//        Animation action = animations.get(animationName);
//
//
//        if (action == null)
//        {
//            return null;
//        }
//
//        return new ActionPlayback(action, new ActionConfig(animationName), looping, priority);
//    }
//
//    public void update(EntityLivingBase target)
//    {
//        /* Fix issue with morphs sudden running action */
//        if (this.prevX == Float.MAX_VALUE)
//        {
//            this.prevX = target.posX;
//            this.prevZ = target.posZ;
//        }
//
//        this.controlBaseLayer();
//
//        this.controlActionLayer(target);
//
//        /* Update primary actions */
//
//        if (this.base != null)
//        {
//            this.base.update();
//        }
//
//        if (this.action != null)
//        {
//            this.action.update();
//        }
//
//        if (this.lastAction != null)
//        {
//            this.lastAction.update();
//        }
//
//        /* Update secondary actions */
//        Iterator<ActionPlayback> it = this.actions.iterator();
//
//        while (it.hasNext())
//        {
//            ActionPlayback action = it.next();
//
//            action.update();
//
//            if (action.finishedFading() && action.isFadingModeOut())
//            {
//                action.stopFade();
//                it.remove();
//            }
//        }
//    }
//
//    protected void controlBaseLayer() {
//        if(base == null) {
//            this.setBaseAnimation(this.staticIdle);
//        }
//
//        if(base != this.staticIdle && base.getTick(1f) >= base.action.getLengthInTicks()) {
//            this.setBaseAnimation(this.staticIdle);
//        }
//    }
//
//    protected void controlActionLayer(EntityLivingBase target)
//    {
//        double dx = target.posX - this.prevX;
//        double dz = target.posZ - this.prevZ;
//        boolean creativeFlying = target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.isFlying;
//        boolean wet = target.isInWater();
//        final float threshold = creativeFlying ? 0.1F : (wet ? 0.025F : 0.01F);
//        boolean moves = Math.abs(dx) > threshold || Math.abs(dz) > threshold;
//
//
//        if (wet)
//        {
//            this.setActionAnimation(!moves ? this.swimmingIdle : this.swimming);
//        }
//        else if (creativeFlying || target.isElytraFlying())
//        {
//            this.setActionAnimation(!moves ? this.flyingIdle : this.flying);
//        }
//        else
//        {
//            if (target.isSneaking())
//            {
//                this.setActionAnimation(!moves ? this.crouchingIdle : this.crouching);
//            }
//            else if (!target.onGround && target.motionY < 0 && target.fallDistance > 1.25)
//            {
//                this.setActionAnimation(this.falling);
//            }
//            else if (target.isSprinting() && this.sprinting != null)
//            {
//                this.setActionAnimation(this.sprinting);
//            }
//            else
//            {
//                this.setActionAnimation(!moves ? this.idle : this.running);
//            }
//
//            if (target.onGround && !this.wasOnGround && !target.isSprinting() && this.prevMY < -0.5)
//            {
//                this.addAction(this.land);
//            }
//        }
//
//        if (!target.onGround && this.wasOnGround && Math.abs(target.motionY) > 0.2F)
//        {
//            this.addAction(this.jump);
//            this.wasOnGround = false;
//        }
//
//        if (target.hurtTime == target.maxHurtTime - 1)
//        {
//            this.addAction(this.hurt);
//        }
//
//        if (target.isSwingInProgress && target.swingProgress == 0 && !target.isPlayerSleeping())
//        {
//            this.addAction(this.swipe);
//        }
//
//        this.prevX = target.posX;
//        this.prevZ = target.posZ;
//        this.prevMY = target.motionY;
//
//        this.wasOnGround = target.onGround;
//    }
//
//    public void setBaseAnimation(ActionPlayback animation)
//    {
//        if (this.base == animation || animation == null)
//        {
//            return;
//        }
//
//        if (this.base != null && animation.priority < this.base.priority)
//        {
//            return;
//        }
//
//
//        this.base = animation;
//        this.base.reset();
//        this.base.fadeIn();
//        this.base.setSpeed(1f);
//    }
//
//    public void setActionAnimation(ActionPlayback action)
//    {
//        if (this.action == action || action == null)
//        {
//            return;
//        }
//
//        if (this.action != null && action.priority < this.action.priority)
//        {
//            return;
//        }
//
//        if (this.action != null)
//        {
//            this.lastAction = this.action;
//        }
//
//        this.action = action;
//        this.action.reset();
//        this.action.fadeIn();
//    }
//
//
//    public void addAction(ActionPlayback action)
//    {
//        if (action == null)
//        {
//            return;
//        }
//
//        if (this.actions.contains(action))
//        {
//            action.reset();
//
//            return;
//        }
//
//        action.reset();
//        action.fadeIn();
//        this.actions.add(action);
//    }
//
//    public void applyActions(EntityLivingBase target, Model armature, float partialTicks)
//    {
////        Pose mainLayer = new Pose();
////        if (this.base != null)
////        {
////            Animator.animate(target, mainLayer, this.base.action, this.base.getTick(partialTicks), 1F);
////        }
////
////        Pose additiveLayer = new Pose();
////        if (this.lastAction != null && this.action.isFading())
////        {
////            Animator.animate(target, additiveLayer, this.lastAction.action, this.lastAction.getTick(partialTicks), 1F);
////        }
////
////        if (this.action != null)
////        {
////            float fade = this.action.isFading() ? this.action.getFadeFactor(partialTicks) : 1F;
////
////            Animator.animate(target, additiveLayer, this.action.action, this.action.getTick(partialTicks), fade);
////        }
//
////        for (ActionPlayback action : this.actions)
////        {
////            if (action.isFading())
////            {
////                HandsAnimator.animate(target, armature, action.action, partialTicks, action.getFadeFactor(partialTicks), true);
////            }
////            else
////            {
////                HandsAnimator.animate(target, armature, action.action, partialTicks, 1F, true);
////            }
////        }
////        Animator.addPose(mainLayer, additiveLayer);
////
////        Animator.applyPose(armature, mainLayer);
//    }
//}
