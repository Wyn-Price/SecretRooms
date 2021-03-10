package com.wynprice.secretrooms.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.data.SecretData;
import com.wynprice.secretrooms.server.items.SecretItems;
import com.wynprice.secretrooms.server.items.SwitchProbe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderTooltipEvent;

public class SwitchProbeTooltipRenderer {

    public static void onTooltip(RenderTooltipEvent.PostBackground event) {
        ItemStack stack = event.getStack();
        if(SecretItems.SWITCH_PROBE.get() != stack.getItem()) {
            return;
        }
        CompoundNBT compound = stack.getChildTag(SwitchProbe.PROBE_HIT_DATA);
        if(compound != null && !compound.isEmpty()) {
            SecretData data = new SecretData(null);
            data.readNBT(compound);
            Item item = data.getBlockState().getBlock().asItem();

            MatrixStack matrixStack = event.getMatrixStack();
            matrixStack.push();
            matrixStack.translate(event.getX(), event.getY(), 401);

            TextureManager tm = Minecraft.getInstance().getTextureManager();
            tm.bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
            tm.getTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);

            for (ITextProperties line : event.getLines()) {
                if(line instanceof TranslationTextComponent && (SecretRooms6.MODID + ".probe.containedblock").equals(((TranslationTextComponent) line).getKey())) {
                    matrixStack.translate(event.getFontRenderer().getStringWidth(line.getString()) - 16, 1, 0);
                    matrixStack.scale(0.625F, 0.625F, 1F);
                    if(item == Items.AIR) {
                        TextureAtlasSprite texture = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(data.getBlockState());
                        int color = Minecraft.getInstance().getBlockColors().getColor(
                            data.getBlockState(),
                            Minecraft.getInstance().world,
                            Minecraft.getInstance().player.getPosition(),
                            0
                        );
                        RenderSystem.color4f(
                            ((color >> 16) & 0xFF) / 255F,
                            ((color >> 8) & 0xFF) / 255F,
                            (color & 0xFF) / 255F,
                            1F);
                        AbstractGui.blit(matrixStack, 0, 0, 0, 16, 16, texture);
                    } else {
                        RenderHelper.enableStandardItemLighting();
                        RenderSystem.enableRescaleNormal();
                        RenderSystem.enableAlphaTest();
                        RenderSystem.defaultAlphaFunc();
                        RenderSystem.enableBlend();
                        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

                        ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
                        ItemStack render = new ItemStack(item);
                        IBakedModel model = ir.getItemModelWithOverrides(render, null, null);

                        boolean diffuse = !model.isSideLit();
                        if (diffuse) {
                            RenderHelper.setupGuiFlatDiffuseLighting();
                        }

                        matrixStack.translate(8, 8, 8);
                        matrixStack.scale(16, -16, 16);

                        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
                        ir.renderItem(render, ItemCameraTransforms.TransformType.GUI, false, matrixStack, buffer, 0xF000F0, OverlayTexture.NO_OVERLAY, model);
                        buffer.finish();

                        RenderSystem.enableDepthTest();
                        if (diffuse) {
                            RenderHelper.setupGui3DDiffuseLighting();
                        }

                        RenderSystem.disableAlphaTest();
                        RenderSystem.disableRescaleNormal();
                        RenderHelper.disableStandardItemLighting();
                    }
                    break;
                }
                matrixStack.translate(0, 10, 0);
            }
            matrixStack.pop();
        }
    }
}
