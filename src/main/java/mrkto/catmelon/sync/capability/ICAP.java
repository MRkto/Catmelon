package mrkto.catmelon.sync.capability;

import net.minecraft.entity.player.EntityPlayerMP;

import java.util.function.BiConsumer;

public interface ICAP {
    void sendToClient(EntityPlayerMP playerMP);
    void iterate(BiConsumer<String, Double> consumer);
    void setValue(String key, double value);
}
