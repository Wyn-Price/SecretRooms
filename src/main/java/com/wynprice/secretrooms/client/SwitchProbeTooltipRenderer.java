package com.wynprice.secretrooms.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.data.SecretData;
import com.wynprice.secretrooms.server.items.SecretItems;
import com.wynprice.secretrooms.server.items.SwitchProbe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderTooltipEvent;

public class SwitchProbeTooltipRenderer {

    public static void onTooltip(RenderTooltipEvent.PostBackground event) {
        ItemStack stack = event.getStack();
        if(SecretItems.SWITCH_PROBE.map(i -> i == stack.getItem()).orElse(true)) {
            return;
        }
        CompoundNBT compound = stack.getChildTag(SwitchProbe.PROBE_HIT_DATA);
        if(compound != null && !compound.isEmpty()) {
            SecretData data = new SecretData(null);
            data.readNBT(compound);
            Item item = data.getBlockState().getBlock().asItem();

            GlStateManager.pushMatrix();
            GlStateManager.translated(event.getX(), event.getY(), 0);

            String formattedText = new TranslationTextComponent(SecretRooms6.MODID + ".probe.containedblock").getUnformattedComponentText();
            for (ITextProperties line : event.getLines()) {
                if(formattedText.equals(TextFormatting.getTextWithoutFormattingCodes(line.getString()))) {
                    GlStateManager.translated(event.getFontRenderer().getStringWidth(line.getString()) - 16D, -2, 0);
                    GlStateManager.scaled(0.75D, 0.75D, 0.75D);
                    if(item == Items.AIR) {
                        Minecraft.getInstance().textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
                        //AbstractGui.blit(0, 0, 1000, 16, 16, Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getTexture(data.getBlockState()));
                    } else {
                        GlStateManager.enableRescaleNormal();
                        RenderHelper.enableStandardItemLighting();
                        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(new ItemStack(item), 0, 0);
                        RenderHelper.disableStandardItemLighting();
                        GlStateManager.disableRescaleNormal();
                    }
                    break;
                }
                GlStateManager.translated(0, 10, 0);
            }
            GlStateManager.popMatrix();
        }
    }
}
