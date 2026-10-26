package mrkto.catmelon.client.assets;

import mrkto.catmelon.api.model.IModel;
import mrkto.catmelon.api.model.IModelRenderer;

import java.util.function.Supplier;

public class ModelRendererFactory<M extends IModel, C extends IModelRenderer<M>> {
    private final Supplier<C> factory;
    private final Class<M> clazz;

    public ModelRendererFactory(Supplier<C> factory, Class<M> clazz) {
        this.factory = factory;
        this.clazz = clazz;
    }
    public C create() {
        return factory.get();
    }
    public boolean isSupported(Class<?> clazz) {
        return this.clazz.isAssignableFrom(clazz);
    }
}
