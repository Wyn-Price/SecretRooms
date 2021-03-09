package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public class SecretBlockTags {

    public static final ITag.INamedTag<Block> ONE_WAY_GLASS_CULL = tag("one_way_glass_cull");

    public static ITag.INamedTag<Block> tag(String id) {
        return BlockTags.createOptional(new ResourceLocation(SecretRooms6.MODID, id));
    }
}
