package com.wynprice.secretrooms.server.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class SecretPressurePlate extends AbstractSecretPressurePlateBase {

    private static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private final Predicate<Entity> entityPredicate;
    public SecretPressurePlate(Properties properties, Predicate<Entity> powerMapper) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(POWERED, false));
        this.entityPredicate = powerMapper;
    }

    @Override
    protected void playClickOnSound(IWorld worldIn, BlockPos pos) {
        worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.8F);
    }

    @Override
    protected void playClickOffSound(IWorld worldIn, BlockPos pos) {
        worldIn.playSound(null, pos, SoundEvents.BLOCK_WOODEN_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.7F);
    }

    @Override
    protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
        return worldIn.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos).expand(0, 0.25, 0), this.entityPredicate).isEmpty() ? 0 : 15;
    }

    @Override
    protected int getRedstoneStrength(BlockState state) {
        return state.get(POWERED) ? 15 : 0;
    }

    @Override
    protected BlockState setRedstoneStrength(BlockState state, int strength) {
        return state.with(POWERED, strength > 0);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(POWERED);
    }
}
