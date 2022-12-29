package net.namozdizex.kisane.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.namozdizex.kisane.entity.client.KisaneMikoAttackGoal;

import java.util.UUID;
import java.util.function.Predicate;

public class KisaneMikoEntity extends Monster{
        private static final UUID SPEED_MODIFIER_BABY_UUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
        private static AttributeModifier SPEED_MODIFIER_BABY;
        private static EntityDataAccessor<Boolean> DATA_BABY_ID;
        private static EntityDataAccessor<Integer> DATA_SPECIAL_TYPE_ID;
        private static final EntityDataAccessor<Boolean> DATA_DROWNED_CONVERSION_ID;
        public static final float ZOMBIE_LEADER_CHANCE = 0.05F;
        public static final int REINFORCEMENT_ATTEMPTS = 50;
        public static final int REINFORCEMENT_RANGE_MAX = 40;
        public static final int REINFORCEMENT_RANGE_MIN = 7;
        private static final float BREAK_DOOR_CHANCE = 0.1F;
        private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE;
        private final BreakDoorGoal breakDoorGoal;
        private boolean canBreakDoors;
        private int inWaterTime;
        private int conversionTime;


    public KisaneMikoEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
            this.breakDoorGoal = new BreakDoorGoal(this, DOOR_BREAKING_PREDICATE);
            this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        }

    protected void registerGoals() {
            this.goalSelector.addGoal(4, new KisaneMikoEntity.ZombieAttackTurtleEggGoal(this, 1.0, 3));
            this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
            this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
            this.addBehaviourGoals();
        }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 35.0).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.ARMOR, 2.0).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
    }

        protected void addBehaviourGoals() {
            this.goalSelector.addGoal(2, new KisaneMikoAttackGoal(this, 1.0, false));
            this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
            this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
            this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[]{ZombifiedPiglin.class}));
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
            this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
        }

        protected void defineSynchedData() {
            super.defineSynchedData();
            this.getEntityData().define(DATA_BABY_ID, false);
            this.getEntityData().define(DATA_SPECIAL_TYPE_ID, 0);
            this.getEntityData().define(DATA_DROWNED_CONVERSION_ID, false);
        }

        public boolean isUnderWaterConverting() {
            return (Boolean)this.getEntityData().get(DATA_DROWNED_CONVERSION_ID);
        }

        public boolean canBreakDoors() {
            return this.canBreakDoors;
        }

        public void setCanBreakDoors(boolean bl) {
            if (this.supportsBreakDoorGoal() && GoalUtils.hasGroundPathNavigation(this)) {
                if (this.canBreakDoors != bl) {
                    this.canBreakDoors = bl;
                    ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(bl);
                    if (bl) {
                        this.goalSelector.addGoal(1, this.breakDoorGoal);
                    } else {
                        this.goalSelector.removeGoal(this.breakDoorGoal);
                    }
                }
            } else if (this.canBreakDoors) {
                this.goalSelector.removeGoal(this.breakDoorGoal);
                this.canBreakDoors = false;
            }

        }

    static {
        SPEED_MODIFIER_BABY = new AttributeModifier(SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.5, AttributeModifier.Operation.MULTIPLY_BASE);
        DATA_BABY_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
        DATA_SPECIAL_TYPE_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.INT);
        DATA_DROWNED_CONVERSION_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
        DOOR_BREAKING_PREDICATE = (difficulty) -> {
            return difficulty == Difficulty.HARD;
        };
    }

    class ZombieAttackTurtleEggGoal extends RemoveBlockGoal {
        ZombieAttackTurtleEggGoal(PathfinderMob pathfinderMob, double d, int i) {
            super(Blocks.TURTLE_EGG, pathfinderMob, d, i);
        }

        public void playDestroyProgressSound(LevelAccessor levelAccessor, BlockPos blockPos) {
            levelAccessor.playSound((Player)null, blockPos, SoundEvents.ZOMBIE_DESTROY_EGG, SoundSource.HOSTILE, 0.5F, 0.9F + KisaneMikoEntity.this.random.nextFloat() * 0.2F);
        }

        public void playBreakSound(Level level, BlockPos blockPos) {
            level.playSound((Player)null, blockPos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + level.random.nextFloat() * 0.2F);
        }

        public double acceptedDistance() {
            return 1.14;
        }
    }

        protected boolean supportsBreakDoorGoal() {
            return true;
        }

        public boolean isBaby() {
            return (Boolean)this.getEntityData().get(DATA_BABY_ID);
        }

        public int getExperienceReward() {
            if (this.isBaby()) {
                this.xpReward = (int)((double)this.xpReward * 2.5);
            }

            return super.getExperienceReward();
        }

        public void setBaby(boolean bl) {
            this.getEntityData().set(DATA_BABY_ID, bl);
            if (this.level != null && !this.level.isClientSide) {
                AttributeInstance attributeInstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
                attributeInstance.removeModifier(SPEED_MODIFIER_BABY);
                if (bl) {
                    attributeInstance.addTransientModifier(SPEED_MODIFIER_BABY);
                }
            }

        }

        public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
            if (DATA_BABY_ID.equals(entityDataAccessor)) {
                this.refreshDimensions();
            }

            super.onSyncedDataUpdated(entityDataAccessor);
        }

        public void aiStep() {
            if (this.isAlive()) {
                boolean bl = this.isSunSensitive() && this.isSunBurnTick();
                if (bl) {
                    ItemStack itemStack = this.getItemBySlot(EquipmentSlot.HEAD);
                    if (!itemStack.isEmpty()) {
                        if (itemStack.isDamageableItem()) {
                            itemStack.setDamageValue(itemStack.getDamageValue() + this.random.nextInt(2));
                            if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
                                this.broadcastBreakEvent(EquipmentSlot.HEAD);
                                this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                            }
                        }

                        bl = false;
                    }

                    if (bl) {
                        this.setSecondsOnFire(8);
                    }
                }
            }

            super.aiStep();
        }

        private void startUnderWaterConversion(int i) {
            this.conversionTime = i;
            this.getEntityData().set(DATA_DROWNED_CONVERSION_ID, true);
        }

        protected boolean isSunSensitive() {
            return true;
        }

        public boolean hurt(DamageSource damageSource, float f) {
            if (!super.hurt(damageSource, f)) {
                return false;
            } else if (!(this.level instanceof ServerLevel)) {
                return false;
            } else {
                ServerLevel serverLevel = (ServerLevel)this.level;
                LivingEntity livingEntity = this.getTarget();
                if (livingEntity == null && damageSource.getEntity() instanceof LivingEntity) {
                    livingEntity = (LivingEntity)damageSource.getEntity();
                }

                if (livingEntity != null && this.level.getDifficulty() == Difficulty.HARD && (double)this.random.nextFloat() < this.getAttributeValue(Attributes.SPAWN_REINFORCEMENTS_CHANCE) && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
                    int i = Mth.floor(this.getX());
                    int j = Mth.floor(this.getY());
                    int k = Mth.floor(this.getZ());
                    Zombie zombie = new Zombie(this.level);

                    for(int l = 0; l < 50; ++l) {
                        int m = i + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                        int n = j + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                        int o = k + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
                        BlockPos blockPos = new BlockPos(m, n, o);
                        EntityType<?> entityType = zombie.getType();
                        SpawnPlacements.Type type = SpawnPlacements.getPlacementType(entityType);
                        if (NaturalSpawner.isSpawnPositionOk(type, this.level, blockPos, entityType) && SpawnPlacements.checkSpawnRules(entityType, serverLevel, MobSpawnType.REINFORCEMENT, blockPos, this.level.random)) {
                            zombie.setPos((double)m, (double)n, (double)o);
                            if (!this.level.hasNearbyAlivePlayer((double)m, (double)n, (double)o, 7.0) && this.level.isUnobstructed(zombie) && this.level.noCollision(zombie) && !this.level.containsAnyLiquid(zombie.getBoundingBox())) {
                                zombie.setTarget(livingEntity);
                                zombie.finalizeSpawn(serverLevel, this.level.getCurrentDifficultyAt(zombie.blockPosition()), MobSpawnType.REINFORCEMENT, (SpawnGroupData)null, (CompoundTag)null);
                                serverLevel.addFreshEntityWithPassengers(zombie);
                                this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806, AttributeModifier.Operation.ADDITION));
                                zombie.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806, AttributeModifier.Operation.ADDITION));
                                break;
                            }
                        }
                    }
                }

                return true;
            }
        }

        public boolean doHurtTarget(Entity entity) {
            boolean bl = super.doHurtTarget(entity);
            if (bl) {
                float f = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
                if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
                    entity.setSecondsOnFire(2 * (int)f);
                }
            }

            return bl;
        }

        protected SoundEvent getAmbientSound() {
            return SoundEvents.CAT_AMBIENT;
        }

        protected SoundEvent getHurtSound(DamageSource damageSource) {
            return SoundEvents.CAT_HURT;
        }

        protected SoundEvent getDeathSound() {
            return SoundEvents.WARDEN_DEATH;
        }

        protected SoundEvent getStepSound() {
            return SoundEvents.CAT_PURR;
        }

        protected void playStepSound(BlockPos blockPos, BlockState blockState) {
            this.playSound(this.getStepSound(), 0.15F, 1.0F);
        }

        public MobType getMobType() {
            return MobType.UNDEAD;
        }

        protected void populateDefaultEquipmentSlots(RandomSource randomSource, DifficultyInstance difficultyInstance) {
            super.populateDefaultEquipmentSlots(randomSource, difficultyInstance);
            if (randomSource.nextFloat() < (this.level.getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
                int i = randomSource.nextInt(3);
                if (i == 0) {
                    this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                } else {
                    this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
                }
            }

        }

        public void addAdditionalSaveData(CompoundTag compoundTag) {
            super.addAdditionalSaveData(compoundTag);
            compoundTag.putBoolean("IsBaby", this.isBaby());
            compoundTag.putBoolean("CanBreakDoors", this.canBreakDoors());
            compoundTag.putInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
            compoundTag.putInt("DrownedConversionTime", this.isUnderWaterConverting() ? this.conversionTime : -1);
        }

        public void readAdditionalSaveData(CompoundTag compoundTag) {
            super.readAdditionalSaveData(compoundTag);
            this.setBaby(compoundTag.getBoolean("IsBaby"));
            this.setCanBreakDoors(compoundTag.getBoolean("CanBreakDoors"));
            this.inWaterTime = compoundTag.getInt("InWaterTime");
            if (compoundTag.contains("DrownedConversionTime", 99) && compoundTag.getInt("DrownedConversionTime") > -1) {
                this.startUnderWaterConversion(compoundTag.getInt("DrownedConversionTime"));
            }

        }

        public boolean wasKilled(ServerLevel serverLevel, LivingEntity livingEntity) {
            boolean bl = super.wasKilled(serverLevel, livingEntity);
            if ((serverLevel.getDifficulty() == Difficulty.NORMAL || serverLevel.getDifficulty() == Difficulty.HARD) && livingEntity instanceof Villager) {
                if (serverLevel.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
                    return bl;
                }

                Villager villager = (Villager)livingEntity;
                ZombieVillager zombieVillager = (ZombieVillager)villager.convertTo(EntityType.ZOMBIE_VILLAGER, false);
                zombieVillager.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(zombieVillager.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), (CompoundTag)null);
                zombieVillager.setVillagerData(villager.getVillagerData());
                zombieVillager.setGossips((Tag)villager.getGossips().store(NbtOps.INSTANCE).getValue());
                zombieVillager.setTradeOffers(villager.getOffers().createTag());
                zombieVillager.setVillagerXp(villager.getVillagerXp());
                if (!this.isSilent()) {
                    serverLevel.levelEvent((Player)null, 1026, this.blockPosition(), 0);
                }

                bl = false;
            }

            return bl;
        }

        protected float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
            return this.isBaby() ? 0.93F : 1.74F;
        }

        public boolean canHoldItem(ItemStack itemStack) {
            return itemStack.is(Items.EGG) && this.isBaby() && this.isPassenger() ? false : super.canHoldItem(itemStack);
        }

        public boolean wantsToPickUp(ItemStack itemStack) {
            return itemStack.is(Items.GLOW_INK_SAC) ? false : super.wantsToPickUp(itemStack);
        }

        public static boolean getSpawnAsBabyOdds(RandomSource randomSource) {
            return randomSource.nextFloat() < 0.05F;
        }

        protected void handleAttributes(float f) {
            this.randomizeReinforcementsChance();
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addPermanentModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05000000074505806, AttributeModifier.Operation.ADDITION));
            double d = this.random.nextDouble() * 1.5 * (double)f;
            if (d > 1.0) {
                this.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier("Random zombie-spawn bonus", d, AttributeModifier.Operation.MULTIPLY_TOTAL));
            }

            if (this.random.nextFloat() < f * 0.05F) {
                this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25 + 0.5, AttributeModifier.Operation.ADDITION));
                this.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0 + 1.0, AttributeModifier.Operation.MULTIPLY_TOTAL));
                this.setCanBreakDoors(this.supportsBreakDoorGoal());
            }

        }

        protected void randomizeReinforcementsChance() {
            this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.random.nextDouble() * 0.10000000149011612);
        }

        public double getMyRidingOffset() {
            return this.isBaby() ? 0.0 : -0.45;
        }

        protected void dropCustomDeathLoot(DamageSource damageSource, int i, boolean bl) {
            super.dropCustomDeathLoot(damageSource, i, bl);
            Entity entity = damageSource.getEntity();
            if (entity instanceof Creeper creeper) {
                if (creeper.canDropMobsSkull()) {
                    ItemStack itemStack = this.getSkull();
                    if (!itemStack.isEmpty()) {
                        creeper.increaseDroppedSkulls();
                        this.spawnAtLocation(itemStack);
                    }
                }
            }

        }

        protected ItemStack getSkull() {
            return new ItemStack(Items.ZOMBIE_HEAD);
        }

static {
        SPEED_MODIFIER_BABY = new AttributeModifier(SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.5, AttributeModifier.Operation.MULTIPLY_BASE);
        DATA_BABY_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
        DATA_SPECIAL_TYPE_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.INT);
        }

    public void playDestroyProgressSound(LevelAccessor levelAccessor, BlockPos blockPos) {
        levelAccessor.playSound((Player)null, blockPos, SoundEvents.ZOMBIE_DESTROY_EGG, SoundSource.HOSTILE, 0.5F, 0.9F + KisaneMikoEntity.this.random.nextFloat() * 0.2F);
    }

    public void playBreakSound(Level level, BlockPos blockPos) {
        level.playSound((Player)null, blockPos, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + level.random.nextFloat() * 0.2F);
    }

    public double acceptedDistance() {
        return 1.14;
    }
}
