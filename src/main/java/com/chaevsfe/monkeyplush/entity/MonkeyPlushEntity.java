package com.chaevsfe.monkeyplush.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class MonkeyPlushEntity extends Animal {
    private static final EntityDataAccessor<Boolean> DATA_CLIMBING =
            SynchedEntityData.defineId(MonkeyPlushEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_IN_TREE =
            SynchedEntityData.defineId(MonkeyPlushEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_ANGRY =
            SynchedEntityData.defineId(MonkeyPlushEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<java.util.Optional<BlockPos>> DATA_GRIP_POS =
            SynchedEntityData.defineId(MonkeyPlushEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    private static final int AGGRO_DURATION = 3600; // 3 minutes in ticks
    private int aggroTicksLeft = 0;

    private float squish = 0.0f;
    private float prevSquish = 0.0f;
    private boolean wasOnGround = true;
    @Nullable private BlockPos gripBlock = null;
    public boolean forceDropFromTree = false; // set by hurt(), checked by goal

    public MonkeyPlushEntity(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        clearTreeState();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_CLIMBING, false);
        builder.define(DATA_IN_TREE, false);
        builder.define(DATA_ANGRY, false);
        builder.define(DATA_GRIP_POS, java.util.Optional.empty());
        builder.define(DATA_ATTACK_TICK, 0);
    }

    public boolean isClimbingTree() { return this.entityData.get(DATA_CLIMBING); }
    public boolean isInTree() { return this.entityData.get(DATA_IN_TREE); }
    public boolean isAngry() { return this.entityData.get(DATA_ANGRY); }
    @Nullable public BlockPos getGripPos() { return this.entityData.get(DATA_GRIP_POS).orElse(null); }

    public void clearTreeState() {
        this.entityData.set(DATA_CLIMBING, false);
        this.entityData.set(DATA_IN_TREE, false);
        this.entityData.set(DATA_GRIP_POS, java.util.Optional.empty());
        this.noPhysics = false;
        this.gripBlock = null;
        this.setDeltaMovement(this.getDeltaMovement().x, -0.5, this.getDeltaMovement().z);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.ATTACK_DAMAGE, 3.0); // same as zombie
    }

    public static AttributeSupplier.Builder createJumboAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.ATTACK_DAMAGE, 7.5)  // same as iron golem
                .add(Attributes.ATTACK_KNOCKBACK, 3.5) // iron golem sends you flying
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    public boolean isJumbo() {
        return this.getType() == com.chaevsfe.monkeyplush.init.ModEntities.JUMBO_EVIL_MONKEY;
    }

    @Override
    public boolean isNoGravity() {
        if (this.isJumbo()) return super.isNoGravity(); // jumbo never floats
        return this.isClimbingTree() || this.isInTree() || super.isNoGravity();
    }

    @Override
    public boolean isInWall() {
        if (this.isJumbo()) return super.isInWall();
        if (this.isClimbingTree() || this.isInTree()) return false;
        return super.isInWall();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean wasInTree = this.isClimbingTree() || this.isInTree();

        this.forceDropFromTree = true;

        this.entityData.set(DATA_CLIMBING, false);
        this.entityData.set(DATA_IN_TREE, false);
        this.gripBlock = null;

        boolean result = super.hurt(source, amount);

        if (wasInTree) {
            this.noPhysics = false;
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.8, 0));
        }

        if (source.getEntity() instanceof Player) {
            this.aggroTicksLeft = AGGRO_DURATION;
            this.entityData.set(DATA_ANGRY, true);
        }
        return result;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5, true));
        if (!this.isJumbo()) {
            this.goalSelector.addGoal(2, new ClimbTreeGoal(this));
        }
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    private static final EntityDataAccessor<Integer> DATA_ATTACK_TICK =
            SynchedEntityData.defineId(MonkeyPlushEntity.class, EntityDataSerializers.INT);

    public int getAttackTick() { return this.entityData.get(DATA_ATTACK_TICK); }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        this.entityData.set(DATA_ATTACK_TICK, 10);
        if (target instanceof net.minecraft.world.entity.LivingEntity living) {
            float damage = this.isJumbo() ? 40.0f : 20.0f;
            living.hurt(this.damageSources().mobAttack(this), damage);
            double kb = this.isJumbo() ? 3.5 : 0.5;
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > 0) {
                target.push(dx / dist * kb, 0.4, dz / dist * kb);
            }
            return true;
        }
        return super.doHurtTarget(target);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide && this.isInTree()) {
            boolean shouldFall = false;

            if (this.gripBlock != null && !isTreeBlock(this.level(), this.gripBlock)) {
                shouldFall = true;
            }

            if (!shouldFall) {
                BlockPos pos = this.blockPosition();
                boolean anyAbove = false;
                for (int dy = 0; dy <= 2; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (isTreeBlock(this.level(), pos.offset(dx, dy, dz))) {
                                anyAbove = true;
                            }
                        }
                    }
                }
                if (!anyAbove) shouldFall = true;
            }

            if (shouldFall) {
                this.forceDropFromTree = true;
                clearTreeState();
            }
        }

        if (!this.level().isClientSide && this.getAttackTick() > 0) {
            this.entityData.set(DATA_ATTACK_TICK, this.getAttackTick() - 1);
        }

        if (!this.level().isClientSide && this.aggroTicksLeft > 0) {
            var target = this.getTarget();
            if (target != null && (!target.isAlive() || target.isDeadOrDying())) {
                this.aggroTicksLeft = 0;
                this.entityData.set(DATA_ANGRY, false);
                this.setTarget(null);
            } else {
                this.aggroTicksLeft--;
                if (this.aggroTicksLeft <= 0) {
                    this.entityData.set(DATA_ANGRY, false);
                    this.setTarget(null);
                }
            }
        }

        this.prevSquish = this.squish;
        if (this.onGround() && !this.wasOnGround) {
            this.squish = Math.min(this.fallDistance * 0.15f, 0.6f);
        }
        if (this.squish > 0.01f) {
            this.squish *= 0.7f;
        } else {
            this.squish = 0.0f;
        }
        this.wasOnGround = this.onGround();
    }

    public float getSquishAmount(float partialTick) {
        return this.prevSquish + (this.squish - this.prevSquish) * partialTick;
    }

    static boolean isTreeBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
        return super.causeFallDamage(fallDistance, multiplier * 0.1f, source);
    }

    @Nullable @Override public AgeableMob getBreedOffspring(ServerLevel l, AgeableMob o) { return null; }
    @Override public boolean isFood(ItemStack s) { return false; }
    @Nullable @Override protected SoundEvent getAmbientSound() { return SoundEvents.WOOL_STEP; }
    @Nullable @Override protected SoundEvent getHurtSound(DamageSource s) { return SoundEvents.WOOL_HIT; }
    @Nullable @Override protected SoundEvent getDeathSound() { return SoundEvents.WOOL_BREAK; }
    @Override protected float getSoundVolume() { return 0.6f; }

    static class ClimbTreeGoal extends Goal {
        private final MonkeyPlushEntity monkey;
        private BlockPos treeBase;
        private int targetY;
        private int cooldown;
        private int phaseTicks;
        private int idleTicks;
        private double climbX, climbZ;
        private float facingAngle;
        private double outDirX, outDirZ;
        private double moveFromX, moveFromZ, moveFromY;
        private double moveToX, moveToZ, moveToY;
        private int moveTicksLeft = 0;
        private static final int MOVE_DURATION = 15; // ticks to traverse one leaf

        private enum Phase { WALKING, CLIMBING, HANGING }
        private Phase phase = Phase.WALKING;

        private static final int MAX_CLIMB = 100;
        private static final double CLIMB_SPEED = 0.1;
        private static final int HANG_TIME = 300;
        private static final int TRAVERSE_INTERVAL = 40; // move to next leaf every 2 sec

        ClimbTreeGoal(MonkeyPlushEntity monkey) {
            this.monkey = monkey;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (this.monkey.getTarget() != null) return false;
            if (this.monkey.isAngry()) return false;
            if (--this.cooldown > 0) return false;
            this.cooldown = 200 + this.monkey.getRandom().nextInt(200);

            BlockPos center = this.monkey.blockPosition();
            for (int i = 0; i < 15; i++) {
                BlockPos check = center.offset(
                        this.monkey.getRandom().nextInt(20) - 10,
                        this.monkey.getRandom().nextInt(4) - 1,
                        this.monkey.getRandom().nextInt(20) - 10);
                if (this.monkey.level().getBlockState(check).is(BlockTags.LOGS)) {
                    this.treeBase = check;
                    this.targetY = -1;
                    this.phaseTicks = 0;
                    this.idleTicks = 0;
                    this.phase = Phase.WALKING;
                    return true;
                }
            }
            return false;
        }

        private int findCanopyBottom() {
            int baseY = this.treeBase.getY();
            for (int dy = 2; dy <= MAX_CLIMB; dy++) {
                int y = baseY + dy;
                BlockPos check = BlockPos.containing(this.climbX, y, this.climbZ);
                for (int bx = -1; bx <= 1; bx++) {
                    for (int bz = -1; bz <= 1; bz++) {
                        if (this.monkey.level().getBlockState(check.offset(bx, 0, bz)).is(BlockTags.LEAVES)) {
                            return Math.max(y - 1, baseY + 2);
                        }
                    }
                }
            }
            int topY = baseY;
            for (int dy = 1; dy <= MAX_CLIMB; dy++) {
                if (this.monkey.level().getBlockState(this.treeBase.above(dy)).is(BlockTags.LOGS)) {
                    topY = baseY + dy;
                } else break;
            }
            return topY;
        }

        private BlockPos findGripBlock(double x, double y, double z) {
            BlockPos pos = BlockPos.containing(x, y, z);
            for (int dy = 1; dy <= 3; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos check = pos.offset(dx, dy, dz);
                        if (this.monkey.level().getBlockState(check).is(BlockTags.LEAVES)) {
                            return check;
                        }
                    }
                }
            }
            for (int dy = 0; dy <= 2; dy++) {
                if (isTreeBlock(this.monkey.level(), pos.above(dy))) return pos.above(dy);
            }
            return pos.above();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.monkey.forceDropFromTree) return false;
            if (this.monkey.getTarget() != null) return false;
            if (this.monkey.isAngry()) return false;
            if (this.treeBase == null) return false;
            if (this.phase == Phase.HANGING && this.idleTicks > HANG_TIME) return false;
            return true;
        }

        @Override
        public void start() {
            if (this.treeBase != null) {
                this.monkey.getNavigation().moveTo(
                        this.treeBase.getX() + 0.5, this.treeBase.getY(),
                        this.treeBase.getZ() + 0.5, 1.0);
            }
        }

        @Override
        public void tick() {
            switch (this.phase) {
                case WALKING -> tickWalking();
                case CLIMBING -> tickClimbing();
                case HANGING -> tickHanging();
            }
        }

        private void tickWalking() {
            this.phaseTicks++;
            double trunkCX = this.treeBase.getX() + 0.5;
            double trunkCZ = this.treeBase.getZ() + 0.5;
            double dx = this.monkey.getX() - trunkCX;
            double dz = this.monkey.getZ() - trunkCZ;
            double dist = Math.sqrt(dx * dx + dz * dz);

            if (dist < 2.5) {
                double nx = dist > 0.01 ? dx / dist : 1.0;
                double nz = dist > 0.01 ? dz / dist : 0.0;
                this.climbX = trunkCX + nx * 1.3;
                this.climbZ = trunkCZ + nz * 1.3;
                for (double tryDist = 1.3; tryDist <= 2.5; tryDist += 0.3) {
                    BlockPos testPos = BlockPos.containing(
                            trunkCX + nx * tryDist, this.monkey.getY(), trunkCZ + nz * tryDist);
                    if (!this.monkey.level().getBlockState(testPos).isSolid()) {
                        this.climbX = trunkCX + nx * tryDist;
                        this.climbZ = trunkCZ + nz * tryDist;
                        break;
                    }
                }
                this.outDirX = nx;
                this.outDirZ = nz;

                double faceDx = trunkCX - this.climbX;
                double faceDz = trunkCZ - this.climbZ;
                this.facingAngle = (float)(Mth.atan2(faceDz, faceDx) * (180.0 / Math.PI)) - 90.0f;

                this.targetY = findCanopyBottom();
                this.phase = Phase.CLIMBING;
                this.phaseTicks = 0;
                this.monkey.getNavigation().stop();
                this.monkey.entityData.set(DATA_CLIMBING, true);
                this.monkey.noPhysics = true;
                this.monkey.setPos(this.climbX, this.monkey.getY(), this.climbZ);
            } else if (this.monkey.getNavigation().isDone() || this.phaseTicks > 200) {
                this.treeBase = null;
            }
        }

        private void tickClimbing() {
            this.phaseTicks++;

            double newY = this.monkey.getY() + CLIMB_SPEED;

            BlockPos climbPos = BlockPos.containing(this.climbX, newY, this.climbZ);
            if (this.monkey.level().getBlockState(climbPos).isSolid()) {
                double trunkCX = this.treeBase.getX() + 0.5;
                double trunkCZ = this.treeBase.getZ() + 0.5;
                this.climbX += this.outDirX * 0.5;
                this.climbZ += this.outDirZ * 0.5;
            }

            this.monkey.setPos(this.climbX, newY, this.climbZ);
            this.monkey.setDeltaMovement(0, 0, 0);

            this.monkey.setYRot(this.facingAngle);
            this.monkey.yBodyRot = this.facingAngle;
            this.monkey.yHeadRot = this.facingAngle;

            if (newY >= this.targetY || this.phaseTicks > 300) {
                int hangY = findValidHangY();

                this.facingAngle += 180.0f; // face outward
                this.monkey.setPos(this.climbX, hangY, this.climbZ);
                this.monkey.setDeltaMovement(0, 0, 0);
                this.monkey.entityData.set(DATA_CLIMBING, false);
                this.monkey.entityData.set(DATA_IN_TREE, true);

                this.monkey.gripBlock = findGripBlock(this.climbX, hangY, this.climbZ);
                this.monkey.entityData.set(DATA_GRIP_POS, java.util.Optional.ofNullable(this.monkey.gripBlock));
                this.moveTicksLeft = 0;
                this.phase = Phase.HANGING;
            }
        }

        private int findValidHangY() {
            Level level = this.monkey.level();
            int baseY = this.treeBase.getY();
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            for (int y = this.targetY; y >= baseY; y--) {
                pos.set((int) Math.floor(this.climbX), y, (int) Math.floor(this.climbZ));
                if (!level.getBlockState(pos).isSolid()) {
                    for (int dy = 1; dy <= 2; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dz = -1; dz <= 1; dz++) {
                                if (level.getBlockState(pos.offset(dx, dy, dz)).is(BlockTags.LEAVES)) {
                                    return y;
                                }
                            }
                        }
                    }
                }
            }

            for (int y = this.targetY + 1; y <= this.targetY + 5; y++) {
                pos.set((int) Math.floor(this.climbX), y, (int) Math.floor(this.climbZ));
                if (!level.getBlockState(pos).isSolid()) {
                    for (int dy = 1; dy <= 2; dy++) {
                        if (level.getBlockState(pos.above(dy)).is(BlockTags.LEAVES)) {
                            return y;
                        }
                    }
                }
            }

            return this.targetY; // last resort
        }

        private void tickHanging() {
            this.idleTicks++;
            this.monkey.setDeltaMovement(0, 0, 0);

            if (this.moveTicksLeft > 0) {
                this.moveTicksLeft--;
                float t = 1.0f - (float) this.moveTicksLeft / MOVE_DURATION;
                double x = this.moveFromX + (this.moveToX - this.moveFromX) * t;
                double y = this.moveFromY + (this.moveToY - this.moveFromY) * t;
                double z = this.moveFromZ + (this.moveToZ - this.moveFromZ) * t;
                this.monkey.setPos(x, y, z);
            } else {
                this.monkey.setPos(this.climbX, this.monkey.getY(), this.climbZ);
            }

            this.monkey.setYRot(this.facingAngle);
            this.monkey.yBodyRot = this.facingAngle;

            if (this.moveTicksLeft <= 0
                    && this.idleTicks % TRAVERSE_INTERVAL == 0
                    && this.idleTicks < HANG_TIME - 40) {
                tryTraverseLeaf();
            }
        }

        private void tryTraverseLeaf() {
            double nextX = this.climbX + this.outDirX;
            double nextZ = this.climbZ + this.outDirZ;
            double curY = this.monkey.getY();
            int monkeyBlockY = (int) Math.floor(curY);

            BlockPos nextLeaf = null;
            for (int dy = 1; dy <= 2; dy++) {
                BlockPos check = BlockPos.containing(nextX, monkeyBlockY + dy, nextZ);
                if (this.monkey.level().getBlockState(check).is(BlockTags.LEAVES)) {
                    nextLeaf = check;
                    break;
                }
            }
            if (nextLeaf == null) return; // no leaf at same level, stay put

            this.moveFromX = this.monkey.getX();
            this.moveFromY = this.monkey.getY();
            this.moveFromZ = this.monkey.getZ();
            this.moveToX = nextX;
            this.moveToY = curY; // always same Y
            this.moveToZ = nextZ;
            this.moveTicksLeft = MOVE_DURATION;

            this.climbX = nextX;
            this.climbZ = nextZ;
            this.monkey.gripBlock = nextLeaf;
            this.monkey.entityData.set(DATA_GRIP_POS, java.util.Optional.of(nextLeaf));
        }

        @Override
        public void stop() {
            this.monkey.clearTreeState();
            this.monkey.forceDropFromTree = false;
            this.treeBase = null;
            this.phaseTicks = 0;
            this.idleTicks = 0;
            this.phase = Phase.WALKING;
        }
    }
}
