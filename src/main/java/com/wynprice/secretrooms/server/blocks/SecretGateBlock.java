package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.server.data.SecretData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Random;

public class SecretGateBlock extends SecretBaseBlock {

    private static final int MAX_LEVELS = 10;

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    private static final DirectionProperty FACING = DirectionalBlock.FACING;

    public SecretGateBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        boolean powered = state.get(POWERED);
        boolean newPowered = worldIn.isBlockPowered(pos);
        if(powered != newPowered) {
            if(newPowered) {
                this.tryBuildGate(worldIn, pos, state);
            } else {
                this.destroyGate(worldIn, pos, state, true);
            }
            worldIn.setBlockState(pos, state.with(POWERED, newPowered).with(OPEN, newPowered));
        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            this.destroyGate(worldIn, pos, state, false);
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return super.getStateForPlacement(context).with(FACING, context.getFace());
    }

    @Override
    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
        if(state.get(OPEN)) {
            tryBuildGate(worldIn, pos, state);
        } else {
            destroyGate(worldIn, pos, state, true);
        }
        super.tick(state, worldIn, pos, random);
    }

    private void tryBuildGate(World world, BlockPos pos, BlockState gateState) {
        Optional<SecretData> data = getMirrorData(world, pos);
        Direction direction = gateState.get(FACING);

        for (int i = 1; i <= MAX_LEVELS; i++) {
            BlockPos off = pos.offset(direction, i);

            BlockState state = world.getBlockState(off);
            if(!state.getMaterial().isReplaceable() && state.getBlock() != SecretBlocks.SECRET_DUMMY_BLOCK) { //TODO: move to BlockState#isReplaceable
                if(state.getBlock() == this) {
                    world.setBlockState(off, state.with(OPEN, true));
                    world.getPendingBlockTicks().scheduleTick(off, this, 3);
                }
                return;
            }

            if(i == MAX_LEVELS) {
                return;
            }

            for (Direction value : Direction.values()) {
                if(value.getAxis() != direction.getAxis()) {
                    BlockPos offPos = off.offset(value);
                    BlockState offState = world.getBlockState(offPos);
                    if(offState.getBlock() == this) {
                        world.setBlockState(offPos, offState.with(OPEN, true));
                        world.getPendingBlockTicks().scheduleTick(offPos, this, 3);
                    }
                }
            }

            world.setBlockState(off, SecretBlocks.SECRET_DUMMY_BLOCK.getDefaultState());
            data.ifPresent(sdata -> getMirrorData(world, off).ifPresent(d -> d.setFrom(sdata)));
            requestModelRefresh(world, off);
            world.getTileEntity(off).markDirty();
        }
    }

    private void destroyGate(World world, BlockPos pos, BlockState gateState, boolean recursive) {
        Direction direction = gateState.get(FACING);
        for (int i = 1; i <= MAX_LEVELS; i++) {
            BlockPos off = pos.offset(direction, i);

            BlockState state = world.getBlockState(off);
            if(state.getBlock() != SecretBlocks.SECRET_DUMMY_BLOCK) {
                if(state.getBlock() == this && recursive) {
                    world.setBlockState(off, state.with(OPEN, false));
                    world.getPendingBlockTicks().scheduleTick(off, this, 3);
                }
                return;
            }

            if(i == MAX_LEVELS) {
                return;
            }

            if(recursive) {
                for (Direction value : Direction.values()) {
                    if(value.getAxis() != direction.getAxis()) {
                        BlockPos offPos = off.offset(value);
                        BlockState offState = world.getBlockState(offPos);
                        if(offState.getBlock() == this) {
                            world.setBlockState(offPos, offState.with(OPEN, false));
                            world.getPendingBlockTicks().scheduleTick(offPos, this, 3);
                        }
                    }
                }
            }

            world.setBlockState(off, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(POWERED, OPEN, FACING);
    }
}
