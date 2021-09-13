package com.wynprice.secretrooms.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.data.SecretData;
import com.wynprice.secretrooms.server.items.SecretItems;
import com.wynprice.secretrooms.server.items.SwitchProbe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import com.mojang.blaze3d.platform.Lighting;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.event.RenderTooltipEvent;

public class SwitchProbeTooltipRenderer {

    public static void onTooltip(RenderTooltipEvent.PostBackground event) {
        ItemStack stack = event.getStack();
        if(SecretItems.SWITCH_PROBE.get() != stack.getItem()) {
            return;
        }
        CompoundTag compound = stack.getTagElement(SwitchProbe.PROBE_HIT_DATA);
        if(compound != null && !compound.isEmpty()) {
            SecretData data = new SecretData(null);
            data.readNBT(compound);
            Item item = data.getBlockState().getBlock().asItem();

            PoseStack matrixStack = event.getMatrixStack();
            matrixStack.pushPose();
            matrixStack.translate(event.getX(), event.getY(), 401);

            TextureManager tm = Minecraft.getInstance().getTextureManager();
            tm.bindForSetup(InventoryMenu.BLOCK_ATLAS);
            tm.getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(false, false);

            for (FormattedText line : event.getLines()) {
                if(line instanceof TranslatableComponent && (SecretRooms6.MODID + ".probe.containedblock").equals(((TranslatableComponent) line).getKey())) {
                    matrixStack.translate(event.getFontRenderer().width(line.getString()) - 16, 1, 0);
                    matrixStack.scale(0.625F, 0.625F, 1F);
                    if(item == Items.AIR) {
                        TextureAtlasSprite texture = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(data.getBlockState());
                        int color = Minecraft.getInstance().getBlockColors().getColor(
                            data.getBlockState(),
                            Minecraft.getInstance().level,
                            Minecraft.getInstance().player.blockPosition(),
                            0
                        );
                        RenderSystem.setShaderColor(
                            ((color >> 16) & 0xFF) / 255F,
                            ((color >> 8) & 0xFF) / 255F,
                            (color & 0xFF) / 255F,
                            1F);
                        GuiComponent.blit(matrixStack, 0, 0, 0, 16, 16, texture);
                    } else {
                        Lighting.setupForEntityInInventory();
                        RenderSystem.enableBlend();
                        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                        ItemRenderer ir = Minecraft.getInstance().getItemRenderer();
                        ItemStack render = new ItemStack(item);
                        BakedModel model = ir.getModel(render, null, null, 0);

                        boolean diffuse = !model.usesBlockLight();
                        if (diffuse) {
                            Lighting.setupForFlatItems();
                        }

                        matrixStack.translate(8, 8, 8);
                        matrixStack.scale(16, -16, 16);

                        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                        ir.render(render, ItemTransforms.TransformType.GUI, false, matrixStack, buffer, 0xF000F0, OverlayTexture.NO_OVERLAY, model);
                        buffer.endBatch();

                        RenderSystem.enableDepthTest();
                        if (diffuse) {
                            Lighting.setupFor3DItems();
                        }
                    }
                    break;
                }
                matrixStack.translate(0, 10, 0);
            }
            matrixStack.popPose();
        }
    }
}
