package mrkto.catmelon.api.model;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class SkeletonBone {
    public final String name;
    public final int id;

    public List<SkeletonBone> children = new ArrayList<>();
    public SkeletonBone parent;

    public final Vector3f pivot = new Vector3f();
    public final Vector3f rotation = new Vector3f();

    public SkeletonBone(String name, int id) {
        this.name = name;
        this.id = id;
    }

}
