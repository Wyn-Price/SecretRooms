package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.server.data.SecretData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import javax.annotation.Nullable;
import java.util.Optional;

public class SecretGateBlock extends SecretBaseBlock {

    private static final int MAX_LEVELS = 10;

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    private static final DirectionProperty FACING = DirectionalBlock.FACING;

    public SecretGateBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean powered = state.getValue(POWERED);
        boolean newPowered = worldIn.hasNeighborSignal(pos);
        if(powered != newPowered) {
            if(newPowered) {
                this.tryBuildGate(worldIn, pos, state);
            } else {
                this.destroyGate(worldIn, pos, state, true);
            }
            worldIn.setBlockAndUpdate(pos, state.setValue(POWERED, newPowered).setValue(OPEN, newPowered));
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            this.destroyGate(worldIn, pos, state, false);
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getClickedFace());
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        if(state.getValue(OPEN)) {
            tryBuildGate(worldIn, pos, state);
        } else {
            destroyGate(worldIn, pos, state, true);
        }
        super.tick(state, worldIn, pos, rand);
    }

    private void tryBuildGate(Level world, BlockPos pos, BlockState gateState) {
        Optional<SecretData> data = getMirrorData(world, pos);
        Direction direction = gateState.getValue(FACING);

        for (int i = 1; i <= MAX_LEVELS; i++) {
            BlockPos off = pos.relative(direction, i);

            BlockState state = world.getBlockState(off);
            if(!state.canBeReplaced() && state.getBlock() != SecretBlocks.SECRET_DUMMY_BLOCK.get()) { //TODO: move to BlockState#isReplaceable
                if(state.getBlock() == this) {
                    world.setBlockAndUpdate(off, state.setValue(OPEN, true));
                    world.scheduleTick(off, this, 3);
                }
                return;
            }

            if(i == MAX_LEVELS) {
                return;
            }

            for (Direction value : Direction.values()) {
                if(value.getAxis() != direction.getAxis()) {
                    BlockPos offPos = off.relative(value);
                    BlockState offState = world.getBlockState(offPos);
                    if(offState.getBlock() == this) {
                        world.setBlockAndUpdate(offPos, offState.setValue(OPEN, true));
                        world.scheduleTick(offPos, this, 3);
                    }
                }
            }

            world.setBlockAndUpdate(off, SecretBlocks.SECRET_DUMMY_BLOCK.get().defaultBlockState());
            data.ifPresent(sdata -> getMirrorData(world, off).ifPresent(d -> d.setFrom(sdata)));
            requestModelRefresh(world, off);
            world.getBlockEntity(off).setChanged();
        }
    }

    private void destroyGate(Level world, BlockPos pos, BlockState gateState, boolean recursive) {
        Direction direction = gateState.getValue(FACING);
        for (int i = 1; i <= MAX_LEVELS; i++) {
            BlockPos off = pos.relative(direction, i);

            BlockState state = world.getBlockState(off);
            if(state.getBlock() != SecretBlocks.SECRET_DUMMY_BLOCK.get()) {
                if(state.getBlock() == this && recursive) {
                    world.setBlockAndUpdate(off, state.setValue(OPEN, false));
                    world.scheduleTick(off, this, 3);
                }
                return;
            }

            if(i == MAX_LEVELS) {
                return;
            }

            if(recursive) {
                for (Direction value : Direction.values()) {
                    if(value.getAxis() != direction.getAxis()) {
                        BlockPos offPos = off.relative(value);
                        BlockState offState = world.getBlockState(offPos);
                        if(offState.getBlock() == this) {
                            world.setBlockAndUpdate(offPos, offState.setValue(OPEN, false));
                            world.scheduleTick(offPos, this, 3);
                        }
                    }
                }
            }

            world.setBlockAndUpdate(off, Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED, OPEN, FACING);
    }
}
