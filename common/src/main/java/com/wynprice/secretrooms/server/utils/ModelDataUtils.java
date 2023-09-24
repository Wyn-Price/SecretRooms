package com.wynprice.secretrooms.server.utils;

import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import java.util.Optional;

public class ModelDataUtils {
    public static <T> Optional<T> getData(IModelData data, ModelProperty<T> property) {
        if(!data.hasProperty(property)) {
            return Optional.empty();
        }
        return Optional.ofNullable(data.getData(property));
    }
}
