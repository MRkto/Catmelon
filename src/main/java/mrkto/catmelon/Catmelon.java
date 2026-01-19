package mrkto.catmelon;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = Catmelon.MODID, name = Catmelon.TITLE, version = "5.0.07")
public class Catmelon {
    public static final String TITLE = "Catmelon";
    public static final String MODID = "catmelon";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent ignored) {

    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
    }
    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
    }
}