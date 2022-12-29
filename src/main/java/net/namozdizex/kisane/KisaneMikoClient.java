package net.namozdizex.kisane;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.namozdizex.kisane.entity.client.renderer.KisaneMikoRenderer;
import net.namozdizex.kisane.registry.KMEntity;

public class KisaneMikoClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        EntityRendererRegistry.register(KMEntity.KISANE_MIKO, KisaneMikoRenderer::new);
    }
}
