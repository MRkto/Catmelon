package mrkto.catmelon.metamorph;

import mrkto.catmelon.api.model.IModel;
import mrkto.catmelon.api.model.Skeleton;
import mrkto.catmelon.client.assets.AnimatorFactory;
import mrkto.catmelon.client.assets.ModelRendererFactory;
import mrkto.catmelon.client.render.scene.ITextureResolver;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class ScenePreparedData {
    public final AnimatorData[] animatorData;
    public final SceneObjectData[] sceneObjectDatas;

    public ScenePreparedData(AnimatorData[] animatorData, SceneObjectData[] sceneObjectDatas) {
        this.animatorData = animatorData;
        this.sceneObjectDatas = sceneObjectDatas;
    }
    public static class AnimatorData {
        public final AnimatorFactory<?, ?> factory;
        public final NBTTagCompound data;
        public AnimatorData(AnimatorFactory<?, ?> animatorFactory, NBTTagCompound data) {
            this.factory = animatorFactory;
            this.data = data;
        }
    }
    public static class SceneObjectData {
        public final String name;
        public final AnimatorData[] animatorData;
        public final Skeleton skeleton;
        public final IModel model;
        public final ModelRendererFactory<?, ?> modelRendererFactory;
        public final ITextureResolver<EntityLivingBase> textureResolver;
        public final ResourceLocation parent;

        public SceneObjectData(String name, AnimatorData[] animatorData, Skeleton skeleton, IModel model, ModelRendererFactory<?, ?> modelRendererFactory, ITextureResolver<EntityLivingBase> textureResolver, ResourceLocation parent) {
            this.name = name;
            this.animatorData = animatorData;
            this.skeleton = skeleton;
            this.model = model;
            this.modelRendererFactory = modelRendererFactory;
            this.textureResolver = textureResolver;
            this.parent = parent;
        }
    }
}
