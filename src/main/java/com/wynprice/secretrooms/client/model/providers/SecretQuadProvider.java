package com.wynprice.secretrooms.client.model.providers;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SecretQuadProvider {

    public static final SecretQuadProvider INSTANCE = new SecretQuadProvider();

    @OnlyIn(Dist.CLIENT)
    public List<BakedQuad> render(@Nullable BlockState mirrorState, @Nonnull BlockState baseState, @Nonnull IBakedModel model, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return new ArrayList<>(model.getQuads(mirrorState, side, rand, extraData));
    }
}
