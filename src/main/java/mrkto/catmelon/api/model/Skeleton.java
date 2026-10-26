package mrkto.catmelon.api.model;

import java.util.List;
import java.util.Map;

public final class Skeleton {
    private final List<SkeletonBone> bones;
    private final List<SkeletonSocket> sockets;
    private final Map<String, Integer> boneIds;
    private final Map<String, Integer> socketIds;

    public Skeleton(List<SkeletonBone> bones, List<SkeletonSocket> sockets, Map<String, Integer> bonesId, Map<String, Integer> socketIds) {
        this.bones = bones;
        this.sockets = sockets;
        this.boneIds = bonesId;
        this.socketIds = socketIds;
    }

    public List<SkeletonBone> getBones() {
        return bones;
    }
    public List<SkeletonSocket> getSockets() {
        return sockets;
    }
    public Integer getBoneId(String boneName) {
        return boneIds.get(boneName);
    }
    public Integer getSocketId(String socketName) {
        return socketIds.get(socketName);
    }
    public int getBonesCount() {
        return boneIds.size();
    }
    public int getSocketsCount() {
        return socketIds.size();
    }
}
