package mrkto.catmelon.sync.network.packets;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import mrkto.catmelon.sync.capability.AnimationStateProvider;
import mrkto.catmelon.sync.capability.ICAP;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Map;

public class AnimationStateSyncPacket implements IMessage {
    public int entityId;
    public Map<String, Double> values;
    public Map<String, Boolean> dirty;
    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        int size = buf.readInt();
        values = Maps.newHashMapWithExpectedSize(size);
        for(int i = 0; i < size; i++) {
            String key = ByteBufUtils.readUTF8String(buf);
            double value = buf.readDouble();
            values.put(key, value);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        int size = 0;
        for (Boolean value : dirty.values()) {
            if(value) {
                size++;
            }
        }
        buf.writeInt(size);
        for (Map.Entry<String, Double> entry : values.entrySet()) {
            if(!dirty.get(entry.getKey())) {
                dirty.put(entry.getKey(), false);
                continue;
            }
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            buf.writeDouble(entry.getValue());
        }
    }
    public static class Handler implements IMessageHandler<AnimationStateSyncPacket, IMessage> {
        @Override
        public IMessage onMessage(AnimationStateSyncPacket message, MessageContext ctx) {
            Entity entityByID = Minecraft.getMinecraft().player.world.getEntityByID(message.entityId);
            if(entityByID == null) {
                return null;
            }
            ICAP capability = entityByID.getCapability(AnimationStateProvider.ANIMATION_STATE_CAP, null);
            if(capability == null) {
                return null;
            }
            for (Map.Entry<String, Double> entry : message.values.entrySet()) {
                capability.setValue(entry.getKey(), entry.getValue());
            }
            return null;
        }
    }
}
