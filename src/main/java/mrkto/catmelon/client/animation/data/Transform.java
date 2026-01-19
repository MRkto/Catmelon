package mrkto.catmelon.client.animation.data;

import mchorse.mclib.utils.Interpolations;

import javax.vecmath.Vector3f;

public class Transform {
    public Vector3f translate = new Vector3f();
    public Vector3f scale = new Vector3f(1.0F, 1.0F, 1.0F);
    public Vector3f rotation = new Vector3f();

    public void reset() {
        translate.set(0, 0, 0);
        rotation.set(0, 0, 0);
        scale.set(1, 1, 1);
    }

    public void copyFrom(Transform other) {
        this.translate.set(other.translate);
        this.rotation.set(other.rotation);
        this.scale.set(other.scale);
    }

    public void add(Transform other) {
        this.translate.add(other.translate);
        this.rotation.add(other.rotation);
        this.scale.x *= other.scale.x;
        this.scale.y *= other.scale.y;
        this.scale.z *= other.scale.z;
    }

    public void blend(Transform other, float weight) {
        this.translate.x = Interpolations.lerp(this.translate.x, other.translate.x, weight);
        this.rotation.x = Interpolations.lerp(this.rotation.x, other.rotation.x, weight);
        this.scale.x = Interpolations.lerp(this.scale.x, other.scale.x, weight);
        this.translate.y = Interpolations.lerp(this.translate.y, other.translate.y, weight);
        this.rotation.y = Interpolations.lerp(this.rotation.y, other.rotation.y, weight);
        this.scale.y = Interpolations.lerp(this.scale.y, other.scale.y, weight);
        this.translate.z = Interpolations.lerp(this.translate.z, other.translate.z, weight);
        this.rotation.z = Interpolations.lerp(this.rotation.z, other.rotation.z, weight);
        this.scale.z = Interpolations.lerp(this.scale.z, other.scale.z, weight);
    }
}
