package mrkto.catmelon.client.animation.manage.state;

import mrkto.catmelon.client.animation.manage.commands.IAnimatorCommand;

import java.util.List;

public interface StateMachine<C> {
    void update(List<IAnimatorCommand> commands, C object);
}
