package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class SecretButton extends SecretBaseBlock {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private final boolean wooden;

    public SecretButton(Properties properties, boolean wooden) {
        super(properties);
        this.wooden = wooden;
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    public int tickRate(LevelReader worldIn) {
        return this.wooden ? 30 : 20;
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (state.getValue(POWERED)) {
            return InteractionResult.SUCCESS;
        } else {
            worldIn.setBlock(pos, state.setValue(POWERED, Boolean.TRUE), 3);
            this.playSound(player, worldIn, pos, true);
            this.updateNeighbors(worldIn, pos);
            worldIn.scheduleTick(pos, this, this.tickRate(worldIn));
            return InteractionResult.SUCCESS;
        }
    }

    private void playSound(@Nullable Player player, LevelAccessor world, BlockPos pos, boolean turnOn) {
        SoundEvent woodenEvent = turnOn ? SoundEvents.WOODEN_BUTTON_CLICK_ON : SoundEvents.WOODEN_BUTTON_CLICK_OFF;
        SoundEvent stoneEvent = turnOn ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF;
        world.playSound(turnOn ? player : null, pos, this.wooden ? woodenEvent : stoneEvent, SoundSource.BLOCKS, 0.3F, turnOn ? 0.6F : 0.5F);
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && state.getBlock() != newState.getBlock()) {
            if (state.getValue(POWERED)) {
                this.updateNeighbors(worldIn, pos);
            }

            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(POWERED) ? 15 : 0;
    }

    // TODO (port): figure out weak power
//    @Override
//    public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
//        return !state.getValue(POWERED);
//    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        if (!worldIn.isClientSide && state.getValue(POWERED)) {
            if (this.wooden) {
                this.checkPressed(state, worldIn, pos);
            } else {
                worldIn.setBlock(pos, state.setValue(POWERED, Boolean.FALSE), 3);
                this.updateNeighbors( worldIn, pos);
                this.playSound(null, worldIn, pos, false);
            }
        }
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (!worldIn.isClientSide && this.wooden && !state.getValue(POWERED)) {
            this.checkPressed(state, worldIn, pos);
        }
    }

    private void checkPressed(BlockState state, Level worldIn, BlockPos pos) {
        List<? extends Entity> list = worldIn.getEntitiesOfClass(AbstractArrow.class, state.getShape(worldIn, pos).bounds().move(pos));
        boolean flag = !list.isEmpty();
        boolean flag1 = state.getValue(POWERED);
        if (flag != flag1) {
            worldIn.setBlock(pos, state.setValue(POWERED, flag), 3);
            this.updateNeighbors(worldIn, pos);
            this.playSound(null, worldIn, pos, flag);
        }

        if (flag) {
            worldIn.scheduleTick(new BlockPos(pos), this, this.tickRate(worldIn));
        }
    }

    private void updateNeighbors(Level world, BlockPos pos) {
        world.updateNeighborsAt(pos, this);
        for (Direction value : Direction.values()) {
            world.updateNeighborsAt(pos.relative(value.getOpposite()), this);
        }
    }
}
