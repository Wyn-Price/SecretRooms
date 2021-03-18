package com.wynprice.secretrooms.client.model;

import com.wynprice.secretrooms.client.model.quads.NoTintBakedQuadRetextured;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SixWayBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.Direction;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class OneWayGlassModel extends SecretBlockModel {
    public OneWayGlassModel(IBakedModel model) {
        super(model);
    }

    private static final Minecraft MC = Minecraft.getInstance();
    private static IBakedModel glassModel;

    @Override
    protected boolean canRenderInLater(BlockState state) {
        return MinecraftForgeClient.getRenderLayer() == RenderType.getCutout() || super.canRenderInLater(state);
    }

    @Override
    public List<BakedQuad> render(@Nonnull BlockState mirrorState, @Nonnull BlockState baseState, @Nonnull IBakedModel model, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        RenderType startLayer = MinecraftForgeClient.getRenderLayer();

        Supplier<List<BakedQuad>> superQuads = () -> getQuadsForSide(mirrorState, baseState, side, rand, extraData);
        List<BakedQuad> out = this.getQuadsNotSolid(baseState, mirrorState, superQuads, extraData);

        ForgeHooksClient.setRenderLayer(startLayer);

        return out;
    }

    private List<BakedQuad> getQuadsNotSolid(BlockState baseState, BlockState delegate, Supplier<List<BakedQuad>> superQuads, IModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>();

        RenderType layer = MinecraftForgeClient.getRenderLayer();
        if(layer == null || layer == RenderType.getCutout()) {
            quads.addAll(this.getGlassQuadsNotSolid(baseState, superQuads, extraData));
        }
        if(layer == null || RenderTypeLookup.canRenderInLayer(delegate, layer)) {
            quads.addAll(this.getDelegateQuadsNotSolid(baseState, superQuads));
        }

        return quads;
    }

    private List<BakedQuad> getGlassQuadsNotSolid(BlockState baseState, Supplier<List<BakedQuad>> superQuads, IModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>();
        for (BakedQuad bakedQuad : this.gatherAllQuads(superQuads)) {
            //If the quads facing direction is set to glass in the one way glass state
            if (baseState.get(SixWayBlock.FACING_TO_PROPERTY_MAP.get(bakedQuad.getFace()))) {
                quads.add(new NoTintBakedQuadRetextured(bakedQuad, glassModel.getParticleTexture(extraData)));
            }
        }
        return quads;
    }

    private List<BakedQuad> getDelegateQuadsNotSolid(BlockState baseState, Supplier<List<BakedQuad>> superQuads) {
        List<BakedQuad> quads = new ArrayList<>();
        for (BakedQuad bakedQuad : superQuads.get()) {
            //If the quads facing direction isn't set to glass in the one way glass state
            if (!baseState.get(SixWayBlock.FACING_TO_PROPERTY_MAP.get(bakedQuad.getFace()))) {
                quads.add(bakedQuad);
            }
        }
        return quads;
    }

    private List<BakedQuad> getQuadsForSide(BlockState mirrorState, BlockState baseState, Direction side, Random rand, IModelData extraData) {
        List<BakedQuad> quads = super.render(mirrorState, baseState, MC.getBlockRendererDispatcher().getModelForState(mirrorState), side, rand, extraData);
        super.render(mirrorState, baseState, MC.getBlockRendererDispatcher().getModelForState(mirrorState), null, rand, extraData).stream()
            .filter(q -> q.getFace() == side)
            .forEach(quads::add);

        return quads;
    }

    @SubscribeEvent
    public static void onModelsReady(ModelBakeEvent event) {
        glassModel = event.getModelManager().getModel(BlockModelShapes.getModelLocation(Blocks.GLASS.getDefaultState()));

    }
}
