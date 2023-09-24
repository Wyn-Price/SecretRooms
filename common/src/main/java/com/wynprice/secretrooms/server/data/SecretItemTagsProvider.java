package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms7;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;

import static com.wynprice.secretrooms.server.data.SecretItemTags.*;

public class SecretItemTagsProvider extends ItemTagsProvider {

    public SecretItemTagsProvider(DataGenerator dataGenerator, ExistingFileHelper existingFileHelper) {
        super(dataGenerator, new SecretBlockTagsProvider(dataGenerator, existingFileHelper), SecretRooms7.MODID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags() {
        tag(EARTH_ITEM).add(Items.CLAY_BALL, Items.DIRT, Items.SAND);
        tag(SECRET_RECIPE_ITEMS).add(Items.ROTTEN_FLESH).addTags(ItemTags.WOOL);
        tag(CLEAR_GLASS).add(Items.GLASS);
    }
}
