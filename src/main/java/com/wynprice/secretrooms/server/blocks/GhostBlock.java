package com.wynprice.secretrooms.server.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class GhostBlock extends SecretBaseBlock {
    public static final BooleanProperty NORMAL_CUBE = BooleanProperty.create("normal_cube");

    public GhostBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(NORMAL_CUBE, false));
    }

    @Override
    public BlockState getPlaceState(IBlockReader wold, BlockPos placedOnPos, BlockState placedOn) {
        return super.getPlaceState(wold, placedOnPos, placedOn).with(NORMAL_CUBE, placedOn.isNormalCube(wold, placedOnPos));
    }

    @Override
    public boolean isSolid(BlockState state) {
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORMAL_CUBE);
        super.fillStateContainer(builder);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState adjacent, Direction side) {
        return (adjacent.getBlock() == this && adjacent.get(NORMAL_CUBE)) || super.isSideInvisible(state, adjacent, side);
    }
}
