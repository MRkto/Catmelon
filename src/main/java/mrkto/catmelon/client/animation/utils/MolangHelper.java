package mrkto.catmelon.client.animation.utils;

import mchorse.mclib.math.Variable;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.math.molang.expressions.MolangExpression;
import mchorse.mclib.utils.Interpolations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MolangHelper {
    public MolangHelper() {
    }
    //query.is_on_ground
    //query.is_sprinting
    //query.is_sneaking
    //query.is_colliding
    //query.is_in_water
    //query.is_wet
    //query.is_on_fire
    //
    //query.health
    //query.max_health
    //
    //query.anim_time
    //query.delta_time
    //query.life_time
    //query.hurt_time
    //query.death_time
    //query.time_of_day
    //
    //query.forward_velocity
    //query.strafe_velocity
    //query.horizontal_velocity
    //query.vertical_velocity
    //query.velocity
    //
    //query.head_x_rotation
    //query.head_y_rotation
    //query.body_y_rotation
    //
    //query.swing_hand
    //query.swing_progress
    //
    //query.hitbox_height
    //query.hitbox_width
    //
    //query.fall_distance
    //
    //query.actor_count
    //
    //bone.ditance_to_camera
    public static void registerVars(MolangParser parser) {
        parser.register(new Variable("query.is_on_ground", 0d));
        parser.register(new Variable("query.is_sprinting", 0d));
        parser.register(new Variable("query.is_sneaking", 0d));
        parser.register(new Variable("query.is_colliding", 0d));
        parser.register(new Variable("query.is_in_water", 0d));
        parser.register(new Variable("query.is_wet", 0d));
        parser.register(new Variable("query.is_on_fire", 0d));

        parser.register(new Variable("query.health", 0d));
        parser.register(new Variable("query.max_health", 0d));

        parser.register(new Variable("query.anim_time", 0d));
        parser.register(new Variable("query.delta_time", 0d));
        parser.register(new Variable("query.life_time", 0d));
        parser.register(new Variable("query.hurt_time", 0d));
        parser.register(new Variable("query.death_time", 0d));
        parser.register(new Variable("query.time_of_day", 0d));

        parser.register(new Variable("query.head_x_rotation", 0d));
        parser.register(new Variable("query.head_y_rotation", 0d));
        parser.register(new Variable("query.body_y_rotation", 0d));

        parser.register(new Variable("query.forward_velocity", 0d));
        parser.register(new Variable("query.strafe_velocity", 0d));
        parser.register(new Variable("query.horizontal_velocity", 0d));
        parser.register(new Variable("query.vertical_velocity", 0d));
        parser.register(new Variable("query.velocity", 0d));
        parser.register(new Variable("query.forward_velocity.normalized", 0d));
        parser.register(new Variable("query.strafe_velocity.normalized", 0d));

        parser.register(new Variable("query.limb_swing", 0d));
        parser.register(new Variable("query.limb_swing_amount", 0d));

        parser.register(new Variable("query.swing_hand", 0d));
        parser.register(new Variable("query.swing_progress", 0d));

        parser.register(new Variable("query.hitbox_height", 0d));
        parser.register(new Variable("query.hitbox_width", 0d));

        parser.register(new Variable("query.fall_distance", 0d));

        parser.register(new Variable("bone.ditance_to_camera", 0d));
    }
    public static void setMolandTime(MolangParser parser, float frame) {
        parser.setValue("query.anim_time", frame / 20);
    }
    public static void setMolangVariablesEntity(MolangParser parser, EntityLivingBase target) {
        float partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();

        parser.setValue("query.is_on_ground", booleanToDouble(target.onGround));
        parser.setValue("query.is_sprinting", booleanToDouble(target.isSprinting()));
        parser.setValue("query.is_sneaking", booleanToDouble(target.isSneaking()));
        parser.setValue("query.is_colliding", booleanToDouble(target.collided));
        parser.setValue("query.is_in_water", booleanToDouble(target.isInWater()));
        parser.setValue("query.is_wet", booleanToDouble(target.isWet()));
        parser.setValue("query.is_on_fire", booleanToDouble(target.isBurning()));

        parser.setValue("query.health", target.getHealth());
        parser.setValue("query.max_health", target.getMaxHealth());

        parser.setValue("query.delta_time", partialTicks);
        parser.setValue("query.life_time", (target.ticksExisted + partialTicks) / 20);
        parser.setValue("query.hurt_time", target.hurtTime);
        parser.setValue("query.death_time", target.deathTime);
        parser.setValue("query.time_of_day", normalizeTime(target.getEntityWorld().getTotalWorldTime()));

        double pitch = Interpolations.lerp(target.prevRotationPitch, target.rotationPitch, partialTicks);
        double yaw = Interpolations.lerp(target.prevRotationYawHead, target.rotationYawHead, partialTicks);
        double body_yaw = Interpolations.lerp(target.prevRenderYawOffset, target.renderYawOffset, partialTicks);
        parser.setValue("query.head_x_rotation", pitch);
        parser.setValue("query.head_y_rotation", yaw - body_yaw);
        parser.setValue("query.body_y_rotation", body_yaw);

        double radians = Math.toRadians(body_yaw);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double dx = target.prevPosX - target.posX;
        double dy = target.prevPosY - target.posY;
        double dz = target.prevPosZ - target.posZ;

        double forward = -(dz * cos - dx * sin);
        double strafing = -(dx * cos + dz * sin);
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        parser.setValue("query.forward_velocity", forward);
        parser.setValue("query.forward_velocity.normalized", (forward > -0.01 ? 1 : 0) - (forward < -0.01 ? 1 : 0));
        parser.setValue("query.strafe_velocity", strafing);
        parser.setValue("query.strafe_velocity.normalized", (strafing > 0 ? 1 : 0) - (strafing < 0 ? 1 : 0));
        parser.setValue("query.horizontal_velocity", horizontal);
        parser.setValue("query.vertical_velocity", target.onGround ? 0 : dy);
        parser.setValue("query.velocity", target.onGround ? horizontal : Math.sqrt(dx * dx + dy * dy + dz * dz));


        parser.setValue("query.limb_swing", target.limbSwing - target.limbSwingAmount * (1.0F - partialTicks));
        parser.setValue("query.limb_swing_amount", Interpolations.lerp(target.prevLimbSwingAmount, target.limbSwingAmount, partialTicks));

        parser.setValue("query.swing_hand", Math.abs(target.getPrimaryHand().ordinal() - 1));
        parser.setValue("query.swing_progress", Interpolations.lerp(target.prevSwingProgress, target.swingProgress, partialTicks));

        parser.setValue("query.hitbox_height", target.height);
        parser.setValue("query.hitbox_width", target.width);

        parser.setValue("query.fall_distance", target.fallDistance);
    }

    private static double normalizeTime(long totalWorldTime) {
        return ((double)totalWorldTime) / 24000d;
    }

    private static double booleanToDouble(boolean bool) {
        return bool ? 1d : 0d;
    }
}