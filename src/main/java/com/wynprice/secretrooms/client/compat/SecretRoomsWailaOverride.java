package com.wynprice.secretrooms.client.compat;

import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.items.TrueVisionGogglesClientHandler;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;

public class SecretRoomsWailaOverride implements IBlockComponentProvider {
    @Override
    public BlockState getOverride(IBlockAccessor accessor, IPluginConfig config) {
        if(TrueVisionGogglesClientHandler.isWearingGoggles(accessor.getPlayer()))
            return accessor.getBlockState();
        return SecretBaseBlock.getMirrorState(accessor.getWorld(), accessor.getPosition()).orElse(Blocks.STONE.getDefaultState());
    }

    @Override
    public ItemStack getDisplayItem(IBlockAccessor accessor, IPluginConfig config) {
        BlockState theState = getOverride(accessor, config);
        try {
            return theState.getPickBlock(accessor.getHitResult(), accessor.getWorld(), accessor.getPosition(), accessor.getPlayer());
        } catch (Throwable e) {
            return new ItemStack(theState.getBlock());
        }
    }
}