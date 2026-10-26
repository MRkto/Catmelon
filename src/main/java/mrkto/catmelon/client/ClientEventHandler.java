package mrkto.catmelon.client;

import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.math.molang.expressions.MolangExpression;
import mrkto.catmelon.api.animation.IAnimation;
import mrkto.catmelon.client.assets.*;
import mrkto.catmelon.client.impl.manage.animators.EventAnimator;
import mrkto.catmelon.client.impl.manage.animators.StateAnimator;
import mrkto.catmelon.client.impl.manage.animators.TriggerAnimator;
import mrkto.catmelon.client.impl.model.parted.PartedModel;
import mrkto.catmelon.client.utils.ModelProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientEventHandler {

    public ClientEventHandler() {
        CatmelonClient.modelProcessor = new ModelProcessor();
        CatmelonClient.molangParser = new MolangParser();
        CatmelonClient.cacheManager = new CacheManager();
    }
    public static void init() {
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());

        IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
        if(resourceManager instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager) resourceManager).registerReloadListener(CatmelonClient.getCacheManager());
        }
    }

    @SubscribeEvent
    public void onAnimatorsLoad(LoadEvent.LoadAnimatorFactoriesEvent event) {
        event.load(new AnimatorFactory<>(
                (nbt) ->
                        new EventAnimator(nbt != null && nbt.hasKey("animation") ?
                                CatmelonClient.getCacheManager().getAnimation(new ResourceLocation(nbt.getString("animation")))
                                : null
                        ), Object.class),
                "catmelon",
                "event"
        );
        event.load(new AnimatorFactory<>(
                        (nbt) -> {
                            if(nbt == null || !nbt.hasKey("animation") || !nbt.hasKey("expression")) {
                                return null;
                            }
                            IAnimation animation = CatmelonClient.getCacheManager().getAnimation(new ResourceLocation(nbt.getString("animation")));
                            if(animation == null) {
                                return null;
                            }
                            try {
                                MolangExpression expression = CatmelonClient.getMolangParser().parseExpression(nbt.getString("expression"));
                                return new TriggerAnimator(expression, animation, nbt.hasKey("max") ? nbt.getInteger("max") : 20);
                            } catch (Exception e) {
                                return null;
                            }
                        },Object.class),
                "catmelon",
                "trigger"
        );
    }
    @SubscribeEvent
    public void onModelRenderersLoad(LoadModelRendererEvent event) {
        event.load(PartedModel.class, new ModelRendererFactory<>(PartedModel.PartedModelRenderer::new, PartedModel.class));
    }
//    @SubscribeEvent
//    public void onModelsLoad(LoadEvent.LoadModelsEvent event) {
//        try {
//            ModelParserJSON.ParsedResult parsedResult = ModelParserJSON.parseModel(new Gson().fromJson(new InputStreamReader(Files.newInputStream(Paths.get("/run/media/mrkto/NeoMeow/projects/java/Delta/LaboratoryDelta/modules/animation/src/main/resources/assets/lab/catmelon/model/human/fred.json"))), JsonObject.class).getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject());
//            event.load(parsedResult.model, "catmelon", "fred");
//            CatmelonClient.getCacheManager().loadSkeleton(new ResourceLocation("catmelon", "skeleton/fred"), parsedResult.skeleton);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
    //
    //
//    @SubscribeEvent
//    public void loadAnimations(LoadEvent.LoadAnimationsEvent event) {
//        try (InputStream stream = Files.newInputStream(Paths.get("/run/media/mrkto/NeoMeow/projects/java/Delta/LaboratoryDelta/modules/animation/src/main/resources/assets/lab/catmelon/animation/fred/animations.json"))) {
//            String content = IOUtils.toString(stream, StandardCharsets.UTF_8);
//            List<Animation> animations = AnimationParserJSON.parse(CatmelonClient.getMolangParser(), new Gson().fromJson(content, JsonObject.class));
//
//            for (Animation animation : animations) {
//                event.load(animation, "lab", "fred/" + animation.id);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//    @SubscribeEvent
//    public void loadAnimators(LoadEvent.LoadAnimatorFactoriesEvent event) {
//        try (InputStream stream = Files.newInputStream(Paths.get("/run/media/mrkto/NeoMeow/projects/java/Delta/LaboratoryDelta/modules/animation/src/main/resources/assets/lab/catmelon/animator/state/move.json"))) {
//            String content = IOUtils.toString(stream, StandardCharsets.UTF_8);
//            StateAnimator.BaseState state = StateAnimatorParserJSON.parse(CatmelonClient.getMolangParser(), new Gson().fromJson(content, JsonArray.class));
//
//            event.load(new AnimatorFactory<>(tag -> new StateAnimator(state, getAnimationGetter(tag == null ? "" : tag.getString("dir"))), Object.class), "lab", "state/move");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//    }
    private static StateAnimator.AnimationGetter getAnimationGetter(String baseDir) {
        HashMap<String, IAnimation> animators = new HashMap<>();
        return (s) -> {
            IAnimation iAnimation = animators.get(s);
            if(iAnimation != null) {
                return iAnimation;
            }
            IAnimation animation = CatmelonClient.getCacheManager().getAnimation(new ResourceLocation("lab", "animation/" + baseDir + s));
            animators.put(s, animation);
            return animation;
        };
    }

}
