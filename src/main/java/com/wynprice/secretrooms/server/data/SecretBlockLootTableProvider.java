package com.wynprice.secretrooms.server.data;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wynprice.secretrooms.SecretRooms6;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Supplier;

import static com.wynprice.secretrooms.server.blocks.SecretBlocks.*;

public class SecretBlockLootTableProvider implements IDataProvider {
    private final DataGenerator dataGenerator;
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public SecretBlockLootTableProvider(DataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }

    @Override
    public void act(DirectoryCache directoryCache) {
        Path folder = this.dataGenerator.getOutputFolder();
        Map<ResourceLocation, LootTable> loot = Maps.newHashMap();


        createBlockDrop(GHOST_BLOCK, loot);
        createBlockDrop(SECRET_STAIRS, loot);
        createBlockDrop(SECRET_LEVER, loot);
        createBlockDrop(SECRET_REDSTONE, loot);
        createBlockDrop(ONE_WAY_GLASS, loot);
        createBlockDrop(SECRET_WOODEN_BUTTON, loot);
        createBlockDrop(SECRET_STONE_BUTTON, loot);
        createBlockDrop(TORCH_LEVER, loot);
        createBlockDrop(WALL_TORCH_LEVER, () -> Items.AIR, loot);
        createBlockDrop(SECRET_PRESSURE_PLATE, loot);
        createBlockDrop(SECRET_PLAYER_PRESSURE_PLATE, loot);
        createBlockDrop(SECRET_DOOR, loot);
        createBlockDrop(SECRET_IRON_DOOR, loot);
        createBlockDrop(SECRET_CHEST, loot);
        createBlockDrop(SECRET_TRAPDOOR, loot);
        createBlockDrop(SECRET_IRON_TRAPDOOR, loot);
        createBlockDrop(SECRET_TRAPPED_CHEST, loot);
        createBlockDrop(SECRET_GATE, loot);
        createBlockDrop(SECRET_DUMMY_BLOCK, loot);
        createBlockDrop(SECRET_DAYLIGHT_DETECTOR, loot);
        createBlockDrop(SECRET_OBSERVER, loot);
        createBlockDrop(SECRET_CLAMBER, loot);


        loot.forEach((location, table) -> {
            Path path = folder.resolve("data/" + location.getNamespace() + "/loot_tables/" + location.getPath() + ".json");
            try {
                IDataProvider.save(GSON, directoryCache, LootTableManager.toJson(table), path);
            } catch (IOException var6) {
                SecretRooms6.LOGGER.error("Couldn't save loot table {}", path, var6);
            }

        });

    }

    @Override
    public String getName() {
        return "SecretRoomsBlockLootTable";
    }

    private static void createBlockDrop(Supplier<Block> block, Map<ResourceLocation, LootTable> loot) {
        createBlockDrop(block, block, loot);
    }

    private static void createBlockDrop(Supplier<Block> block, Supplier<? extends IItemProvider> item, Map<ResourceLocation, LootTable> loot) {
        loot.put(block.get().getLootTable(), createSingleItemTable(item.get()));
    }

    private static LootTable createSingleItemTable(IItemProvider item) {
        return LootTable.builder().addLootPool(LootPool.builder().rolls(ConstantRange.of(1)).addEntry(ItemLootEntry.builder(item)).acceptCondition(SurvivesExplosion.builder())).build();
    }
}
