package mrkto.catmelon.client.utils;

import mrkto.catmelon.api.Pose;
import mrkto.catmelon.api.Transform;
import mrkto.catmelon.api.model.Skeleton;
import mrkto.catmelon.api.model.SkeletonBone;

import java.util.List;

public class ModelProcessor {
    public MatrixStack matrixStack = new MatrixStack(32);
    public void processModel(RenderCall call, Skeleton model, Pose pose) {
        List<SkeletonBone> bones = model.getBones();
        for (int i = 0, bonesSize = bones.size(); i < bonesSize; i++) {
            SkeletonBone bone = bones.get(i);
            processRenderRecursively(call, bone, pose, matrixStack);
        }
    }
    public void processModelStatic(RenderCall call, Skeleton model) {
        List<SkeletonBone> bones = model.getBones();
        for (int i = 0, bonesSize = bones.size(); i < bonesSize; i++) {
            SkeletonBone bone = bones.get(i);
            processRenderRecursively(call, bone, matrixStack);
        }
    }
    private void processRenderRecursively(RenderCall call, SkeletonBone bone, MatrixStack matrixStack) {
        matrixStack.push();

        matrixStack.translate(bone.pivot.x, bone.pivot.y, bone.pivot.z);
        matrixStack.rotateZ(bone.rotation.getZ());
        matrixStack.rotateY(bone.rotation.getY());
        matrixStack.rotateX(bone.rotation.getX());
        matrixStack.translate(-bone.pivot.x, -bone.pivot.y, -bone.pivot.z);

        call.call(bone.name, bone.id, matrixStack);
        for (SkeletonBone child : bone.children) {
            processRenderRecursively(call, child, matrixStack);
        }

        matrixStack.pop();
    }
    private void processRenderRecursively(RenderCall call, SkeletonBone bone, Pose pose, MatrixStack matrixStack) {
        matrixStack.push();
        Transform transform = pose.bones.get(bone.name);
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

        call.call(bone.name, bone.id, matrixStack);
        List<SkeletonBone> children = bone.children;
        for (int i = 0, childrenSize = children.size(); i < childrenSize; i++) {
            SkeletonBone child = children.get(i);
            processRenderRecursively(call, child, pose, matrixStack);
        }

        matrixStack.pop();
    }
    @FunctionalInterface
    public interface RenderCall {
        void call(String boneName, int boneID, MatrixStack stack);
    }
}
