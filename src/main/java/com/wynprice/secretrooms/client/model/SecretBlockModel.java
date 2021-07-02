package com.wynprice.secretrooms.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.wynprice.secretrooms.client.model.quads.TrueVisionBakedQuad;
import com.wynprice.secretrooms.client.world.DelegateWorld;
import com.wynprice.secretrooms.server.items.TrueVisionGogglesClientHandler;
import com.wynprice.secretrooms.server.items.TrueVisionGogglesHandler;
import com.wynprice.secretrooms.server.utils.ModelDataUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

import static com.wynprice.secretrooms.client.SecretModelData.MODEL_MAP_STATE;
import static com.wynprice.secretrooms.client.SecretModelData.SRM_BLOCKSTATE;

public class SecretBlockModel implements IBakedModel {

    private static final Supplier<BlockRendererDispatcher> DISPATCHER = () -> Minecraft.getInstance().getBlockRendererDispatcher();
    private final IBakedModel model;

    public SecretBlockModel(IBakedModel model) {
        this.model = model;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
        Optional<BlockState> data = ModelDataUtils.getData(extraData, SRM_BLOCKSTATE);
        if (!data.isPresent() || !this.canRenderInLater(data.get())) {
            return Collections.emptyList();
        }
        BlockState mirrorState = data.get();
        Supplier<List<BakedQuad>> quads = () -> this.render(mirrorState, state, DISPATCHER.get().getModelForState(mirrorState), side, rand, extraData);
        if (trueVision() && MinecraftForgeClient.getRenderLayer() == RenderType.getTranslucent()) {
            List<BakedQuad> quadList = quads.get();
            this.getHelmetQuads(this.gatherAllQuads(quads), quadList);
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

    protected boolean canRenderInLater(BlockState state) {
        RenderType renderLayer = MinecraftForgeClient.getRenderLayer();
        return (trueVision() && renderLayer == RenderType.getTranslucent()) ||
                (renderLayer == null || RenderTypeLookup.canRenderInLayer(state, renderLayer));
    }

    protected List<BakedQuad> gatherAllQuads(Supplier<List<BakedQuad>> superQuads) {
        RenderType layer = MinecraftForgeClient.getRenderLayer();

        ForgeHooksClient.setRenderLayer(null);
        List<BakedQuad> quads = new ArrayList<>(superQuads.get());
        ForgeHooksClient.setRenderLayer(layer);
        return quads;
    }

    protected List<BakedQuad> render(@Nonnull BlockState mirrorState, @Nonnull BlockState baseState, @Nonnull IBakedModel model, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return new ArrayList<>(model.getQuads(mirrorState, side, rand, extraData));
    }

    @Override
    public TextureAtlasSprite getParticleTexture(IModelData data) {
        return trueVision() ?
            this.model.getParticleTexture(data) :
            ModelDataUtils.getData(data, SRM_BLOCKSTATE).map(DISPATCHER.get()::getModelForState).orElse(this.model).getParticleTexture(data);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        if(tileData == EmptyModelData.INSTANCE) {
            TileEntity entity = world.getTileEntity(pos);
            if(entity != null) {
                tileData = entity.getModelData();
            }
        }

        Optional<BlockState> mirror = ModelDataUtils.getData(tileData, SRM_BLOCKSTATE);
        if(mirror.isPresent()) {
            DelegateWorld pooled = DelegateWorld.getPooled(world);
            tileData = DISPATCHER.get().getModelForState(mirror.get()).getModelData(pooled, pos, mirror.get(), tileData);
            pooled.release();
        }

        IModelData data = this.model.getModelData(world, pos, state, tileData);
        if(data != tileData && tileData != EmptyModelData.INSTANCE) {
            if(tileData.hasProperty(SRM_BLOCKSTATE) && !data.hasProperty(SRM_BLOCKSTATE)) {
                data.setData(SRM_BLOCKSTATE, tileData.getData(SRM_BLOCKSTATE));
            }
            if(tileData.hasProperty(MODEL_MAP_STATE) && !data.hasProperty(MODEL_MAP_STATE)) {
                data.setData(MODEL_MAP_STATE, tileData.getData(MODEL_MAP_STATE));
            }
        }

        return data;
    }

    @Override
    public boolean isGui3d() {
        return this.model.isGui3d();
    }

    @Override
    public boolean isSideLit() {
        return trueVision() && this.model.isSideLit();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        return this.model.getQuads(state, side, rand);
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return this.model.getItemCameraTransforms();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.model.isAmbientOcclusion();
    }

    @Override
    public boolean isAmbientOcclusion(BlockState state) {
        return this.model.isAmbientOcclusion(state);
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.model.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.model.getOverrides();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack mat) {
        return this.model.handlePerspective(cameraTransformType, mat);
    }
}
