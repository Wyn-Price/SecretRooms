package com.wynprice.secretrooms.server.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.client.SecretModelData;
import com.wynprice.secretrooms.server.blocks.states.SecretMappedModelState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.ModelDataMap;

import javax.annotation.Nullable;

public class SecretTrapdoor extends SecretBaseBlock {

    private static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    private static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    private static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final VoxelShape EAST_OPEN_AABB = Block.makeCuboidShape(0.00101D, 0.001D, 0.001D, 3.0D, 15.999D, 15.999D);
    private static final VoxelShape WEST_OPEN_AABB = Block.makeCuboidShape(13.0D, 0.001D, 0.001D, 15.999D, 15.999D, 15.999D);
    private static final VoxelShape SOUTH_OPEN_AABB = Block.makeCuboidShape(0.001D, 0.001D, 0.001D, 15.999D, 15.999D, 3.0D);
    private static final VoxelShape NORTH_OPEN_AABB = Block.makeCuboidShape(0.001D, 0.001D, 13.0D, 15.999D, 15.999D, 15.999D);
    private static final VoxelShape BOTTOM_AABB = Block.makeCuboidShape(0.001D, 0.001D, 0.001D, 15.999D, 3.0D, 15.999D);
    private static final VoxelShape TOP_AABB = Block.makeCuboidShape(0.001D, 13.0D, 0.001D, 15.999D, 15.999D, 15.999D);

    public SecretTrapdoor(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState()
            .with(HORIZONTAL_FACING, Direction.NORTH)
            .with(OPEN, false)
            .with(HALF, Half.BOTTOM)
            .with(POWERED, false)
        );
    }

    @Override
    protected BlockState createNewState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
        return new SecretMappedModelState(block, propertiesToValueMap, codec);
    }

    @Override
    public void applyExtraModelData(IBlockReader world, BlockPos pos, BlockState state, ModelDataMap.Builder builder) {
        builder.withInitial(SecretModelData.MODEL_MAP_STATE, Blocks.OAK_TRAPDOOR.getDefaultState()
            .with(HORIZONTAL_FACING, state.get(HORIZONTAL_FACING))
            .with(OPEN, state.get(OPEN))
            .with(HALF, state.get(HALF))
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape mirrorShape = super.getShape(state, worldIn, pos, context);
        VoxelShape shape;
        if (!state.get(OPEN)) {
            shape = state.get(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
        } else {
            switch(state.get(HORIZONTAL_FACING)) {
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
        return  VoxelShapes.combineAndSimplify(shape, mirrorShape, IBooleanFunction.AND);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return this.getShape(state, worldIn, pos, context);
    }

    @Override
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader world, BlockPos pos) {
        return this.getShape(state, world, pos, ISelectionContext.dummy());
    }

    @Override
    public VoxelShape getRayTraceShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return state.getShape(reader, pos);
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return this.getShape(state, worldIn, pos, ISelectionContext.dummy());
    }

    @Override
    public Boolean getSolidValue() {
        return false;
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    public int getOpacity(BlockState state, IBlockReader world, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        switch(type) {
            case LAND:
            case AIR:
                return state.get(OPEN);
            default:
                return false;
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (this.material == SecretBlocks.Materials.SRM_MATERIAL_IRON) {
            return ActionResultType.PASS;
        } else {
            state = state.func_235896_a_(OPEN);
            worldIn.setBlockState(pos, state, 2);
            requestModelRefresh(worldIn, pos);
            this.playSound(player, worldIn, pos, state.get(OPEN));
            return ActionResultType.SUCCESS;
        }
    }


    protected void playSound(@Nullable PlayerEntity player, World worldIn, BlockPos pos, boolean p_185731_4_) {
        if (p_185731_4_) {
            int i = this.material == SecretBlocks.Materials.SRM_MATERIAL_IRON ? 1037 : 1007;
            worldIn.playEvent(player, i, pos, 0);
        } else {
            int j = this.material == SecretBlocks.Materials.SRM_MATERIAL_IRON ? 1036 : 1013;
            worldIn.playEvent(player, j, pos, 0);
        }

    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isRemote) {
            boolean flag = worldIn.isBlockPowered(pos);
            if (flag != state.get(POWERED)) {
                if (state.get(OPEN) != flag) {
                    state = state.with(OPEN, flag);
                    this.playSound(null, worldIn, pos, flag);
                }
                worldIn.setBlockState(pos, state.with(POWERED, flag), 2);
                requestModelRefresh(worldIn, pos);
                if (state.get(BlockStateProperties.WATERLOGGED)) {
                    worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
                }
            }

        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        return stateIn;
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = this.getDefaultState();
        Direction direction = context.getFace();
        if (!context.replacingClickedOnBlock() && direction.getAxis().isHorizontal()) {
            blockstate = blockstate.with(HORIZONTAL_FACING, direction).with(HALF, context.getHitVec().y - (double)context.getPos().getY() > 0.5D ? Half.TOP : Half.BOTTOM);
        } else {
            blockstate = blockstate.with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite()).with(HALF, direction == Direction.UP ? Half.BOTTOM : Half.TOP);
        }

        if (context.getWorld().isBlockPowered(context.getPos())) {
            blockstate = blockstate.with(OPEN, Boolean.TRUE).with(POWERED, Boolean.TRUE);
        }

        return blockstate;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HORIZONTAL_FACING, OPEN, HALF, POWERED);
    }


    @Override
    public boolean isLadder(BlockState state, net.minecraft.world.IWorldReader world, BlockPos pos, net.minecraft.entity.LivingEntity entity) {
        if (state.get(OPEN)) {
            BlockState down = world.getBlockState(pos.down());
            if (down.getBlock() == net.minecraft.block.Blocks.LADDER)
                return down.get(LadderBlock.FACING) == state.get(HORIZONTAL_FACING);
        }
        return false;
    }

    public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
        return false;
    }
}
