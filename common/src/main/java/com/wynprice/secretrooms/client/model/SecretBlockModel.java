package com.wynprice.secretrooms.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wynprice.secretrooms.client.model.quads.TrueVisionBakedQuad;
import com.wynprice.secretrooms.client.world.DelegateWorld;
import com.wynprice.secretrooms.server.items.TrueVisionGogglesClientHandler;
import com.wynprice.secretrooms.server.utils.ModelDataUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

public class SecretBlockModel implements BakedModel {

    private static final Supplier<BlockRenderDispatcher> DISPATCHER = () -> Minecraft.getInstance().getBlockRenderer();

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
        Optional<BlockState> data = ModelDataUtils.getData(extraData, SRM_BLOCKSTATE);
        if (!data.isPresent() || !this.canRenderInLater(data.get())) {
            return Collections.emptyList();
        }
        BlockState mirrorState = data.get();
        Supplier<List<BakedQuad>> quads = () -> this.render(mirrorState, state, DISPATCHER.get().getBlockModel(mirrorState), side, rand, extraData);
        if (trueVision() && MinecraftForgeClient.getRenderType() == RenderType.translucent()) {
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
        RenderType renderLayer = MinecraftForgeClient.getRenderType();
        return (trueVision() && renderLayer == RenderType.translucent()) ||
                (renderLayer == null || ItemBlockRenderTypes.canRenderInLayer(state, renderLayer));
    }

    protected List<BakedQuad> gatherAllQuads(Supplier<List<BakedQuad>> superQuads) {
        RenderType layer = MinecraftForgeClient.getRenderType();

        ForgeHooksClient.setRenderType(null);
        List<BakedQuad> quads = new ArrayList<>(superQuads.get());
        ForgeHooksClient.setRenderType(layer);
        return quads;
    }

    protected List<BakedQuad> render(@Nonnull BlockState mirrorState, @Nonnull BlockState baseState, @Nonnull BakedModel model, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return new ArrayList<>(model.getQuads(mirrorState, side, rand, extraData));
    }

    @Override
    public TextureAtlasSprite getParticleIcon(IModelData data) {
        return trueVision() ?
            this.model.getParticleIcon(data) :
            ModelDataUtils.getData(data, SRM_BLOCKSTATE).map(DISPATCHER.get()::getBlockModel).orElse(this.model).getParticleIcon(data);
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        if(tileData == EmptyModelData.INSTANCE) {
            BlockEntity entity = world.getBlockEntity(pos);
            if(entity != null) {
                tileData = entity.getModelData();
            }
        }

        Optional<BlockState> mirror = ModelDataUtils.getData(tileData, SRM_BLOCKSTATE);
        if(mirror.isPresent()) {
            DelegateWorld pooled = DelegateWorld.getPooled(world);
            tileData = DISPATCHER.get().getBlockModel(mirror.get()).getModelData(pooled, pos, mirror.get(), tileData);
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
    public boolean usesBlockLight() {
        return trueVision() && this.model.usesBlockLight();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
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

    @Override
    public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack mat) {
        return this.model.handlePerspective(cameraTransformType, mat);
    }
}
