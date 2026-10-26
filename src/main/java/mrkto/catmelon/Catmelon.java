package mrkto.catmelon;

import mchorse.metamorph.api.MorphManager;
import mrkto.catmelon.client.ClientEventHandler;
import mrkto.catmelon.metamorph.CatmelonMetamorphFactory;
import mrkto.catmelon.sync.EventHandler;
import mrkto.catmelon.sync.capability.AnimationStateData;
import mrkto.catmelon.sync.capability.ICAP;
import mrkto.catmelon.sync.network.Dispatcher;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;

@Mod(modid = Catmelon.MODID, name = Catmelon.TITLE, version = "5.0.07")
public class Catmelon {
    public static final String TITLE = "Catmelon";
    public static final String MODID = "catmelon";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent ignored) {
        if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ClientEventHandler.init();
        }

        MorphManager.INSTANCE.factories.add(new CatmelonMetamorphFactory());
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        CapabilityManager.INSTANCE.register(ICAP.class, new Capability.IStorage<ICAP>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<ICAP> capability, ICAP instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<ICAP> capability, ICAP instance, EnumFacing side, NBTBase nbt) {

            }
        }, AnimationStateData.class);
        Dispatcher.register();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
    }
    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
    }
}