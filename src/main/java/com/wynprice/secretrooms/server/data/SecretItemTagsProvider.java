package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms6;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Random;

import static com.wynprice.secretrooms.server.data.SecretItemTags.*;

public class SecretItemTagsProvider extends ItemTagsProvider {

    public SecretItemTagsProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
        super(dataGenerator, new SecretBlockTagsProvider(dataGenerator, existingFileHelper), SecretRooms6.MODID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void registerTags() {
        getOrCreateBuilder(EARTH_ITEM).add(Items.CLAY_BALL, Items.DIRT, Items.SAND);
        getOrCreateBuilder(SECRET_RECIPE_ITEMS).add(Items.ROTTEN_FLESH).addTags(ItemTags.WOOL);
        getOrCreateBuilder(CLEAR_GLASS).add(Items.GLASS);
    }
}
