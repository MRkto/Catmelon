package mrkto.catmelon.client.impl.animation.actions;

import mrkto.catmelon.api.animation.IAction;

public class InstructionAction implements IAction {
    public final String instruction;
    public InstructionAction(String instruction) {
        this.instruction = instruction;
    }
}
