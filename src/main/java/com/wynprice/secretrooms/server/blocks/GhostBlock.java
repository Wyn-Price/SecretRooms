package com.wynprice.secretrooms.server.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class GhostBlock extends SecretBaseBlock {
    public static final BooleanProperty NORMAL_CUBE = BooleanProperty.create("normal_cube");

    public GhostBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(NORMAL_CUBE, false));
    }

    @Override
    public BlockState getPlaceState(BlockGetter wold, BlockPos placedOnPos, BlockState placedOn, BlockState fallback) {
        return super.getPlaceState(wold, placedOnPos, placedOn, fallback).setValue(NORMAL_CUBE, placedOn.isRedstoneConductor(wold, placedOnPos));
    }

    @Override
    public Boolean getSolidValue() {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORMAL_CUBE);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction side) {
        return (adjacent.getBlock() == this && adjacent.getValue(NORMAL_CUBE)) || super.skipRendering(state, adjacent, side);
    }
}
