package mrkto.catmelon.api;

import java.util.HashMap;
import java.util.Map;

public class Pose {
    public final Map<String, Transform> bones = new HashMap<>();

    public void reset() {
        for (Transform transform : bones.values()) {
            transform.reset();
        }
    }
    public void clear() {
        bones.clear();
    }


    public void overwrite(Pose other) {
        for (Map.Entry<String, Transform> entry : other.bones.entrySet()) {
            String bone = entry.getKey();
            Transform otherTransform = entry.getValue();
            Transform current = bones.computeIfAbsent(bone, k -> new Transform());
            current.copyFrom(otherTransform);
        }
    }

    public void add(Pose other) {
        for (Map.Entry<String, Transform> entry : other.bones.entrySet()) {
            String bone = entry.getKey();
            Transform otherTransform = entry.getValue();
            Transform current = bones.computeIfAbsent(bone, k -> new Transform());
            current.add(otherTransform);
        }
    }

    public void blend(Pose other, float weight) {
        if (weight <= 0f) return;
        if (weight >= 1f) {
            overwrite(other);
            return;
        }

        for (Map.Entry<String, Transform> entry : other.bones.entrySet()) {
            String bone = entry.getKey();
            Transform otherTransform = entry.getValue();
            Transform current = bones.computeIfAbsent(bone, k -> new Transform());
            current.blend(otherTransform, weight);
        }
    }
}
