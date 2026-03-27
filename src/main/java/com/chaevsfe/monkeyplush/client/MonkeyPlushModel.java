package com.chaevsfe.monkeyplush.client;

import com.chaevsfe.monkeyplush.MonkeyPlush;
import com.chaevsfe.monkeyplush.entity.MonkeyPlushEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

public class MonkeyPlushModel extends EntityModel<MonkeyPlushEntity> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(MonkeyPlush.resource("evil_monkey"), "main");

    private static final int TEX_WIDTH = 64;
    private static final int TEX_HEIGHT = 64;

    private final ModelPart root;
    public ModelPart root() { return this.root; }
    private final ModelPart body;
    private final ModelPart muzzle;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart leftArm;
    private final ModelPart leftPom;
    private final ModelPart rightArm;
    private final ModelPart rightPom;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart tailBase;
    private final ModelPart tailMid;
    private final ModelPart tailTip;

    public MonkeyPlushModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.root = root;
        this.body = root.getChild("body");
        this.muzzle = this.body.getChild("muzzle");
        this.leftEar = this.body.getChild("left_ear");
        this.rightEar = this.body.getChild("right_ear");
        this.leftArm = this.body.getChild("left_arm");
        this.leftPom = this.leftArm.getChild("left_pom");
        this.rightArm = this.body.getChild("right_arm");
        this.rightPom = this.rightArm.getChild("right_pom");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.tailBase = this.body.getChild("tail_base");
        this.tailMid = this.tailBase.getChild("tail_mid");
        this.tailTip = this.tailMid.getChild("tail_tip");
    }

    public static LayerDefinition createBodyLayer() {
        var mesh = new MeshDefinition();
        var root = mesh.getRoot();

        var body = root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0f, -9.0f, -4.0f, 8, 9, 8, new CubeDeformation(0.15f)),
                PartPose.offset(0.0f, 22.0f, 0.0f));

        body.addOrReplaceChild("muzzle",
                CubeListBuilder.create()
                        .texOffs(0, 17)
                        .addBox(-3.0f, -2.0f, -3.0f, 6, 4, 3, new CubeDeformation(0.1f)),
                PartPose.offset(0.0f, -4.5f, -4.0f));

        body.addOrReplaceChild("left_ear",
                CubeListBuilder.create()
                        .texOffs(32, 0)
                        .addBox(0.0f, -1.0f, -0.5f, 2, 2, 1),
                PartPose.offset(4.0f, -6.0f, -2.0f));

        body.addOrReplaceChild("right_ear",
                CubeListBuilder.create()
                        .texOffs(32, 3)
                        .addBox(-2.0f, -1.0f, -0.5f, 2, 2, 1),
                PartPose.offset(-4.0f, -6.0f, -2.0f));

        var leftArm = body.addOrReplaceChild("left_arm",
                CubeListBuilder.create()
                        .texOffs(0, 24)
                        .addBox(-1.0f, 0.0f, -1.0f, 2, 16, 2),
                PartPose.offsetAndRotation(5.0f, -3.0f, 0.0f,
                        0.08f, 0.0f, -0.1f));

        leftArm.addOrReplaceChild("left_pom",
                CubeListBuilder.create()
                        .texOffs(8, 24)
                        .addBox(-1.0f, 0.0f, -1.0f, 2, 2, 2, new CubeDeformation(0.5f)),
                PartPose.offset(0.0f, 16.0f, 0.0f));

        var rightArm = body.addOrReplaceChild("right_arm",
                CubeListBuilder.create()
                        .texOffs(16, 24)
                        .addBox(-1.0f, 0.0f, -1.0f, 2, 16, 2),
                PartPose.offsetAndRotation(-5.0f, -3.0f, 0.0f,
                        0.08f, 0.0f, 0.1f));

        rightArm.addOrReplaceChild("right_pom",
                CubeListBuilder.create()
                        .texOffs(24, 24)
                        .addBox(-1.0f, 0.0f, -1.0f, 2, 2, 2, new CubeDeformation(0.5f)),
                PartPose.offset(0.0f, 16.0f, 0.0f));

        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create()
                        .texOffs(38, 0)
                        .addBox(-1.5f, 0.0f, -1.5f, 3, 2, 3),
                PartPose.offset(2.0f, 22.0f, -0.5f));

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create()
                        .texOffs(38, 5)
                        .addBox(-1.5f, 0.0f, -1.5f, 3, 2, 3),
                PartPose.offset(-2.0f, 22.0f, -0.5f));

        var tailBase = body.addOrReplaceChild("tail_base",
                CubeListBuilder.create()
                        .texOffs(50, 0)
                        .addBox(-1.0f, -3.0f, -1.0f, 2, 3, 2),
                PartPose.offsetAndRotation(0.0f, -3.0f, 4.0f,
                        -0.35f, 0.0f, 0.0f));

        var tailMid = tailBase.addOrReplaceChild("tail_mid",
                CubeListBuilder.create()
                        .texOffs(50, 5)
                        .addBox(-1.0f, -3.0f, -1.0f, 2, 3, 2),
                PartPose.offsetAndRotation(0.0f, -3.0f, 0.0f,
                        -0.45f, 0.0f, 0.0f));

        tailMid.addOrReplaceChild("tail_tip",
                CubeListBuilder.create()
                        .texOffs(50, 10)
                        .addBox(-1.0f, -3.0f, -1.0f, 2, 3, 2),
                PartPose.offsetAndRotation(0.0f, -3.0f, 0.0f,
                        -0.5f, 0.0f, 0.0f));

        return LayerDefinition.create(mesh, TEX_WIDTH, TEX_HEIGHT);
    }

    @Override
    public void setupAnim(MonkeyPlushEntity entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float headYaw, float headPitch) {
        this.root.getAllParts().forEach(ModelPart::resetPose);

        boolean climbing = entity.isClimbingTree();
        boolean inTree = entity.isInTree();

        if (climbing) {
            animateClimbing(ageInTicks);
        } else if (inTree) {
            animateTreeIdle(entity, ageInTicks, headYaw);
        } else {
            animateGround(entity, limbSwing, limbSwingAmount, ageInTicks, headYaw);
        }

        int attackTick = entity.getAttackTick();
        if (attackTick > 0) {
            float progress = (10 - attackTick) / 10.0f;
            float armAngle;
            if (progress < 0.4f) {
                armAngle = -Mth.PI * (progress / 0.4f);
            } else {
                float slamProgress = (progress - 0.4f) / 0.6f;
                armAngle = -Mth.PI + (Mth.PI + 0.8f) * slamProgress;
            }
            this.leftArm.xRot = armAngle;
            this.rightArm.xRot = armAngle;
            this.leftArm.zRot = -0.1f;
            this.rightArm.zRot = 0.1f;
        }
    }

    private void animateClimbing(float age) {
        float cycle = age * 0.25f;

        this.body.xRot = 0.2f;
        this.body.zRot = Mth.sin(cycle * 0.5f) * 0.04f;

        this.leftArm.xRot = -2.0f + Mth.sin(cycle) * 0.7f;
        this.rightArm.xRot = -2.0f + Mth.sin(cycle + Mth.PI) * 0.7f;
        this.leftArm.zRot = -0.3f;
        this.rightArm.zRot = 0.3f;

        this.leftLeg.xRot = -0.3f + Mth.sin(cycle + Mth.PI) * 0.5f;
        this.rightLeg.xRot = -0.3f + Mth.sin(cycle) * 0.5f;

        this.tailBase.xRot = -0.6f;
        this.tailMid.xRot = -0.7f;
        this.tailTip.xRot = -0.8f;
        this.tailBase.yRot = Mth.sin(age * 0.1f) * 0.1f;
    }

    private void animateTreeIdle(MonkeyPlushEntity entity, float age, float headYaw) {
        float yaw = Mth.clamp(headYaw, -45.0f, 45.0f) * Mth.DEG_TO_RAD;
        float sway = Mth.sin(age * 0.04f);

        this.body.yRot = yaw * 0.3f + Mth.sin(age * 0.02f) * 0.15f;

        this.body.zRot = sway * 0.08f;
        this.body.xRot = 0.05f; // slight forward lean

        this.leftArm.xRot = -Mth.PI + 0.2f + Mth.sin(age * 0.04f) * 0.1f;
        this.rightArm.xRot = -Mth.PI + 0.2f + Mth.sin(age * 0.04f + 0.5f) * 0.1f;
        this.leftArm.zRot = 0.15f + sway * 0.05f;
        this.rightArm.zRot = -0.15f + sway * 0.05f;

        float entityOffset = (entity.getId() * 137) % 100;
        if (Mth.sin((age + entityOffset) * 0.015f) > 0.85f) {
            this.rightArm.xRot = 0.3f + Mth.sin(age * 0.1f) * 0.2f;
            this.rightArm.zRot = 0.3f;
            this.body.zRot += -0.08f;
        }

        this.leftLeg.xRot = 0.2f + Mth.sin(age * 0.06f) * 0.1f;
        this.rightLeg.xRot = 0.2f + Mth.sin(age * 0.06f + 0.8f) * 0.1f;

        this.tailBase.xRot = 2.8f + Mth.sin(age * 0.06f) * 0.1f;
        this.tailBase.yRot = sway * 0.2f;
        this.tailMid.xRot = 0.3f + Mth.sin(age * 0.07f + 0.3f) * 0.1f;
        this.tailMid.yRot = sway * 0.15f;
        this.tailTip.xRot = 0.2f + Mth.sin(age * 0.08f + 0.6f) * 0.08f;
        this.tailTip.yRot = sway * 0.1f;

        float earTwitch = (Mth.sin(age * 0.2f) > 0.92f) ? 0.2f : 0.0f;
        this.leftEar.zRot = -earTwitch;
        this.rightEar.zRot = earTwitch;
    }

    private void animateGround(MonkeyPlushEntity entity, float limbSwing, float limbSwingAmount,
                               float age, float headYaw) {
        float yaw = Mth.clamp(headYaw, -30.0f, 30.0f) * Mth.DEG_TO_RAD;
        this.body.yRot = yaw * 0.4f;

        this.body.y = 22.0f + Mth.sin(age * 0.08f) * 0.2f;
        this.body.zRot = Mth.sin(age * 0.05f) * 0.015f;

        this.leftArm.xRot = 0.08f + Mth.sin(age * 0.07f) * 0.15f;
        this.rightArm.xRot = 0.08f + Mth.sin(age * 0.07f + 0.8f) * 0.15f;
        this.leftArm.zRot = -0.1f + Mth.sin(age * 0.06f) * 0.06f;
        this.rightArm.zRot = 0.1f - Mth.sin(age * 0.06f + 0.5f) * 0.06f;

        float tailSway = Mth.sin(age * 0.15f);
        this.tailBase.xRot = -0.35f + Mth.sin(age * 0.1f) * 0.08f;
        this.tailBase.yRot = tailSway * 0.15f;
        this.tailMid.xRot = -0.45f + Mth.sin(age * 0.12f + 0.3f) * 0.1f;
        this.tailMid.yRot = tailSway * 0.1f;
        this.tailTip.xRot = -0.5f + Mth.sin(age * 0.14f + 0.6f) * 0.12f;
        this.tailTip.yRot = tailSway * 0.08f;

        float earTwitch = (Mth.sin(age * 0.3f) > 0.95f) ? 0.15f : 0.0f;
        this.leftEar.zRot = -earTwitch;
        this.rightEar.zRot = earTwitch;

        if (limbSwingAmount > 0.01f) {
            float walk = Mth.clamp(limbSwingAmount, 0.0f, 1.0f);
            this.body.y += Math.abs(Mth.sin(limbSwing * 0.5f)) * 1.2f * walk;
            this.body.zRot += Mth.sin(limbSwing * 0.5f) * 0.08f * walk;
            this.leftArm.xRot += Mth.cos(limbSwing * 0.5f) * 1.4f * walk;
            this.rightArm.xRot += Mth.cos(limbSwing * 0.5f + Mth.PI) * 1.4f * walk;
            this.leftArm.zRot += Math.abs(Mth.sin(limbSwing * 0.5f)) * -0.2f * walk;
            this.rightArm.zRot += Math.abs(Mth.sin(limbSwing * 0.5f + Mth.PI)) * 0.2f * walk;
            this.leftLeg.xRot = Mth.cos(limbSwing * 0.5f + Mth.PI) * 0.5f * walk;
            this.rightLeg.xRot = Mth.cos(limbSwing * 0.5f) * 0.5f * walk;
            this.tailBase.xRot += Mth.sin(limbSwing * 0.5f) * 0.3f * walk;
        }

        float squish = entity.getSquishAmount(age - (int) age);
        if (squish > 0.0f) {
            this.body.xScale = 1.0f + squish * 0.3f;
            this.body.yScale = 1.0f - squish * 0.25f;
            this.body.zScale = 1.0f + squish * 0.3f;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer,
                               int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, color);
    }
}
