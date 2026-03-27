package com.chaevsfe.monkeyplush.block;

import com.chaevsfe.monkeyplush.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MonkeyPlushBlockEntity extends BlockEntity {
    public static final int NUM_POSES = 5;
    private int poseVariant = -1;

    public MonkeyPlushBlockEntity(BlockPos pos, BlockState state) {
        super(ModEntities.PLUSH_BLOCK_ENTITY, pos, state);
    }

    public int getPoseVariant() {
        if (this.poseVariant < 0 && this.level != null) {
            this.poseVariant = this.level.random.nextInt(NUM_POSES);
            this.setChanged();
        }
        return Math.max(this.poseVariant, 0);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("PoseVariant", this.getPoseVariant());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("PoseVariant")) {
            this.poseVariant = tag.getInt("PoseVariant");
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        var tag = new CompoundTag();
        this.saveAdditional(tag, registries);
        return tag;
    }
}
