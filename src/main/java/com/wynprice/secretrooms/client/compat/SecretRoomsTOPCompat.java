package com.wynprice.secretrooms.client.compat;

import mcjty.theoneprobe.api.ITheOneProbe;

import java.util.function.Function;

public class SecretRoomsTOPCompat implements Function<ITheOneProbe, Void> {
    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        theOneProbe.registerBlockDisplayOverride(new SecretRoomsTOPProvider());
        return null;
    }
}
