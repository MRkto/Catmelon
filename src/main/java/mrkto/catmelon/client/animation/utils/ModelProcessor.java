package mrkto.catmelon.client.animation.utils;

import mrkto.catmelon.client.animation.data.Pose;
import mrkto.catmelon.client.animation.data.Transform;
import mrkto.catmelon.client.animation.data.model.Model;
import mrkto.catmelon.client.animation.data.model.ModelBone;
import mrkto.catmelon.client.animation.data.model.parts.IModelPart;

public class ModelProcessor {
    public MatrixStack matrixStack = new MatrixStack(32);
    public <T extends IModelPart> void processModel(Class<T> callClazz, RenderCall<T> call, Model model, Pose pose) {
        for (ModelBone bone : model.bones) {
            processRenderRecursively(callClazz,call, bone, pose, matrixStack);
        }
    }
    public <T extends IModelPart> void processModelStatic(Class<T> callClazz, RenderCall<T> call, Model model) {
        for (ModelBone bone : model.bones) {
            processRenderRecursively(callClazz, call, bone, matrixStack);
        }
    }
    private <T extends IModelPart> void processRenderRecursively(Class<T> callClazz, RenderCall<T> call, ModelBone bone, MatrixStack matrixStack) {
        matrixStack.push();

        matrixStack.translate(bone.pivot.x, bone.pivot.y, bone.pivot.z);
        matrixStack.rotateZ(bone.rotation.getZ());
        matrixStack.rotateY(bone.rotation.getY());
        matrixStack.rotateX(bone.rotation.getX());
        matrixStack.translate(-bone.pivot.x, -bone.pivot.y, -bone.pivot.z);

        if(bone.visible) {
            for (IModelPart part : bone.parts) {
                if(callClazz.isInstance(part)) {
                    call.call((T) part, matrixStack);
                }
            }
            for (ModelBone child : bone.children) {
                processRenderRecursively(callClazz, call, child, matrixStack);
            }
        }
        matrixStack.pop();
    }
    private <T extends IModelPart> void processRenderRecursively(Class<T> callClazz, RenderCall<T> call, ModelBone bone, Pose pose, MatrixStack matrixStack) {
        matrixStack.push();
        Transform transform = pose.bones.get(bone.id);
        if(transform == null) {
            matrixStack.translate(bone.pivot.x, bone.pivot.y, bone.pivot.z);
            matrixStack.rotateZ(bone.rotation.getZ());
            matrixStack.rotateY(bone.rotation.getY());
            matrixStack.rotateX(bone.rotation.getX());
        } else {
            matrixStack.translate(bone.pivot.x + transform.translate.x, bone.pivot.y + transform.translate.y, bone.pivot.z + transform.translate.z);
            matrixStack.rotateZ(bone.rotation.getZ() + transform.rotation.z);
            matrixStack.rotateY(bone.rotation.getY() + transform.rotation.y);
            matrixStack.rotateX(bone.rotation.getX() + transform.rotation.x);


            matrixStack.scale(transform.scale.x, transform.scale.y, transform.scale.z);
        }
        matrixStack.translate(-bone.pivot.x, -bone.pivot.y, -bone.pivot.z);
        if(bone.visible) {
            for (IModelPart part : bone.parts) {
                if(callClazz.isInstance(part)) {
                    call.call((T) part, matrixStack);
                }
            }
            for (ModelBone child : bone.children) {
                processRenderRecursively(callClazz, call, child, pose, matrixStack);
            }
        }
        matrixStack.pop();
    }
    @FunctionalInterface
    public interface RenderCall<C extends IModelPart> {
        void call(C part, MatrixStack stack);
    }
}
