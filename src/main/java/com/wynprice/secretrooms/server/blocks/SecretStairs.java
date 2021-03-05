package com.wynprice.secretrooms.server.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.state.properties.StairsShape;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

public class SecretStairs extends SecretBaseBlock {
    public SecretStairs(Properties properties) {
        super(properties);
    }

    //The following is just copied and pasted from Stairs
    public static final DirectionProperty FACING = StairsBlockAccess.FACING;
    public static final EnumProperty<Half> HALF = StairsBlockAccess.HALF;
    public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
    protected static final VoxelShape[] SLAB_TOP_SHAPES = StairsBlockAccess.SLAB_TOP_SHAPES;
    protected static final VoxelShape[] SLAB_BOTTOM_SHAPES = StairsBlockAccess.SLAB_BOTTOM_SHAPES;
    private static final int[] idsToShape = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }


    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return (state.get(HALF) == Half.TOP ? SLAB_TOP_SHAPES : SLAB_BOTTOM_SHAPES)[idsToShape[this.stateToId(state)]];
    }

    private int stateToId(BlockState state) {
        return state.get(SHAPE).ordinal() * 4 + state.get(FACING).getHorizontalIndex();
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        Direction direction = context.getFace();
        BlockPos pos = context.getPos();
        BlockState state = this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing()).with(HALF, direction != Direction.DOWN && (direction == Direction.UP || context.getHitVec().y - (double)pos.getY() <= 0.5D) ? Half.BOTTOM : Half.TOP);
        return state.with(SHAPE, getShapeProperty(state, context.getWorld(), pos));
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState fromState, IWorld world, BlockPos pos, BlockPos posFrom) {
        return direction.getAxis().isHorizontal() ? state.with(SHAPE, getShapeProperty(state, world, pos)) : super.updatePostPlacement(state, direction, fromState, world, pos, posFrom);
    }

    private static StairsShape getShapeProperty(BlockState state, IBlockReader world, BlockPos position) {
        Direction direction = state.get(FACING);
        BlockState oppositeState = world.getBlockState(position.offset(direction));
        if (isBlockStairs(oppositeState) && state.get(HALF) == oppositeState.get(HALF)) {
            Direction oppositeDirection = oppositeState.get(FACING);
            if (oppositeDirection.getAxis() != state.get(FACING).getAxis() && isDifferentStairs(state, world, position, oppositeDirection.getOpposite())) {
                if (oppositeDirection == direction.rotateYCCW()) {
                    return StairsShape.OUTER_LEFT;
                }

                return StairsShape.OUTER_RIGHT;
            }
        }

        BlockState othersideState = world.getBlockState(position.offset(direction.getOpposite()));
        if (isBlockStairs(othersideState) && state.get(HALF) == othersideState.get(HALF)) {
            Direction othersideDirection = othersideState.get(FACING);
            if (othersideDirection.getAxis() != state.get(FACING).getAxis() && isDifferentStairs(state, world, position, othersideDirection)) {
                if (othersideDirection == direction.rotateYCCW()) {
                    return StairsShape.INNER_LEFT;
                }

                return StairsShape.INNER_RIGHT;
            }
        }

        return StairsShape.STRAIGHT;
    }

    private static boolean isDifferentStairs(BlockState state, IBlockReader world, BlockPos pos, Direction direction) {
        BlockState offsetState = world.getBlockState(pos.offset(direction));
        return !isBlockStairs(offsetState) || offsetState.get(FACING) != state.get(FACING) || offsetState.get(HALF) != state.get(HALF);
    }

    public static boolean isBlockStairs(BlockState state) {
        return state.getBlock() instanceof StairsBlock || state.getBlock() instanceof SecretStairs;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(FACING, HALF, SHAPE);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader world, BlockPos pos, PathType type) {
        return false;
    }

    private static class StairsBlockAccess extends StairsBlock {

        protected static final VoxelShape[] SLAB_TOP_SHAPES = StairsBlock.SLAB_TOP_SHAPES;
        protected static final VoxelShape[] SLAB_BOTTOM_SHAPES = StairsBlock.SLAB_BOTTOM_SHAPES;

        protected StairsBlockAccess(BlockState p_i48321_1_, Properties p_i48321_2_) {
            super(p_i48321_1_, p_i48321_2_);
        }
    }

}
