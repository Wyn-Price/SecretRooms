package com.wynprice.secretrooms.server.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.server.blocks.states.OneWayGlassState;
import com.wynprice.secretrooms.server.data.SecretBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class OneWayGlass extends SecretBaseBlock {

    private static final BooleanProperty NORTH = PipeBlock.NORTH;
    private static final BooleanProperty EAST = PipeBlock.EAST;
    private static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    private static final BooleanProperty WEST = PipeBlock.WEST;
    private static final BooleanProperty UP = PipeBlock.UP;
    private static final BooleanProperty DOWN = PipeBlock.DOWN;

    public OneWayGlass(Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState()
            .setValue(NORTH, true)
            .setValue(EAST, true)
            .setValue(SOUTH, true)
            .setValue(WEST, true)
            .setValue(UP, true)
            .setValue(DOWN, true)
        );
    }

    @Override
    protected BlockState createNewState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
        return new OneWayGlassState(block, propertiesToValueMap, codec);
    }

    @Override
    public Boolean getSolidValue() {
        return true;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(player.getItemInHand(handIn).isEmpty()) {
            worldIn.setBlock(pos, state.cycle(PipeBlock.PROPERTY_BY_DIRECTION.get(hit.getDirection())), 3);
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if(tileEntity != null) {
                tileEntity.requestModelDataUpdate();
            }
            return InteractionResult.SUCCESS;
        }
        return super.use(state, worldIn, pos, player, handIn, hit);
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
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        boolean isGlass = state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(side));
        boolean isInCullTag = adjacentBlockState.is(SecretBlockTags.ONE_WAY_GLASS_CULL);

        return super.skipRendering(state, adjacentBlockState, side) || (isGlass && isInCullTag);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos offFace = context.replacingClickedOnBlock() ? context.getClickedPos() : context.getClickedPos().relative(context.getClickedFace().getOpposite());
        BlockState state = context.getLevel().getBlockState(offFace);
        if(state.getBlock() == this) {
            return state;
        }
        return super.getStateForPlacement(context).setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(context.getNearestLookingDirection().getOpposite()), false);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        BlockEntity tileEntity = worldIn.getBlockEntity(currentPos);
        if(tileEntity != null && tileEntity.getLevel().isClientSide) {
            tileEntity.requestModelDataUpdate();
        }
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }
}
