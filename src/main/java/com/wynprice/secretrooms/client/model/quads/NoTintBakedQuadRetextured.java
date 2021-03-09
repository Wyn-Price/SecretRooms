package com.wynprice.secretrooms.client.model.quads;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;

import java.util.Arrays;

/**
 * The same as {@link } but with the tintIndex being {@code -1} instead of {@link BakedQuad#tintIndex}
 */
public class NoTintBakedQuadRetextured extends BakedQuad {
    private final TextureAtlasSprite texture;

    public NoTintBakedQuadRetextured(BakedQuad quad, TextureAtlasSprite textureIn) {
        super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), -1, FaceBakery.getFacingFromVertexData(quad.getVertexData()), quad.getSprite(), quad.applyDiffuseLighting());
        this.texture = textureIn;
        this.remapQuad();

    }

    private void remapQuad() {
        for(int i = 0; i < 4; ++i) {
            int j = DefaultVertexFormats.BLOCK.getIntegerSize() * i;
            int uvIndex = 4;
            this.vertexData[j + uvIndex] = Float.floatToRawIntBits(this.texture.getInterpolatedU(this.getUnInterpolatedU(Float.intBitsToFloat(this.vertexData[j + uvIndex]))));
            this.vertexData[j + uvIndex + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV(this.getUnInterpolatedV(Float.intBitsToFloat(this.vertexData[j + uvIndex + 1]))));
        }
    }

    private float getUnInterpolatedU(float u) {
        float f = this.sprite.getMaxU() - this.sprite.getMinU();
        return (u - this.sprite.getMinU()) / f * 16.0F;
    }

    private float getUnInterpolatedV(float v) {
        float f = this.sprite.getMaxV() - this.sprite.getMinV();
        return (v - this.sprite.getMinV()) / f * 16.0F;
    }

    @Override
    public TextureAtlasSprite getSprite() {
        return texture;
    }
}