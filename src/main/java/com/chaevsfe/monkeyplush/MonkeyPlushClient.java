package com.chaevsfe.monkeyplush;

import com.chaevsfe.monkeyplush.client.JumboMonkeyRenderer;
import com.chaevsfe.monkeyplush.client.MonkeyPlushBlockRenderer;
import com.chaevsfe.monkeyplush.client.MonkeyPlushModel;
import com.chaevsfe.monkeyplush.client.MonkeyPlushRenderer;
import com.chaevsfe.monkeyplush.init.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public final class MonkeyPlushClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.EVIL_MONKEY, MonkeyPlushRenderer::new);
        EntityRendererRegistry.register(ModEntities.JUMBO_EVIL_MONKEY, JumboMonkeyRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(MonkeyPlushModel.LAYER_LOCATION, MonkeyPlushModel::createBodyLayer);
        BlockEntityRenderers.register(ModEntities.PLUSH_BLOCK_ENTITY, MonkeyPlushBlockRenderer::new);
    }
}
