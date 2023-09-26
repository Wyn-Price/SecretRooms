package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.server.items.SecretItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.wynprice.secretrooms.server.blocks.SecretBlocks.*;

public class SecretBlockLootTableProvider extends LootTableProvider {

    public SecretBlockLootTableProvider(PackOutput output) {
        super(output,  Set.of(), VanillaLootTableProvider.create(output).getTables());
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationtracker) {
        map.forEach((res, table) -> table.validate(validationtracker));
    }

    @Override
    public List<SubProviderEntry> getTables() {
        return List.of(
                new SubProviderEntry(SecretRoomsBlockLootTables::new, LootContextParamSets.BLOCK)
        );
    }

    private static class SecretRoomsBlockLootTables extends BlockLootSubProvider {

        protected SecretRoomsBlockLootTables() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        protected void generate() {
            this.dropSelf(GHOST_BLOCK.get());
            this.dropSelf(SECRET_STAIRS.get());
            this.dropSelf(SECRET_LEVER.get());
            this.dropSelf(SECRET_REDSTONE.get());
            this.dropSelf(ONE_WAY_GLASS.get());
            this.dropSelf(SECRET_WOODEN_BUTTON.get());
            this.dropSelf(SECRET_STONE_BUTTON.get());
            this.dropSelf(TORCH_LEVER.get());
            this.dropOther(WALL_TORCH_LEVER.get(), SecretItems.TORCH_LEVER.get());
            this.dropSelf(SECRET_PRESSURE_PLATE.get());
            this.dropSelf(SECRET_PLAYER_PRESSURE_PLATE.get());
            this.createDoorTable(SECRET_DOOR.get());
            this.createDoorTable(SECRET_IRON_DOOR.get());
            this.dropSelf(SECRET_CHEST.get());
            this.dropSelf(SECRET_TRAPDOOR.get());
            this.dropSelf(SECRET_IRON_TRAPDOOR.get());
            this.dropSelf(SECRET_TRAPPED_CHEST.get());
            this.dropSelf(SECRET_GATE.get());
            this.dropSelf(SECRET_DAYLIGHT_DETECTOR.get());
            this.dropSelf(SECRET_OBSERVER.get());
            this.dropSelf(SECRET_CLAMBER.get());
        }


    }

}
