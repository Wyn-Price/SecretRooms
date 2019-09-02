package com.wynprice.secretrooms.client.model;

import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class RemovedModelData implements IModelData {

    private final IModelData data;

    private List<ModelProperty> removedProperties = new ArrayList<>();

    public RemovedModelData(IModelData data) {
        this.data = data;
    }

    public RemovedModelData removeProperty(ModelProperty property) {
        this.removedProperties.add(property);
        return this;
    }

    @Override
    public boolean hasProperty(ModelProperty<?> prop) {
        return !this.removedProperties.contains(prop) && this.data.hasProperty(prop);
    }

    @Nullable
    @Override
    public <T> T getData(ModelProperty<T> prop) {
        return this.removedProperties.contains(prop) ? null : this.data.getData(prop);
    }

    @Nullable
    @Override
    public <T> T setData(ModelProperty<T> prop, T data) {
        return this.removedProperties.contains(prop) ? null : this.data.setData(prop, data);
    }
}
