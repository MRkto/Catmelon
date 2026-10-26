package mrkto.catmelon.sync.capability;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AnimationStateProvider implements ICapabilityProvider {
    @CapabilityInject(ICAP.class)
    public static final Capability<ICAP> ANIMATION_STATE_CAP = null;

    private ICAP instance = ANIMATION_STATE_CAP.getDefaultInstance();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == ANIMATION_STATE_CAP;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == ANIMATION_STATE_CAP ? ANIMATION_STATE_CAP.cast(instance) : null;
    }
}
