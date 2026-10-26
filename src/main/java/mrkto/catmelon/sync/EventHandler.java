package mrkto.catmelon.sync;

import mrkto.catmelon.Catmelon;
import mrkto.catmelon.sync.capability.AnimationStateProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
    public static final ResourceLocation CAP = new ResourceLocation(Catmelon.MODID, "animation_state");
    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof EntityPlayer)) return;
        event.addCapability(CAP, new AnimationStateProvider());
    }
}
