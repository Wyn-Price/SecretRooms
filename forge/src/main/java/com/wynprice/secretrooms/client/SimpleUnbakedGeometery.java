package com.wynprice.secretrooms.client;

import com.wynprice.secretrooms.client.model.SecretBlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleUnbakedGeometery implements IUnbakedGeometry<SimpleUnbakedGeometery> {

    private final Supplier<SecretBlockModel> supplier;

    public SimpleUnbakedGeometery(Supplier<SecretBlockModel> supplier) {
        this.supplier = supplier;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        return new SecretModelForge(this.supplier.get());
    }
}
