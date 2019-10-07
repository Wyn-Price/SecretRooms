package com.wynprice.secretrooms.client;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.client.model.OneWayGlassModel;
import com.wynprice.secretrooms.client.model.SecretBlockModel;
import com.wynprice.secretrooms.client.model.SecretMappedModel;
import com.wynprice.secretrooms.client.world.DelegateWorld;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.function.Function;

import static com.wynprice.secretrooms.server.blocks.SecretBlocks.*;

@Mod.EventBusSubscriber(modid = SecretRooms6.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class SecretModelHandler {

    @SubscribeEvent
    public static void onBlockColors(ColorHandlerEvent.Block event) {
        BlockColors colors = event.getBlockColors();
        colors.register((state, world, pos, index) -> SecretBaseBlock.getMirrorState(world, pos).map(mirror -> colors.getColor(mirror, new DelegateWorld(world), pos, index)).orElse(-1),
                GHOST_BLOCK.get(), SECRET_STAIRS.get(), SECRET_LEVER.get(), SECRET_REDSTONE.get(), ONE_WAY_GLASS.get(), SECRET_WOODEN_BUTTON.get(),
                SECRET_STONE_BUTTON.get(), SECRET_PRESSURE_PLATE.get(), SECRET_PLAYER_PRESSURE_PLATE.get(), SECRET_DOOR.get(), SECRET_IRON_DOOR.get(),
                SECRET_CHEST.get(), SECRET_TRAPDOOR.get(), SECRET_IRON_TRAPDOOR.get(), SECRET_TRAPPED_CHEST.get(), SECRET_GATE.get(), SECRET_DUMMY_BLOCK.get(),
                SECRET_DAYLIGHT_DETECTOR.get(), SECRET_OBSERVER.get(), SECRET_CLAMBER.get()
        );
    }

    @SubscribeEvent
    public static void onModelBaked(ModelBakeEvent event) {
        Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();

        put(registry, SecretBlockModel::new,
                GHOST_BLOCK.get(), SECRET_STAIRS.get(), SECRET_LEVER.get(), SECRET_REDSTONE.get(), SECRET_WOODEN_BUTTON.get(), SECRET_STONE_BUTTON.get(),
                SECRET_PRESSURE_PLATE.get(), SECRET_PLAYER_PRESSURE_PLATE.get(), SECRET_CHEST.get(), SECRET_TRAPPED_CHEST.get(), SECRET_GATE.get(),
                SECRET_DUMMY_BLOCK.get(), SECRET_DAYLIGHT_DETECTOR.get(), SECRET_OBSERVER.get(), SECRET_CLAMBER.get()
        );

        put(registry, SecretMappedModel::new, SECRET_DOOR.get(), SECRET_IRON_DOOR.get(), SECRET_TRAPDOOR.get(), SECRET_IRON_TRAPDOOR.get());
        put(registry, OneWayGlassModel::new, ONE_WAY_GLASS.get());
    }

    private static void put(Map<ResourceLocation, IBakedModel> registry, Function<IBakedModel, IBakedModel> creator, Block... blocks) {
        for(Block block : blocks) {
            for(BlockState state : block.getStateContainer().getValidStates()) {
                registry.put(BlockModelShapes.getModelLocation(state), creator.apply(registry.get(BlockModelShapes.getModelLocation(state))));
            }
        }
    }
}
