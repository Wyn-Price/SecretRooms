package com.wynprice.secretrooms.client;

import com.wynprice.secretrooms.client.model.OneWayGlassModel;
import com.wynprice.secretrooms.client.model.SecretBlockModel;
import com.wynprice.secretrooms.client.model.SecretMappedModel;
import com.wynprice.secretrooms.client.model.TrueVisionGogglesModel;
import com.wynprice.secretrooms.client.world.DelegateWorld;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;

import java.util.Map;
import java.util.function.Function;

import static com.wynprice.secretrooms.server.blocks.SecretBlocks.*;

public class SecretModelHandler {

    public static void onEntityModelRegistered(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(TrueVisionGogglesModel.TRUE_VISION_GOGGLES_MODEL, () -> LayerDefinition.create(TrueVisionGogglesModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 32));
    }

    public static void onBlockColors(ColorHandlerEvent.Block event) {
        BlockColors colors = event.getBlockColors();
        colors.register((state, world, pos, index) -> SecretBaseBlock.getMirrorState(world, pos).map(DelegateWorld.createFunction(world, (reader, mirror) -> colors.getColor(mirror, reader, pos, index))).orElse(-1),
            GHOST_BLOCK.get(), SECRET_STAIRS.get(), SECRET_LEVER.get(), SECRET_REDSTONE.get(), ONE_WAY_GLASS.get(), SECRET_WOODEN_BUTTON.get(),
            SECRET_STONE_BUTTON.get(), SECRET_PRESSURE_PLATE.get(), SECRET_PLAYER_PRESSURE_PLATE.get(), SECRET_DOOR.get(), SECRET_IRON_DOOR.get(),
            SECRET_CHEST.get(), SECRET_TRAPDOOR.get(), SECRET_IRON_TRAPDOOR.get(), SECRET_TRAPPED_CHEST.get(), SECRET_GATE.get(), SECRET_DUMMY_BLOCK.get(),
            SECRET_DAYLIGHT_DETECTOR.get(), SECRET_OBSERVER.get(), SECRET_CLAMBER.get()
        );
    }

    public static void onModelBaked(ModelBakeEvent event) {
        Map<ResourceLocation, BakedModel> registry = event.getModelRegistry();

        put(registry, SecretBlockModel::new,
            GHOST_BLOCK.get(), SECRET_STAIRS.get(), SECRET_LEVER.get(), SECRET_REDSTONE.get(), SECRET_WOODEN_BUTTON.get(), SECRET_STONE_BUTTON.get(),
            SECRET_PRESSURE_PLATE.get(), SECRET_PLAYER_PRESSURE_PLATE.get(), SECRET_CHEST.get(), SECRET_TRAPPED_CHEST.get(), SECRET_GATE.get(),
            SECRET_DUMMY_BLOCK.get(), SECRET_DAYLIGHT_DETECTOR.get(), SECRET_OBSERVER.get(), SECRET_CLAMBER.get()
        );

        put(registry, SecretMappedModel::new, SECRET_DOOR.get(), SECRET_IRON_DOOR.get(), SECRET_TRAPDOOR.get(), SECRET_IRON_TRAPDOOR.get());
        put(registry, OneWayGlassModel::new, ONE_WAY_GLASS.get());
    }

    private static void put(Map<ResourceLocation, BakedModel> registry, Function<BakedModel, BakedModel> creator, Block... blocks) {
        for (Block block : blocks) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                registry.put(BlockModelShaper.stateToModelLocation(state), creator.apply(registry.get(BlockModelShaper.stateToModelLocation(state))));
            }
        }
    }
}
