package com.wynprice.secretrooms.server.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class TorchLever extends TorchBlock {

    private static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    protected TorchLever(Properties properties) {
        super(properties, ParticleTypes.SOUL_FIRE_FLAME);
        this.setDefaultState(this.getDefaultState().with(POWERED, false));
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
        state = state.func_235896_a_(POWERED);
        boolean isPowered = state.get(POWERED);
        if (world.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            world.setBlockState(pos, state, 3);
            float pitch = isPowered ? 0.6F : 0.5F;
            world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, pitch);
            this.updateNeighbors(world, pos);
            return ActionResultType.SUCCESS;
        }
    }

    private void updateNeighbors(World world, BlockPos pos) {
        world.notifyNeighborsOfStateChange(pos, this);
        for (Direction direction : Direction.values()) {
            world.notifyNeighborsOfStateChange(pos.offset(direction), this);
        }
    }

    @Override
    public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction direction) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.get(POWERED) && side == Direction.UP ? 15 : 0;
    }

    @Override
    public boolean canProvidePower(BlockState p_149744_1_) {
        return true;
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && state.getBlock() != newState.getBlock()) {
            if (state.get(POWERED)) {
                this.updateNeighbors(world, pos);
            }

            super.onReplaced(state, world, pos, newState, isMoving);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(POWERED);
    }
}
