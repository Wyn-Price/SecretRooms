package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;

public class SecretBlockTags {

    public static final Tag.Named<Block> ONE_WAY_GLASS_CULL = tag("one_way_glass_cull");

    public static Tag.Named<Block> tag(String id) {
        return BlockTags.createOptional(new ResourceLocation(SecretRooms6.MODID, id));
    }
}
