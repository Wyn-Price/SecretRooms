package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;

import java.util.function.Predicate;

public class SecretPressurePlate extends AbstractSecretPressurePlateBase {

    private static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private final Predicate<Entity> entityPredicate;
    public SecretPressurePlate(Properties properties, Predicate<Entity> powerMapper) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
        this.entityPredicate = powerMapper;
    }

    @Override
    protected void playClickOnSound(LevelAccessor worldIn, BlockPos pos) {
        worldIn.playSound(null, pos, SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.3F, 0.8F);
    }

    @Override
    protected void playClickOffSound(LevelAccessor worldIn, BlockPos pos) {
        worldIn.playSound(null, pos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundSource.BLOCKS, 0.3F, 0.7F);
    }

    @Override
    protected int computeRedstoneStrength(Level worldIn, BlockPos pos) {
        return worldIn.getEntitiesOfClass(Entity.class, new AABB(pos).expandTowards(0, 0.25, 0), this.entityPredicate).isEmpty() ? 0 : 15;
    }

    @Override
    protected int getRedstoneStrength(BlockState state) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    @Override
    protected BlockState setRedstoneStrength(BlockState state, int strength) {
        return state.setValue(POWERED, strength > 0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }
}
