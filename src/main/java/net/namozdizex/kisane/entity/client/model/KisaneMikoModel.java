package net.namozdizex.kisane.entity.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Zombie;
import net.namozdizex.kisane.entity.KisaneMikoEntity;

@Environment(EnvType.CLIENT)
public class KisaneMikoModel<T extends KisaneMikoEntity> extends AbstractZombieModel<T> {
    public KisaneMikoModel(ModelPart modelPart) {
        super(modelPart);
    }

    public boolean isAggressive(T zombie) {
        return zombie.isAggressive();
    }
}
