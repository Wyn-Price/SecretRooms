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
        List<Vector3f> positions = new ArrayList<>();
        List<Vector2f> uvs = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int j = DefaultVertexFormats.BLOCK.getIntegerSize() * i;
            positions.add(new Vector3f(
                intBitsToFloat(data[j]) + 0.001F*quad.getFace().getXOffset(),
                intBitsToFloat(data[j+1]) + 0.001F*quad.getFace().getYOffset(),
                intBitsToFloat(data[j+2]) + 0.001F*quad.getFace().getZOffset()
            ));
            uvs.add(new Vector2f(
                intBitsToFloat(data[j+4]),
                intBitsToFloat(data[j+5])
            ));
        }

        positions.sort((o1, o2) -> {
            if (!DoubleMath.fuzzyEquals(o1.getX(), o2.getX(), 0.001D)) {
                return Double.compare(o1.getX(), o2.getX());
            }
            if (!DoubleMath.fuzzyEquals(o1.getY(), o2.getY(), 0.001D)) {
                return Double.compare(o2.getY(), o1.getY());
            }
            if (!DoubleMath.fuzzyEquals(o1.getZ(), o2.getZ(), 0.001D)) {
                return Double.compare(o1.getZ(), o2.getZ());
            }
            return 0;
        });
        uvs.sort((o1, o2) -> {
            if (!DoubleMath.fuzzyEquals(o1.x, o2.x, 0.001D)) {
                return Double.compare(o1.x, o2.x);
            }
            if (!DoubleMath.fuzzyEquals(o1.y, o2.y, 0.001D)) {
                return Double.compare(o1.y, o2.y);
            }
            return 0;
        });

        Vector3f tempPos = positions.get(3);
        positions.set(3, positions.get(2));
        positions.set(2, tempPos);

        Vector2f tempUv = uvs.get(3);
        uvs.set(3, uvs.get(2));
        uvs.set(2, tempUv);

        if(quad.getFace().getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
            Collections.reverse(positions);
            Collections.reverse(uvs);
        }

        for (int i = 0; i < 4; i++) {
            int j = DefaultVertexFormats.BLOCK.getIntegerSize() * i;
            Vector3f pos = positions.get(i);
            Vector2f uv = uvs.get(i);

            data[j] = floatToRawIntBits(pos.getX());
            data[j+1] = floatToRawIntBits(pos.getY());
            data[j+2] = floatToRawIntBits(pos.getZ());

            float ui;
            float vi;

            switch (quad.getFace().getAxis()) {
                case X:
                    ui = pos.getZ();
                    vi = 1-pos.getY();
                    break;
                case Y:
                default:
                    ui = pos.getX();
                    vi = pos.getZ();
                    break;
                case Z:
                    ui = pos.getX();
                    vi = 1-pos.getY();
                    break;
            }

            data[j+4] = floatToRawIntBits(overlaySprite.getInterpolatedU(ui*16F));
            data[j+5] = floatToRawIntBits(overlaySprite.getInterpolatedV(vi*16F));

            data[j+6] = (240 << 16) | 240;
        }

        return new BakedQuad(data, -1, quad.getFace(), overlaySprite, quad.applyDiffuseLighting());

    }
}
