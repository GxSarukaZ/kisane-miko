package net.namozdizex.kisane.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.namozdizex.kisane.KisaneMikoMod;

public class KMItems {
    public static final Item KISANE_MIKO_SPAWN_EGG = new SpawnEggItem(KMEntity.KISANE_MIKO, 0x1cb704, 0xfb0407, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    public static final Item CRUCIFIX = new Item(new Item.Properties().tab(KisaneMikoMod.TAB).stacksTo(1));

    public static void init()
    {
        register("kisane_miko_spawn_egg", KISANE_MIKO_SPAWN_EGG);
        register("crucifix", CRUCIFIX);
    }
    private static void register(String key, Item item)
    {
        Registry.register(Registry.ITEM, new ResourceLocation(KisaneMikoMod.MOD_ID, key), item);
    }
}
