//package mrkto.catmelon.client.render;
//
//
//import mchorse.mclib.client.render.RenderLightmap;
//import mrkto.catmelon.client.animation.data.DataLoader;
//import mrkto.catmelon.client.animation.manage.animators.HandsAnimationController;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.EntityPlayerSP;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.client.renderer.OpenGlHelper;
//import net.minecraft.client.renderer.RenderHelper;
//import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.EnumHandSide;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.math.BlockPos;
//
//import javax.vecmath.Vector3f;
//
//public class HandsRenderer {
//    private Model handsModel;
//    private String hands = null;
//
//    private ItemStack lastItemStack = ItemStack.EMPTY;
//    private final HandsAnimationController animator = new HandsAnimationController();
//
//    public void render(float partialTicks) {
//        Model handsModel = getHandsModel();
//        EntityPlayerSP player = getPlayer();
//
//        if(handsModel == null || player == null) return;
//
//        GlStateManager.pushMatrix();
//
//        if(isPlayerLeftHanded()) {
//            GlStateManager.scale(-1, 1, 1);
//        }
//
//        //apply animations
//        updateEquipment(player);
//
//        ChameleonAnimator.resetPose(handsModel);
//        getAnimator().applyActions(player, handsModel, partialTicks);
//
//        //apply texture
//        Minecraft.getMinecraft().getTextureManager().bindTexture(getHandsTexture());
//
//        //render
//        int i = player.world.getCombinedLight(new BlockPos(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ), 0);
//        float f = (float)(i & 65535);
//        float f1 = (float)(i >> 16);
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
//        boolean hurtLight = RenderLightmap.set(player, partialTicks);
//
//        ChameleonRenderer.render(handsModel);
//
//        if (hurtLight)
//        {
//            RenderLightmap.unset();
//        }
//
//        if (ChameleonRenderer.postRender(handsModel, "item"))
//        {
//            renderItem(getStackToRender());
//        }
//
//        Minecraft.getMinecraft().entityRenderer.disableLightmap();
//        RenderHelper.disableStandardItemLighting();
//
//        GlStateManager.popMatrix();
//    }
//    public Vector3f getCameraRotation(float partialTicks) {
//        EntityPlayerSP player = getPlayer();
//
//        if(player == null) return null;
//
//        Model cameraModel = new Model();
//        cameraModel.bones.add(new ModelBone("camera"));
//
//        getAnimator().applyActions(player, handsModel, partialTicks);
//
//        return cameraModel.bones.get(0).current.rotation;
//    }
//    private void renderItem(ItemStack stack) {
//        GlStateManager.disableLighting();
//
//        Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
//    }
//    private void updateEquipment(EntityPlayerSP player) {
//        if(!player.getHeldItemMainhand().equals(lastItemStack) && !(player.getHeldItemMainhand().isEmpty() && lastItemStack.isEmpty())) {
//            getAnimator().setAnimations(getAnimationsForStack(player.getHeldItemMainhand()));
//            getAnimator().reset();
//            getAnimator().setBaseAnimation(getAnimator().equip);
//            lastItemStack = player.getHeldItemMainhand();
//        }
//    }
//
//    public void update() {
//        EntityPlayerSP player = getPlayer();
//
//        if(player == null) return;
//
//        animator.update(player);
//    }
//
//    private Model getHandsModel() {
//        if(handsModel == null) {
//            handsModel = DataLoader.getModel(new ResourceLocation("onlylab", "hands/"+(hands == null ? "fred" : hands)+".geo.json"));
//        }
//        return handsModel;
//    }
//    private ResourceLocation getHandsTexture() {
//        if((hands == null || hands.equals("fred") || hands.equals("alex")) && getPlayer() != null) {
//            return getPlayer().getLocationSkin();
//        }
//        return new ResourceLocation("lab","hands/textures/"+hands+".png");
//    }
//
//    public HandsAnimationController getAnimator() {
//        return animator;
//    }
//    private Animations getAnimationsForStack(ItemStack stack) {
//        Animations animations = DataLoader.getAnimations(new ResourceLocation("onlylab", "hands/animations/" + stack.getItem().getRegistryName().getResourcePath() + ".animation.json"));
//        if(animations == null) {
//            return DataLoader.getAnimations(new ResourceLocation("onlylab", "hands/animations/default.animation.json"));
//        }
//        return animations;
//    }
//
//    private EntityPlayerSP getPlayer() {
//        return Minecraft.getMinecraft().player;
//    }
//    private boolean isPlayerLeftHanded() {
//        return Minecraft.getMinecraft().gameSettings.mainHand == EnumHandSide.LEFT;
//    }
//
//    private ItemStack getStackToRender() {
//        return lastItemStack;
//    }
//
//
//
//
//
//    public void setCustomHands(String hands) {
//        this.hands = hands;
//        handsModel = getHandsModel();
//    }
//}
