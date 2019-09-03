package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.client.model.providers.SecretQuadProvider;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IEnviromentBlockReader;

import javax.annotation.Nullable;
import java.util.Optional;

public class SecretBaseBlock extends Block {

    public static final BooleanProperty SOLID = BooleanProperty.create("solid");

    private final SecretQuadProvider provider;

    public SecretBaseBlock(Properties properties) {
        this(properties, null);
    }

    public SecretBaseBlock(Properties properties, SecretQuadProvider provider) {
        super(properties.variableOpacity());
        this.provider = provider;
        this.setDefaultState(this.getStateContainer().getBaseState().with(SOLID, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SOLID);
    }

    @Override
    public int getLightValue(BlockState state, IEnviromentBlockReader world, BlockPos pos) {
        return getMirrorState(world, pos).map(mirror -> mirror.getLightValue(world, pos)).orElse(super.getLightValue(state, world, pos));
    }

    @Override
    public int getOpacity(BlockState state, IBlockReader world, BlockPos pos) {
        return getMirrorState(world, pos).map(mirror -> mirror.getOpacity(world, pos)).orElse(super.getOpacity(state, world, pos));
    }

    @Override
    public float func_220080_a(BlockState state, IBlockReader world, BlockPos pos) {
        return getMirrorState(world, pos).map(mirror -> mirror.func_215703_d(world, pos)).orElse(super.func_220080_a(state, world, pos));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos) {
        return getMirrorState(world, pos).map(mirror -> mirror.propagatesSkylightDown(world, pos)).orElse(super.propagatesSkylightDown(state, world, pos));
    }

    @Override
    public boolean isNormalCube(BlockState state, IBlockReader world, BlockPos pos) {
        return getMirrorState(world, pos).map(mirror -> mirror.isNormalCube(world, pos)).orElse(super.isNormalCube(state, world, pos));

    }

    @Override
    public boolean isSolid(BlockState state) {
        return state.get(SOLID);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SecretTileEntity();
    }

    @Nullable
    public SecretQuadProvider getProvider(IBlockReader world, BlockPos pos, BlockState state) {
        return this.provider;
    }

    public static Optional<BlockState> getMirrorState(IBlockReader world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return world.getBlockState(pos).getBlock() instanceof SecretBaseBlock && te instanceof SecretTileEntity ?
                Optional.of(((SecretTileEntity) te).getData().getBlockState()) : Optional.empty();
    }
}
