package mrkto.catmelon.client.animation.manage;

import mrkto.catmelon.client.animation.data.Pose;
import mrkto.catmelon.client.animation.manage.animators.IAnimator;
import mrkto.catmelon.client.animation.manage.commands.IAnimatorCommand;
import mrkto.catmelon.client.animation.manage.state.StateMachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnimationController<C> {
    private AnimatorChain animatorChain;
    private StateMachine<C> stateMachine;

    private final AnimationResult animationResult = new AnimationResult(new ArrayList<>(), new Pose());
    private final List<IAnimatorCommand> commands = new ArrayList<>();

    float prevPartialTicks = 0f;
    public AnimationController(IAnimator[] animators, StateMachine<C> stateMachine) {
        this.stateMachine = stateMachine;
        animatorChain = new AnimatorChain(Arrays.asList(animators));
    }
    public void update(C object) {
        commands.clear();
        stateMachine.update(commands, object);
        for (IAnimatorCommand command : commands) {
            animatorChain.processCommand(command);
        }
        animatorChain.update();
        prevPartialTicks = 0f;
    }
    public AnimationResult animate(float partialTicks) {
                animationResult.getActions().clear();
        animationResult.getPose().clear();

        animatorChain.animate(animationResult.getPose(), animationResult.getActions(), partialTicks, prevPartialTicks);
        prevPartialTicks = partialTicks;
        return animationResult;
    }
    public <T extends IAnimator> T getAnimator(Class<T> clazz) {
        return animatorChain.getAnimator(clazz);
    }
    
}
