package net.namozdizex.kisane.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.namozdizex.kisane.KisaneMikoMod;

public class KMItems {
    public static final Item KISANE_MIKO_SPAWN_EGG = new SpawnEggItem(KMEntity.KISANE_MIKO, 0x1cb704, 0xfb0407, new Item.Properties().tab(CreativeModeTab.TAB_MISC));

    public static final Item NORMAL_CRUCIFIX = new Item(new Item.Properties().tab(KisaneMikoMod.TAB).stacksTo(1));
    public static final Item TRUE_CRUCIFIX = new Item(new Item.Properties().tab(KisaneMikoMod.TAB));
    public static final Item BLOODY_CRUCIFIX = new Item(new Item.Properties().tab(KisaneMikoMod.TAB));
    public static final Item CRUCIFIX_SHARD = new Item(new Item.Properties().tab(KisaneMikoMod.TAB));
    public static final Item CRUCIFIX_SHARD_1 = new Item(new Item.Properties().tab(KisaneMikoMod.TAB));
    public static final Item CRUCIFIX_SHARD_2 = new Item(new Item.Properties().tab(KisaneMikoMod.TAB));
    public static final Item CRUCIFIX_SHARD_3 = new Item(new Item.Properties().tab(KisaneMikoMod.TAB));
    public static final Item CRUCIFIX_SHARD_4 = new Item(new Item.Properties().tab(KisaneMikoMod.TAB));

    public static void init()
    {
        register("kisane_miko_spawn_egg", KISANE_MIKO_SPAWN_EGG);

        register("normal_crucifix", NORMAL_CRUCIFIX);
        register("true_crucifix", TRUE_CRUCIFIX);
        register("bloody_crucifix", BLOODY_CRUCIFIX);
        register("crucifix_shard", CRUCIFIX_SHARD);
        register("crucifix_shard1", CRUCIFIX_SHARD_1);
        register("crucifix_shard2", CRUCIFIX_SHARD_2);
        register("crucifix_shard3", CRUCIFIX_SHARD_3);
        register("crucifix_shard4", CRUCIFIX_SHARD_4);
    }
    private static void register(String key, Item item)
    {
        Registry.register(Registry.ITEM, new ResourceLocation(KisaneMikoMod.MOD_ID, key), item);
    }
}
