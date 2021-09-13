package com.wynprice.secretrooms.server.blocks;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public abstract class AbstractSecretPressurePlateBase extends SecretBaseBlock {
    public AbstractSecretPressurePlateBase(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return super.getCollisionShape(state, worldIn, pos, context);
    }

    public int tickRate(LevelReader worldIn) {
        return 20;
    }




    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, Random rand) {
        if (!worldIn.isClientSide) {
            int i = this.getRedstoneStrength(state);
            if (i > 0) {
                this.updateState(worldIn, pos, state, i);
            }
        }
    }

    @Override
    public void stepOn(Level worldIn, BlockPos pos, BlockState state, Entity entityIn) {
        if (!worldIn.isClientSide) {
            int i = this.getRedstoneStrength(state);
//            if (i == 0) {
            this.updateState(worldIn, pos, state, i);
//            }
        }
    }

    protected void updateState(Level worldIn, BlockPos pos, BlockState state, int oldRedstoneStrength) {
        int i = this.computeRedstoneStrength(worldIn, pos);
        boolean flag = oldRedstoneStrength > 0;
        boolean flag1 = i > 0;
        if (oldRedstoneStrength != i) {
            BlockState blockstate = this.setRedstoneStrength(state, i);
            worldIn.setBlock(pos, blockstate, 2);
            this.updateNeighbors(worldIn, pos);
            worldIn.setBlocksDirty(pos, state, blockstate);
        }

        if (!flag1 && flag) {
            this.playClickOffSound(worldIn, pos);
        } else if (flag1 && !flag) {
            this.playClickOnSound(worldIn, pos);
        }

        if (flag1) {
            worldIn.getBlockTicks().scheduleTick(new BlockPos(pos), this, this.tickRate(worldIn));
        }

    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return this.getRedstoneStrength(blockState);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && state.getBlock() != newState.getBlock()) {
            if (this.getRedstoneStrength(state) > 0) {
                this.updateNeighbors(worldIn, pos);
            }

            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    private void updateNeighbors(Level worldIn, BlockPos pos) {
        worldIn.updateNeighborsAt(pos, this);
        worldIn.updateNeighborsAt(pos.below(), this);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
        return this.getRedstoneStrength(state) == 0;
    }


    protected abstract void playClickOnSound(LevelAccessor worldIn, BlockPos pos);

    protected abstract void playClickOffSound(LevelAccessor worldIn, BlockPos pos);

    protected abstract int computeRedstoneStrength(Level worldIn, BlockPos pos);

    protected abstract int getRedstoneStrength(BlockState state);

    protected abstract BlockState setRedstoneStrength(BlockState state, int strength);
}
