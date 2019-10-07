package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class SwitchProbe extends Item {

    public static final String PROBE_HIT_DATA = "probe_hit_data";

    public SwitchProbe(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
//        stack.getOrCreateTag().remove(PROBE_HIT_DATA);
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        Optional<SecretData> data = SecretBaseBlock.getMirrorData(context.getWorld(), context.getPos());
        if(data.isPresent()) {
            CompoundNBT compound = stack.getOrCreateTag().getCompound(PROBE_HIT_DATA);
            if(!compound.isEmpty()) {
                SecretData d = new SecretData(null);
                d.readNBT(compound);
                data.get().setFrom(d);
            }
        } else {
            SecretData d = new SecretData(null);
            d.setBlockState(context.getWorld().getBlockState(context.getPos()));
            TileEntity tileEntity = context.getWorld().getTileEntity(context.getPos());
            d.setTileEntityNBT(tileEntity != null ? tileEntity.serializeNBT() : null);

            d.writeNBT(stack.getOrCreateChildTag(PROBE_HIT_DATA));
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag) {
        CompoundNBT compound = stack.getOrCreateTag().getCompound(PROBE_HIT_DATA);
        text.add(new TranslationTextComponent(SecretRooms6.MODID + ".probe.containedblock").applyTextStyle(TextFormatting.GOLD));
        if(compound.isEmpty()) {
            text.add(new TranslationTextComponent(SecretRooms6.MODID + ".probe.noneset"));
        } else if(Screen.hasShiftDown()) {
            SecretData data = new SecretData(null);
            data.readNBT(compound);

            BlockState state = data.getBlockState();
            text.add(new TranslationTextComponent(SecretRooms6.MODID + ".probe.data").applyTextStyles(TextFormatting.BLUE));
            text.add(new TranslationTextComponent(SecretRooms6.MODID + ".probe.blockset", state.getBlock().getRegistryName()).applyTextStyles(TextFormatting.AQUA));

            for(IProperty<?> property : state.getProperties()) {
                text.add(new TranslationTextComponent(SecretRooms6.MODID + ".probe.blockproperty", property.getName(), propertyString(property, state.get(property))).applyTextStyles(TextFormatting.DARK_AQUA));
            }
        }
    }

    private static <T extends Comparable<T>> String propertyString(IProperty<T> property, Comparable<?> value) {
        return property.getName((T) value);
    }
}
