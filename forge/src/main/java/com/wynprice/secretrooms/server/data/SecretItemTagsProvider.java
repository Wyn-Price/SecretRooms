package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms7;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static com.wynprice.secretrooms.server.data.SecretItemTags.*;

public class SecretItemTagsProvider extends ItemTagsProvider {

    public SecretItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, SecretRooms7.MODID, existingFileHelper);
    }



    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(HolderLookup.Provider p_256380_) {
        tag(EARTH_ITEM).add(Items.CLAY_BALL, Items.DIRT, Items.SAND);
        tag(SECRET_RECIPE_ITEMS).add(Items.ROTTEN_FLESH).addTags(ItemTags.WOOL);
        tag(CLEAR_GLASS).add(Items.GLASS);
    }
}
