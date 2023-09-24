package com.wynprice.secretrooms.client;

import com.wynprice.secretrooms.client.model.TrueVisionGogglesModel;
import com.wynprice.secretrooms.client.world.DelegateWorld;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.level.block.Block;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static com.wynprice.secretrooms.server.blocks.SecretBlocks.*;

public class SecretModelHandler {

    // EntityRenderersEvent.RegisterLayerDefinitions
    public static void onEntityModelRegistered(BiConsumer<ModelLayerLocation, Supplier<LayerDefinition>> consumer) {
        consumer.accept(TrueVisionGogglesModel.TRUE_VISION_GOGGLES_MODEL, () -> LayerDefinition.create(TrueVisionGogglesModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 32));
    }

    // ColorHandlerEvent.Block
    public static void onBlockColors(RegisterBlockColours colours) {
        colours.register((state, world, pos, index) -> SecretBaseBlock.getMirrorState(world, pos).map(
                    DelegateWorld.createFunction(world,
                            (reader, mirror) -> Minecraft.getInstance().getBlockColors().getColor(mirror, reader, pos, index)
                    )
                ).orElse(-1),
            GHOST_BLOCK.get(), SECRET_STAIRS.get(), SECRET_LEVER.get(), SECRET_REDSTONE.get(), ONE_WAY_GLASS.get(), SECRET_WOODEN_BUTTON.get(),
            SECRET_STONE_BUTTON.get(), SECRET_PRESSURE_PLATE.get(), SECRET_PLAYER_PRESSURE_PLATE.get(), SECRET_DOOR.get(), SECRET_IRON_DOOR.get(),
            SECRET_CHEST.get(), SECRET_TRAPDOOR.get(), SECRET_IRON_TRAPDOOR.get(), SECRET_TRAPPED_CHEST.get(), SECRET_GATE.get(), SECRET_DUMMY_BLOCK.get(),
            SECRET_DAYLIGHT_DETECTOR.get(), SECRET_OBSERVER.get(), SECRET_CLAMBER.get()
        );
    }

    // TODO (port): this has been moved to a custom model loader
//    public static void onModelBaked(ModelBakeEvent event) {
//        Map<ResourceLocation, BakedModel> registry = event.getModelRegistry();
//
//        put(registry, SecretBlockModel::new,
//            GHOST_BLOCK.get(), SECRET_STAIRS.get(), SECRET_LEVER.get(), SECRET_REDSTONE.get(), SECRET_WOODEN_BUTTON.get(), SECRET_STONE_BUTTON.get(),
//            SECRET_PRESSURE_PLATE.get(), SECRET_PLAYER_PRESSURE_PLATE.get(), SECRET_CHEST.get(), SECRET_TRAPPED_CHEST.get(), SECRET_GATE.get(),
//            SECRET_DUMMY_BLOCK.get(), SECRET_DAYLIGHT_DETECTOR.get(), SECRET_OBSERVER.get(), SECRET_CLAMBER.get()
//        );
//
//        put(registry, SecretMappedModel::new, SECRET_DOOR.get(), SECRET_IRON_DOOR.get(), SECRET_TRAPDOOR.get(), SECRET_IRON_TRAPDOOR.get());
//        put(registry, OneWayGlassModel::new, ONE_WAY_GLASS.get());
//    }

    @FunctionalInterface
    public interface RegisterBlockColours {
        void register(BlockColor colors, Block... blocks);
    }
}
