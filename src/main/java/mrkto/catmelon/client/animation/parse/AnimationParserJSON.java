package mrkto.catmelon.client.animation.parse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mchorse.mclib.math.Constant;
import mchorse.mclib.math.Operation;
import mchorse.mclib.math.Operator;
import mchorse.mclib.math.molang.MolangException;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.math.molang.expressions.MolangExpression;
import mchorse.mclib.math.molang.expressions.MolangMultiStatement;
import mchorse.mclib.math.molang.expressions.MolangValue;
import mrkto.catmelon.client.animation.data.animation.*;
import mrkto.catmelon.client.animation.data.animation.actions.InstructionAction;
import mrkto.catmelon.client.animation.data.animation.actions.ParticleAction;
import mrkto.catmelon.client.animation.data.animation.actions.SoundAction;
import mrkto.catmelon.client.animation.data.animation.interpolation.BaseInterpolation;
import mrkto.catmelon.client.animation.data.animation.interpolation.EasingInterpolation;
import mrkto.catmelon.client.animation.data.animation.interpolation.HermiteInterpolation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.*;

public class AnimationParserJSON {
    public static List<Animation> parse(MolangParser parser, JsonObject object) {
        JsonObject animations = object.getAsJsonObject("animations");
        if (animations == null) return Collections.emptyList();

        List<Animation> result = new ArrayList<>(animations.size());
        for (Map.Entry<String, JsonElement> entry : animations.entrySet()) {
            result.add(parseAnimation(parser, entry.getKey(), entry.getValue().getAsJsonObject()));
        }
        return result;
    }
    public static Animation parseAnimation(MolangParser parser, String id, JsonObject animationObject) {
        double length = animationObject.has("animation_length") ? animationObject.getAsJsonPrimitive("animation_length").getAsDouble() * 20f : 0d;
        boolean loop = animationObject.has("loop") && animationObject.getAsJsonPrimitive("loop").getAsBoolean();
        boolean override = animationObject.has("override_previous_animation") && animationObject.getAsJsonPrimitive("override_previous_animation").getAsBoolean();

        Animation animation = new Animation(id, length, loop, override);


        if (animationObject.has("bones")) {
            for (Map.Entry<String, JsonElement> entry : animationObject.get("bones").getAsJsonObject().entrySet()) {
                animation.bones.put(entry.getKey(), parseBone(parser, entry.getValue().getAsJsonObject()));
            }
        }
        Map<Double, ActionsKeyframe> actions = new HashMap<>();
        if (animationObject.has("sound_effects")) {
            parseSoundEffects(actions, animationObject.getAsJsonObject("sound_effects"));
        }
        if (animationObject.has("particle_effects")) {
            parseParticleEffects(actions, animationObject.getAsJsonObject("particle_effects"));
        }
        if (animationObject.has("timeline")) {
            parseInstructions(actions, animationObject.getAsJsonObject("timeline"));
        }
        animation.actions = new KeyframesTimeLine<>(new ArrayList<>(actions.values()));

        return animation;
    }
    private static void parseSoundEffects(Map<Double, ActionsKeyframe> actionsKeyframeMap, JsonObject soundEffectsObject) {
        for (Map.Entry<String, JsonElement> entry : soundEffectsObject.entrySet()) {
            double time;
            try {
                time = Double.parseDouble(entry.getKey()) * 20.0;
            } catch (Exception e) {
                continue;
            }
            String sound = entry.getValue().getAsJsonObject().get("effect").getAsString();
            String locator = entry.getValue().getAsJsonObject().get("locator").getAsString();
            ActionsKeyframe actionsKeyframe = actionsKeyframeMap.computeIfAbsent(time, ActionsKeyframe::new);
            actionsKeyframe.getActions().add(new SoundAction(new ResourceLocation(sound), locator));
        }
    }
    private static void parseParticleEffects(Map<Double, ActionsKeyframe> actionsKeyframeMap, JsonObject particleEffectsObject) {
        for (Map.Entry<String, JsonElement> entry : particleEffectsObject.entrySet()) {
            double time;
            try {
                time = Double.parseDouble(entry.getKey()) * 20.0;
            } catch (Exception e) {
                continue;
            }
            String pre_effect_script = "";
            String particle = "";
            String locator = "";
            if(entry.getValue().getAsJsonObject().has("effect")) {
                particle = entry.getValue().getAsJsonObject().get("effect").getAsString();
            }
            if(entry.getValue().getAsJsonObject().has("locator")) {
                locator = entry.getValue().getAsJsonObject().get("locator").getAsString();
            }
            if(entry.getValue().getAsJsonObject().has("pre_effect_script")) {
                pre_effect_script = entry.getValue().getAsJsonObject().get("pre_effect_script").getAsString();
            }

            ActionsKeyframe actionsKeyframe = actionsKeyframeMap.computeIfAbsent(time, ActionsKeyframe::new);
            actionsKeyframe.getActions().add(new ParticleAction(particle, locator, pre_effect_script));
        }
    }
    private static void parseInstructions(Map<Double, ActionsKeyframe> actionsKeyframeMap, JsonObject instructionsObject) {
        for (Map.Entry<String, JsonElement> entry : instructionsObject.entrySet()) {
            double time;
            try {
                time = Double.parseDouble(entry.getKey()) * 20.0;
            } catch (Exception e) {
                continue;
            }
            ActionsKeyframe actionsKeyframe = actionsKeyframeMap.computeIfAbsent(time, ActionsKeyframe::new);
            actionsKeyframe.getActions().add(new InstructionAction(entry.getValue().getAsString()));
        }
    }
    private static AnimationBone parseBone(MolangParser parser, JsonObject boneObject) {
        AnimationBone bone = new AnimationBone();
        bone.position = boneObject.has("position") ? parseTimeline(parser, boneObject.get("position"), TimelineFixer.POSITION) : new KeyframesTimeLine<Keyframe>(Collections.emptyList());
        bone.rotation = boneObject.has("rotation") ? parseTimeline(parser, boneObject.get("rotation"), TimelineFixer.ROTATION) : new KeyframesTimeLine<Keyframe>(Collections.emptyList());
        bone.scale = boneObject.has("scale") ? parseTimeline(parser, boneObject.get("scale"), TimelineFixer.SCALE) : new KeyframesTimeLine<Keyframe>(Collections.emptyList());
        return bone;
    }
    private static KeyframesTimeLine<Keyframe> parseTimeline(MolangParser parser, JsonElement element, TimelineFixer fixer) {
        if (element.isJsonArray() || element.isJsonPrimitive()) {
            return new KeyframesTimeLine<>(Collections.singletonList(parseKeyframe(0d, parser, element, fixer)));
        }

        JsonObject object = element.getAsJsonObject();

        if (object.has("vector")) {
            return new KeyframesTimeLine<>(Collections.singletonList(parseKeyframe(0d, parser, object.get("vector"), fixer)));
        } else {
            List<Keyframe> unsorted = new ArrayList<>(object.size());
            for (Map.Entry<String, JsonElement> entry : object.entrySet())
            {
                double time;
                try {
                    time = Double.parseDouble(entry.getKey()) * 20f;
                } catch (Exception e) {
                    continue;
                }

                unsorted.add(parseKeyframe(time, parser, entry.getValue(), fixer));
            }
            return new KeyframesTimeLine<>(unsorted);
        }
    }

