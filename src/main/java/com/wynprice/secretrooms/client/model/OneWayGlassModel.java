package com.wynprice.secretrooms.client.model;

import com.wynprice.secretrooms.client.model.quads.NoTintBakedQuadRetextured;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class OneWayGlassModel extends SecretBlockModel {
    public OneWayGlassModel(BakedModel model) {
        super(model);
    }

    private static final Minecraft MC = Minecraft.getInstance();
    private static BakedModel glassModel;

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
        return ChunkRenderTypeSet.union(super.getRenderTypes(state, rand, data), ChunkRenderTypeSet.of(RenderType.cutout()));
    }

    @Override
    protected List<BakedQuad> render(@NotNull BlockState mirrorState, @NotNull BlockState baseState, @NotNull BakedModel model, @org.jetbrains.annotations.Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @org.jetbrains.annotations.Nullable RenderType renderType) {
        Function<RenderType, List<BakedQuad>> superQuads = type -> getQuadsForSide(mirrorState, baseState, side, rand, extraData, type);
        return this.getQuadsNotSolid(baseState, mirrorState, model, superQuads, rand, extraData, renderType);
    }


    private List<BakedQuad> getQuadsNotSolid(BlockState baseState, BlockState delegate, BakedModel delegateModel, Function<RenderType, List<BakedQuad>> superQuads, RandomSource rand, ModelData extraData, RenderType layer) {
        List<BakedQuad> quads = new ArrayList<>();

        if(layer == null || layer == RenderType.cutout()) {
            quads.addAll(this.getGlassQuadsNotSolid(baseState, superQuads, extraData));
        }
        if(layer == null || delegateModel.getRenderTypes(delegate, rand, extraData).contains(layer)) {
            quads.addAll(this.getDelegateQuadsNotSolid(baseState, () -> superQuads.apply(layer)));
        }

        return quads;
    }

    private List<BakedQuad> getGlassQuadsNotSolid(BlockState baseState, Function<RenderType, List<BakedQuad>> superQuads, ModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>();
        for (BakedQuad bakedQuad : this.gatherAllQuads(superQuads)) {
            //If the quads facing direction is set to glass in the one way glass state
            if (baseState.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(bakedQuad.getDirection()))) {
                quads.add(new NoTintBakedQuadRetextured(bakedQuad, glassModel.getParticleIcon(extraData)));
            }
        }
        return quads;
    }

    private List<BakedQuad> getDelegateQuadsNotSolid(BlockState baseState, Supplier<List<BakedQuad>> superQuads) {
        List<BakedQuad> quads = new ArrayList<>();
        for (BakedQuad bakedQuad : superQuads.get()) {
            //If the quads facing direction isn't set to glass in the one way glass state
            if (!baseState.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(bakedQuad.getDirection()))) {
                quads.add(bakedQuad);
            }
        }
        return quads;
    }

    private List<BakedQuad> getQuadsForSide(BlockState mirrorState, BlockState baseState, Direction side, RandomSource rand, ModelData extraData, RenderType renderType) {
        List<BakedQuad> quads = super.render(mirrorState, baseState, MC.getBlockRenderer().getBlockModel(mirrorState), side, rand, extraData, renderType);
        super.render(mirrorState, baseState, MC.getBlockRenderer().getBlockModel(mirrorState), null, rand, extraData, renderType).stream()
            .filter(q -> q.getDirection() == side)
            .forEach(quads::add);

        return quads;
    }

    @SubscribeEvent
    public static void onModelsReady(ModelEvent.BakingCompleted event) {
        glassModel = event.getModelManager().getModel(BlockModelShaper.stateToModelLocation(Blocks.GLASS.defaultBlockState()));
    }
}
