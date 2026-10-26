package mrkto.catmelon.client.assets;

import mrkto.catmelon.api.model.IModel;
import mrkto.catmelon.api.model.IModelRenderer;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.Map;

public class

LoadModelRendererEvent extends Event {
    private final Map<Class<?>, ModelRendererFactory<?, ?>> link;
    public LoadModelRendererEvent(Map<Class<?>, ModelRendererFactory<?, ?>> link) {
        this.link = link;
    }
    public <T extends IModel> void load(Class<T> modelType, ModelRendererFactory<T, IModelRenderer<T>> factory) {
        Object put =  link.put(modelType, factory);
        if(put != null) {
            System.out.println("Overwrite: " + modelType.getName());
        }
    }
}
