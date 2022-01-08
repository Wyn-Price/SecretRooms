package com.wynprice.secretrooms.client;

import com.wynprice.secretrooms.server.data.SecretData;
import com.wynprice.secretrooms.server.items.SecretItems;
import com.wynprice.secretrooms.server.items.SwitchProbe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SwitchProbeTooltipComponent implements TooltipComponent {

    private final FormattedCharSequence text;

    private TextureAtlasSprite spriteIfRender;
    private int spriteColourIfRender;

    private ItemStack itemStackIfRender;

    public SwitchProbeTooltipComponent(FormattedCharSequence text, ItemStack stack) {
        this.text = text;

        CompoundTag compound = stack.getTagElement(SwitchProbe.PROBE_HIT_DATA);
        if(SecretItems.SWITCH_PROBE.get() != stack.getItem() || compound == null || compound.isEmpty()) {
            return;
        }

        SecretData data = new SecretData(null);
        data.readNBT(compound);
        Item item = data.getBlockState().getBlock().asItem();
        if(item == Items.AIR) {
            this.spriteIfRender = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getParticleIcon(data.getBlockState());
            this.spriteColourIfRender = Minecraft.getInstance().getBlockColors().getColor(
                data.getBlockState(),
                Minecraft.getInstance().level,
                Minecraft.getInstance().player.blockPosition(),
                0
            );

//            matrixStack.scale(0.625F, 0.625F, 1F);
//            RenderSystem.setShaderColor(
//                ((color >> 16) & 0xFF) / 255F,
//                ((color >> 8) & 0xFF) / 255F,
//                (color & 0xFF) / 255F,
//                1F);
//            GuiComponent.blit(matrixStack, x + startfromsuper, y, 0, 16, 16, texture);
        } else {
            this.itemStackIfRender = new ItemStack(item);
        }
    }

    public FormattedCharSequence getText() {
        return this.text;
    }

    public TextureAtlasSprite getSpriteIfRender() {
        return this.spriteIfRender;
    }

    public int getSpriteColourIfRender() {
        return this.spriteColourIfRender;
    }

    public ItemStack getItemStackIfRender() {
        return this.itemStackIfRender;
    }


}
