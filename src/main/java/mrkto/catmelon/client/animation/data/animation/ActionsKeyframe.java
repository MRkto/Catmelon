package mrkto.catmelon.client.animation.data.animation;

import mrkto.catmelon.client.animation.data.animation.actions.IAction;

import java.util.ArrayList;
import java.util.List;

public class ActionsKeyframe extends BaseKeyframe<ActionsKeyframe> {
    private final List<IAction> actions = new ArrayList<>();
    public ActionsKeyframe(double time) {
        super(time);
    }
    public List<IAction> getActions() {
        return actions;
    }
}
