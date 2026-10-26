package mrkto.catmelon.metamorph;

import mchorse.mclib.utils.resources.MultiResourceLocation;
import mchorse.mclib.utils.resources.MultiResourceLocationManager;
import mchorse.mclib.utils.resources.RLUtils;
import mchorse.metamorph.api.IMorphFactory;
import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.gui.editor.GuiAbstractMorph;
import mrkto.catmelon.api.manage.AnimationController;
import mrkto.catmelon.api.manage.IAnimator;
import mrkto.catmelon.api.model.IModel;
import mrkto.catmelon.api.model.IModelRenderer;
import mrkto.catmelon.api.model.Skeleton;
import mrkto.catmelon.client.CatmelonClient;
import mrkto.catmelon.client.assets.AnimatorFactory;
import mrkto.catmelon.client.assets.ModelRendererFactory;
import mrkto.catmelon.client.render.scene.CatmelonRenderScene;
import mrkto.catmelon.metamorph.gui.GuiCatmelonMorph;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

public class CatmelonMetamorphFactory implements IMorphFactory {
    @Override
    public void register(MorphManager morphManager) {
        morphManager.list.register(new CatmelonSection("catmelon"));
    }

    @Override
    public void registerMorphEditors(Minecraft minecraft, List<GuiAbstractMorph> list) {
        list.add(new GuiCatmelonMorph(minecraft));
    }

    @Override
    public boolean hasMorph(String s) {
        return s.equals("catmelon.character");
    }

