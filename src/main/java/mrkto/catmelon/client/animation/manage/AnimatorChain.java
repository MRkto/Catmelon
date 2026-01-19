package mrkto.catmelon.client.animation.manage;

import mrkto.catmelon.client.animation.data.Pose;
import mrkto.catmelon.client.animation.data.animation.actions.IAction;
import mrkto.catmelon.client.animation.manage.animators.IAnimator;
import mrkto.catmelon.client.animation.manage.commands.IAnimatorCommand;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnimatorChain {
    private final List<IAnimator> animators;

    public AnimatorChain(List<IAnimator> animators) {
        Set<Class<? extends IAnimator>> seen = new HashSet<>();
        if(animators.stream().map(IAnimator::getClass).anyMatch(c -> !seen.add(c))) {
            throw new RuntimeException("Animator chain contains same animator classes");
        }
        this.animators = animators;
    }

    public void update() {
        animators.forEach(IAnimator::update);
    }
    public boolean processCommand(IAnimatorCommand command) {
        for (IAnimator animator : animators) {
            if(animator.processCommand(command)) {
                return true;
            }
        }
        return false;
    }
    public void animate(Pose pose, List<IAction> actions, float partialTicks, float prevPartialTicks) {
        for(int i = animators.size() - 1; i >= 0; i--) {
            IAnimator action = animators.get(i);
            action.animate(pose, actions, partialTicks, prevPartialTicks);
        }
    }
    @SuppressWarnings("unchecked")
    public <T extends IAnimator> T getAnimator(Class<T> clazz) {
        for (IAnimator animator : animators) {
            if(animator.getClass().equals(clazz)) {
                return (T) animator;
            }
        }
        return null;
    }
}
