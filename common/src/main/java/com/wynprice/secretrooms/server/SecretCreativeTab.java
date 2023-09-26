package com.wynprice.secretrooms.server;

import com.wynprice.secretrooms.platform.SecretRoomsServices;
import com.wynprice.secretrooms.server.items.SecretItems;
import com.wynprice.secretrooms.server.registry.RegistryHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class SecretCreativeTab {
    public static final RegistryHolder<CreativeModeTab> DR = SecretRoomsServices.PLATFORM.createCreativeTabRegistryHolder();

    public static Supplier<CreativeModeTab> SECRETROOMS_TAB = DR.register("secretrooms", () ->
            SecretRoomsServices.PLATFORM.createTabBuilder()
                    .title(Component.translatable("itemGroup.secretroomsmod"))
                    .icon(() -> new ItemStack(SecretItems.CAMOUFLAGE_PASTE.get()))
                    .displayItems((itemDisplayParameters, output) -> {
                        SecretItems.REGISTRY.register()
                    })
                    .build()
    );
}
