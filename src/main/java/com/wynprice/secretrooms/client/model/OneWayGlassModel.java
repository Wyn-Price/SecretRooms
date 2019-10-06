package com.wynprice.secretrooms.client.model;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.client.model.quads.NoTintBakedQuadRetextured;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SixWayBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = SecretRooms6.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class OneWayGlassModel extends SecretBlockModel {
    public OneWayGlassModel(IBakedModel model) {
        super(model);
    }

    private static final Minecraft MC = Minecraft.getInstance();
    private static IBakedModel glassModel;

    @Override
    protected boolean canRenderInLater(BlockState state) {
        return MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT || super.canRenderInLater(state);
    }

    @Override
    public List<BakedQuad> render(@Nonnull BlockState mirrorState, @Nonnull BlockState baseState, @Nonnull IBakedModel model, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        BlockRenderLayer startLayer = MinecraftForgeClient.getRenderLayer();

        LazyOptional<List<BakedQuad>> superQuads = LazyOptional.of(() -> getQuadsForSide(mirrorState, baseState, side, rand, extraData));
        List<BakedQuad> out = this.getQuadsNotSolid(baseState, mirrorState, superQuads, extraData);

        ForgeHooksClient.setRenderLayer(startLayer);

        return out;
    }

    private List<BakedQuad> getQuadsNotSolid(BlockState baseState, BlockState delegate, LazyOptional<List<BakedQuad>> superQuads, IModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>();

        if(MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT) {
            quads.addAll(this.getGlassQuadsNotSolid(baseState, delegate, superQuads, extraData));
            ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT);
        }
        quads.addAll(this.getDelegateQuadsNotSolid(baseState, delegate, superQuads));

        return quads;
    }

    private List<BakedQuad> getGlassQuadsNotSolid(BlockState baseState, BlockState delegate, LazyOptional<List<BakedQuad>> superQuads, IModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>();

        //Go through the render layers to get all the quads. As they are remapped to the glass texture, it doesn't matter.
        for (BlockRenderLayer value : BlockRenderLayer.values()) {
            ForgeHooksClient.setRenderLayer(value);

            if(delegate.canRenderInLayer(value) || MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT) {
                for (BakedQuad bakedQuad : superQuads.orElseThrow(NullPointerException::new)) {
                    //If the quads facing direction is set to glass in the one way glass state
                    if (baseState.get(SixWayBlock.FACING_TO_PROPERTY_MAP.get(bakedQuad.getFace())) && MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT) {
                        quads.add(new NoTintBakedQuadRetextured(bakedQuad, glassModel.getParticleTexture(extraData)));
                    }
                }
            }
        }
        return quads;
    }

    private List<BakedQuad> getDelegateQuadsNotSolid(BlockState baseState, BlockState delegate, LazyOptional<List<BakedQuad>> superQuads) {
        List<BakedQuad> quads = new ArrayList<>();
        if(delegate.canRenderInLayer(MinecraftForgeClient.getRenderLayer())) {
            for (BakedQuad bakedQuad : superQuads.orElseThrow(NullPointerException::new)) {
                //If the quads facing direction isn't set to glass in the one way glass state
                if (!baseState.get(SixWayBlock.FACING_TO_PROPERTY_MAP.get(bakedQuad.getFace()))) {
                    quads.add(bakedQuad);
                }
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
