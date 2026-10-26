package mrkto.catmelon.sync.network;

import mrkto.catmelon.sync.network.packets.AnimationStateSyncPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Dispatcher {
    private static final SimpleNetworkWrapper dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel("catmelon");
    public static void sendTo(IMessage message, EntityPlayerMP player) {
        dispatcher.sendTo(message, player);
    }
    public static void sendToAllAround(IMessage message, EntityPlayerMP player, int range) {
        dispatcher.sendToAllAround(message, new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, range));
    }
    public static void sendToTracked(IMessage message, Entity entity) {
        EntityTracker tracker = ((WorldServer)entity.world).getEntityTracker();
        for(EntityPlayer player : tracker.getTrackingPlayers(entity)) {
            dispatcher.sendTo(message, (EntityPlayerMP)player);
        }

        if(entity instanceof EntityPlayerMP) {
            dispatcher.sendTo(message, (EntityPlayerMP)entity);
        }
    }
    public static void sendToAll(IMessage message) {
        dispatcher.sendToAll(message);
    }

    public static void sendToServer(IMessage message) {
        dispatcher.sendToServer(message);
    }
    public static void register() {
        dispatcher.registerMessage(AnimationStateSyncPacket.Handler.class, AnimationStateSyncPacket.class, 0, Side.CLIENT);
    }
}
