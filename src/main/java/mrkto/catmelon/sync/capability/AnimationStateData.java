package mrkto.catmelon.sync.capability;

import mrkto.catmelon.sync.network.Dispatcher;
import mrkto.catmelon.sync.network.packets.AnimationStateSyncPacket;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

//TODO rewrite all sync system
public class AnimationStateData implements ICAP{
    private final Map<String, Double> values = new HashMap<>();
    private final Map<String, Boolean> dirty = new HashMap<>();
    public void setValue(String key, double value) {
        values.put(key, value);
        dirty.put(key, true);
    }
    public void sendToClient(EntityPlayerMP playerMP) {
        AnimationStateSyncPacket animationStateSyncPacket = new AnimationStateSyncPacket();
        animationStateSyncPacket.values = values;
        animationStateSyncPacket.dirty = dirty;
        animationStateSyncPacket.entityId = playerMP.getEntityId();
        Dispatcher.sendToTracked(animationStateSyncPacket, playerMP);
    }
    public void iterate(BiConsumer<String, Double> consumer) {
        values.forEach(consumer);
    }
}
