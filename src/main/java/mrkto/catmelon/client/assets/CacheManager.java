package mrkto.catmelon.client.assets;

import mrkto.catmelon.api.animation.IAnimation;
import mrkto.catmelon.api.manage.IAnimator;
import mrkto.catmelon.api.model.IModel;
import mrkto.catmelon.api.model.IModelRenderer;
import mrkto.catmelon.api.model.Skeleton;
import mrkto.catmelon.metamorph.ScenePreparedData;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CacheManager implements IResourceManagerReloadListener {
    private final Map<ResourceLocation, AnimatorFactory<?, ?>> animatorFactories =  new HashMap<>();
    private final Map<ResourceLocation, IAnimation> animations = new HashMap<>();
    private final Map<ResourceLocation, Skeleton> skeletons = new HashMap<>();
    private final Map<ResourceLocation, IModel> models = new HashMap<>();
    private final Map<ResourceLocation, ScenePreparedData> preparedDatas = new HashMap<>();
    private final Map<Class<?>, ModelRendererFactory<?, ?>> modelRendererFactories =  new HashMap<>();

    public void loadSkeleton(ResourceLocation skeletonName, Skeleton skeleton){
        skeletons.put(skeletonName, skeleton);
    }
    public void loadModel(ResourceLocation modelName, IModel model){
        models.put(modelName, model);
    }
    public <T extends IModel> void loadModelRendererFactory(Class<T> modelType, ModelRendererFactory<T, IModelRenderer<T>> factory){
        modelRendererFactories.put(modelType, factory);
    }
    public void loadAnimation(ResourceLocation animationName, IAnimation parsedAnimation){
        animations.put(animationName, parsedAnimation);
    }
    public void loadPreparedData(ResourceLocation characterName, ScenePreparedData characterData) {
        preparedDatas.put(characterName, characterData);
    }
    public <A extends IAnimator<C>, C> void loadAnimatorFactory(ResourceLocation animatorName, AnimatorFactory<A, C> animationController){
        animatorFactories.put(animatorName, animationController);
    }


    public Skeleton getSkeleton(ResourceLocation skeletonName){
        return skeletons.get(skeletonName);
    }
    public IModel getModel(ResourceLocation modelName){
        return models.get(modelName);
    }
    public <T extends IModel> ModelRendererFactory<T, IModelRenderer<T>> getModelRendererFactory(Class<T> modelType){
        ModelRendererFactory<?, ?> modelRendererFactory = modelRendererFactories.get(modelType);
        if(modelRendererFactory == null || !modelRendererFactory.isSupported(modelType)){
            return null;
        }
        return (ModelRendererFactory<T, IModelRenderer<T>>) modelRendererFactory;
    }
    public IAnimation getAnimation(ResourceLocation animationName){
        return animations.get(animationName);
    }
    public AnimatorFactory<?, ?> getAnimatorFactory(ResourceLocation animatorName){
        return animatorFactories.get(animatorName);
    }
    public ScenePreparedData getPreparedData(ResourceLocation characterName){
        return preparedDatas.get(characterName);
    }

    public Collection<ResourceLocation> getSkeletonNames() {
        return skeletons.keySet();
    }
    public Collection<ResourceLocation> getModelNames() {
        return models.keySet();
    }
    public Collection<ResourceLocation> getAnimationNames() {
        return animations.keySet();
    }
    public Collection<ResourceLocation> getAnimatorFactoryNames() {
        return animatorFactories.keySet();
    }
    public Collection<ResourceLocation> getPreparedDataNames() {
        return preparedDatas.keySet();
    }


    protected void reload() {
        animatorFactories.clear();
        animations.clear();
        skeletons.clear();
        preparedDatas.clear();
        models.clear();
        modelRendererFactories.clear();

        MinecraftForge.EVENT_BUS.post(new LoadEvent.LoadAnimationsEvent(animations, "animation"));
        MinecraftForge.EVENT_BUS.post(new LoadEvent.LoadSkeletonsEvent(skeletons, "skeleton"));
        MinecraftForge.EVENT_BUS.post(new LoadEvent.LoadModelsEvent(models, "model"));
        MinecraftForge.EVENT_BUS.post(new LoadModelRendererEvent(modelRendererFactories));
        MinecraftForge.EVENT_BUS.post(new LoadEvent.LoadAnimatorFactoriesEvent(animatorFactories, "animator"));
        MinecraftForge.EVENT_BUS.post(new LoadEvent.LoadPreparedDataEvent(preparedDatas, "data"));
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        reload();
    }
}
