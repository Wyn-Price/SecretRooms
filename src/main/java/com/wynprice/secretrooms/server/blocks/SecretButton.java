package com.wynprice.secretrooms.server.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class SecretButton extends SecretBaseBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private final boolean wooden;

    public SecretButton(Properties properties, boolean wooden) {
        super(properties);
        this.wooden = wooden;
        this.setDefaultState(this.getDefaultState().with(POWERED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(POWERED);
    }

    @Override
    public int tickRate(IWorldReader worldIn) {
        return this.wooden ? 30 : 20;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(state.get(POWERED)) {
            return true;
        } else {
            worldIn.setBlockState(pos, state.with(POWERED, Boolean.TRUE), 3);
            this.playSound(player, worldIn, pos, true);
            this.updateNeighbors(worldIn, pos);
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
            return true;
        }
    }

    private void playSound(@Nullable PlayerEntity player, IWorld world, BlockPos pos, boolean turnOn) {
        SoundEvent woodenEvent = turnOn ? SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_ON : SoundEvents.BLOCK_WOODEN_BUTTON_CLICK_OFF;
        SoundEvent stoneEvent = turnOn ? SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON : SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
        world.playSound(turnOn ? player : null, pos, this.wooden ? woodenEvent : stoneEvent, SoundCategory.BLOCKS, 0.3F, turnOn ? 0.6F : 0.5F);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if(!isMoving && state.getBlock() != newState.getBlock()) {
            if(state.get(POWERED)) {
                this.updateNeighbors(worldIn, pos);
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.get(POWERED) ? 15 : 0;
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
        return !state.get(POWERED);
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }

    @Override
    public void tick(BlockState state, World worldIn, BlockPos pos, Random random) {
        if(!worldIn.isRemote && state.get(POWERED)) {
            if(this.wooden) {
                this.checkPressed(state, worldIn, pos);
            } else {
                worldIn.setBlockState(pos, state.with(POWERED, Boolean.FALSE), 3);
                this.updateNeighbors(worldIn, pos);
                this.playSound(null, worldIn, pos, false);
            }
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if(!worldIn.isRemote && this.wooden && !state.get(POWERED)) {
            this.checkPressed(state, worldIn, pos);
        }
    }

    private void checkPressed(BlockState state, World worldIn, BlockPos pos) {
        List<? extends Entity> list = worldIn.getEntitiesWithinAABB(AbstractArrowEntity.class, state.getShape(worldIn, pos).getBoundingBox().offset(pos));
        boolean flag = !list.isEmpty();
        boolean flag1 = state.get(POWERED);
        if(flag != flag1) {
            worldIn.setBlockState(pos, state.with(POWERED, flag), 3);
            this.updateNeighbors(worldIn, pos);
            this.playSound(null, worldIn, pos, flag);
        }

        if(flag) {
            worldIn.getPendingBlockTicks().scheduleTick(new BlockPos(pos), this, this.tickRate(worldIn));
        }
    }

    private void updateNeighbors(World world, BlockPos pos) {
        world.notifyNeighborsOfStateChange(pos, this);
        for(Direction value : Direction.values()) {
            world.notifyNeighborsOfStateChange(pos.offset(value.getOpposite()), this);
        }
    }
}
