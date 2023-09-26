package com.wynprice.secretrooms.client.model;

import com.wynprice.secretrooms.client.SecretModelRenderContext;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class OneWayGlassModel extends SecretBlockModel {

    private static final Minecraft MC = Minecraft.getInstance();
    private static BakedModel glassModel;

    @Override
    protected boolean canRenderInLayer(BlockState state, SecretModelRenderContext context) {
        return context.canCurrentlyRender(RenderType.cutout()) || super.canRenderInLayer(state, context);
    }

    @Override
    protected List<BakedQuad> render(@NotNull BlockState mirrorState, @NotNull BlockState baseState, @NotNull BakedModel model, @org.jetbrains.annotations.Nullable Direction side, @NotNull RandomSource rand, SecretModelRenderContext context) {
        Supplier<List<BakedQuad>> superQuads = () -> getQuadsForSide(mirrorState, baseState, side, rand, context);
        return this.getQuadsNotSolid(baseState, mirrorState, superQuads, context);
    }


    private List<BakedQuad> getQuadsNotSolid(BlockState baseState, BlockState delegate, Supplier<List<BakedQuad>> superQuads, SecretModelRenderContext context) {
        List<BakedQuad> quads = new ArrayList<>();

        if(context.canCurrentlyRender(RenderType.cutout())) {
            quads.addAll(this.getGlassQuadsNotSolid(baseState, superQuads, context));
        }
        if(context.canCurrentlyRender(delegate)) {
            quads.addAll(this.getDelegateQuadsNotSolid(baseState, superQuads));
        }

        return quads;
    }

    private List<BakedQuad> getGlassQuadsNotSolid(BlockState baseState, Supplier<List<BakedQuad>> superQuads, SecretModelRenderContext context) {
        if(glassModel == null) {
            refreshModel();
        }
        List<BakedQuad> quads = new ArrayList<>();
        for (BakedQuad bakedQuad : this.gatherAllQuads(context, superQuads)) {
            //If the quads facing direction is set to glass in the one way glass state
            if (baseState.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(bakedQuad.getDirection()))) {
                quads.add(new NoTintBakedQuadRetextured(bakedQuad, glassModel.getParticleIcon()));
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

    private List<BakedQuad> getQuadsForSide(BlockState mirrorState, BlockState baseState, Direction side, RandomSource rand, SecretModelRenderContext context) {
        List<BakedQuad> quads = super.render(mirrorState, baseState, MC.getBlockRenderer().getBlockModel(mirrorState), side, rand, context);
        super.render(mirrorState, baseState, MC.getBlockRenderer().getBlockModel(mirrorState), null, rand, context).stream()
            .filter(q -> q.getDirection() == side)
            .forEach(quads::add);

        return quads;
    }

    public static void refreshModel() {
        glassModel = Minecraft.getInstance().getModelManager().getModel(BlockModelShaper.stateToModelLocation(Blocks.GLASS.defaultBlockState()));

    }
}
