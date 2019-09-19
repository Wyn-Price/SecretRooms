package com.wynprice.secretrooms.client.model;

import com.wynprice.secretrooms.SecretRooms6;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.function.Function;

import static com.wynprice.secretrooms.server.blocks.SecretBlocks.*;

@Mod.EventBusSubscriber(modid = SecretRooms6.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SecretModelReplacer {

    @SubscribeEvent
    public static void onModelBaked(ModelBakeEvent event) {
        Map<ResourceLocation, IBakedModel> registry = event.getModelRegistry();

        put(registry, SecretBlockModel::new, SECRET_STAIRS, SECRET_LEVER, SECRET_REDSTONE, SECRET_WOODEN_BUTTON, SECRET_STONE_BUTTON, SECRET_PRESSURE_PLATE, SECRET_PLAYER_PRESSURE_PLATE);

        put(registry, SecretMappedModel::new, SECRET_DOOR, SECRET_IRON_DOOR);
        put(registry, OneWayGlassModel::new, ONE_WAY_GLASS);
        put(registry, GhostBlockModel::new, GHOST_BLOCK);
    }

    private static void put(Map<ResourceLocation, IBakedModel> registry, Function<IBakedModel, IBakedModel> creator, Block... blocks) {
        for (Block block : blocks) {
            for (BlockState state : block.getStateContainer().getValidStates()) {
                registry.put(BlockModelShapes.getModelLocation(state), creator.apply(registry.get(BlockModelShapes.getModelLocation(state))));
            }
        }
    }
}
