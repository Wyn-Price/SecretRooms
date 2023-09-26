package com.wynprice.secretrooms.client.model;

import com.google.common.collect.ImmutableList;
import com.wynprice.secretrooms.SecretRooms7;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class TrueVisionGogglesModel extends HumanoidModel<LivingEntity> {

    public static final ModelLayerLocation TRUE_VISION_GOGGLES_MODEL = new ModelLayerLocation(new ResourceLocation(SecretRooms7.MODID, "true_vision"), "main");

    public TrueVisionGogglesModel(ModelPart part) {
        super(part, RenderType::entityTranslucent);
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.head, this.hat);
    }

    public static MeshDefinition createMesh(CubeDeformation deformation, float yOff) {
        MeshDefinition mesh = HumanoidModel.createMesh(deformation, yOff);
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, deformation.extend(0.2F)), PartPose.offset(0.0F, 0.0F + yOff, 0.0F));
        return mesh;
    }

}
