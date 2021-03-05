package com.wynprice.secretrooms.server.data;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;

import java.util.Random;

import static com.wynprice.secretrooms.server.data.SecretItemTags.*;

public class SecretItemTagsProvider extends ItemTagsProvider {
    public SecretItemTagsProvider(DataGenerator generatorIn) {
        super(generatorIn, new BlockTagsProvider(generatorIn));
    }

    @Override
    protected void registerTags() {
        getOrCreateBuilder((ITag.INamedTag<Item>) EARTH_ITEM).add(Items.CLAY_BALL, Items.DIRT, Items.SAND);
        getOrCreateBuilder((ITag.INamedTag<Item>) SECRET_RECIPE_ITEMS).add(Items.ROTTEN_FLESH).add(ItemTags.WOOL.getRandomElement(new Random()));
        getOrCreateBuilder((ITag.INamedTag<Item>) CLEAR_GLASS).add(Items.GLASS);
    }
}
