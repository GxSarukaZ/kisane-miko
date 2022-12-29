package net.namozdizex.kisane;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.namozdizex.kisane.entity.KisaneMikoEntity;
import net.namozdizex.kisane.registry.KMEntity;
import net.namozdizex.kisane.registry.KMItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KisaneMikoMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	public static final String MOD_ID = "kisane";

	@Override
	public void onInitialize() {

		LOGGER.info("Hello Fabric world!");

		KMItems.init();
		KMEntity.init();

		FabricDefaultAttributeRegistry.register(KMEntity.KISANE_MIKO, KisaneMikoEntity.createMobAttributes());
	}
}
