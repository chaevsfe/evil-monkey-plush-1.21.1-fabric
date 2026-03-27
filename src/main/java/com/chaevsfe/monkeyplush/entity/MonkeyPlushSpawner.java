package com.chaevsfe.monkeyplush.entity;

import com.chaevsfe.monkeyplush.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;

public class MonkeyPlushSpawner {
    private int tickCounter = 0;
    private static final int CHECK_INTERVAL = 6000; // 5 minutes
    private static final double SPAWN_CHANCE = 0.15;
    private static final int MIN_DISTANCE = 24;
    private static final int MAX_DISTANCE = 48;

    public void tick(ServerLevel level) {
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) return;

        if (++this.tickCounter < CHECK_INTERVAL) return;
        this.tickCounter = 0;

        for (ServerPlayer player : level.players()) {
            if (player.isSpectator()) continue;
            if (level.getRandom().nextDouble() > SPAWN_CHANCE) continue;

            int attempts = 10;
            for (int i = 0; i < attempts; i++) {
                int dx = level.getRandom().nextInt(MAX_DISTANCE - MIN_DISTANCE + 1) + MIN_DISTANCE;
                int dz = level.getRandom().nextInt(MAX_DISTANCE - MIN_DISTANCE + 1) + MIN_DISTANCE;
                if (level.getRandom().nextBoolean()) dx = -dx;
                if (level.getRandom().nextBoolean()) dz = -dz;

                int x = player.blockPosition().getX() + dx;
                int z = player.blockPosition().getZ() + dz;
                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

                BlockPos spawnPos = new BlockPos(x, y, z);

                if (!level.isLoaded(spawnPos)) continue;

                var monkey = ModEntities.EVIL_MONKEY.create(level);
                if (monkey == null) continue;

                monkey.moveTo(x + 0.5, y, z + 0.5, level.getRandom().nextFloat() * 360.0f, 0.0f);
                monkey.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos),
                        MobSpawnType.EVENT, null);

                if (level.addFreshEntity(monkey)) {
                    return; // only spawn one per cycle
                }
            }
        }
    }
}
