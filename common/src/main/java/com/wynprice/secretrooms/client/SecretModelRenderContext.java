package com.wynprice.secretrooms.client;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface SecretModelRenderContext {
        Optional<BlockState> mirrorState();
        Optional<BlockState> mappedState();

        // MinecraftForgeClient.getRenderType() == RenderType.translucent()
        boolean canCurrentlyRender(RenderType type);

        boolean canCurrentlyRender(BlockState state);

        List<BakedQuad> gatherAllAQuadsFromSupplier(Supplier<List<BakedQuad>> supplier);

        List<BakedQuad> getQuads(BakedModel model, @Nullable BlockState state, @Nullable Direction direction, RandomSource rand);


}
