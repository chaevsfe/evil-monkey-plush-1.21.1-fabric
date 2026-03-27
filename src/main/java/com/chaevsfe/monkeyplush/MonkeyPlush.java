package com.chaevsfe.monkeyplush;

import com.chaevsfe.monkeyplush.entity.MonkeyPlushSpawner;
import com.chaevsfe.monkeyplush.init.ModEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MonkeyPlush implements ModInitializer {
    public static final String MOD_ID = "evilmonkey";
    public static final Logger LOGGER = LoggerFactory.getLogger("Evil Monkey");

    private final MonkeyPlushSpawner spawner = new MonkeyPlushSpawner();

    @Override
    public void onInitialize() {
        ModEntities.registerAll();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ServerLevel overworld = server.overworld();
            if (overworld != null) {
                spawner.tick(overworld);
            }
        });

        LOGGER.info("Evil Monkey loaded!");
    }

    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
