package com.wynprice.secretrooms.server.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public abstract class AbstractSecretPressurePlateBase extends SecretBaseBlock {
    public AbstractSecretPressurePlateBase(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return super.getCollisionShape(state, worldIn, pos, context);
    }

    @Override
    public int tickRate(IWorldReader worldIn) {
        return 20;
    }


    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
        if (!worldIn.isRemote) {
            int i = this.getRedstoneStrength(state);
            if (i > 0) {
                this.updateState(worldIn, pos, state, i);
            }
        }
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        BlockState state = worldIn.getBlockState(pos);
        if (!worldIn.isRemote) {
            int i = this.getRedstoneStrength(state);
//            if (i == 0) {
            this.updateState(worldIn, pos, state, i);
//            }
        }
    }

    protected void updateState(World worldIn, BlockPos pos, BlockState state, int oldRedstoneStrength) {
        int i = this.computeRedstoneStrength(worldIn, pos);
        boolean flag = oldRedstoneStrength > 0;
        boolean flag1 = i > 0;
        if (oldRedstoneStrength != i) {
            BlockState blockstate = this.setRedstoneStrength(state, i);
            worldIn.setBlockState(pos, blockstate, 2);
            this.updateNeighbors(worldIn, pos);
            worldIn.markBlockRangeForRenderUpdate(pos, state, blockstate);
        }

        if (!flag1 && flag) {
            this.playClickOffSound(worldIn, pos);
        } else if (flag1 && !flag) {
            this.playClickOnSound(worldIn, pos);
        }

        if (flag1) {
            worldIn.getPendingBlockTicks().scheduleTick(new BlockPos(pos), this, this.tickRate(worldIn));
        }

    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return this.getRedstoneStrength(blockState);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && state.getBlock() != newState.getBlock()) {
            if (this.getRedstoneStrength(state) > 0) {
                this.updateNeighbors(worldIn, pos);
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    private void updateNeighbors(World worldIn, BlockPos pos) {
        worldIn.notifyNeighborsOfStateChange(pos, this);
        worldIn.notifyNeighborsOfStateChange(pos.down(), this);
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
        return this.getRedstoneStrength(state) == 0;
    }


    protected abstract void playClickOnSound(IWorld worldIn, BlockPos pos);

    protected abstract void playClickOffSound(IWorld worldIn, BlockPos pos);

    protected abstract int computeRedstoneStrength(World worldIn, BlockPos pos);

    protected abstract int getRedstoneStrength(BlockState state);

    protected abstract BlockState setRedstoneStrength(BlockState state, int strength);
}
