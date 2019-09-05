package com.wynprice.secretrooms.server.utils;

import java.util.function.Supplier;

public class CachedObject<T> implements Supplier<T> {
    private final Supplier<T> supplier;

    private T cache;
    private boolean set;

    public CachedObject(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if(!this.set) {
            this.cache = this.supplier.get();
            this.set = true;
        }
        return this.cache;
    }
}
