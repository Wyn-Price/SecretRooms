package com.wynprice.secretrooms.server.registry;

import java.util.Collection;
import java.util.function.Supplier;

public interface RegistryHolder<T> {
    <I extends T> Supplier<I> register(String name, Supplier<I> supplier);

    Collection<Supplier<T>> listAll();
}
