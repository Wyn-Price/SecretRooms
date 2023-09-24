package com.wynprice.secretrooms.server.platform;

import com.wynprice.secretrooms.SecretRooms7;
import com.wynprice.secretrooms.platform.services.ISecretRoomsPlatformHelper;
import com.wynprice.secretrooms.registry.RegistryHolder;
import com.wynprice.secretrooms.server.registry.ForgeRegistryHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;

public class ForgePlatformHelper implements ISecretRoomsPlatformHelper {

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

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
}