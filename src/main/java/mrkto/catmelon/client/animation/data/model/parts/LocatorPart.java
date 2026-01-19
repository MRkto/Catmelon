package mrkto.catmelon.client.animation.data.model.parts;


import javax.vecmath.Vector3f;

public class LocatorPart implements IModelPart {
    public String name;
    public Vector3f pivot = new Vector3f();
    public LocatorPart(String name) {
        this.name = name;
    }
}
