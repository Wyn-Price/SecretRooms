package com.wynprice.secretrooms.server.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

    @Override
    public Collection<Supplier<T>> listAll() {
        // TODO: can we just cast to a Collection<Supplier<T>>
        return this.wrapped.getEntries().stream().map(ro -> (Supplier<T>)ro).toList();
    }

    public void register(IEventBus bus) {
        this.wrapped.register(bus);
    }
}
