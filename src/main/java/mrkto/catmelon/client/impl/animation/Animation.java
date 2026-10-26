package mrkto.catmelon.client.impl.animation;

import mrkto.catmelon.api.Pose;
import mrkto.catmelon.api.Transform;
import mrkto.catmelon.api.animation.IAnimation;
import mrkto.catmelon.api.animation.IAction;

import javax.vecmath.Vector3f;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Animation implements IAnimation {
    public double length;
    public String id;
    public boolean loop;
    public boolean override;

    public final Map<String, AnimationBone> bones = new HashMap<>();

    public KeyframesTimeLine<ActionsKeyframe> actions;

    public Animation(String id, double length, boolean loop, boolean override) {
        this.id = id;
        this.length = length;
        this.loop = loop;
        this.override = override;
    }

    public void animatePose(Pose pose, double time) {
        for (Map.Entry<String, AnimationBone> bone : bones.entrySet()) {
            Transform transform = pose.bones.computeIfAbsent(bone.getKey(), (key) -> new Transform());
            animateBone(transform, bone.getValue(), time);
        }
    }
    private void animateBone(Transform transform, AnimationBone bone, double time) {
        animateTransform(transform.translate, bone.position, time);
        animateTransform(transform.rotation, bone.rotation, time);
        animateTransform(transform.scale, bone.scale, time);
    }
    private void animateTransform(Vector3f output, KeyframesTimeLine<Keyframe> keyframes, double time) {
        if(keyframes.getKeyframes().isEmpty()) {
            return;
        }
        Keyframe firstKeyframe = keyframes.getKeyframe(0);
        if(keyframes.getKeyframes().size() == 1 || time <= firstKeyframe.time) {
            output.set((float) firstKeyframe.x.get(), (float) firstKeyframe.y.get(), (float) firstKeyframe.z.get());
            return;
        }
        Keyframe lastKeyframe = keyframes.getKeyframe(keyframes.getKeyframes().size() - 1);
        if(lastKeyframe.time <= time) {
            output.set((float) lastKeyframe.x.get(), (float) lastKeyframe.y.get(), (float) lastKeyframe.z.get());
            return;
        }

        int low = 0;
        int high = keyframes.getKeyframes().size() - 1;
        while (low + 1 != high) {
            int mid = (low + high) / 2;
            Keyframe midKeyframe = keyframes.getKeyframe(mid);
            if(midKeyframe.time > time) {
                high = mid;
            } else if(midKeyframe.time < time) {
                low = mid;
            } else {
                output.set((float) midKeyframe.x.get(), (float) midKeyframe.y.get(), (float) midKeyframe.z.get());
                return;
            }
        }
        Keyframe prev = keyframes.getKeyframe(low);
        Keyframe next = keyframes.getKeyframe(high);
        double factor = (time - prev.time) / (next.time - prev.time);
        prev.interpolation.interpolate(output, prev, factor);
    }
    public void collectEvents(double prevTime, double time, List<IAction> result) {
        if (actions == null || actions.getKeyframes().isEmpty()) {
            return;
        }

        if (time <= prevTime) {
            return;
        }

        List<ActionsKeyframe> keyframes = actions.getKeyframes();

        for (ActionsKeyframe keyframe : keyframes) {
            double t = keyframe.time;

            if (t > prevTime && t <= time) {
                result.addAll(keyframe.getActions());
            }

            if (t > time) {
                break;
            }
        }
    }

    @Override
    public double getLength() {
        return length;
    }

    @Override
    public boolean isLooping() {
        return loop;
    }
}
