package mrkto.catmelon.client.animation.manage.commands;

public class MoveStateCommand implements IAnimatorCommand {
    public State state;
    public MoveStateCommand(State state) {
        this.state = state;
    }
    public enum State {
        IDLE,
        CROUCHING_IDLE,
        CROUCHING,
        FLYING_IDLE,
        FLYING,
        SWIMMING_IDLE,
        SWIMMING,
        RUNNING_FORWARD,
        RUNNING_BACKWARD,
        SPRINTING,
        FALLING;
    }
}
