package mrkto.catmelon.api.model;

import mrkto.catmelon.client.render.scene.SkeletonInstance;

public interface IModelRenderer<T extends IModel> {
    void init(Skeleton skeleton, T model);
    void render(SkeletonInstance skeletonInstance, float partialTicks);
}
