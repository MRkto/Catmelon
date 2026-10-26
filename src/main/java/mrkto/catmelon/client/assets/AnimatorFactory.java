package mrkto.catmelon.client.assets;

import mrkto.catmelon.api.manage.IAnimator;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;


public class AnimatorFactory<A extends IAnimator<C>, C> {
    private final FactoryFunctionInterface<A, C> factory;
    private final Class<C> clazz;

    public AnimatorFactory(FactoryFunctionInterface<A, C> factory, Class<C> clazz) {
        this.factory = factory;
        this.clazz = clazz;
    }
    public A create(@Nullable NBTTagCompound args) {
        return factory.create(args);
    }
    public boolean isSupported(Class<?> clazz) {
        return this.clazz.isAssignableFrom(clazz);
    }
    @FunctionalInterface
    public interface FactoryFunctionInterface<A extends IAnimator<C>, C> {
        A create(@Nullable NBTTagCompound args);
    }
}
