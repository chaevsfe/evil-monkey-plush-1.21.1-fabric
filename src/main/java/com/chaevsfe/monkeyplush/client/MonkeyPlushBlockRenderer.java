package com.chaevsfe.monkeyplush.client;

import com.chaevsfe.monkeyplush.MonkeyPlush;
import com.chaevsfe.monkeyplush.block.MonkeyPlushBlock;
import com.chaevsfe.monkeyplush.block.MonkeyPlushBlockEntity;
import com.chaevsfe.monkeyplush.init.ModEntities;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class MonkeyPlushBlockRenderer implements BlockEntityRenderer<MonkeyPlushBlockEntity> {
    private static final ResourceLocation TEXTURE = MonkeyPlush.resource("textures/entity/evil_monkey.png");
    private final MonkeyPlushModel model;

    public MonkeyPlushBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new MonkeyPlushModel(context.bakeLayer(MonkeyPlushModel.LAYER_LOCATION));
    }

    @Override
    public void render(MonkeyPlushBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();

        boolean isJumbo = blockEntity.getBlockState().getBlock() == ModEntities.JUMBO_MONKEY_PLUSH_BLOCK;

        poseStack.translate(0.5, 0.0, 0.5);

        Direction facing = blockEntity.getBlockState().getValue(MonkeyPlushBlock.FACING);
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

        if (isJumbo) {
            poseStack.scale(4.0f, 4.0f, 4.0f);
        }

        poseStack.translate(0.0, 1.5, 0.0);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        setupPose(blockEntity.getPoseVariant());

        var vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        poseStack.popPose();
    }

    private void setupPose(int variant) {
        this.model.root().getAllParts().forEach(net.minecraft.client.model.geom.ModelPart::resetPose);

        var body = this.model.root().getChild("body");
        var leftArm = body.getChild("left_arm");
        var rightArm = body.getChild("right_arm");
        var leftLeg = this.model.root().getChild("left_leg");
        var rightLeg = this.model.root().getChild("right_leg");
        var tailBase = body.getChild("tail_base");
        var tailMid = tailBase.getChild("tail_mid");
        var tailTip = tailMid.getChild("tail_tip");

        switch (variant) {
            case 0 -> { // Arms forward, sitting
                leftArm.xRot = -1.3f;
                leftArm.zRot = -0.2f;
                rightArm.xRot = -1.3f;
                rightArm.zRot = 0.2f;
            }
            case 1 -> { // Arms up — tight to body
                leftArm.xRot = -3.0f;
                leftArm.zRot = 0.15f;
                rightArm.xRot = -3.0f;
                rightArm.zRot = -0.15f;
            }
            case 2 -> { // Arms out to sides, close to body
                leftArm.zRot = -0.4f;
                rightArm.zRot = 0.4f;
            }
            case 3 -> { // One arm waving, other down
                leftArm.xRot = -2.8f;
                leftArm.zRot = 0.6f;
                rightArm.xRot = 0.0f;
                rightArm.zRot = -0.15f;
            }
            case 4 -> { // Upside down on head
                body.xRot = 3.14f;
                body.y -= 7.0f;
                leftLeg.y -= 9.0f;
                rightLeg.y -= 9.0f;
            }
        }

        tailBase.xRot = -0.2f;
        tailBase.yRot = 0.15f;
        tailMid.xRot = -0.3f;
        tailTip.xRot = -0.4f;
    }
}
