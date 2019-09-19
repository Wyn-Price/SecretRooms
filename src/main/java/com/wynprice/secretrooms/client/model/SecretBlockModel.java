package com.wynprice.secretrooms.client.model;

import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.server.utils.ModelDataUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class SecretBlockModel implements IBakedModel {

    private static final Supplier<BlockRendererDispatcher> DISPATCHER = () -> Minecraft.getInstance().getBlockRendererDispatcher();
    private final IBakedModel model;

    public SecretBlockModel(IBakedModel model) {
        this.model = model;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
        return ModelDataUtils.getData(extraData, SecretModelData.SRM_BLOCKSTATE)
            .filter(this::canRenderInLater)
            .map(mirrorState -> this.render(mirrorState, state, DISPATCHER.get().getModelForState(mirrorState), side, rand, extraData))
            .orElse(new ArrayList<>());
    }

    protected boolean canRenderInLater(BlockState state) {
        return state.canRenderInLayer(MinecraftForgeClient.getRenderLayer());
    }

    protected List<BakedQuad> render(@Nonnull BlockState mirrorState, @Nonnull BlockState baseState, @Nonnull IBakedModel model, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return new ArrayList<>(model.getQuads(mirrorState, side, rand, extraData));
    }

    @Override
    public TextureAtlasSprite getParticleTexture(IModelData data) {
        return ModelDataUtils.getData(data, SecretModelData.SRM_BLOCKSTATE).map(DISPATCHER.get()::getModelForState).orElse(this.model).getParticleTexture(data);
    }

    @Override
    public IModelData getModelData(IEnviromentBlockReader world, BlockPos pos, BlockState state, IModelData tileData) {
        return this.model.getModelData(world, pos, state, tileData);
    }

    @Override
    public boolean isGui3d() {
        return this.model.isGui3d();
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
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return this.model.handlePerspective(cameraTransformType);
    }
}
