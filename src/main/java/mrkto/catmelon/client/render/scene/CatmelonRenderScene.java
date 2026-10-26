package mrkto.catmelon.client.render.scene;

import mrkto.catmelon.api.Pose;
import mrkto.catmelon.api.animation.IAction;
import mrkto.catmelon.api.manage.AnimationController;
import mrkto.catmelon.api.model.IModelRenderer;
import mrkto.catmelon.api.model.Skeleton;
import mrkto.catmelon.client.CatmelonClient;
import mrkto.catmelon.client.impl.animation.actions.InstructionAction;
import mrkto.catmelon.client.utils.MatrixStack;
import mrkto.catmelon.client.utils.MolangHelper;
import net.minecraft.client.renderer.GlStateManager;

import java.util.*;

public class CatmelonRenderScene<C> {
    private AnimationController<C> baseAnimationController;
    private final Pose basePose = new Pose();
    private final List<IAction> actions = new ArrayList<>();

    private final Map<String, Integer> sceneObjectsLookup = new HashMap<>();
    private final List<CatmelonRenderSceneObject<C>> sceneObjects = new ArrayList<>();



    public void render(C dataIn, float partialTicks) {
        MolangHelper.setMolangVariables(CatmelonClient.getMolangParser(), dataIn);
        basePose.clear();
        actions.clear();
        for (int i = 0; i < sceneObjects.size(); i++) {
            CatmelonRenderSceneObject<C> sceneObject = sceneObjects.get(i);
            sceneObject.skeletonInstance.reset();
            sceneObject.objectPose.clear();
        }

        if(baseAnimationController != null) {
            baseAnimationController.animate(basePose, actions, partialTicks);
        }
        for (int i = 0; i < sceneObjects.size(); i++) {
            CatmelonRenderSceneObject<C> sceneObject = sceneObjects.get(i);
            sceneObject.objectPose.overwrite(basePose);
            if(sceneObject.overwriteAnimationController != null) {
                sceneObject.overwriteAnimationController.animate(sceneObject.objectPose, actions, partialTicks);
            }
        }

        for (int i = 0; i < actions.size(); i++) {
            IAction action = actions.get(i);

            if(action instanceof InstructionAction) {
                String instruction = ((InstructionAction) action).instruction;
                if(instruction.startsWith("setParent")) {
                    String[] split = instruction.substring(10, instruction.indexOf(')')).split(",");
                    String object = split[0].trim();
                    String[] newParent = split[1].trim().split(":");
                    getSceneObject(object).setParent(newParent[0], newParent[1]);
                }
            }
        }


        for (int i = 0; i < sceneObjects.size(); i++) {
            CatmelonRenderSceneObject<C> sceneObject = sceneObjects.get(i);
            if(!sceneObject.skeletonInstance.isUpdated()) {
                processSkeleton(sceneObject.skeletonInstance, sceneObject.objectPose, sceneObject.parentObject, sceneObject.parentSocket);
            }
            if(sceneObject.visible) {
                if(sceneObject.textureResolver != null) {
                    GlStateManager.bindTexture(sceneObject.textureResolver.getTextureId(dataIn));
                }
                if(sceneObject.sceneObjectRender != null) {
                    sceneObject.sceneObjectRender.render(sceneObject.skeletonInstance, partialTicks);
                }
                if(sceneObject.textureResolver != null) {
                    GlStateManager.bindTexture(0);
                }
            }
        }
    }
    public void processSkeleton(SkeletonInstance skeletonInstance, Pose pose, int parentObject, int parentMatrix) {
        if(parentObject == -1 || parentMatrix == -1) {
            skeletonInstance.update(pose);
            return;
        }
        CatmelonRenderSceneObject<C> parentSceneObject = sceneObjects.get(parentObject);
        if(!skeletonInstance.isUpdated()) {
            processSkeleton(parentSceneObject.skeletonInstance, parentSceneObject.objectPose, parentSceneObject.parentObject, parentSceneObject.parentSocket);
        }
        MatrixStack matrixStack = CatmelonClient.getModelProcessor().matrixStack;
        matrixStack.push();
        matrixStack.getModelMatrix().set(parentSceneObject.skeletonInstance.socket[parentMatrix]);
        skeletonInstance.update(pose);
        matrixStack.pop();
    }
    public void update(C dataIn) {
        MolangHelper.setMolangVariables(CatmelonClient.getMolangParser(), dataIn);
        if(baseAnimationController != null) {
            baseAnimationController.update(dataIn);
        }
        for (CatmelonRenderSceneObject<C> sceneObject : sceneObjects) {
            if(sceneObject.overwriteAnimationController != null) {
                sceneObject.overwriteAnimationController.update(dataIn);
            }
        }
    }

