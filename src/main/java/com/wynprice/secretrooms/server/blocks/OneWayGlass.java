package com.wynprice.secretrooms.server.blocks;

import com.google.common.collect.Maps;
import com.wynprice.secretrooms.server.blocks.states.OneWayGlassState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class OneWayGlass extends SecretBaseBlock {

    private static final BooleanProperty NORTH = SixWayBlock.NORTH;
    private static final BooleanProperty EAST = SixWayBlock.EAST;
    private static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
    private static final BooleanProperty WEST = SixWayBlock.WEST;
    private static final BooleanProperty UP = SixWayBlock.UP;
    private static final BooleanProperty DOWN = SixWayBlock.DOWN;

    private static final VoxelShape NORTH_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 16, 2);
    private static final VoxelShape EAST_SHAPE = Block.makeCuboidShape(14, 0, 0, 16, 16, 16);
    private static final VoxelShape SOUTH_SHAPE = Block.makeCuboidShape(0, 0, 14, 16, 16, 16);
    private static final VoxelShape WEST_SHAPE = Block.makeCuboidShape(0, 0, 0, 2, 16, 16);
    private static final VoxelShape UP_SHAPE = Block.makeCuboidShape(0, 14, 0, 16, 16, 16);
    private static final VoxelShape DOWN_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 2, 16);

    private static final Map<Direction, VoxelShape> FACING_TO_SHAPE_MAP = Util.make(Maps.newEnumMap(Direction.class), map -> {
        map.put(Direction.NORTH, NORTH_SHAPE);
        map.put(Direction.EAST, EAST_SHAPE);
        map.put(Direction.SOUTH, SOUTH_SHAPE);
        map.put(Direction.WEST, WEST_SHAPE);
        map.put(Direction.UP, UP_SHAPE);
        map.put(Direction.DOWN, DOWN_SHAPE);
    });

    //This is needed as BlockState#func_215702_a doesn't allow for directional sensitive calls on the Block
    private final StateContainer<Block, BlockState> oneWayGlassStateContainer;

    public OneWayGlass(Properties properties) {
        super(properties);

        StateContainer.Builder<Block, BlockState> builder = new StateContainer.Builder<>(this);
        this.fillStateContainer(builder);
        //this.oneWayGlassStateContainer = builder.func_235882_a_(OneWayGlassState::new, OneWayGlassState::new);
        this.oneWayGlassStateContainer = null;

        /*this.setDefaultState(this.oneWayGlassStateContainer.getBaseState()
            .with(NORTH, true)
            .with(EAST, true)
            .with(SOUTH, true)
            .with(WEST, true)
            .with(UP, true)
            .with(DOWN, true)
        );*/
    }

    @Override
    public StateContainer<Block, BlockState> getStateContainer() {
        return this.oneWayGlassStateContainer != null ? this.oneWayGlassStateContainer : super.getStateContainer();
    }

    @Override
    public boolean isSolid(BlockState state) {
        return true;
    }

    /*@Override
    public boolean func_220074_n(BlockState state) {
        return true;
    }*/

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(player.getHeldItem(handIn).isEmpty()) {
            worldIn.setBlockState(pos, state.func_235896_a_(SixWayBlock.FACING_TO_PROPERTY_MAP.get(hit.getFace())), 3);
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if(tileEntity != null) {
                tileEntity.requestModelDataUpdate();
            }
            return ActionResultType.SUCCESS;
        }
        return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    @Override
    public int getOpacity(BlockState state, IBlockReader world, BlockPos pos) {
        return 0;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos) {
        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockPos offFace = context.replacingClickedOnBlock() ? context.getPos() : context.getPos().offset(context.getFace().getOpposite());
        BlockState state = context.getWorld().getBlockState(offFace);
        if(state.getBlock() == this) {
            return state;
        }
        return super.getStateForPlacement(context).with(SixWayBlock.FACING_TO_PROPERTY_MAP.get(context.getNearestLookingDirection().getOpposite()), false);
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        TileEntity tileEntity = worldIn.getTileEntity(currentPos);
        if(tileEntity != null && tileEntity.getWorld().isRemote) {
            tileEntity.requestModelDataUpdate();
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        Optional<BlockState> mirror = getMirrorState(worldIn, pos);
        return state.get(SOLID) && mirror.isPresent() ?
                VoxelShapes.or(VoxelShapes.empty(),
                        Arrays.stream(Direction.values())
                                .filter(value -> !state.get(SixWayBlock.FACING_TO_PROPERTY_MAP.get(value)) && Block.hasSolidSideOnTop(worldIn, pos.offset(value)))
                                .map(FACING_TO_SHAPE_MAP::get)
                                .toArray(VoxelShape[]::new)
                )
                : VoxelShapes.empty();
    }
}
