package net.namozdizex.kisane.entity.client.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.monster.Zombie;
import net.namozdizex.kisane.entity.KisaneMikoEntity;
import net.namozdizex.kisane.entity.client.model.KisaneMikoModel;

@Environment(EnvType.CLIENT)
public class KisaneMikoRenderer extends AbstractKisaneMikoRenderer<KisaneMikoEntity, KisaneMikoModel<KisaneMikoEntity>> {
    public KisaneMikoRenderer(EntityRendererProvider.Context context) {
        this(context, ModelLayers.ZOMBIE, ModelLayers.ZOMBIE_INNER_ARMOR, ModelLayers.ZOMBIE_OUTER_ARMOR);
    }

    public KisaneMikoRenderer(EntityRendererProvider.Context context, ModelLayerLocation modelLayerLocation, ModelLayerLocation modelLayerLocation2, ModelLayerLocation modelLayerLocation3) {
        super(context, new KisaneMikoModel<>(context.bakeLayer(modelLayerLocation)), new KisaneMikoModel(context.bakeLayer(modelLayerLocation2)), new KisaneMikoModel(context.bakeLayer(modelLayerLocation3)));
    }
}
