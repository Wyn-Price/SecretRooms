package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class SwitchProbe extends Item {

    public static final String PROBE_HIT_DATA = "probe_hit_data";

    public SwitchProbe(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
//        stack.getOrCreateTag().remove(PROBE_HIT_DATA);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Optional<SecretData> data = SecretBaseBlock.getMirrorData(context.getLevel(), context.getClickedPos());
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if(data.isPresent()) {
            CompoundTag compound = stack.getOrCreateTag().getCompound(PROBE_HIT_DATA);
            if(!compound.isEmpty()) {
                SecretData d = new SecretData(null);
                d.readNBT(compound);
                data.get().setFrom(d);

                boolean solid = d.getBlockState().canOcclude();
                if(solid != state.canOcclude()) {
                    context.getLevel().setBlockAndUpdate(context.getClickedPos(), state.setValue(SecretBaseBlock.SOLID, solid));
                }

                if(state.hasProperty(WATERLOGGED) && d.getBlockState().hasProperty(WATERLOGGED)) {
                    d.setBlockState(d.getBlockState().setValue(WATERLOGGED, state.getValue(WATERLOGGED)));
                }
            }
        } else {
            SecretData d = new SecretData(null);
            d.setBlockState(state);
            BlockEntity tileEntity = context.getLevel().getBlockEntity(context.getClickedPos());
            d.setTileEntityNBT(tileEntity != null ? tileEntity.serializeNBT() : null);

            d.writeNBT(stack.getOrCreateTagElement(PROBE_HIT_DATA));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        CompoundTag compound = stack.getOrCreateTag().getCompound(PROBE_HIT_DATA);
        text.add(new TranslatableComponent(SecretRooms6.MODID + ".probe.containedblock").withStyle(ChatFormatting.GOLD));
        if(compound.isEmpty()) {
            text.add(new TranslatableComponent(SecretRooms6.MODID + ".probe.noneset"));
        } else if(Screen.hasShiftDown()){
            SecretData data = new SecretData(null);
            data.readNBT(compound);

            BlockState state = data.getBlockState();
            text.add(new TranslatableComponent(SecretRooms6.MODID + ".probe.data").withStyle(ChatFormatting.BLUE));
            text.add(new TranslatableComponent(SecretRooms6.MODID + ".probe.blockset", state.getBlock().getRegistryName()).withStyle(ChatFormatting.AQUA));

            for (Property<?> property : state.getProperties()) {
                text.add(new TranslatableComponent(SecretRooms6.MODID + ".probe.blockproperty", property.getName(), propertyString(property, state.getValue(property))).withStyle(ChatFormatting.DARK_AQUA));
            }
        }
    }

    private static <T extends Comparable<T>> String propertyString(Property<T> property, Comparable<?> value) {
        return property.getName((T)value);
    }
}
