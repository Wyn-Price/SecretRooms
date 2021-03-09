package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SecretBlockTagsProvider extends BlockTagsProvider {
    public SecretBlockTagsProvider(DataGenerator generatorIn, ExistingFileHelper existingFileHelper) {
        super(generatorIn, SecretRooms6.MODID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void registerTags() {
        getOrCreateBuilder(SecretBlockTags.ONE_WAY_GLASS_CULL)
            .add(SecretBlocks.ONE_WAY_GLASS.get())
            .addTags(Tags.Blocks.GLASS);
    }
}
