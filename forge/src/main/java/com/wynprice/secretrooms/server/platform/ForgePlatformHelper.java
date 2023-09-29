package com.wynprice.secretrooms.server.platform;

import com.wynprice.secretrooms.SecretRooms7;
import com.wynprice.secretrooms.platform.services.ISecretRoomsPlatformHelper;
import com.wynprice.secretrooms.server.registry.RegistryHolder;
import com.wynprice.secretrooms.server.registry.ForgeRegistryHolder;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class ForgePlatformHelper implements ISecretRoomsPlatformHelper {
    @Override
    public RegistryHolder<Item> createItemRegistryHolder() {
        return new ForgeRegistryHolder<>(ForgeRegistries.ITEMS, SecretRooms7.MODID);
    }

    @Override
    public RegistryHolder<Block> createBlockRegistryHolder() {
        return new ForgeRegistryHolder<>(ForgeRegistries.BLOCKS, SecretRooms7.MODID);
    }

    @Override
    public RegistryHolder<BlockEntityType<?>> createBlockEntityRegistryHolder() {
        return new ForgeRegistryHolder<>(ForgeRegistries.BLOCK_ENTITY_TYPES, SecretRooms7.MODID);
    }

    @Override
    public RegistryHolder<CreativeModeTab> createCreativeTabRegistryHolder() {
        return new ForgeRegistryHolder<>(Registries.CREATIVE_MODE_TAB, SecretRooms7.MODID);
    }

    @Override
    public CreativeModeTab.Builder createTabBuilder() {
        return CreativeModeTab.builder();
    }

    @Override
    public <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> creator, Supplier<Block>... blockSupplier) {
        Block[] blocks = new Block[blockSupplier.length];
        for (int i = 0; i < blockSupplier.length; i++) {
            blocks[i] = blockSupplier[i].get();
        }
        return BlockEntityType.Builder.of(creator::apply, blocks).build(null);
    }

    @Override
    public TagKey<Item> getDyesItemTag() {
        return Tags.Items.DYES;
    }

    @Override
    public void updateModelData(SecretTileEntity tileEntity) {
        tileEntity.requestModelDataUpdate();
    }
}