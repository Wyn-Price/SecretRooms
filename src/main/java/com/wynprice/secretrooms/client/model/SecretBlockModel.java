package com.wynprice.secretrooms.client.model;

import com.mojang.datafixers.util.Pair;
import com.wynprice.secretrooms.client.model.quads.TrueVisionBakedQuad;
import com.wynprice.secretrooms.server.items.TrueVisionGogglesClientHandler;
import com.wynprice.secretrooms.server.utils.ModelDataUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.wynprice.secretrooms.client.SecretModelData.MODEL_MAP_STATE;
import static com.wynprice.secretrooms.client.SecretModelData.SRM_BLOCKSTATE;

public class SecretBlockModel implements BakedModel {

    private static final Supplier<BlockRenderDispatcher> DISPATCHER = () -> Minecraft.getInstance().getBlockRenderer();
    private final BakedModel model;

    public SecretBlockModel(BakedModel model) {
        this.model = model;
    }

    public Optional<Pair<BlockState, BakedModel>> getModel(ModelData data) {
        return ModelDataUtils.getData(data, SRM_BLOCKSTATE).map(s -> Pair.of(s, DISPATCHER.get().getBlockModel(s)));
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        var data = this.getModel(extraData);
        if (data.isEmpty()) {
            return Collections.emptyList();
        }
        BlockState mirrorState = data.get().getFirst();
        BakedModel mirrorModel = data.get().getSecond();

        Function<RenderType, List<BakedQuad>> quads = type -> this.render(mirrorState, state, mirrorModel, side, rand, extraData, type);
        if (trueVision() && renderType == RenderType.translucent()) {
            List<BakedQuad> quadList = quads.apply(renderType);
            this.getHelmetQuads(this.gatherAllQuads(quads), quadList);
            return quadList;
        }
        return quads.apply(renderType);
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        var optional = this.getModel(data);
        if (optional.isEmpty()) {
            return ChunkRenderTypeSet.none();
        }
        BlockState mirrorState = optional.get().getFirst();
        BakedModel mirrorModel = optional.get().getSecond();

        ChunkRenderTypeSet mirrorSet = mirrorModel.getRenderTypes(mirrorState, rand, data);

        if(!trueVision()) {
            return ChunkRenderTypeSet.union(mirrorSet, ChunkRenderTypeSet.of(RenderType.translucent()));
        }

        return mirrorSet;
    }

    private void getHelmetQuads(List<BakedQuad> allQuads, List<BakedQuad> quads) {
        for (BakedQuad quad : allQuads) {
            quads.add(TrueVisionBakedQuad.generateQuad(quad));
        }
    }

    private boolean trueVision() {
        return TrueVisionGogglesClientHandler.isWearingGoggles(Minecraft.getInstance().player);
    }

    protected List<BakedQuad> gatherAllQuads(Function<RenderType, List<BakedQuad>> superQuads) {
        //TODO: maybe loop through?
        return new ArrayList<>(superQuads.apply(null));
    }

    protected List<BakedQuad> render(@Nonnull BlockState mirrorState, @Nonnull BlockState baseState, @NotNull BakedModel model, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        return new ArrayList<>(model.getQuads(mirrorState, side, rand, extraData, renderType));
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data) {
        return trueVision() ?
            this.model.getParticleIcon(data) :
            ModelDataUtils.getData(data, SRM_BLOCKSTATE).map(DISPATCHER.get()::getBlockModel).orElse(this.model).getParticleIcon(data);
    }

    @Nonnull
    @Override
    public ModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData) {
//        if(tileData == EmptyModelData.INSTANCE) {
//            BlockEntity entity = world.getBlockEntity(pos);
//            if(entity != null) {
//                tileData = entity.getModelData();
//            }
//        }
//
//        Optional<BlockState> mirror = ModelDataUtils.getData(tileData, SRM_BLOCKSTATE);
//        if(mirror.isPresent()) {
//            DelegateWorld pooled = DelegateWorld.getPooled(world);
//            tileData = DISPATCHER.get().getBlockModel(mirror.get()).getModelData(pooled, pos, mirror.get(), tileData);
//            pooled.release();
//        }

        ModelData data = this.model.getModelData(world, pos, state, tileData);
        ModelData.Builder builder = data.derive();
        if(data != tileData && !tileData.getProperties().isEmpty()) {
            if(tileData.has(SRM_BLOCKSTATE) && !data.has(SRM_BLOCKSTATE)) {
                builder.with(SRM_BLOCKSTATE, tileData.get(SRM_BLOCKSTATE));
            }
            if(tileData.has(MODEL_MAP_STATE) && !data.has(MODEL_MAP_STATE)) {
                builder.with(MODEL_MAP_STATE, tileData.get(MODEL_MAP_STATE));
            }
        }

        return builder.build();
    }

    @Override
    public boolean isGui3d() {
        return this.model.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return trueVision() && this.model.usesBlockLight();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand) {
        return this.model.getQuads(state, side, rand);
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.model.getTransforms();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.model.useAmbientOcclusion();
    }

    @Override
    public boolean useAmbientOcclusion(BlockState state) {
        return this.model.useAmbientOcclusion(state);
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.model.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.model.getOverrides();
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

}
