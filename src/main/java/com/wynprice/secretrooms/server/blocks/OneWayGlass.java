package com.wynprice.secretrooms.server.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.server.blocks.states.OneWayGlassState;
import com.wynprice.secretrooms.server.data.SecretBlockTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SixWayBlock;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
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

    public OneWayGlass(Properties properties) {
        super(properties);

        this.setDefaultState(this.getDefaultState()
            .with(NORTH, true)
            .with(EAST, true)
            .with(SOUTH, true)
            .with(WEST, true)
            .with(UP, true)
            .with(DOWN, true)
        );
    }

    @Override
    protected BlockState createNewState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
        return new OneWayGlassState(block, propertiesToValueMap, codec);
    }

    @Override
    public boolean isSolid(BlockState state) {
        return true;
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }

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

    @Override
    public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
        boolean isGlass = state.get(SixWayBlock.FACING_TO_PROPERTY_MAP.get(side));
        boolean isInCullTag = adjacentBlockState.isIn(SecretBlockTags.ONE_WAY_GLASS_CULL);

        return super.isSideInvisible(state, adjacentBlockState, side) || (isGlass && isInCullTag);
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
}
