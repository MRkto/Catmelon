package mrkto.catmelon.client.render;

import mchorse.blockbuster.Blockbuster;
import mrkto.catmelon.client.RenderEventHandler;
import mrkto.catmelon.client.animation.data.Pose;
import mrkto.catmelon.client.animation.data.animation.actions.IAction;
import mrkto.catmelon.client.animation.data.animation.actions.InstructionAction;
import mrkto.catmelon.client.animation.data.animation.actions.ParticleAction;
import mrkto.catmelon.client.animation.data.animation.actions.SoundAction;
import mrkto.catmelon.client.animation.data.model.Model;
import mrkto.catmelon.client.animation.data.model.parts.LocatorPart;
import mrkto.catmelon.client.animation.utils.ModelProcessor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.List;

public class ActionsPerformer {
    private final Vector4f temp = new Vector4f();
    private final List<IAction> actions = new ArrayList<>();
    public void perform(List<IAction> actionsIn, Model model, Pose pose, float x, float y, float z, float yaw, float pitch, EntityLivingBase entity, ModelProcessor modelProcessor) {
        this.actions.clear();
        this.actions.addAll(actionsIn);
        this.actions.removeIf(action -> {
            if(action instanceof InstructionAction) {
                System.out.println(((InstructionAction) action).instruction);
                return true;
            }
            return false;
        });
        float v = (float) (Math.PI / 180f);
        modelProcessor.matrixStack.push();
        modelProcessor.matrixStack.translate(x, y, z);
        modelProcessor.matrixStack.rotateY(-yaw * v);
        modelProcessor.matrixStack.rotateX(pitch * v);
        modelProcessor.processModel(LocatorPart.class, (locator, matrixStack) -> {
            Vector3f locatorPivot = locator.pivot;
            temp.x = locatorPivot.x;
            temp.y = locatorPivot.y;
            temp.z = locatorPivot.z;
            temp.w = 1f;
            matrixStack.getModelMatrix().transform(temp);


            this.actions.removeIf(action -> {
                if(action instanceof ParticleAction && ((ParticleAction) action).locator.equals(locator.name)) {
                    spawnParticle(((ParticleAction) action).particle, temp.x, temp.y, temp.z);
                    return true;
                }
                if(action instanceof SoundAction && ((SoundAction) action).locator.equals(locator.name)) {
                    playSound(((SoundAction) action).sound, temp.x, temp.y, temp.z, entity);
                    return true;
                }
                return false;
            });
        }, model, pose);
        for(IAction action : this.actions) {
            if(action instanceof ParticleAction) {
                spawnParticle(((ParticleAction) action).particle, 0, 0, 0);
                continue;
            }
            if(action instanceof SoundAction) {
                playSound(((SoundAction) action).sound, 0, 0, 0, entity);
                continue;
            }
        }
        modelProcessor.matrixStack.pop();
    }
    private void spawnParticle(String particle, float x, float y, float z) {
        RenderEventHandler.particlesRenderer.createEmitter(Blockbuster.proxy.particles.presets.get(particle), x, y, z, 0, 0);
    }
    private void playSound(ResourceLocation sound, float x, float y, float z, EntityLivingBase entity) {
        SoundEvent value = ForgeRegistries.SOUND_EVENTS.getValue(sound);
        if(value != null) {
            entity.world.playSound(x + entity.posX, y + entity.posY, z + entity.posZ, value, entity.getSoundCategory(), 1f, 1f, false);
        }
    }
}
