package mrkto.catmelon.client.impl.parse;

import com.google.gson.*;
import mchorse.mclib.utils.resources.MultiResourceLocation;
import mchorse.mclib.utils.resources.MultiResourceLocationManager;
import mchorse.mclib.utils.resources.RLUtils;
import mrkto.catmelon.api.model.IModel;
import mrkto.catmelon.api.model.IModelRenderer;
import mrkto.catmelon.client.CatmelonClient;
import mrkto.catmelon.client.assets.AnimatorFactory;
import mrkto.catmelon.client.assets.ModelRendererFactory;
import mrkto.catmelon.client.render.scene.ITextureResolver;
import mrkto.catmelon.metamorph.ScenePreparedData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class ScenePreparedDataParserJSON {
    public static ScenePreparedData parse(JsonObject jsonObject) {
        ScenePreparedData.AnimatorData[] animatorData = parseAnimators(jsonObject.getAsJsonArray("animators"));
        ScenePreparedData.SceneObjectData[] sceneObjectData = parseSceneObjects(jsonObject.getAsJsonObject("scene_objects"));

        return new ScenePreparedData(animatorData, sceneObjectData);
    }
    private static ScenePreparedData.AnimatorData[] parseAnimators(JsonArray array) {
        if (array == null) {
            return null;
        }

        ScenePreparedData.AnimatorData[] result = new ScenePreparedData.AnimatorData[array.size()];
        for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            ResourceLocation id = new ResourceLocation(obj.get("id").getAsString());
            AnimatorFactory<?, ?> factory = CatmelonClient.getCacheManager().getAnimatorFactory(id);
            if (factory == null) {
                throw new JsonParseException("Unknown animator: " + id);
            }

            NBTTagCompound data = new NBTTagCompound();
            if (obj.has("data")) {
                data = jsonToNBT(obj.getAsJsonObject("data"));
            }
            result[i] = new ScenePreparedData.AnimatorData(factory, data);
        }

        return result;
    }
    private static ScenePreparedData.SceneObjectData[] parseSceneObjects(JsonObject object) {
        ScenePreparedData.SceneObjectData[] sceneObjectDatas = new ScenePreparedData.SceneObjectData[object.size()];
        int i = 0;
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if(!entry.getValue().isJsonObject()) {
                throw new JsonParseException("Expected JsonObject but found " + entry.getValue().getClass().getSimpleName());
            }
            JsonObject jsonObject = entry.getValue().getAsJsonObject();

            ScenePreparedData.AnimatorData[] animatorData = parseAnimators(jsonObject.getAsJsonArray("animators"));
            String skeleton = jsonObject.getAsJsonPrimitive("skeleton") == null ? null : jsonObject.getAsJsonPrimitive("skeleton").getAsString();
            String modelLocation = jsonObject.getAsJsonPrimitive("model") == null ? null : jsonObject.getAsJsonPrimitive("model").getAsString();
            IModel model = modelLocation == null ? null : CatmelonClient.getCacheManager().getModel(new ResourceLocation(modelLocation));
            ModelRendererFactory<? extends IModel, ? extends IModelRenderer<? extends IModel>> modelRendererFactory = model == null ? null : CatmelonClient.getCacheManager().getModelRendererFactory(model.getClass());
            JsonElement textureLocation = jsonObject.getAsJsonPrimitive("texture") == null ? null : jsonObject.getAsJsonPrimitive("texture");
            String parent = jsonObject.getAsJsonPrimitive("parent") == null ? null : jsonObject.getAsJsonPrimitive("parent").getAsString();

            sceneObjectDatas[i++] = new ScenePreparedData.SceneObjectData(
                    entry.getKey(),
                    animatorData,
                    skeleton == null ? null : CatmelonClient.getCacheManager().getSkeleton(new ResourceLocation(skeleton)),
                    model,
                    modelRendererFactory,
                    createTextureResolver(textureLocation),
                    parent == null ? null : new ResourceLocation(parent)
            );
        }
        return sceneObjectDatas;
    }

    private static NBTTagCompound jsonToNBT(JsonObject json) {
        NBTTagCompound tag = new NBTTagCompound();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (value.isJsonPrimitive()) {
                JsonPrimitive p = value.getAsJsonPrimitive();

                if (p.isBoolean()) {
                    tag.setBoolean(key, p.getAsBoolean());
                } else if (p.isNumber()) {
                    Number n = p.getAsNumber();

                    if (n instanceof Integer) {
                        tag.setInteger(key, n.intValue());
                    } else if (n instanceof Long) {
                        tag.setLong(key, n.longValue());
                    } else if (n instanceof Float) {
                        tag.setFloat(key, n.floatValue());
                    } else if (n instanceof Double) {
                        tag.setDouble(key, n.doubleValue());
                    } else {
                        tag.setDouble(key, n.doubleValue());
                    }
                } else {
                    tag.setString(key, p.getAsString());
                }
            } else if (value.isJsonObject()) {
                tag.setTag(key, jsonToNBT(value.getAsJsonObject()));
            } else if (value.isJsonArray()) {
                tag.setTag(key, jsonArrayToNBT(value.getAsJsonArray()));
            }
        }

        return tag;
    }

    private static NBTTagList jsonArrayToNBT(JsonArray array) {
        NBTTagList list = new NBTTagList();

        for (JsonElement element : array) {
            if (element.isJsonObject()) {
                list.appendTag(jsonToNBT(element.getAsJsonObject()));
            } else if (element.isJsonPrimitive()) {
                JsonPrimitive p = element.getAsJsonPrimitive();

                if (p.isBoolean()) {
                    list.appendTag(new NBTTagByte((byte) (p.getAsBoolean() ? 1 : 0)));
                } else if (p.isNumber()) {
                    list.appendTag(new NBTTagDouble(p.getAsDouble()));
                } else {
                    list.appendTag(new NBTTagString(p.getAsString()));
                }
            }
        }

        return list;
    }
    private static ITextureResolver<EntityLivingBase> createTextureResolver(JsonElement element) {
        if(element == null) {
            return null;
        }
        ResourceLocation texture = RLUtils.create(element);
        if(texture != null) {
            if(texture instanceof MultiResourceLocation) {
                int id = MultiResourceLocationManager.getId((MultiResourceLocation) texture);
                return (e) -> id;
            } else {
                return (e) -> {
                    ITextureObject itextureobject = Minecraft.getMinecraft().getTextureManager().getTexture(texture);
                    if (itextureobject == null)
                    {
                        itextureobject = new SimpleTexture(texture);
                        Minecraft.getMinecraft().getTextureManager().loadTexture(texture, itextureobject);
                    }
                    return itextureobject.getGlTextureId();
                };
            }
        }
        return null;
    }
}
