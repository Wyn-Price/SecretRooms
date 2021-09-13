package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms6;
import net.minecraft.world.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.Tags;

public class SecretItemTags {

    public static final Tag.Named<Item> EARTH_ITEM = tag("earth_item");
    public static final Tag.Named<Item> SECRET_RECIPE_ITEMS = tag("secret_recipe_items");
    public static final Tag.Named<Item> CLEAR_GLASS = tag("clear_glass");

    private static Tag.Named<Item> tag(String id) {
        return ItemTags.createOptional(new ResourceLocation(SecretRooms6.MODID, id));
    }
}
