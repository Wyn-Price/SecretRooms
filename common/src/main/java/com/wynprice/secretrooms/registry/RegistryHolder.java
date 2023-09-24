package com.wynprice.secretrooms.registry;

import java.util.function.Supplier;

public interface RegistryHolder<T> {

    <I extends T> Supplier<I> register(String name, Supplier<I> supplier);
}
