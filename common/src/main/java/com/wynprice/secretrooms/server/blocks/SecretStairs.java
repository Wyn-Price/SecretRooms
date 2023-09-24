package com.wynprice.secretrooms.server.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

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
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }


    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return (state.getValue(HALF) == Half.TOP ? SLAB_TOP_SHAPES : SLAB_BOTTOM_SHAPES)[idsToShape[this.stateToId(state)]];
    }

    private int stateToId(BlockState state) {
        return state.getValue(SHAPE).ordinal() * 4 + state.getValue(FACING).get2DDataValue();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()).setValue(HALF, direction != Direction.DOWN && (direction == Direction.UP || context.getClickLocation().y - (double)pos.getY() <= 0.5D) ? Half.BOTTOM : Half.TOP);
        return state.setValue(SHAPE, getShapeProperty(state, context.getLevel(), pos));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState fromState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        return direction.getAxis().isHorizontal() ? state.setValue(SHAPE, getShapeProperty(state, world, pos)) : super.updateShape(state, direction, fromState, world, pos, posFrom);
    }

    private static StairsShape getShapeProperty(BlockState state, BlockGetter world, BlockPos position) {
        Direction direction = state.getValue(FACING);
        BlockState oppositeState = world.getBlockState(position.relative(direction));
        if (isBlockStairs(oppositeState) && state.getValue(HALF) == oppositeState.getValue(HALF)) {
            Direction oppositeDirection = oppositeState.getValue(FACING);
            if (oppositeDirection.getAxis() != state.getValue(FACING).getAxis() && isDifferentStairs(state, world, position, oppositeDirection.getOpposite())) {
                if (oppositeDirection == direction.getCounterClockWise()) {
                    return StairsShape.OUTER_LEFT;
                }

                return StairsShape.OUTER_RIGHT;
            }
        }

        BlockState othersideState = world.getBlockState(position.relative(direction.getOpposite()));
        if (isBlockStairs(othersideState) && state.getValue(HALF) == othersideState.getValue(HALF)) {
            Direction othersideDirection = othersideState.getValue(FACING);
            if (othersideDirection.getAxis() != state.getValue(FACING).getAxis() && isDifferentStairs(state, world, position, othersideDirection)) {
                if (othersideDirection == direction.getCounterClockWise()) {
                    return StairsShape.INNER_LEFT;
                }

                return StairsShape.INNER_RIGHT;
            }
        }

        return StairsShape.STRAIGHT;
    }

    private static boolean isDifferentStairs(BlockState state, BlockGetter world, BlockPos pos, Direction direction) {
        BlockState offsetState = world.getBlockState(pos.relative(direction));
        return !isBlockStairs(offsetState) || offsetState.getValue(FACING) != state.getValue(FACING) || offsetState.getValue(HALF) != state.getValue(HALF);
    }

    public static boolean isBlockStairs(BlockState state) {
        return state.getBlock() instanceof StairBlock || state.getBlock() instanceof SecretStairs;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, HALF, SHAPE);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return false;
    }

    private static class StairsBlockAccess extends StairBlock {

        protected static final VoxelShape[] SLAB_TOP_SHAPES = StairBlock.TOP_SHAPES;
        protected static final VoxelShape[] SLAB_BOTTOM_SHAPES = StairBlock.BOTTOM_SHAPES;

        protected StairsBlockAccess(BlockState p_i48321_1_, Properties p_i48321_2_) {
            super(p_i48321_1_, p_i48321_2_);
        }
    }

}
