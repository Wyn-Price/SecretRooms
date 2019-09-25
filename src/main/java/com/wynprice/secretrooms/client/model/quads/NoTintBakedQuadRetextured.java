package com.wynprice.secretrooms.client.model.quads;

import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.BakedQuadRetextured;
import net.minecraft.client.renderer.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import java.util.Arrays;

/**
 * The same as {@link BakedQuadRetextured} but with the tintIndex being {@code -1} instead of {@link BakedQuad#tintIndex}
 */
public class NoTintBakedQuadRetextured extends BakedQuad {
    private final TextureAtlasSprite texture;

    public NoTintBakedQuadRetextured(BakedQuad quad, TextureAtlasSprite textureIn) {
        super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), -1, FaceBakery.getFacingFromVertexData(quad.getVertexData()), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat());
        this.texture = textureIn;
        this.remapQuad();
    }

    private void remapQuad() {
        for(int i = 0; i < 4; ++i) {
            int j = format.getIntegerSize() * i;
            int uvIndex = format.getUvOffsetById(0) / 4;
            this.vertexData[j + uvIndex] = Float.floatToRawIntBits(this.texture.getInterpolatedU((double)this.sprite.getUnInterpolatedU(Float.intBitsToFloat(this.vertexData[j + uvIndex]))));
            this.vertexData[j + uvIndex + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV((double)this.sprite.getUnInterpolatedV(Float.intBitsToFloat(this.vertexData[j + uvIndex + 1]))));
        }
    }

    @Override
    public TextureAtlasSprite getSprite() {
        return texture;
    }
}