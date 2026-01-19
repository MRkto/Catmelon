package mrkto.catmelon.client.animation.data.model.parts;

import mrkto.catmelon.client.animation.utils.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;

public interface IRenderablePart extends IModelPart {
    void renderPart(BufferBuilder builder, MatrixStack stack, float partialTicks);
}