    private static Keyframe parseKeyframe(double time, MolangParser parser, JsonElement element, TimelineFixer fixer) {
        if(element.isJsonPrimitive()) {
            MolangExpression molangExpression = parseValue(parser, element, TimelineFixer.SCALE, EnumFacing.Axis.X);
            return new Keyframe(time, new BaseInterpolation(), molangExpression, molangExpression, molangExpression);
        }
        if(element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            return new Keyframe(time, new BaseInterpolation(), parseValue(parser, array.get(0), fixer, EnumFacing.Axis.X), parseValue(parser, array.get(1), fixer, EnumFacing.Axis.Y), parseValue(parser, array.get(2), fixer, EnumFacing.Axis.Z));
        }


        if (element.isJsonObject()) {
            JsonObject object = element.getAsJsonObject();
            JsonArray array = null;
            if (object.has("vector")) {
                array = element.getAsJsonObject().get("vector").getAsJsonArray();
            } else if (object.has("post")) {
                if (object.get("post").isJsonArray()) {
                    array = object.get("post").getAsJsonArray();
                } else if (object.get("post").isJsonObject() && object.get("post").getAsJsonObject().has("vector")) {
                    array = object.get("post").getAsJsonObject().get("vector").getAsJsonArray();
                }
            }
            if (object.has("lerp_mode") && object.get("lerp_mode").isJsonPrimitive() && object.get("lerp_mode").getAsString().equals("catmullrom")) {
                return new Keyframe(time, new HermiteInterpolation(), parseValue(parser, array.get(0), fixer, EnumFacing.Axis.X), parseValue(parser, array.get(1), fixer, EnumFacing.Axis.Y), parseValue(parser, array.get(2), fixer, EnumFacing.Axis.Z));
            } else if (object.has("easing") && object.get("easing").isJsonPrimitive()) {
                return new Keyframe(time, new EasingInterpolation(object.get("easing").getAsString()), parseValue(parser, array.get(0), fixer, EnumFacing.Axis.X), parseValue(parser, array.get(1), fixer, EnumFacing.Axis.Y), parseValue(parser, array.get(2), fixer, EnumFacing.Axis.Z));
            }
            return new Keyframe(time, new BaseInterpolation(), parseValue(parser, array.get(0), fixer, EnumFacing.Axis.X), parseValue(parser, array.get(1), fixer, EnumFacing.Axis.Y), parseValue(parser, array.get(2), fixer, EnumFacing.Axis.Z));
        }

        return null;
    }
    private static MolangExpression parseValue(MolangParser parser, JsonElement element, TimelineFixer fixer, EnumFacing.Axis axis) {
        JsonPrimitive primitive = element.getAsJsonPrimitive();
        try {
            return primitive.isNumber() ? new MolangValue(parser, new Constant(primitive.getAsDouble() * fixer.fixForAxis(axis))) : fixMolangExpression(parser.parseExpression(primitive.getAsString()), fixer, axis);
        } catch (MolangException e) {
            return new MolangValue(parser, new Constant(0));
        }
    }
    private static MolangExpression fixMolangExpression(MolangExpression molangExpression, TimelineFixer fixer, EnumFacing.Axis axis) {
        if(molangExpression instanceof MolangValue) {
            ((MolangValue) molangExpression).value = new Operator(Operation.MUL, ((MolangValue) molangExpression).value, new Constant(fixer.fixForAxis(axis)));
            return molangExpression;
        }
        if(molangExpression instanceof MolangMultiStatement) {
            for (MolangExpression expression : ((MolangMultiStatement) molangExpression).expressions) {
                fixMolangExpression(expression, fixer, axis);
            }
        }
        return molangExpression;
    }

    private enum TimelineFixer {
        POSITION(-0.0625, 0.0625, 0.0625),
        ROTATION(-(Math.PI / 180f), -(Math.PI / 180f), (Math.PI / 180f)),
        SCALE(1, 1, 1);

        final double x, y, z;
        TimelineFixer(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public double fixForAxis(EnumFacing.Axis axis) {
            if(axis == EnumFacing.Axis.X) {
                return x;
            }
            if(axis == EnumFacing.Axis.Y) {
                return y;
            }
            if(axis == EnumFacing.Axis.Z) {
                return z;
            }
            return 0;
        }
    }
}
