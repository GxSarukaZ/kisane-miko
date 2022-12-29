package net.namozdizex.kisane.entity.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import net.namozdizex.kisane.entity.KisaneMikoEntity;
import net.namozdizex.kisane.entity.client.model.KisaneMikoModel;

@Environment(EnvType.CLIENT)
public abstract class AbstractKisaneMikoRenderer<T extends KisaneMikoEntity, M extends KisaneMikoModel<T>> extends HumanoidMobRenderer<T, M> {
    private static final ResourceLocation MIKO_LOCATION = new ResourceLocation("kisane:textures/entity/cat/kisane_miko.png");

    protected AbstractKisaneMikoRenderer(EntityRendererProvider.Context context, M zombieModel, M zombieModel2, M zombieModel3) {
        super(context, zombieModel, 0.5F);
        this.addLayer(new HumanoidArmorLayer(this, zombieModel2, zombieModel3));
    }

    public ResourceLocation getTextureLocation(KisaneMikoEntity kisaneMikoEntity) {
        return MIKO_LOCATION;
    }
}
