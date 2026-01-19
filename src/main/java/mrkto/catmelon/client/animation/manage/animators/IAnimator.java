package mrkto.catmelon.client.animation.manage.animators;

import mrkto.catmelon.client.animation.data.Pose;
import mrkto.catmelon.client.animation.data.animation.actions.IAction;
import mrkto.catmelon.client.animation.manage.commands.IAnimatorCommand;

import java.util.List;

public interface IAnimator {
    void update();
    boolean processCommand(IAnimatorCommand command);
    void animate(Pose pose, List<IAction> actions, float partialTicks, float prevPartialTicks);
}
