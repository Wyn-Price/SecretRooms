package com.wynprice.secretrooms.client.model;

import com.wynprice.secretrooms.client.SecretModelRenderContext;
import com.wynprice.secretrooms.client.model.quads.TrueVisionBakedQuad;
import com.wynprice.secretrooms.server.items.TrueVisionGogglesClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class SecretBlockModel {

    private static final Supplier<BlockRenderDispatcher> DISPATCHER = () -> Minecraft.getInstance().getBlockRenderer();

    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, SecretModelRenderContext context) {
        Optional<BlockState> data = context.mirrorState();
        if (data.isEmpty() || !this.canRenderInLayer(data.get(), context)) {
            return Collections.emptyList();
        }
        BlockState mirrorState = data.get();
        Supplier<List<BakedQuad>> quads = () -> this.render(mirrorState, state, DISPATCHER.get().getBlockModel(mirrorState), side, rand, context);
        if (trueVision() && context.canCurrentlyRender(RenderType.translucent())) {
            List<BakedQuad> quadList = quads.get();
            this.getHelmetQuads(this.gatherAllQuads(context, quads), quadList);
            return quadList;
        }
        return quads.get();
    }

    private void getHelmetQuads(List<BakedQuad> allQuads, List<BakedQuad> quads) {
        for (BakedQuad quad : allQuads) {
            quads.add(TrueVisionBakedQuad.generateQuad(quad));
        }
    }

    private boolean trueVision() {
        return TrueVisionGogglesClientHandler.isWearingGoggles(Minecraft.getInstance().player);
    }

    protected boolean canRenderInLayer(BlockState state, SecretModelRenderContext context) {
        return (trueVision() && context.canCurrentlyRender(RenderType.translucent())) || context.canCurrentlyRender(state);
    }

    protected List<BakedQuad> gatherAllQuads(SecretModelRenderContext context, Supplier<List<BakedQuad>> superQuads) {
//        RenderType layer = MinecraftForgeClient.getRenderType();
//
//        ForgeHooksClient.setRenderType(null);
//        List<BakedQuad> quads = new ArrayList<>(superQuads.get());
//        ForgeHooksClient.setRenderType(layer);
//        return quads;
        return context.gatherAllAQuadsFromSupplier(superQuads);
    }

    protected List<BakedQuad> render(@Nonnull BlockState mirrorState, @Nonnull BlockState baseState, @Nonnull BakedModel model, @Nullable Direction side, @Nonnull RandomSource rand, SecretModelRenderContext context) {
        return new ArrayList<>(context.getQuads(model, mirrorState, side, rand));
    }

//    @Override
//    public TextureAtlasSprite getParticleIcon(IModelData data) {
//        return trueVision() ?
//            this.model.getParticleIcon(data) :
//            ModelDataUtils.getData(data, SRM_BLOCKSTATE).map(DISPATCHER.get()::getBlockModel).orElse(this.model).getParticleIcon(data);
//    }

//    @Nonnull
//    @Override
//    public IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
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
//
//        IModelData data = this.model.getModelData(world, pos, state, tileData);
//        if(data != tileData && tileData != EmptyModelData.INSTANCE) {
//            if(tileData.hasProperty(SRM_BLOCKSTATE) && !data.hasProperty(SRM_BLOCKSTATE)) {
//                data.setData(SRM_BLOCKSTATE, tileData.getData(SRM_BLOCKSTATE));
//            }
//            if(tileData.hasProperty(MODEL_MAP_STATE) && !data.hasProperty(MODEL_MAP_STATE)) {
//                data.setData(MODEL_MAP_STATE, tileData.getData(MODEL_MAP_STATE));
//            }
//        }
//
//        return data;
//    }

//    @Override
//    public boolean usesBlockLight() {
//        return trueVision() && this.model.usesBlockLight();
//    }
}
