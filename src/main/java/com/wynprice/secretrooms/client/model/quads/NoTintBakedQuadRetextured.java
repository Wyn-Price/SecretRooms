package com.wynprice.secretrooms.client.model.quads;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.Arrays;

/**
 * The same as {@link } but with the tintIndex being {@code -1} instead of {@link BakedQuad#tintIndex}
 */
public class NoTintBakedQuadRetextured extends BakedQuad {
    private final TextureAtlasSprite texture;

    public NoTintBakedQuadRetextured(BakedQuad quad, TextureAtlasSprite texture) {
        super(Arrays.copyOf(quad.getVertices(), quad.getVertices().length), -1, FaceBakery.calculateFacing(quad.getVertices()), quad.getSprite(), quad.isShade());
        this.texture = texture;
        this.remapQuad();
    }


    protected void remapQuad() {
        for (int i = 0; i < 4; ++i) {
            int off = DefaultVertexFormat.BLOCK.getIntegerSize() * i;
            this.vertices[off + 4] = Float.floatToRawIntBits(this.texture.getU(getUnInterpolatedU(Float.intBitsToFloat(this.vertices[off + 4]), this.sprite)));
            this.vertices[off + 5] = Float.floatToRawIntBits(this.texture.getV(getUnInterpolatedV(Float.intBitsToFloat(this.vertices[off + 5]), this.sprite)));
        }
    }

    public static float getUnInterpolatedU(float u, TextureAtlasSprite sprite) {
        float f = sprite.getU1() - sprite.getU0();
        return (u - sprite.getU0()) / f * 16.0F;
    }

    public static float getUnInterpolatedV(float v, TextureAtlasSprite sprite) {
        float f = sprite.getV1() - sprite.getV0();
        return (v - sprite.getV0()) / f * 16.0F;
    }

    @Override
    public TextureAtlasSprite getSprite() {
        return texture;
    }
}