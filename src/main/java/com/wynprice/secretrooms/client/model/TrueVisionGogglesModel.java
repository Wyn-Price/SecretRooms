package com.wynprice.secretrooms.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;

public class TrueVisionGogglesModel extends BipedModel<LivingEntity> {

    public static final TrueVisionGogglesModel INSTANCE = new TrueVisionGogglesModel();

    private TrueVisionGogglesModel() {
        super(RenderType::getEntityTranslucent, 0, 0, 64, 32);
        this.bipedHeadwear = new ModelRenderer(this, 32, 0);
        this.bipedHeadwear.setRotationPoint(0, 0, 0);
        this.bipedHeadwear.addBox(-4, -8, -4, 8, 8, 8, 0.2F);
    }

    @Override
    protected Iterable<ModelRenderer> getBodyParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelRenderer> getHeadParts() {
        return ImmutableList.of(this.bipedHead, this.bipedHeadwear);
    }
}
