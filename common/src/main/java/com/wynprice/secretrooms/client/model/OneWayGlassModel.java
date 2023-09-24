package com.wynprice.secretrooms.client.model;

import com.wynprice.secretrooms.client.model.quads.NoTintBakedQuadRetextured;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class OneWayGlassModel extends SecretBlockModel {

    private static final Minecraft MC = Minecraft.getInstance();
    private static BakedModel glassModel;

    @Override
    protected boolean canRenderInLater(BlockState state) {
        return MinecraftForgeClient.getRenderType() == RenderType.cutout() || super.canRenderInLater(state);
    }

    @Override
    public List<BakedQuad> render(@Nonnull BlockState mirrorState, @Nonnull BlockState baseState, @Nonnull BakedModel model, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        RenderType startLayer = MinecraftForgeClient.getRenderType();

        Supplier<List<BakedQuad>> superQuads = () -> getQuadsForSide(mirrorState, baseState, side, rand, extraData);
        List<BakedQuad> out = this.getQuadsNotSolid(baseState, mirrorState, superQuads, extraData);

        ForgeHooksClient.setRenderType(startLayer);

        return out;
    }

    private List<BakedQuad> getQuadsNotSolid(BlockState baseState, BlockState delegate, Supplier<List<BakedQuad>> superQuads, IModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>();

        RenderType layer = MinecraftForgeClient.getRenderType();
        if(layer == null || layer == RenderType.cutout()) {
            quads.addAll(this.getGlassQuadsNotSolid(baseState, superQuads, extraData));
        }
        if(layer == null || ItemBlockRenderTypes.canRenderInLayer(delegate, layer)) {
            quads.addAll(this.getDelegateQuadsNotSolid(baseState, superQuads));
        }

        return quads;
    }

    private List<BakedQuad> getGlassQuadsNotSolid(BlockState baseState, Supplier<List<BakedQuad>> superQuads, IModelData extraData) {
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

    private List<BakedQuad> getQuadsForSide(BlockState mirrorState, BlockState baseState, Direction side, Random rand, IModelData extraData) {
        List<BakedQuad> quads = super.render(mirrorState, baseState, MC.getBlockRenderer().getBlockModel(mirrorState), side, rand, extraData);
        super.render(mirrorState, baseState, MC.getBlockRenderer().getBlockModel(mirrorState), null, rand, extraData).stream()
            .filter(q -> q.getDirection() == side)
            .forEach(quads::add);

        return quads;
    }

    @SubscribeEvent
    public static void onModelsReady(ModelBakeEvent event) {
        glassModel = event.getModelManager().getModel(BlockModelShaper.stateToModelLocation(Blocks.GLASS.defaultBlockState()));

    }
}
