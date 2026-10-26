package mrkto.catmelon.metamorph;

import mchorse.mclib.client.render.RenderLightmap;
import mchorse.mclib.utils.Interpolations;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mrkto.catmelon.api.model.IModel;
import mrkto.catmelon.api.model.IModelRenderer;
import mrkto.catmelon.api.model.Skeleton;
import mrkto.catmelon.client.CatmelonClient;
import mrkto.catmelon.client.render.scene.CatmelonRenderScene;
import mrkto.catmelon.client.utils.MatrixStack;
import mrkto.catmelon.client.utils.ModelProcessor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CatmelonSceneMetamorphMorph extends AbstractMorph {
    private NBTTagCompound savedTag;
    public float scale = 1F;

    @SideOnly(Side.CLIENT)
    public CatmelonRenderScene<EntityLivingBase> catmelonRenderScene;



    @Override
    public void renderOnScreen(EntityPlayer entityPlayer, int x, int y, float yaw, float partialTicks) {
        if(catmelonRenderScene == null) {
            return;
        }

        ModelProcessor modelProcessor = CatmelonClient.getModelProcessor();

//        MatrixStack matrixStack = modelProcessor.matrixStack;
//        float v = (float) (Math.PI / 180f);
//        matrixStack.push();
//        matrixStack.rotateY(-yaw * v + ((float)Math.PI));
//        matrixStack.translate((float) x, (float) y, (float) 0);
//
//
//        Minecraft.getMinecraft().getTextureManager().bindTexture(texture == null && entityPlayer instanceof AbstractClientPlayer ? ((AbstractClientPlayer)entityPlayer).getLocationSkin() : texture);
//        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
//        buffer.begin(7, VertexBuilder.getFormat(false, true, true, true));
//        modelProcessor.processModelStatic(IRenderablePart.class, (part, stack) -> part.renderPart(Tessellator.getInstance().getBuffer(), stack, Minecraft.getMinecraft().getRenderPartialTicks()), model);
//        Tessellator.getInstance().draw();
//        GlStateManager.bindTexture(0);
//
//        matrixStack.pop();
    }

    @Override
    public void render(EntityLivingBase entityLivingBase, double x, double y, double z, float yaw, float partialTicks) {
        if(catmelonRenderScene == null) {
            return;
        }
        //render
        ModelProcessor modelProcessor = CatmelonClient.getModelProcessor();

        MatrixStack matrixStack = modelProcessor.matrixStack;
        float v = (float) (Math.PI / 180f);
        matrixStack.push();
        matrixStack.translate((float) x, (float) y, (float) z);

        matrixStack.scale(scale, scale, scale);
        matrixStack.rotateY(-Interpolations.lerp(entityLivingBase.prevRenderYawOffset, entityLivingBase.renderYawOffset, partialTicks) * v + ((float)Math.PI));
        try {
//            Minecraft.getMinecraft().getTextureManager().getTexture(texture == null ? entityLivingBase instanceof AbstractClientPlayer ? ((AbstractClientPlayer)entityLivingBase).getLocationSkin() : new ResourceLocation("") : texture).getGlTextureId();

            boolean hurtLight = RenderLightmap.set(entityLivingBase, partialTicks);
            GlStateManager.disableCull();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableAlpha();
            catmelonRenderScene.render(entityLivingBase, partialTicks);
            GlStateManager.enableCull();

            if(hurtLight) {
                RenderLightmap.unset();
            }
        } finally {
            matrixStack.pop();
        }
    }


    @Override
    public void update(EntityLivingBase target) {
        super.update(target);
        if(FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT || catmelonRenderScene == null) {
            return;
        }
        catmelonRenderScene.update(target);
    }

    @Override
    public AbstractMorph create() {
        return new CatmelonSceneMetamorphMorph();
    }

    @Override
    public void toNBT(NBTTagCompound tag) {
        super.toNBT(tag);

        if(savedTag.hasKey("scene_data", 8)) {
            tag.setString("scene_data", savedTag.getString("scene_data"));
        }
        if(savedTag.hasKey("animators", 9)) {
            tag.setTag("animators", savedTag.getTagList("animators", 10));
        }
        if(savedTag.hasKey("objects", 10)) {
            tag.setTag("objects", savedTag.getCompoundTag("objects"));
        }
        tag.setFloat("scale", scale);
    }

    @Override
    public void fromNBT(NBTTagCompound tag) {
        super.fromNBT(tag);
        savedTag = tag.copy();

        scale = tag.getFloat("scale");
        if(FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT) {
            return;
        }
        if(catmelonRenderScene == null) {
            catmelonRenderScene = new CatmelonRenderScene<>();
        }

        CatmelonMetamorphFactory.fromNBTClient(this, tag);
    }

    @SuppressWarnings("unchecked")
    public <T extends IModel> void initModel(IModelRenderer<?> renderer, Skeleton skeleton, IModel model) {
        ((IModelRenderer<T>)renderer).init(skeleton, (T) model);
    }

    @Override
    public void copy(AbstractMorph from) {
        super.copy(from);
        if(from instanceof CatmelonSceneMetamorphMorph) {
            //i'm lazy :3
            NBTTagCompound compound = new NBTTagCompound();
            from.toNBT(compound);
            this.fromNBT(compound);
        }

    }

    @Override
    public float getWidth(EntityLivingBase entityLivingBase) {
        return 0.6f;
    }


    @Override
    public float getHeight(EntityLivingBase entityLivingBase) {
        return 1.8f;
    }

    public CatmelonRenderScene<EntityLivingBase> getCatmelonRenderScene() {
        return catmelonRenderScene;
    }
}
