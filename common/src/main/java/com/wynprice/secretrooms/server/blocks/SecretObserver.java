package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import javax.annotation.Nullable;
import java.util.Random;

public class SecretObserver extends SecretBaseBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public SecretObserver(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.SOUTH).setValue(POWERED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, POWERED);
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource random) {
        if (state.getValue(POWERED)) {
            worldIn.setBlock(pos, state.setValue(POWERED, false), 2);
        } else {
            worldIn.setBlock(pos, state.setValue(POWERED, true), 2);
            worldIn.scheduleTick(pos, this, 2);
        }

        this.updateNeighborsInFront(worldIn, pos, state);
    }



    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(FACING) == facing && !stateIn.getValue(POWERED)) {
            this.startSignal(worldIn, currentPos);
        }
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    private void startSignal(LevelAccessor worldIn, BlockPos pos) {
        if (!worldIn.isClientSide() && !worldIn.getBlockTicks().hasScheduledTick(pos, this)) {
            worldIn.scheduleTick(pos, this, 2);
        }
    }

    private void updateNeighborsInFront(Level worldIn, BlockPos pos, BlockState state) {
        Direction direction = state.getValue(FACING);
        BlockPos blockpos = pos.relative(direction.getOpposite());
        worldIn.neighborChanged(blockpos, this, pos);
        worldIn.updateNeighborsAtExceptFromFacing(blockpos, this, direction);
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    //TODO: something like this
//    @Override
//    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction side) {
//        return side == state.getValue(ObserverBlock.FACING);
//    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getSignal(blockAccess, pos, side);
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(POWERED) && blockState.getValue(FACING) == side ? 15 : 0;
    }

    // TODO (port): figure out weak power
//    @Override
//    public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
//        return false;
//    }

    @Override
    public void onPlace(BlockState state, Level worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (state.getBlock() != oldState.getBlock()) {
            if (!worldIn.isClientSide() && state.getValue(POWERED) && !worldIn.getBlockTicks().hasScheduledTick(pos, this)) {
                BlockState blockstate = state.setValue(POWERED, false);
                worldIn.setBlock(pos, blockstate, 18);
                this.updateNeighborsInFront(worldIn, pos, blockstate);
            }

        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            if (!worldIn.isClientSide && state.getValue(POWERED) && worldIn.getBlockTicks().hasScheduledTick(pos, this)) {
                this.updateNeighborsInFront(worldIn, pos, state.setValue(POWERED, false));
            }

        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite().getOpposite());
    }
}
