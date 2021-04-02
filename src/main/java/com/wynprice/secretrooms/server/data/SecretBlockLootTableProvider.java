package com.wynprice.secretrooms.server.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.wynprice.secretrooms.server.items.SecretItems;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.wynprice.secretrooms.server.blocks.SecretBlocks.*;

public class SecretBlockLootTableProvider extends LootTableProvider {

    public SecretBlockLootTableProvider(DataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        map.forEach((res, table) -> LootTableManager.validateLootTable(validationtracker, res, table));
    }

    @Override
    protected List<
        Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return ImmutableList.of(
            Pair.of(SecretRoomsBlockLootTables::new, LootParameterSets.BLOCK)
        );
    }

    private static class SecretRoomsBlockLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {

        private final Map<ResourceLocation, LootTable.Builder> lootTables = Maps.newHashMap();

        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            this.lootTables.clear();
            this.addTables();
            this.lootTables.forEach(consumer);
        }
        
        private void addTables() {
            this.createBlockDrop(GHOST_BLOCK);
            this.createBlockDrop(SECRET_STAIRS);
            this.createBlockDrop(SECRET_LEVER);
            this.createBlockDrop(SECRET_REDSTONE);
            this.createBlockDrop(ONE_WAY_GLASS);
            this.createBlockDrop(SECRET_WOODEN_BUTTON);
            this.createBlockDrop(SECRET_STONE_BUTTON);
            this.createBlockDrop(TORCH_LEVER);
            this.createBlockDrop(WALL_TORCH_LEVER, SecretItems.TORCH_LEVER);
            this.createBlockDrop(SECRET_PRESSURE_PLATE);
            this.createBlockDrop(SECRET_PLAYER_PRESSURE_PLATE);
            this.createDoorItemTable(SECRET_DOOR);
            this.createDoorItemTable(SECRET_IRON_DOOR);
            this.createBlockDrop(SECRET_CHEST);
            this.createBlockDrop(SECRET_TRAPDOOR);
            this.createBlockDrop(SECRET_IRON_TRAPDOOR);
            this.createBlockDrop(SECRET_TRAPPED_CHEST);
            this.createBlockDrop(SECRET_GATE);
            this.createBlockDrop(SECRET_DAYLIGHT_DETECTOR);
            this.createBlockDrop(SECRET_OBSERVER);
            this.createBlockDrop(SECRET_CLAMBER);
        }

        private void createBlockDrop(Supplier<Block> block) {
            createBlockDrop(block, block);
        }

        private void createBlockDrop(Supplier<Block> block, Supplier<? extends IItemProvider> item) {
            this.lootTables.put(block.get().getLootTable(), createSingleItemTable(item.get()));
        }

        private LootTable.Builder createSingleItemTable(IItemProvider item) {
            return LootTable.builder()
                .addLootPool(
                    LootPool.builder()
                        .rolls(ConstantRange.of(1))
                        .addEntry(ItemLootEntry.builder(item))
                        .acceptCondition(SurvivesExplosion.builder())
                );
        }

        private void createDoorItemTable(Supplier<Block> block) {
            this.lootTables.put(block.get().getLootTable(), BlockLootTables.registerDoor(block.get()));
        }
    }

}
