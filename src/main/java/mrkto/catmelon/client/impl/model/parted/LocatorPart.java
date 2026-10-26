package mrkto.catmelon.client.impl.model.parted;


import javax.vecmath.Vector3f;

public class LocatorPart implements IModelPart {
    public String name;
    public Vector3f position = new Vector3f();
    public Vector3f rotation = new Vector3f();
    public LocatorPart(String name) {
        this.name = name;
    }
}
