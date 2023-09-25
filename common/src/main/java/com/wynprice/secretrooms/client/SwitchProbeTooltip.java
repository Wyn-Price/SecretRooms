package com.wynprice.secretrooms.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;

public class SwitchProbeTooltip extends ClientTextTooltip {

    private final SwitchProbeTooltipComponent component;

    public SwitchProbeTooltip(SwitchProbeTooltipComponent component) {
        super(component.getText());
        this.component = component;
    }


    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        int xStart = x + super.getWidth(font) + 10;
        if(this.component.getItemStackIfRender() != null) {
            graphics.renderItem(this.component.getItemStackIfRender(), xStart, y, 0);
            graphics.renderItemDecorations(font, this.component.getItemStackIfRender(), xStart, y);
        } else if(this.component.getSpriteIfRender() != null) {
            int colour = this.component.getSpriteColourIfRender();
            RenderSystem.setShaderColor(
                ((colour >> 16) & 0xFF) / 255F,
                ((colour >> 8) & 0xFF) / 255F,
                (colour & 0xFF) / 255F,
                1F);
            graphics.blit(xStart, y, 0, 16, 16, this.component.getSpriteIfRender());

        }
    }

    @Override
    public int getWidth(Font font) {
        int superWidth = super.getWidth(font);
        if(this.component.getItemStackIfRender() == null && this.component.getSpriteIfRender() == null) {
            return superWidth;
        }
        return superWidth + 20;
    }
}
