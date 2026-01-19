package mrkto.catmelon.client.animation.manage;

import mrkto.catmelon.client.animation.data.Pose;
import mrkto.catmelon.client.animation.data.animation.actions.IAction;

import java.util.List;

public class AnimationResult {
    private final List<IAction> actions;
    private final Pose pose;
    public AnimationResult(List<IAction> actions, Pose pose) {
        this.actions = actions;
        this.pose = pose;
    }
    public List<IAction> getActions() {
        return actions;
    }
    public Pose getPose() {
        return pose;
    }

}
