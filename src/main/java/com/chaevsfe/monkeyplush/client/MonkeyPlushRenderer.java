package com.chaevsfe.monkeyplush.client;

import com.chaevsfe.monkeyplush.MonkeyPlush;
import com.chaevsfe.monkeyplush.entity.MonkeyPlushEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class MonkeyPlushRenderer extends MobRenderer<MonkeyPlushEntity, MonkeyPlushModel> {
    private static final ResourceLocation TEXTURE = MonkeyPlush.resource("textures/entity/evil_monkey.png");
    private static final ResourceLocation TEXTURE_ANGRY = MonkeyPlush.resource("textures/entity/evil_monkey_angry.png");

    public MonkeyPlushRenderer(EntityRendererProvider.Context context) {
        super(context, new MonkeyPlushModel(context.bakeLayer(MonkeyPlushModel.LAYER_LOCATION)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(MonkeyPlushEntity entity) {
        return entity.isAngry() ? TEXTURE_ANGRY : TEXTURE;
    }
}