    public AnimationController<C> getAnimationController() {
        return baseAnimationController;
    }
    public Collection<String> getKeys() {
        return sceneObjectsLookup.keySet();
    }
    public CatmelonRenderSceneObject<C> getSceneObject(String name) {
        return sceneObjectsLookup.get(name) == null ? null : sceneObjects.get(sceneObjectsLookup.get(name));
    }
    public CatmelonRenderSceneObject<C> createSceneObject(String name, Skeleton skeleton) {
        Integer index = sceneObjectsLookup.get(name);
        CatmelonRenderSceneObject<C> sceneObject = new CatmelonRenderSceneObject<>(skeleton);
        if(index == null) {
            sceneObjectsLookup.put(name, sceneObjects.size());
            sceneObjects.add(sceneObject);
            return sceneObject;
        }
        sceneObjects.set(index, sceneObject);

        for (int i = 0; i < sceneObjects.size(); i++) {
            CatmelonRenderSceneObject<C> sceneObjectI = sceneObjects.get(i);
            if(sceneObjectI.parentObject != i) {
                continue;
            }
            String socket = sceneObjectI.parentSocketName;
            sceneObjectI.removeParent();
            sceneObjectI.setParent(name, socket);
        }
        return sceneObject;
    }
    public void removeSceneObject(String name) {
        Integer index = sceneObjectsLookup.remove(name);
        if(index == null) {
            return;
        }
        for (int i = 0; i < sceneObjects.size(); i++) {
            CatmelonRenderSceneObject<C> sceneObjectI = sceneObjects.get(i);
            if(sceneObjectI.parentObject != index) {
                continue;
            }
            sceneObjectI.removeParent();
        }

        if(index + 1 < sceneObjects.size()) {
            sceneObjects.set(index, sceneObjects.get(sceneObjects.size() - 1));
            for (Map.Entry<String, Integer> entry : sceneObjectsLookup.entrySet()) {
                if(entry.getValue() != sceneObjects.size() - 1) {
                    continue;
                }
                entry.setValue(index);
            }

            for (int i = 0; i < sceneObjects.size(); i++) {
                CatmelonRenderSceneObject<C> sceneObjectI = sceneObjects.get(i);
                if(sceneObjectI.parentObject != sceneObjects.size() - 1) {
                    continue;
                }
                sceneObjectI.parentObject = index;
            }
        }

        sceneObjects.remove(sceneObjects.size() - 1);
    }

    public void setAnimationController(AnimationController<C> controller) {
        this.baseAnimationController = controller;
    }


    public class CatmelonRenderSceneObject<O> {
        private AnimationController<O> overwriteAnimationController;
        private final Pose objectPose = new Pose();

        private final SkeletonInstance skeletonInstance;
        private int parentObject = -1;
        private int parentSocket = -1;
        private String parentSocketName;

        private IModelRenderer<?> sceneObjectRender;
        private ITextureResolver<? super O> textureResolver;
        private boolean visible = true;

        public CatmelonRenderSceneObject(Skeleton skeleton) {
            skeletonInstance = new SkeletonInstance(skeleton);
        }

        public boolean setParent(String parentObject, String parentSocket) {
            Integer parentIndex = sceneObjectsLookup.get(parentObject);
            if(parentIndex == null) {
                return false;
            }
            CatmelonRenderSceneObject<C> parentSceneObject = sceneObjects.get(parentIndex);
            Integer boneId = parentSceneObject.skeletonInstance.skeleton.getSocketId(parentSocket);
            if(boneId == null || checkForCycle(sceneObjects.indexOf(this), parentIndex)) {
                return false;
            }

            this.parentObject = parentIndex;
            this.parentSocket = boneId;
            this.parentSocketName = parentSocket;
            return true;
        }
        public void removeParent() {
            this.parentObject = -1;
            this.parentSocket = -1;
            this.parentSocketName = null;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }
        public boolean isVisible() {
            return visible;
        }

        public SkeletonInstance getSkeletonInstance() {
            return this.skeletonInstance;
        }
        public void setTextureResolver(ITextureResolver<? super O> textureResolver) {
            this.textureResolver = textureResolver;
        }
        public ITextureResolver<? super O> getTextureResolver() {
            return textureResolver;
        }
        public void setRender(IModelRenderer<?> sceneObjectRender) {
            this.sceneObjectRender = sceneObjectRender;
        }
        public IModelRenderer<?> getRender() {
            return sceneObjectRender;
        }
        public void setAnimationController(AnimationController<O> overwriteAnimationController) {
            this.overwriteAnimationController = overwriteAnimationController;
        }
        public AnimationController<O> getAnimationController() {
            return overwriteAnimationController;
        }

        private boolean checkForCycle(int start, int next) {
            if(start == next) {
                return true;
            }
            if(next == -1) {
                return false;
            }
            return checkForCycle(start, sceneObjects.get(next).parentObject);
        }
    }
}
