package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms7;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class SecretItemTags {

    public static final TagKey<Item> EARTH_ITEM = tag("earth_item");
    public static final TagKey<Item> SECRET_RECIPE_ITEMS = tag("secret_recipe_items");
    public static final TagKey<Item> CLEAR_GLASS = tag("clear_glass");

    private static TagKey<Item> tag(String id) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(SecretRooms7.MODID, id));
    }
}
