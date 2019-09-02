package com.wynprice.secretrooms.client.model;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.client.model.providers.SecretQuadProvider;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = SecretRooms6.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BakedModelDelegate implements IBakedModel {

    private static final Minecraft MC = Minecraft.getInstance();

    private final IBakedModel model;

    public BakedModelDelegate(IBakedModel model) {
        this.model = model;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if(extraData.hasProperty(SecretModelData.SRM_MIRRORSTATE)) {
            IModelData removedModelData = new RemovedModelData(extraData).removeProperty(SecretModelData.SRM_MIRRORSTATE).removeProperty(SecretModelData.SRM_RENDER);
            BlockState mirrorState = extraData.getData(SecretModelData.SRM_MIRRORSTATE);
            if(mirrorState != null) {
                if(extraData.hasProperty(SecretModelData.SRM_RENDER)) {
                    SecretQuadProvider data = extraData.getData(SecretModelData.SRM_RENDER);
                    if(data != null) {
                        return data.render(mirrorState, rand, removedModelData);
                    }
                }

                return MC.getBlockRendererDispatcher().getBlockModelShapes().getModel(mirrorState).getQuads(mirrorState, side, rand, removedModelData);
            }
        }
        return IBakedModel.super.getQuads(state, side, rand, extraData);
    }

    public boolean isGui3d() {
        return this.model.isGui3d();
    }

    public boolean isAmbientOcclusion(BlockState state) {
        return this.model.isAmbientOcclusion(state);
    }

    public IModelData getModelData(IEnviromentBlockReader world, BlockPos pos, BlockState state, IModelData tileData) {
        return this.model.getModelData(world, pos, state, tileData);
    }

    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        return this.model.getQuads(state, side, rand);
    }

    public boolean isAmbientOcclusion() {
        return this.model.isAmbientOcclusion();
    }

    public IBakedModel getBakedModel() {
        return this.model.getBakedModel();
    }

    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return this.model.handlePerspective(cameraTransformType);
    }

    public boolean isBuiltInRenderer() {
        return this.model.isBuiltInRenderer();
    }

    public ItemOverrideList getOverrides() {
        return this.model.getOverrides();
    }

    public TextureAtlasSprite getParticleTexture() {
        return this.model.getParticleTexture();
    }

    public TextureAtlasSprite getParticleTexture(IModelData data) {
        return this.model.getParticleTexture(data);
    }

    public ItemCameraTransforms getItemCameraTransforms() {
        return this.model.getItemCameraTransforms();
    }

    private interface DelegateOverrides {
        List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData);
    }

    @SubscribeEvent
    public static void onModelBaked(ModelBakeEvent event) {
        event.getModelRegistry().replaceAll((location, model) -> new BakedModelDelegate(model));
    }
}
