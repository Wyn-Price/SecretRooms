package com.wynprice.secretrooms.server;

import com.wynprice.secretrooms.platform.SecretRoomsServices;
import com.wynprice.secretrooms.server.items.SecretItems;
import com.wynprice.secretrooms.server.registry.RegistryHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class SecretCreativeTab {
    public static final RegistryHolder<CreativeModeTab> REGISTRY = SecretRoomsServices.PLATFORM.createCreativeTabRegistryHolder();

    public static Supplier<CreativeModeTab> SECRETROOMS_TAB = REGISTRY.register("secretrooms", () ->
            SecretRoomsServices.PLATFORM.createTabBuilder()
                    .title(Component.translatable("itemGroup.secretroomsmod"))
                    .icon(() -> new ItemStack(SecretItems.CAMOUFLAGE_PASTE.get()))
                    .displayItems((itemDisplayParameters, output) -> {
                        for (Supplier<Item> supplier : SecretItems.REGISTRY.listAll()) {
                            output.accept(supplier.get());
                        }
                    })
                    .build()
    );
}
