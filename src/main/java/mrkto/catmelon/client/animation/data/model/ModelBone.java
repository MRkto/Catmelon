package mrkto.catmelon.client.animation.data.model;

import mrkto.catmelon.client.animation.data.model.parts.IModelPart;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.List;

public class ModelBone {
    public final String id;
    public List<ModelBone> children = new ArrayList<>();
    public ModelBone parent;
    public List<IModelPart> parts = new ArrayList<>();
    public boolean visible = true;

    public Vector3f pivot = new Vector3f();
    public Vector3f rotation = new Vector3f();

    public ModelBone(String id) {
        this.id = id;
    }
}
