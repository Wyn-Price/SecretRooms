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

    public NoTintBakedQuadRetextured(BakedQuad quad, TextureAtlasSprite texture) {
        super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), -1, FaceBakery.getFacingFromVertexData(quad.getVertexData()), quad.getSprite(), quad.applyDiffuseLighting());
        this.texture = texture;
        this.remapQuad();
    }


    protected void remapQuad() {
        for (int i = 0; i < 4; ++i) {
            int off = DefaultVertexFormats.BLOCK.getIntegerSize() * i;
            this.vertexData[off + 4] = Float.floatToRawIntBits(this.texture.getInterpolatedU(getUnInterpolatedU(Float.intBitsToFloat(this.vertexData[off + 4]), this.sprite)));
            this.vertexData[off + 5] = Float.floatToRawIntBits(this.texture.getInterpolatedV(getUnInterpolatedV(Float.intBitsToFloat(this.vertexData[off + 5]), this.sprite)));
        }
    }

    public static float getUnInterpolatedU(float u, TextureAtlasSprite sprite) {
        float f = sprite.getMaxU() - sprite.getMinU();
        return (u - sprite.getMinU()) / f * 16.0F;
    }

    public static float getUnInterpolatedV(float v, TextureAtlasSprite sprite) {
        float f = sprite.getMaxV() - sprite.getMinV();
        return (v - sprite.getMinV()) / f * 16.0F;
    }

    @Override
    public TextureAtlasSprite getSprite() {
        return texture;
    }
}