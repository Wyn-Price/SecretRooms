package com.wynprice.secretrooms.server.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ObserverBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

public class SecretObserver extends SecretBaseBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public SecretObserver(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(FACING, Direction.SOUTH).with(POWERED, Boolean.FALSE));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(FACING, POWERED);
    }

    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if (state.get(POWERED)) {
            worldIn.setBlockState(pos, state.with(POWERED, false), 2);
        } else {
            worldIn.setBlockState(pos, state.with(POWERED, true), 2);
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2);
        }

        this.updateNeighborsInFront(worldIn, pos, state);
    }



    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(FACING) == facing && !stateIn.get(POWERED)) {
            this.startSignal(worldIn, currentPos);
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    private void startSignal(IWorld worldIn, BlockPos pos) {
        if (!worldIn.isRemote() && !worldIn.getPendingBlockTicks().isTickScheduled(pos, this)) {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2);
        }
    }

    private void updateNeighborsInFront(World worldIn, BlockPos pos, BlockState state) {
        Direction direction = state.get(FACING);
        BlockPos blockpos = pos.offset(direction.getOpposite());
        worldIn.neighborChanged(blockpos, this, pos);
        worldIn.notifyNeighborsOfStateExcept(blockpos, this, direction);
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return side == state.get(ObserverBlock.FACING);
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.getWeakPower(blockAccess, pos, side);
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.get(POWERED) && blockState.get(FACING) == side ? 15 : 0;
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getBlock() != oldState.getBlock()) {
            if (!worldIn.isRemote() && state.get(POWERED) && !worldIn.getPendingBlockTicks().isTickScheduled(pos, this)) {
                BlockState blockstate = state.with(POWERED, false);
                worldIn.setBlockState(pos, blockstate, 18);
                this.updateNeighborsInFront(worldIn, pos, blockstate);
            }

        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (!worldIn.isRemote && state.get(POWERED) && worldIn.getPendingBlockTicks().isTickScheduled(pos, this)) {
                this.updateNeighborsInFront(worldIn, pos, state.with(POWERED, false));
            }

        }
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite().getOpposite());
    }
}
