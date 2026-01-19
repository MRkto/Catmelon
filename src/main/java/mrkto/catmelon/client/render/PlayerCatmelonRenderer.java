package mrkto.catmelon.client.render;

import mchorse.mclib.client.render.VertexBuilder;
import mchorse.mclib.math.molang.MolangParser;
import mrkto.catmelon.client.RenderEventHandler;
import mrkto.catmelon.client.animation.data.animation.Animation;
import mrkto.catmelon.client.animation.data.model.Model;
import mrkto.catmelon.client.animation.data.model.parts.IModelPart;
import mrkto.catmelon.client.animation.data.model.parts.IRenderablePart;
import mrkto.catmelon.client.animation.data.model.parts.LocatorPart;
import mrkto.catmelon.client.animation.manage.AnimationController;
import mrkto.catmelon.client.animation.manage.AnimationResult;
import mrkto.catmelon.client.animation.manage.animators.IAnimator;
import mrkto.catmelon.client.animation.manage.animators.MoveAnimator;
import mrkto.catmelon.client.animation.manage.state.PlayerStateMachine;
import mrkto.catmelon.client.animation.utils.MatrixStack;
import mrkto.catmelon.client.animation.utils.MolangHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerCatmelonRenderer {
    private final AbstractClientPlayer player;
    private final AnimationController<AbstractClientPlayer> controller;
    private final Model model;
    private final MolangParser molangParser;

    private final Map<String, Vector3f> locators =  new HashMap<>();
    private final Vector4f temp = new Vector4f();

    public PlayerCatmelonRenderer(AbstractClientPlayer player, Model model, List<Animation> moveAnimations, MolangParser molangParser) {
        this.player = player;
        this.model = model;
        this.molangParser = molangParser;
        this.controller = new AnimationController<>(new IAnimator[] {new MoveAnimator(moveAnimations)}, new PlayerStateMachine());

        RenderEventHandler.modelProcessor.processModelStatic(LocatorPart.class, (locator, matrix) -> locators.put(locator.name, new Vector3f()), model);
    }
    public void update() {
        controller.update(player);
    }
    public void doRender(float x, float y, float z, float yaw, float pitch, float partialTicks) {
        MolangHelper.setMolangVariablesEntity(molangParser, player);
        AnimationResult result = controller.animate(partialTicks);

        MatrixStack matrixStack = RenderEventHandler.modelProcessor.matrixStack;
        float v = (float) (Math.PI / 180f);
        matrixStack.push();
        matrixStack.translate(x, y, z);
        matrixStack.rotateY(-yaw * v);
        matrixStack.rotateX(pitch * v);

        Minecraft.getMinecraft().getTextureManager().bindTexture(player.getLocationSkin());
        BufferBuilder builder = Tessellator.getInstance().getBuffer();
        builder.begin(7, VertexBuilder.getFormat(false, true, true, true));

        RenderEventHandler.modelProcessor.processModel(IModelPart.class, this::process, model, result.getPose());

        Tessellator.getInstance().draw();
        matrixStack.pop();
    }
    private void process(IModelPart part, MatrixStack stack) {
        if(part instanceof IRenderablePart) {
            ((IRenderablePart) part).renderPart(Tessellator.getInstance().getBuffer(), stack, Minecraft.getMinecraft().getRenderPartialTicks());
            return;
        }
        if(part instanceof LocatorPart) {
            LocatorPart locatorPart = (LocatorPart) part;
            Vector3f locatorPivot = locatorPart.pivot;
            temp.x = locatorPivot.x;
            temp.y = locatorPivot.y;
            temp.z = locatorPivot.z;
            temp.w = 1f;
            stack.getModelMatrix().transform(temp);

            Vector3f vector3f = locators.get(locatorPart.name);
            vector3f.x = temp.x;
            vector3f.y = temp.y;
            vector3f.z = temp.z;
        }
    }
}
