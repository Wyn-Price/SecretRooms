package com.wynprice.secretrooms.server.blocks;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.*;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class SecretRedstone extends SecretBaseBlock {

    private static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;

    private boolean blockedPower = false;

    private final Set<BlockPos> toUpdate = Sets.newHashSet();

    public SecretRedstone(Properties properties) {
        super(properties);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(POWER);
    }

    private BlockState updateSurroundingRedstone(World worldIn, BlockPos pos, BlockState state) {
        state = this.updateNeighbourRedone(worldIn, pos, state);
        List<BlockPos> list = Lists.newArrayList(this.toUpdate);
        this.toUpdate.clear();

        for(BlockPos blockpos : list) {
            worldIn.notifyNeighborsOfStateChange(blockpos, this);
        }

        return state;
    }

    private BlockState updateNeighbourRedone(World world, BlockPos pos, BlockState state) {
        BlockState blockstate = state;
        int power = state.get(POWER);
        this.blockedPower = true;
        int neighborPower = world.getRedstonePowerFromNeighbors(pos);
        this.blockedPower = false;
        int maxPower = 0;
        if (neighborPower < 15) {
            for(Direction direction : Direction.values()) {
                maxPower = this.maxSignal(maxPower, world.getBlockState(pos.offset(direction)));
            }
        }

        int stateMaxPower = maxPower - 1;
        if (neighborPower > stateMaxPower) {
            stateMaxPower = neighborPower;
        }

        if (power != stateMaxPower) {
            state = state.with(POWER, stateMaxPower);
            if (world.getBlockState(pos) == blockstate) {
                world.setBlockState(pos, state, 2);
            }

            this.toUpdate.add(pos);

            for(Direction direction : Direction.values()) {
                this.toUpdate.add(pos.offset(direction));
            }
        }

        return state;
    }

    private void notifyWireNeighborsOfStateChange(World worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).getBlock() == this) {
            worldIn.notifyNeighborsOfStateChange(pos, this);

            for(Direction direction : Direction.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
            }

        }
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() != state.getBlock() && !worldIn.isRemote) {
            this.updateSurroundingRedstone(worldIn, pos, state);

            for(Direction direction : Direction.Plane.VERTICAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
            }

            for(Direction direction1 : Direction.Plane.HORIZONTAL) {
                this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(direction1));
            }

            for(Direction direction2 : Direction.Plane.HORIZONTAL) {
                BlockPos blockpos = pos.offset(direction2);
                if (worldIn.getBlockState(blockpos).isNormalCube(worldIn, blockpos)) {
                    this.notifyWireNeighborsOfStateChange(worldIn, blockpos.up());
                } else {
                    this.notifyWireNeighborsOfStateChange(worldIn, blockpos.down());
                }
            }

        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && state.getBlock() != newState.getBlock()) {
            super.onReplaced(state, worldIn, pos, newState, false);
            if (!worldIn.isRemote) {
                for(Direction direction : Direction.values()) {
                    worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
                }

                this.updateSurroundingRedstone(worldIn, pos, state);

                for(Direction direction : Direction.values()) {
                    this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(direction));
                }
            }
        }
    }

    private int maxSignal(int existingSignal, BlockState neighbor) {
        if (neighbor.getBlock() != this && neighbor.getBlock() != Blocks.REDSTONE_WIRE) {
            return existingSignal;
        } else {
            int i = neighbor.get(POWER);
            return i > existingSignal ? i : existingSignal;
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isRemote) {
            if (state.isValidPosition(worldIn, pos)) {
                this.updateSurroundingRedstone(worldIn, pos, state);
            } else {
                spawnDrops(state, worldIn, pos);
                worldIn.removeBlock(pos, false);
            }

        }
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return this.blockedPower ? 0 : blockState.getWeakPower(blockAccess, pos, side);
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        if (this.blockedPower) {
            return 0;
        } else {
            int power = blockState.get(POWER);
            if (power == 0) {
                return 0;
            } else {
                EnumSet<Direction> enumset = EnumSet.noneOf(Direction.class);

                for(Direction direction : Direction.values()) {
                    if (this.isPowerSourceAt(blockAccess, pos, direction)) {
                        enumset.add(direction);
                    }
                }

                if (side.getAxis().isHorizontal() && enumset.isEmpty()) {
                    return power;
                } else if (enumset.contains(side) && !enumset.contains(side.rotateYCCW()) && !enumset.contains(side.rotateY())) {
                    return power;
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean isPowerSourceAt(IBlockReader worldIn, BlockPos pos, Direction side) {
        BlockPos offsetPos = pos.offset(side);
        BlockState state = worldIn.getBlockState(offsetPos);
        if (canConnectTo(state, worldIn, offsetPos, side)) {
            return true;
        } else {
            return state.getBlock() == Blocks.REPEATER && state.get(RedstoneDiodeBlock.POWERED) && state.get(RedstoneDiodeBlock.HORIZONTAL_FACING) == side;
        }
    }

    protected static boolean canConnectTo(BlockState blockState, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        Block block = blockState.getBlock();
        if (block == Blocks.REDSTONE_WIRE || block == SecretBlocks.SECRET_REDSTONE) {
            return true;
        } else if (blockState.getBlock() == Blocks.REPEATER) {
            Direction direction = blockState.get(RepeaterBlock.HORIZONTAL_FACING);
            return direction == side || direction.getOpposite() == side;
        } else if (Blocks.OBSERVER == blockState.getBlock()) {
            return side == blockState.get(ObserverBlock.FACING);
        } else {
            return blockState.canConnectRedstone(world, pos, side) && side != null;
        }
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return !this.blockedPower;
    }
}
