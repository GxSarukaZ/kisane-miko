package net.namozdizex.kisane.entity.client;

import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.namozdizex.kisane.entity.KisaneMikoEntity;

public class KisaneMikoAttackGoal extends MeleeAttackGoal {
    private final KisaneMikoEntity kisaneMikoEntity;
    private int raiseArmTicks;

    public KisaneMikoAttackGoal(KisaneMikoEntity kisaneMikoEntity, double d, boolean bl) {
        super(kisaneMikoEntity, d, bl);
        this.kisaneMikoEntity = kisaneMikoEntity;
    }

    public void start() {
        super.start();
        this.raiseArmTicks = 0;
    }

    public void stop() {
        super.stop();
        this.kisaneMikoEntity.setAggressive(false);
    }

    public void tick() {
        super.tick();
        ++this.raiseArmTicks;
        if (this.raiseArmTicks >= 5 && this.getTicksUntilNextAttack() < this.getAttackInterval() / 2) {
            this.kisaneMikoEntity.setAggressive(true);
        } else {
            this.kisaneMikoEntity.setAggressive(false);
        }

    }
}