package com.wynprice.secretrooms.server.registry;

import com.wynprice.secretrooms.registry.RegistryHolder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class ForgeRegistryHolder<T> implements RegistryHolder<T> {
    private final DeferredRegister<T> wrapped;

    public ForgeRegistryHolder(IForgeRegistry<T> registry, String modid) {
        this.wrapped = DeferredRegister.create(registry, modid);
    }

    @Override
    public <I extends T> Supplier<I> register(String name, Supplier<I> supplier) {
        return this.wrapped.register(name, supplier);
    }

    public void register(IEventBus bus) {
        this.wrapped.register(bus);
    }
}
