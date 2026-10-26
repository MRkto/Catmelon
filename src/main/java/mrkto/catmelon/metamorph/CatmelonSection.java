package mrkto.catmelon.metamorph;

import mchorse.metamorph.api.creative.categories.MorphCategory;
import mchorse.metamorph.api.creative.sections.MorphSection;
import mrkto.catmelon.client.CatmelonClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CatmelonSection extends MorphSection {
    public CatmelonSection(String title) {
        super(title);
    }

    @Override
    public void update(World world) {
        MorphCategory category = new MorphCategory(this, "characters");

        for (ResourceLocation characterName : CatmelonClient.getCacheManager().getPreparedDataNames()) {
            CatmelonSceneMetamorphMorph morph = new CatmelonSceneMetamorphMorph();
            morph.name = "catmelon.character";
            NBTTagCompound compound = new NBTTagCompound();
            compound.setString("character", characterName.toString());
            compound.setString("Name", "catmelon.character");
            morph.fromNBT(compound);
            category.add(morph);
        }
        this.categories.clear();
        this.add(category);
    }
}
