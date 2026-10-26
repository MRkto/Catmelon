package mrkto.catmelon.client.render.scene;

import mrkto.catmelon.api.Pose;
import mrkto.catmelon.api.model.Skeleton;
import mrkto.catmelon.api.model.SkeletonSocket;
import mrkto.catmelon.client.CatmelonClient;
import mrkto.catmelon.client.utils.MatrixStack;

import javax.vecmath.Matrix4f;
import java.util.List;

public class SkeletonInstance {
    public final Skeleton skeleton;

    public final Matrix4f[] world;
    public final Matrix4f[] socket;

    private boolean isUpdated = false;

    public SkeletonInstance(Skeleton skeleton) {
        this.skeleton = skeleton;
        this.world = new Matrix4f[skeleton.getBonesCount()];
        this.socket = new Matrix4f[skeleton.getSocketsCount()];
        for (int i = 0; i < skeleton.getBonesCount(); i++) {
            this.world[i] = new Matrix4f();
        }
        for (int i = 0; i < skeleton.getSocketsCount(); i++) {
            this.socket[i] = new Matrix4f();
        }
    }
    public void reset() {
        this.isUpdated = false;
    }
    public void update(Pose pose) {
        if(isUpdated) {
            return;
        }

        CatmelonClient.getModelProcessor().processModel(this::processSkeleton, skeleton, pose);

        List<SkeletonSocket> sockets = skeleton.getSockets();

        MatrixStack matrixStack = CatmelonClient.getModelProcessor().matrixStack;
        matrixStack.push();
        for (int i = 0, socketsSize = sockets.size(); i < socketsSize; i++) {
            SkeletonSocket socketI = sockets.get(i);
            int boneIndex = socketI.boneIndex;
            matrixStack.getModelMatrix().set(world[boneIndex]);
            matrixStack.translate(socketI.position.x, socketI.position.y, socketI.position.z);
            matrixStack.rotateZ(socketI.rotation.z);
            matrixStack.rotateY(socketI.rotation.y);
            matrixStack.rotateX(socketI.rotation.x);
            socket[i].set(matrixStack.getModelMatrix());
        }
        matrixStack.pop();
        isUpdated = true;
    }

    private void processSkeleton(String name, int id, MatrixStack stack) {
        world[id].set(stack.getModelMatrix());
    }
    public boolean isUpdated() {
        return isUpdated;
    }
}
