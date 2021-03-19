package com.wynprice.secretrooms.client.model.quads;

import com.google.common.math.DoubleMath;
import com.wynprice.secretrooms.SecretRooms6;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.Float.floatToRawIntBits;
import static java.lang.Float.intBitsToFloat;

public class TrueVisionBakedQuad {
    private static final ResourceLocation OVERLAY_LOCATION = new ResourceLocation(SecretRooms6.MODID, "block/overlay");
    private static TextureAtlasSprite overlaySprite;

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if(PlayerContainer.LOCATION_BLOCKS_TEXTURE.equals(event.getMap().getTextureLocation())) {
            event.addSprite(OVERLAY_LOCATION);
        }
    }

    public static void onTextureStitched(TextureStitchEvent.Post event) {
        if(PlayerContainer.LOCATION_BLOCKS_TEXTURE.equals(event.getMap().getTextureLocation())) {
            overlaySprite = event.getMap().getSprite(OVERLAY_LOCATION);
        }
    }

    public static BakedQuad generateQuad(BakedQuad quad) {
        int[] data = Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length);
        for (int i = 0; i < 4; i++) {
            int j = DefaultVertexFormats.BLOCK.getIntegerSize() * i;

            float x = intBitsToFloat(data[j]) + 0.001F*quad.getFace().getXOffset();
            float y = intBitsToFloat(data[j+1]) + 0.001F*quad.getFace().getYOffset();
            float z = intBitsToFloat(data[j+2]) + 0.001F*quad.getFace().getZOffset();

            data[j] = floatToRawIntBits(x);
            data[j+1] = floatToRawIntBits(y);
            data[j+2] = floatToRawIntBits(z);

            float ui;
            float vi;

            switch (quad.getFace().getAxis()) {
                case X:
                    ui = z;
                    vi = 1-y;
                    break;
                case Y:
                default:
                    ui = x;
                    vi = z;
                    break;
                case Z:
                    ui = x;
                    vi = 1-y;
                    break;
            }

            data[j+4] = floatToRawIntBits(overlaySprite.getInterpolatedU(ui*16F));
            data[j+5] = floatToRawIntBits(overlaySprite.getInterpolatedV(vi*16F));

            data[j+6] = (240 << 16) | 240;
        }

        return new BakedQuad(data, -1, quad.getFace(), overlaySprite, quad.applyDiffuseLighting());

    }
}
