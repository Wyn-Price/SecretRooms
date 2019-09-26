package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms6;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;

public class SecretItemTags {

    public static final Tag<Item> EARTH_ITEM = tag("earth_item");
    public static final Tag<Item> SECRET_RECIPE_ITEMS = tag("secret_recipe_items");
    public static final Tag<Item> CLEAR_GLASS = tag("clear_glass");

    private static Tag<Item> tag(String id) {
        return new ItemTags.Wrapper(new ResourceLocation(SecretRooms6.MODID, id));
    }
}
