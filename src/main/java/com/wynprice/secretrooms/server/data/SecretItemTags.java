package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms6;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class SecretItemTags {

    public static final ITag.INamedTag<Item> EARTH_ITEM = tag("earth_item");
    public static final ITag.INamedTag<Item> SECRET_RECIPE_ITEMS = tag("secret_recipe_items");
    public static final ITag.INamedTag<Item> CLEAR_GLASS = tag("clear_glass");

    private static ITag.INamedTag<Item> tag(String id) {
        return ItemTags.createOptional(new ResourceLocation(SecretRooms6.MODID, id));
    }
}
