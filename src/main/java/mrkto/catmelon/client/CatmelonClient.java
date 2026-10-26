package mrkto.catmelon.client;

import mchorse.mclib.math.molang.MolangParser;
import mrkto.catmelon.client.assets.CacheManager;
import mrkto.catmelon.client.utils.ModelProcessor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CatmelonClient {
    protected static ModelProcessor modelProcessor = null;
    protected static MolangParser molangParser = null;
    protected static CacheManager cacheManager = null;

    public static MolangParser getMolangParser() {
        return molangParser;
    }
    public static ModelProcessor getModelProcessor() {
        return modelProcessor;
    }
    public static CacheManager getCacheManager() {
        return cacheManager;
    }
}
