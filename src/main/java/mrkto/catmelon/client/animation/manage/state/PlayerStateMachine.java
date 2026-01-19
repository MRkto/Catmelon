package mrkto.catmelon.client.animation.manage.state;


import mrkto.catmelon.client.animation.manage.commands.IAnimatorCommand;
import mrkto.catmelon.client.animation.manage.commands.MoveStateCommand;
import mrkto.catmelon.client.animation.manage.commands.TryPlayAnimationCommand;
import net.minecraft.client.entity.AbstractClientPlayer;

import java.util.List;

public class PlayerStateMachine implements StateMachine<AbstractClientPlayer> {
    private boolean wasOnGround;
    private double prevMY;
    @Override
    public void update(List<IAnimatorCommand> commands, AbstractClientPlayer player) {
        updateInner(commands, player);

        this.wasOnGround = player.onGround;
        this.prevMY = player.posY - player.prevPosY;
    }
    private void updateInner(List<IAnimatorCommand> commands, AbstractClientPlayer player) {
        double dx = player.posX - player.prevPosX;
        double dz = player.posZ - player.prevPosZ;
        double dy = player.posY - player.prevPosY;
        boolean creativeFlying = player.capabilities.isFlying;
        boolean wet = player.isInWater();
        final float threshold = creativeFlying ? 0.3F : (wet ? 0.025F : 0.02F);
        boolean moves = Math.abs(dx) > threshold || Math.abs(dz) > threshold;
        if (player.hurtTime == player.maxHurtTime - 1) {
            commands.add(new TryPlayAnimationCommand("hurt"));
        }
        if(creativeFlying) {
            commands.add(new MoveStateCommand(moves ? MoveStateCommand.State.FLYING : MoveStateCommand.State.FLYING_IDLE));
            return;
        }
        if (!player.onGround && dy < -0.4) {
//            moveLayer.setCurrentState(MoveState.FALLING);
            return;
        }
        if(wet) {
            commands.add(new MoveStateCommand(moves ? MoveStateCommand.State.SWIMMING : MoveStateCommand.State.SWIMMING_IDLE));
            return;
        }
        if (!player.onGround && this.wasOnGround && Math.abs(player.motionY) > 0.2F) {
            commands.add(new TryPlayAnimationCommand("jump"));
        }
        if (player.onGround && !this.wasOnGround && this.prevMY < -0.5) {
            commands.add(new TryPlayAnimationCommand("land"));
        }
        if(player.isSneaking()) {
            commands.add(new MoveStateCommand(moves ? MoveStateCommand.State.CROUCHING : MoveStateCommand.State.CROUCHING_IDLE));
            return;
        }
        if(moves) {
            double body_yaw = player.renderYawOffset / 180 * Math.PI;
            double forward = (dz * Math.cos(body_yaw) - dx * Math.sin(body_yaw));
            commands.add(new MoveStateCommand(player.isSprinting() ? MoveStateCommand.State.SPRINTING : forward < -0.01 ? MoveStateCommand.State.RUNNING_BACKWARD : MoveStateCommand.State.RUNNING_FORWARD));
            return;
        }
        commands.add(new MoveStateCommand(MoveStateCommand.State.IDLE));
    }
}
