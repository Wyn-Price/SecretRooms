package com.wynprice.secretrooms.client.model.providers;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.client.model.OneWayGlassBlockstateDelegate;
import com.wynprice.secretrooms.server.utils.CachedObject;
import com.wynprice.secretrooms.server.utils.ModelDataUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SixWayBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BakedQuadRetextured;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = SecretRooms6.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OneWayGlassProvider extends SecretQuadProvider {

    private static final Minecraft MC = Minecraft.getInstance();

    public static final OneWayGlassProvider ONE_WAY_GLASS = new OneWayGlassProvider();

    private static IBakedModel glassModel;

    @Override
    public List<BakedQuad> render(@Nullable BlockState mirrorState, @Nonnull BlockState baseState, @Nonnull IBakedModel model, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {

        //Make sure that the mirror state isn't the delegate state, otherwise the model will still be missing.
        BlockState delegateImpl = mirrorState instanceof OneWayGlassBlockstateDelegate ? ((OneWayGlassBlockstateDelegate) mirrorState).getDelegate() : mirrorState;
        if(delegateImpl == null) {
            if(mirrorState == null) {
                delegateImpl = Blocks.STONE.getDefaultState();
            } else {
                delegateImpl = mirrorState;
            }
        }
        BlockState delegate = delegateImpl; //Used as needs to be effectively final

        CachedObject<List<BakedQuad>> superQuads = new CachedObject<>(() -> super.render(delegate, baseState, MC.getBlockRendererDispatcher().getModelForState(delegate), side, rand, extraData));

        if(delegate.isSolid()) {
            return this.getQuadsSolid(baseState, delegate, superQuads, side, rand, extraData);
        } else {
            return this.getQuadsNotSolid(baseState, delegate, superQuads, extraData);
        }
    }

    private List<BakedQuad> getQuadsSolid(BlockState baseState, BlockState delegate, CachedObject<List<BakedQuad>> superQuads, Direction side, Random rand, IModelData extraData) {
        BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();

        if(side != null && baseState.get(SixWayBlock.FACING_TO_PROPERTY_MAP.get(side))) {
            if (layer == BlockRenderLayer.CUTOUT && ModelDataUtils.getData(extraData, SecretModelData.SRM_ONE_WAY_GLASS_SIDES).map(directions -> directions.contains(side)).orElse(true)) {
                return glassModel.getQuads(Blocks.GLASS.getDefaultState(), side, rand, extraData);
            }
        } else {
            if(delegate.canRenderInLayer(layer)) {
                //Render the delegate model
                return superQuads.get();
            }
        }
        return new ArrayList<>();
    }

    private List<BakedQuad> getQuadsNotSolid(BlockState baseState, BlockState delegate, CachedObject<List<BakedQuad>> superQuads, IModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>();

        if(MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT) {
            quads.addAll(this.getGlassQuadsNotSolid(baseState, delegate, superQuads, extraData));
        }
        quads.addAll(this.getDelegateQuadsNotSolid(baseState, delegate, superQuads));

        return quads;
    }

    private List<BakedQuad> getGlassQuadsNotSolid(BlockState baseState, BlockState delegate, CachedObject<List<BakedQuad>> superQuads, IModelData extraData) {
        List<BakedQuad> quads = new ArrayList<>();

        //Go through the render layers to get all the quads. As they are remapped to the glass texture, it doesn't matter.
        for (BlockRenderLayer value : BlockRenderLayer.values()) {
            ForgeHooksClient.setRenderLayer(value);

            if(delegate.canRenderInLayer(value)) {
                for (BakedQuad bakedQuad : superQuads.get()) {
                    //If the quads facing direction is set to glass in the one way glass state
                    if (baseState.get(SixWayBlock.FACING_TO_PROPERTY_MAP.get(bakedQuad.getFace()))) {
                        if (MinecraftForgeClient.getRenderLayer() == BlockRenderLayer.CUTOUT) {
                            quads.add(new BakedQuadRetextured(bakedQuad, glassModel.getParticleTexture(extraData)));
                        }
                    }
                }
            }

        }
        ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT);

        return quads;
    }

    private List<BakedQuad> getDelegateQuadsNotSolid(BlockState baseState, BlockState delegate, CachedObject<List<BakedQuad>> superQuads) {
        List<BakedQuad> quads = new ArrayList<>();
        if(delegate.canRenderInLayer(MinecraftForgeClient.getRenderLayer())) {
            for (BakedQuad bakedQuad : superQuads.get()) {
                //If the quads facing direction isn't set to glass in the one way glass state
                if (!baseState.get(SixWayBlock.FACING_TO_PROPERTY_MAP.get(bakedQuad.getFace()))) {
                    quads.add(bakedQuad);
                }
            }
        }
        return quads;
    }

    @SubscribeEvent
    public static void onModelsReady(ModelBakeEvent event) {
        glassModel = event.getModelManager().getModel(BlockModelShapes.getModelLocation(Blocks.GLASS.getDefaultState()));
    }
}
