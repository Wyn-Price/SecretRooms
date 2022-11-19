package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms6;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class SecretBlockTags {

    public static final TagKey<Block> ONE_WAY_GLASS_CULL = tag("one_way_glass_cull");

    public static TagKey<Block> tag(String id) {
        return BlockTags.create(new ResourceLocation(SecretRooms6.MODID, id));
    }
}
