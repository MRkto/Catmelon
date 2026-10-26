package mrkto.catmelon.client.impl.model.parted;

import mrkto.catmelon.client.utils.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;

public interface IRenderablePart extends IModelPart {
    void renderPart(BufferBuilder builder, MatrixStack stack, float partialTicks);
}
