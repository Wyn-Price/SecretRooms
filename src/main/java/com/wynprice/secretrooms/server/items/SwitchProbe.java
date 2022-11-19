package com.wynprice.secretrooms.server.items;

import com.mojang.datafixers.util.Either;
import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.client.SwitchProbeTooltip;
import com.wynprice.secretrooms.client.SwitchProbeTooltipComponent;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;

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

                boolean solid = d.getBlockState().canOcclude();
                if(solid != state.canOcclude()) {
                    context.getLevel().setBlockAndUpdate(context.getClickedPos(), state.setValue(SecretBaseBlock.SOLID, solid));
                }

                // Set the mirror state waterlogged to be the blockstate's waterlogged
                if(state.hasProperty(WATERLOGGED) && d.getBlockState().hasProperty(WATERLOGGED)) {
                    d.setBlockState(d.getBlockState().setValue(WATERLOGGED, state.getValue(WATERLOGGED)));
                }

                data.get().setFrom(d);
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

    public static void onRegisterTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(SwitchProbeTooltipComponent.class, SwitchProbeTooltip::new);
    }

    public static void appendHover(RenderTooltipEvent.GatherComponents event) {
        if(event.getItemStack().getItem() != SecretItems.SWITCH_PROBE.get()) {
            return;
        }
        ItemStack stack = event.getItemStack();
        var elements = event.getTooltipElements();

        CompoundTag compound = stack.getOrCreateTag().getCompound(PROBE_HIT_DATA);
        elements.add(Either.right(new SwitchProbeTooltipComponent(
            Component.translatable(SecretRooms6.MODID + ".probe.containedblock").withStyle(ChatFormatting.GOLD).getVisualOrderText(),
            stack
        )));
        if(compound.isEmpty()) {
            elements.add(Either.left(Component.translatable(SecretRooms6.MODID + ".probe.noneset")));
        } else if(Screen.hasShiftDown()){
            SecretData data = new SecretData(null);
            data.readNBT(compound);

            BlockState state = data.getBlockState();
            elements.add(Either.left(Component.translatable(SecretRooms6.MODID + ".probe.data").withStyle(ChatFormatting.BLUE)));
            elements.add(Either.left(Component.translatable(SecretRooms6.MODID + ".probe.blockset", state.getBlock().getName()).withStyle(ChatFormatting.AQUA)));

            for (Property<?> property : state.getProperties()) {
                elements.add(Either.left(Component.translatable(SecretRooms6.MODID + ".probe.blockproperty", property.getName(), propertyString(property, state.getValue(property))).withStyle(ChatFormatting.DARK_AQUA)));
            }
        }
    }

    private static <T extends Comparable<T>> String propertyString(Property<T> property, Comparable<?> value) {
        return property.getName((T)value);
    }
}
