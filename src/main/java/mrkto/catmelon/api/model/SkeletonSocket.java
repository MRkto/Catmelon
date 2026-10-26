package mrkto.catmelon.api.model;

import javax.vecmath.Vector3f;

public class SkeletonSocket {
    public final int boneIndex;

    public final Vector3f position;
    public final Vector3f rotation;

    public SkeletonSocket(int boneIndex, Vector3f position, Vector3f rotation) {
        this.boneIndex = boneIndex;
        this.position = position;
        this.rotation = rotation;
    }
}
