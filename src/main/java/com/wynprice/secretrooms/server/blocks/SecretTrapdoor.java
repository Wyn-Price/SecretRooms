package com.wynprice.secretrooms.server.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.server.blocks.states.SecretMappedModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nullable;

public class SecretTrapdoor extends SecretBaseBlock {

    private static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    private static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    private static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final VoxelShape EAST_OPEN_AABB = Block.box(0.00101D, 0.001D, 0.001D, 3.0D, 15.999D, 15.999D);
    private static final VoxelShape WEST_OPEN_AABB = Block.box(13.0D, 0.001D, 0.001D, 15.999D, 15.999D, 15.999D);
    private static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.001D, 0.001D, 0.001D, 15.999D, 15.999D, 3.0D);
    private static final VoxelShape NORTH_OPEN_AABB = Block.box(0.001D, 0.001D, 13.0D, 15.999D, 15.999D, 15.999D);
    private static final VoxelShape BOTTOM_AABB = Block.box(0.001D, 0.001D, 0.001D, 15.999D, 3.0D, 15.999D);
    private static final VoxelShape TOP_AABB = Block.box(0.001D, 13.0D, 0.001D, 15.999D, 15.999D, 15.999D);

    public SecretTrapdoor(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
            .setValue(HORIZONTAL_FACING, Direction.NORTH)
            .setValue(OPEN, false)
            .setValue(HALF, Half.BOTTOM)
            .setValue(POWERED, false)
        );
    }

    @Override
    protected BlockState createNewState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
        return new SecretMappedModelState(block, propertiesToValueMap, codec);
    }

    @Override
    public void applyExtraModelData(BlockGetter world, BlockPos pos, BlockState state, ModelDataMap.Builder builder) {
        builder.withInitial(SecretModelData.MODEL_MAP_STATE, Blocks.OAK_TRAPDOOR.defaultBlockState()
            .setValue(HORIZONTAL_FACING, state.getValue(HORIZONTAL_FACING))
            .setValue(OPEN, state.getValue(OPEN))
            .setValue(HALF, state.getValue(HALF))
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        VoxelShape mirrorShape = super.getShape(state, worldIn, pos, context);
        VoxelShape shape;
        if (!state.getValue(OPEN)) {
            shape = state.getValue(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
        } else {
            switch(state.getValue(HORIZONTAL_FACING)) {
                case NORTH:
                default:
                    shape = NORTH_OPEN_AABB;
                    break;
                case SOUTH:
                    shape = SOUTH_OPEN_AABB;
                    break;
                case WEST:
                    shape = WEST_OPEN_AABB;
                    break;
                case EAST:
                    shape = EAST_OPEN_AABB;
                    break;
            }
        }
        return  Shapes.join(shape, mirrorShape, BooleanOp.AND);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return this.getShape(state, worldIn, pos, context);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return this.getShape(state, world, pos, CollisionContext.empty());
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return state.getShape(reader, pos);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return this.getShape(state, worldIn, pos, CollisionContext.empty());
    }

    @Override
    public Boolean getSolidValue() {
        return false;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        switch(type) {
            case LAND:
            case AIR:
                return state.getValue(OPEN);
            default:
                return false;
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (this.material == SecretBlocks.Materials.SRM_MATERIAL_IRON) {
            return InteractionResult.PASS;
        } else {
            state = state.cycle(OPEN);
            worldIn.setBlock(pos, state, 2);
            requestModelRefresh(worldIn, pos);
            this.playSound(player, worldIn, pos, state.getValue(OPEN));
            return InteractionResult.SUCCESS;
        }
    }


    protected void playSound(@Nullable Player player, Level worldIn, BlockPos pos, boolean p_185731_4_) {
        if (p_185731_4_) {
            int i = this.material == SecretBlocks.Materials.SRM_MATERIAL_IRON ? 1037 : 1007;
            worldIn.levelEvent(player, i, pos, 0);
        } else {
            int j = this.material == SecretBlocks.Materials.SRM_MATERIAL_IRON ? 1036 : 1013;
            worldIn.levelEvent(player, j, pos, 0);
        }

    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isClientSide) {
            boolean flag = worldIn.hasNeighborSignal(pos);
            if (flag != state.getValue(POWERED)) {
                if (state.getValue(OPEN) != flag) {
                    state = state.setValue(OPEN, flag);
                    this.playSound(null, worldIn, pos, flag);
                }
                worldIn.setBlock(pos, state.setValue(POWERED, flag), 2);
                requestModelRefresh(worldIn, pos);
                if (state.getValue(BlockStateProperties.WATERLOGGED)) {
                    worldIn.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
                }
            }

        }
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        return stateIn;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockstate = this.defaultBlockState();
        Direction direction = context.getClickedFace();
        if (!context.replacingClickedOnBlock() && direction.getAxis().isHorizontal()) {
            blockstate = blockstate.setValue(HORIZONTAL_FACING, direction).setValue(HALF, context.getClickLocation().y - (double)context.getClickedPos().getY() > 0.5D ? Half.TOP : Half.BOTTOM);
        } else {
            blockstate = blockstate.setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite()).setValue(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);
        }

        if (context.getLevel().hasNeighborSignal(context.getClickedPos())) {
            blockstate = blockstate.setValue(OPEN, Boolean.TRUE).setValue(POWERED, Boolean.TRUE);
        }

        return blockstate;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HORIZONTAL_FACING, OPEN, HALF, POWERED);
    }


    @Override
    public boolean isLadder(BlockState state, net.minecraft.world.level.LevelReader world, BlockPos pos, net.minecraft.world.entity.LivingEntity entity) {
        if (state.getValue(OPEN)) {
            BlockState down = world.getBlockState(pos.below());
            if (down.getBlock() == net.minecraft.world.level.block.Blocks.LADDER)
                return down.getValue(LadderBlock.FACING) == state.getValue(HORIZONTAL_FACING);
        }
        return false;
    }

    public boolean canEntitySpawn(BlockState state, BlockGetter worldIn, BlockPos pos, EntityType<?> type) {
        return false;
    }
}