    @Override
    public AbstractMorph getMorphFromNBT(NBTTagCompound nbtTagCompound) {
        CatmelonSceneMetamorphMorph morph = new CatmelonSceneMetamorphMorph();
        morph.fromNBT(nbtTagCompound);
        return morph;
    }
    @SideOnly(Side.CLIENT)
    public static void fromNBTClient(CatmelonSceneMetamorphMorph morph, NBTTagCompound tag) {
        ScenePreparedData sceneData = tag.hasKey("scene_data", 8) ? CatmelonClient.getCacheManager().getPreparedData(new ResourceLocation(tag.getString("scene_data"))) : null;
        NBTTagList animatorsBase = tag.hasKey("animators", 9) ? tag.getTagList("animators", 10) : null;
        NBTTagCompound objects = tag.hasKey("objects", 10) ? tag.getCompoundTag("objects") : null;


        if(animatorsBase == null && sceneData != null && sceneData.animatorData != null) {
            IAnimator<EntityLivingBase>[] animators = new IAnimator[sceneData.animatorData.length];
            for (int i = 0; i < animators.length; i++) {
                if(!sceneData.animatorData[i].factory.isSupported(EntityLivingBase.class)) {
                    break;
                }
                animators[i] = (IAnimator<EntityLivingBase>) sceneData.animatorData[i].factory.create(sceneData.animatorData[i].data);
            }
            if(animators[animators.length - 1] != null) {
                morph.catmelonRenderScene.setAnimationController(new AnimationController<>(animators, EntityLivingBase.class));
            }

        } else if(animatorsBase != null){
            IAnimator<EntityLivingBase>[] animatorsArray = new IAnimator[animatorsBase.tagCount()];
            for(int i = 0; i < animatorsArray.length; i++) {
                NBTTagCompound animatorTag = animatorsBase.getCompoundTagAt(i);
                ResourceLocation location = new ResourceLocation(animatorTag.getString("id"));
                NBTTagCompound args = animatorTag.hasKey("data") ? animatorTag.getCompoundTag("data") : null;
                AnimatorFactory<?, ?> animatorFactory = CatmelonClient.getCacheManager().getAnimatorFactory(location);
                if(animatorFactory == null || !animatorFactory.isSupported(EntityLivingBase.class)) {
                    break;
                }
                IAnimator<EntityLivingBase> entityLivingBaseIAnimator = (IAnimator<EntityLivingBase>) animatorFactory.create(args);
                if(entityLivingBaseIAnimator == null) {
                    break;
                }
                animatorsArray[i] = entityLivingBaseIAnimator;
            }
            if(animatorsArray[animatorsArray.length - 1] != null) {
                morph.catmelonRenderScene.setAnimationController(new AnimationController<>(animatorsArray, EntityLivingBase.class));
            }
        }


        Map<String, ResourceLocation> parentMap = new HashMap<>();
        Set<String> existed = new HashSet<>();

        if(sceneData != null) {
            for (int i = 0; i < sceneData.sceneObjectDatas.length; i++) {
                ScenePreparedData.SceneObjectData sceneObjectData = sceneData.sceneObjectDatas[i];
                CatmelonRenderScene<EntityLivingBase>.CatmelonRenderSceneObject<EntityLivingBase> sceneObject = morph.catmelonRenderScene.getSceneObject(sceneObjectData.name);
                if(sceneObject == null) {
                    sceneObject = morph.catmelonRenderScene.createSceneObject(sceneObjectData.name, sceneObjectData.skeleton);
                }
                if(sceneObjectData.animatorData != null && sceneObjectData.animatorData.length != 0) {
                    IAnimator<EntityLivingBase>[] animators = new IAnimator[sceneObjectData.animatorData.length];
                    for (int ii = 0; ii < animators.length; ii++) {
                        if(!sceneObjectData.animatorData[ii].factory.isSupported(EntityLivingBase.class)) {
                            break;
                        }
                        animators[ii] = (IAnimator<EntityLivingBase>) sceneObjectData.animatorData[ii].factory.create(sceneObjectData.animatorData[ii].data);
                    }
                    if(animators[animators.length - 1] != null) {
                        sceneObject.setAnimationController(new AnimationController<>(animators, EntityLivingBase.class));
                    }
                }
                if(sceneObjectData.model != null && sceneObjectData.modelRendererFactory != null && sceneObjectData.modelRendererFactory.isSupported(sceneObjectData.model.getClass())) {
                    IModelRenderer<?> o = sceneObjectData.modelRendererFactory.create();
                    morph.initModel(o, sceneObjectData.skeleton, sceneObjectData.model);
                    sceneObject.setRender(o);
                }
                if(sceneObjectData.textureResolver != null) {
                    sceneObject.setTextureResolver(sceneObjectData.textureResolver);
                }
                if(sceneObjectData.parent != null) {
                    parentMap.put(sceneObjectData.name, sceneObjectData.parent);
                }
                existed.add(sceneObjectData.name);
            }
        }
        if(objects != null) {
            for (String key : objects.getKeySet()) {
                NBTTagCompound object = objects.getCompoundTag(key);
                IModel model = object.hasKey("model") ? CatmelonClient.getCacheManager().getModel(new ResourceLocation(object.getString("model"))) : null;
                ModelRendererFactory<?, ?> modelRendererFactory = model != null ? CatmelonClient.getCacheManager().getModelRendererFactory(model.getClass()) : null;
                Skeleton skeleton = object.hasKey("skeleton") ? CatmelonClient.getCacheManager().getSkeleton(new ResourceLocation(object.getString("skeleton"))) : null;
                ResourceLocation texture = object.hasKey("texture") ? RLUtils.create(object.getTag("texture")) : null;
                ResourceLocation parent = object.hasKey("parent") ? RLUtils.create(object.getTag("parent")) : null;
                NBTTagList animators = object.hasKey("animators", 9) ? object.getTagList("animators", 10) : null;
                if(skeleton == null) {
                    continue;
                }
                CatmelonRenderScene<EntityLivingBase>.CatmelonRenderSceneObject<EntityLivingBase> sceneObject = morph.catmelonRenderScene.getSceneObject(key);
                if(sceneObject == null) {
                    sceneObject = morph.catmelonRenderScene.createSceneObject(key, skeleton);
                }
                if(animators != null && animators.tagCount() > 0) {
                    IAnimator<EntityLivingBase>[] animatorsArray = new IAnimator[animators.tagCount()];
                    for(int i = 0; i < animatorsArray.length; i++) {
                        NBTTagCompound animatorTag = animators.getCompoundTagAt(i);
                        ResourceLocation location = new ResourceLocation(animatorTag.getString("id"));
                        NBTTagCompound args = animatorTag.hasKey("data") ? animatorTag.getCompoundTag("data") : null;
                        AnimatorFactory<?, ?> animatorFactory = CatmelonClient.getCacheManager().getAnimatorFactory(location);
                        if(animatorFactory == null || !animatorFactory.isSupported(EntityLivingBase.class)) {
                            break;
                        }
                        IAnimator<EntityLivingBase> entityLivingBaseIAnimator = (IAnimator<EntityLivingBase>) animatorFactory.create(args);
                        if(entityLivingBaseIAnimator == null) {
                            break;
                        }
                        animatorsArray[i] = entityLivingBaseIAnimator;
                    }
                    if(animatorsArray[animatorsArray.length - 1] != null) {
                        sceneObject.setAnimationController(new AnimationController<>(animatorsArray, EntityLivingBase.class));
                    }
                }
                if(model != null && modelRendererFactory != null && modelRendererFactory.isSupported(model.getClass())) {
                    IModelRenderer<?> o = modelRendererFactory.create();
                    morph.initModel(o, skeleton, model);
                    sceneObject.setRender(o);
                }
                if(texture != null) {
                    if(texture instanceof MultiResourceLocation) {
                        int id = MultiResourceLocationManager.getId((MultiResourceLocation) texture);
                        sceneObject.setTextureResolver((e) -> id);
                    } else {
                        sceneObject.setTextureResolver((e) -> {
                            ITextureObject itextureobject = Minecraft.getMinecraft().getTextureManager().getTexture(texture);
                            if (itextureobject == null)
                            {
                                itextureobject = new SimpleTexture(texture);
                                Minecraft.getMinecraft().getTextureManager().loadTexture(texture, itextureobject);
                            }
                            return itextureobject.getGlTextureId();
                        });
                    }
                }
                if(parent != null) {
                    parentMap.put(key, parent);
                }
                existed.add(key);
            }
        }



        for (String key : morph.catmelonRenderScene.getKeys()) {
            if(existed.contains(key)) {
                continue;
            }
            morph.catmelonRenderScene.removeSceneObject(key);
        }

        for (Map.Entry<String, ResourceLocation> entry : parentMap.entrySet()) {
            morph.catmelonRenderScene.getSceneObject(entry.getKey()).setParent(entry.getValue().getResourceDomain(), entry.getValue().getResourcePath());
        }
    }
}
