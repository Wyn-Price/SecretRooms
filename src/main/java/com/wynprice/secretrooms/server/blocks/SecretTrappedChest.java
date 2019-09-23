package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.server.tileentity.SecretChestTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;

import javax.annotation.Nullable;

public class SecretTrappedChest extends SecretChest {

    public SecretTrappedChest(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        TileEntity tileEntity = blockAccess.getTileEntity(pos);
        if(tileEntity instanceof SecretChestTileEntity) {
            return MathHelper.clamp(((SecretChestTileEntity) tileEntity).getNumPlayersUsing() + 3, 0, 15);
        }
        return 0;
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
        return true;
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return side == Direction.UP ? blockState.getWeakPower(blockAccess, pos, side) : 0;
    }
}
