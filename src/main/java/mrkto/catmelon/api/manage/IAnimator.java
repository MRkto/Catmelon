package mrkto.catmelon.api.manage;

import mrkto.catmelon.api.Pose;
import mrkto.catmelon.api.animation.IAction;

import java.util.List;

public interface IAnimator<C> {
    void update(C object);
    void animate(Pose pose, List<IAction> actions, float partialTicks, float prevPartialTicks);
}
