package com.wynprice.secretrooms.client.model.providers;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SecretQuadProvider {

    @OnlyIn(Dist.CLIENT)
    public List<BakedQuad> render(@Nullable BlockState mirrorState, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return new ArrayList<>();
    }
}
