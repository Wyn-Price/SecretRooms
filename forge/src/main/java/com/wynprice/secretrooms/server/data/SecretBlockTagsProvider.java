package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms7;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SecretBlockTagsProvider extends BlockTagsProvider {

    public SecretBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SecretRooms7.MODID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(HolderLookup.Provider provider) {
        tag(SecretBlockTags.ONE_WAY_GLASS_CULL)
            .add(SecretBlocks.ONE_WAY_GLASS.get())
            .addTags(Tags.Blocks.GLASS);
    }
}
