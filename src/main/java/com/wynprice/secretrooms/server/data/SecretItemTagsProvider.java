package com.wynprice.secretrooms.server.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;

import static com.wynprice.secretrooms.server.data.SecretItemTags.*;

public class SecretItemTagsProvider extends ItemTagsProvider {
    public SecretItemTagsProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerTags() {
        getBuilder(EARTH_ITEM).add(Items.CLAY_BALL, Items.DIRT, Items.SAND);
        getBuilder(SECRET_RECIPE_ITEMS).add(Items.ROTTEN_FLESH).add(ItemTags.WOOL);
        getBuilder(CLEAR_GLASS).add(Items.GLASS);
    }
}
