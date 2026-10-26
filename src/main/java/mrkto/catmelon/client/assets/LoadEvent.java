package mrkto.catmelon.client.assets;

import mrkto.catmelon.api.animation.IAnimation;
import mrkto.catmelon.api.model.IModel;
import mrkto.catmelon.api.model.Skeleton;
import mrkto.catmelon.metamorph.ScenePreparedData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Map;

public class LoadEvent<T> extends Event {
    private final Map<ResourceLocation, T> link;
    private final String prefix;
    public LoadEvent(Map<ResourceLocation, T> link, String prefix) {
        this.link = link;
        this.prefix = prefix;
    }
    public void load(T data, String loaderID, String name) {
        T put = link.put(new ResourceLocation(loaderID, prefix + "/" + name), data);
        if(put != null) {
            System.out.println("Overwrite: " + new ResourceLocation(loaderID, prefix + "/" + name));
        }
    }

    public static class LoadAnimationsEvent extends LoadEvent<IAnimation> {
        public LoadAnimationsEvent(Map<ResourceLocation, IAnimation> link, String prefix) {
            super(link, prefix);
        }
    }
    public static class LoadSkeletonsEvent extends LoadEvent<Skeleton> {
        public LoadSkeletonsEvent(Map<ResourceLocation, Skeleton> link, String prefix) {
            super(link, prefix);
        }
    }
    public static class LoadModelsEvent extends LoadEvent<IModel> {
        public LoadModelsEvent(Map<ResourceLocation, IModel> link, String prefix) {
            super(link, prefix);
        }
    }
    public static class LoadAnimatorFactoriesEvent extends LoadEvent<AnimatorFactory<?, ?>> {
        public LoadAnimatorFactoriesEvent(Map<ResourceLocation, AnimatorFactory<?, ?>> link, String prefix) {
            super(link, prefix);
        }
    }
    public static class LoadPreparedDataEvent extends LoadEvent<ScenePreparedData> {
        public LoadPreparedDataEvent(Map<ResourceLocation, ScenePreparedData> link, String prefix) {
            super(link, prefix);
        }
    }
}
