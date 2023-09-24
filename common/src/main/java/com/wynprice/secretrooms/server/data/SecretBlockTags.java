package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms7;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;

public class SecretBlockTags {

    public static final TagKey<Block> ONE_WAY_GLASS_CULL = tag("one_way_glass_cull");

    public static TagKey<Block> tag(String id) {
        return BlockTags.create(new ResourceLocation(SecretRooms7.MODID, id));
    }
}
