package net.namozdizex.kisane.entity.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.namozdizex.kisane.entity.KisaneMikoEntity;
import net.namozdizex.kisane.entity.client.model.KisaneMikoModel;

@Environment(EnvType.CLIENT)
public class KisaneMikoGlowingLayer <T extends KisaneMikoEntity> extends EyesLayer<T, KisaneMikoModel<T>> {
    public KisaneMikoGlowingLayer(RenderLayerParent<T, KisaneMikoModel<T>> renderLayerParent) {
        super(renderLayerParent);
    }

    private static final RenderType MIKO_LOCATION = RenderType.eyes(new ResourceLocation("kisane:textures/entity/cat/miko_layer.png"));

    @Override
    public RenderType renderType() {
        return MIKO_LOCATION;
    }
}
