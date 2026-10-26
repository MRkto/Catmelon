package mrkto.catmelon.client.impl.model.parted;

import mchorse.mclib.client.render.VertexBuilder;
import mrkto.catmelon.api.model.IModel;
import mrkto.catmelon.api.model.IModelRenderer;
import mrkto.catmelon.api.model.Skeleton;
import mrkto.catmelon.client.CatmelonClient;
import mrkto.catmelon.client.render.scene.SkeletonInstance;
import mrkto.catmelon.client.utils.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartedModel implements IModel {
    public Map<String, List<IModelPart>> parts = new HashMap<>();

    public static class PartedModelRenderer implements IModelRenderer<PartedModel> {
        private IRenderablePart[] parts;
        private int[] partMatrices;
        private boolean[] visible;
        private PartedModel model;
        @Override
        public void init(Skeleton skeleton, PartedModel model) {
            this.model = model;
            int size = 0;
            for (List<IModelPart> entry : model.parts.values()) {
                for (IModelPart iModelPart : entry) {
                    if(iModelPart instanceof IRenderablePart){
                        size++;
                    }
                }
            }
            this.parts = new IRenderablePart[size];
            this.partMatrices = new int[size];
            this.visible = new boolean[size];
            int index = 0;
            for (Map.Entry<String, List<IModelPart>> entry : model.parts.entrySet()) {
                Integer indexI = skeleton.getBoneId(entry.getKey());
                if(indexI == null){
                    continue;
                }
                for (IModelPart iModelPart : entry.getValue()) {
                    if(iModelPart instanceof IRenderablePart){
                        parts[index] = (IRenderablePart) iModelPart;
                        partMatrices[index] = indexI;
                        visible[index++] = true;
                    }
                }
            }
        }

        @Override
        public void render(SkeletonInstance skeletonInstance, float partialTicks) {
            MatrixStack matrixStack = CatmelonClient.getModelProcessor().matrixStack;
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_TRIANGLES, VertexBuilder.getFormat(false, true, true, true));
            matrixStack.push();
            for (int i = 0; i < parts.length; i++) {
                if(!visible[i]){
                    continue;
                }
                matrixStack.getModelMatrix().set(skeletonInstance.world[partMatrices[i]]);
                parts[i].renderPart(buffer, matrixStack, partialTicks);
            }
            matrixStack.pop();
            tessellator.draw();
        }
        public void setVisible(int i, boolean visible){
            this.visible[i] = visible;
        }
        public boolean isVisible(int i){
            return this.visible[i];
        }
        public PartedModel getModel(){
            return this.model;
        }
    }
}
