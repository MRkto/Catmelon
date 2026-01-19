package mrkto.catmelon.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import mchorse.mclib.math.molang.MolangParser;
import mchorse.mclib.utils.Interpolations;
import mrkto.catmelon.client.animation.data.animation.Animation;
import mrkto.catmelon.client.animation.data.model.Model;
import mrkto.catmelon.client.animation.parse.AnimationParserJSON;
import mrkto.catmelon.client.animation.parse.ModelParserJSON;
import mrkto.catmelon.client.animation.utils.ModelProcessor;
import mrkto.catmelon.client.render.ParticlesCatmelonRenderer;
import mrkto.catmelon.client.render.PlayerCatmelonRenderer;
import mrkto.catmelon.client.animation.utils.MolangHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.io.IOUtils;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class RenderEventHandler {
    public static ModelProcessor modelProcessor = null;
    public static MolangParser molangParser = null;
    public static PlayerCatmelonRenderer renderer = null;

    public static ParticlesCatmelonRenderer particlesRenderer = null;

//
//    @SubscribeEvent
//    public void renderHand(RenderHandEvent event) {
//        if(handsRenderer == null) {
//            return;
//        }
//        event.setCanceled(true);
//
//        handsRenderer.render(event.getPartialTicks());
//    }
//    @SubscribeEvent
//    public void renderHand(EntityViewRenderEvent.CameraSetup event) {
//        if(handsRenderer == null) {
//            return;
//        }
//        Vector3f cameraRotation = handsRenderer.getCameraRotation((float) event.getRenderPartialTicks());
//        event.setPitch(cameraRotation.x + event.getPitch());
//        event.setYaw(cameraRotation.y + event.getYaw());
//        event.setRoll(cameraRotation.z + event.getRoll());
//    }
//
//
//    @SubscribeEvent
//    public void tick(TickEvent.ClientTickEvent event) {
//        if (event.phase == TickEvent.Phase.END) {
//            if(handsRenderer != null) {
//                handsRenderer.update();
//            }
//        }
//    }
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        particlesRenderer = new ParticlesCatmelonRenderer(100, event.getWorld());
        modelProcessor = new ModelProcessor();
        molangParser = new MolangParser();
        MolangHelper.registerVars(molangParser);

        reload();
    }
    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        modelProcessor = null;
        renderer = null;
        molangParser = null;
        particlesRenderer = null;
    }
    private void reload() {
        if(Minecraft.getMinecraft().player == null) {
            return;
        }
        Model playerModel;
        List<Animation> animations;
        try (InputStream stream = Files.newInputStream(Paths.get("/run/media/MRkto/nolacaldisck/Projects/Java/Delta/LaboratoryDelta/modules/animation/src/main/resources/assets/lab/characters/fred/geo.json"))) {
            String content = IOUtils.toString(stream, StandardCharsets.UTF_8);
            playerModel = ModelParserJSON.parseModel(new Gson().fromJson(content, JsonObject.class).getAsJsonArray("minecraft:geometry").get(0).getAsJsonObject());
        }catch (IOException ignore) {
            return;
        }
        try (InputStream stream = Files.newInputStream(Paths.get("/run/media/MRkto/nolacaldisck/Projects/Java/Delta/LaboratoryDelta/modules/animation/src/main/resources/assets/lab/characters/fred/animation.json"))) {
            String content = IOUtils.toString(stream, StandardCharsets.UTF_8);
            animations = AnimationParserJSON.parse(molangParser, new Gson().fromJson(content, JsonObject.class));
        }catch (IOException ignore) {
            return;
        }
        renderer = new PlayerCatmelonRenderer(Minecraft.getMinecraft().player, playerModel, animations, molangParser);
    }

    @SubscribeEvent
    public void key(GuiScreenEvent.KeyboardInputEvent event) {
        if(Keyboard.getEventKey() == Keyboard.KEY_P &&  Keyboard.getEventKeyState()) {
            reload();
        }
    }
    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if(event.phase == TickEvent.Phase.START || renderer == null || particlesRenderer == null) {
            return;
        }
        renderer.update();
        particlesRenderer.update();
    }

    @SubscribeEvent
    public void render(RenderPlayerEvent.Pre event) {
        if(renderer == null) {
            return;
        }
        event.setCanceled(true);
        if(event.getEntity() instanceof AbstractClientPlayer) {

            renderer.doRender((float) event.getX(), (float) event.getY(), (float) event.getZ(), Interpolations.lerpYaw(((AbstractClientPlayer) event.getEntity()).prevRenderYawOffset, ((AbstractClientPlayer) event.getEntity()).renderYawOffset, event.getPartialRenderTick()) + 180f, 0f, event.getPartialRenderTick());
        }
    }
    @SubscribeEvent
    public void renderWorldLast(RenderWorldLastEvent event) {
        if(particlesRenderer == null) {
            return;
        }
        particlesRenderer.render(event.getPartialTicks());
    }
}
