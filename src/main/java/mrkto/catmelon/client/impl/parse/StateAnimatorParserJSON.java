package mrkto.catmelon.client.impl.parse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mchorse.mclib.math.molang.MolangException;
import mchorse.mclib.math.molang.MolangParser;
import mrkto.catmelon.client.impl.manage.animators.StateAnimator;

public class StateAnimatorParserJSON {
    public static StateAnimator.BaseState parse(MolangParser parser, JsonElement object) throws MolangException {
        if(!object.isJsonArray()) {
            return null;
        }
        StateAnimator.GroupState state = new StateAnimator.GroupState();
        JsonArray array = object.getAsJsonArray();
        parseStates(state, parser, array);
        return state;
    }
    private static void parseStates(StateAnimator.GroupState parent, MolangParser parser, JsonArray array) throws MolangException {
        for (JsonElement jsonElement : array) {
            if(!jsonElement.isJsonObject()) {
                continue;
            }
            parseState(parent, parser, (JsonObject) jsonElement);
        }
    }
    private static void parseState(StateAnimator.GroupState parent, MolangParser parser, JsonObject object) throws MolangException {
        if(object.has("states")) {
            StateAnimator.GroupState state = new StateAnimator.GroupState();
            parent.states.add(state);
            state.parent = parent;
            state.condition_in = object.has("condition_in") ? parser.parseExpression(object.getAsJsonPrimitive("condition_in").getAsString()) : parser.parseExpression("1");
            state.condition_out = object.has("condition_out") ? parser.parseExpression(object.getAsJsonPrimitive("condition_out").getAsString()) : parser.parseExpression("1");
            parseStates(state, parser, object.getAsJsonArray("states"));
        }
        if(object.has("animation")) {
            StateAnimator.AnimationState state = new StateAnimator.AnimationState();
            parent.states.add(state);
            state.parent = parent;
            state.condition_in = object.has("condition_in") ? parser.parseExpression(object.getAsJsonPrimitive("condition_in").getAsString()) : parser.parseExpression("1");
            state.condition_out = object.has("condition_out") ? parser.parseExpression(object.getAsJsonPrimitive("condition_out").getAsString()) : parser.parseExpression("1");
            state.animation = object.getAsJsonPrimitive("animation").getAsString();
        }
    }
}
