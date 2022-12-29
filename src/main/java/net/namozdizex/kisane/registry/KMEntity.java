package net.namozdizex.kisane.registry;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.namozdizex.kisane.KisaneMikoMod;
import net.namozdizex.kisane.entity.KisaneMikoEntity;

public class KMEntity {
    public static final EntityType<KisaneMikoEntity> KISANE_MIKO = FabricEntityTypeBuilder.create(MobCategory.MONSTER, KisaneMikoEntity::new).dimensions(EntityDimensions.fixed(1.8F, 1.8F)).trackRangeBlocks(4).build();

    public static void init()
    {
        register("kisane_miko", KISANE_MIKO);
    }

    private static <T extends Entity> EntityType<T> register(String key, EntityType<T> type)
    {
        return Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(KisaneMikoMod.MOD_ID, key), type);
    }
}
