package com.wynprice.secretrooms.client;

import com.wynprice.secretrooms.ModelDataUtils;
import com.wynprice.secretrooms.server.SecretModelData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SecretRenderContextForge implements SecretModelRenderContext {

    private final RandomSource rand;
    private final ModelData data;
    private final RenderType type;

    private boolean acceptAllRendering;

    public SecretRenderContextForge(RandomSource rand, ModelData data, RenderType type) {
        this.rand = rand;
        this.data = data;
        this.type = type;
    }

    @Override
    public Optional<BlockState> mirrorState() {
        return ModelDataUtils.getData(data, SecretModelData.SRM_BLOCKSTATE);
    }

    @Override
    public Optional<BlockState> mappedState() {
        return ModelDataUtils.getData(data, SecretModelData.MODEL_MAP_STATE);
    }

    @Override
    public boolean canCurrentlyRender(RenderType type) {
        return this.acceptAllRendering || this.type == null || type == this.type;
    }

    @Override
    public boolean canCurrentlyRender(BlockState state) {
        if (this.acceptAllRendering) {
            return true;
        }
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(BlockModelShaper.stateToModelLocation(state));
        return model.getRenderTypes(state, this.rand, this.data).contains(this.type);
    }

    @Override
    public List<BakedQuad> gatherAllAQuadsFromSupplier(Supplier<List<BakedQuad>> supplier) {
        this.acceptAllRendering = true;
        List<BakedQuad> bakedQuads = supplier.get();
        this.acceptAllRendering = false;
        return bakedQuads;
    }

    @Override
    public List<BakedQuad> getQuads(BakedModel model, @Nullable BlockState state, @Nullable Direction direction, RandomSource rand) {
        return model.getQuads(state, direction, rand, this.data, this.acceptAllRendering ? null : this.type);
    }
}
