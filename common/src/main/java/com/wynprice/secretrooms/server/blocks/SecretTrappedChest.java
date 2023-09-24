package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.server.tileentity.SecretChestTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SecretTrappedChest extends SecretChest {

    public SecretTrappedChest(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        BlockEntity tileEntity = blockAccess.getBlockEntity(pos);
        if(tileEntity instanceof SecretChestTileEntity) {
            return Mth.clamp(((SecretChestTileEntity) tileEntity).getNumPlayersUsing(), 0, 15);
        }
        return 0;
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return side == Direction.UP ? blockState.getSignal(blockAccess, pos, side) : 0;
    }
}
