package com.wynprice.secretrooms.client.model.quads;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.wynprice.secretrooms.SecretRooms7;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import java.util.Arrays;

import static java.lang.Float.floatToRawIntBits;
import static java.lang.Float.intBitsToFloat;

public class TrueVisionBakedQuad {
    private static final ResourceLocation OVERLAY_LOCATION = new ResourceLocation(SecretRooms7.MODID, "block/overlay");

    public static BakedQuad generateQuad(BakedQuad quad) {
        // TODO: cache?
        TextureAtlasSprite overlaySprite = Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(OVERLAY_LOCATION);
        int[] data = Arrays.copyOf(quad.getVertices(), quad.getVertices().length);
        for (int i = 0; i < 4; i++) {
            int j = DefaultVertexFormat.BLOCK.getIntegerSize() * i;

            float x = intBitsToFloat(data[j]) + 0.001F*quad.getDirection().getStepX();
            float y = intBitsToFloat(data[j+1]) + 0.001F*quad.getDirection().getStepY();
            float z = intBitsToFloat(data[j+2]) + 0.001F*quad.getDirection().getStepZ();

            data[j] = floatToRawIntBits(x);
            data[j+1] = floatToRawIntBits(y);
            data[j+2] = floatToRawIntBits(z);

            float ui;
            float vi;

            switch (quad.getDirection().getAxis()) {
                case X -> {
                    ui = z;
                    vi = 1 - y;
                }
                default -> {
                    ui = x;
                    vi = z;
                }
                case Z -> {
                    ui = x;
                    vi = 1 - y;
                }
            }

            data[j+4] = floatToRawIntBits(overlaySprite.getU(ui*16F));
            data[j+5] = floatToRawIntBits(overlaySprite.getV(vi*16F));

            data[j+6] = (240 << 16) | 240;
        }

        return new BakedQuad(data, -1, quad.getDirection(), overlaySprite, quad.isShade());

    }
}