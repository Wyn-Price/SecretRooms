package com.wynprice.secretrooms.clients;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.geometry.IGeometryLoader;

import java.util.function.Supplier;

public class SimpleUnbakedGeometryLoader implements IGeometryLoader<SimpleUnbakedGeometery> {

    private final Supplier<BakedModel> supplier;

    private SimpleUnbakedGeometryLoader(Supplier<BakedModel> supplier) {
        this.supplier = supplier;
    }

    @Override
    public SimpleUnbakedGeometery read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        return new SimpleUnbakedGeometery(this.supplier);
    }

    public static SimpleUnbakedGeometryLoader create(Supplier<BakedModel> supplier) {
        return new SimpleUnbakedGeometryLoader(supplier);
    }
}
