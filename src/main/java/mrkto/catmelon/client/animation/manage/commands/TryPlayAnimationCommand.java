package mrkto.catmelon.client.animation.manage.commands;

public class TryPlayAnimationCommand implements IAnimatorCommand{
    public String animation;
    public TryPlayAnimationCommand(String animation) {
        this.animation = animation;
    }
}
