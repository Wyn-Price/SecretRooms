package com.wynprice.secretrooms.platform.services;

import com.wynprice.secretrooms.server.registry.RegistryHolder;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface ISecretRoomsPlatformHelper {

    RegistryHolder<Item> createItemRegistryHolder();

    RegistryHolder<Block> createBlockRegistryHolder();

    RegistryHolder<BlockEntityType<?>> createBlockEntityRegistryHolder();

    RegistryHolder<CreativeModeTab> createCreativeTabRegistryHolder();

    CreativeModeTab.Builder createTabBuilder();

    @SuppressWarnings("unchecked")
    <T extends BlockEntity> BlockEntityType<T> createBlockEntityType(BiFunction<BlockPos, BlockState, T> creator, Supplier<Block>... blocks);
    TagKey<Item> getDyesItemTag();

    void updateModelData(SecretTileEntity tileEntity);
}