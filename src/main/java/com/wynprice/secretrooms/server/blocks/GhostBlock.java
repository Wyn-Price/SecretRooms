package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.client.model.providers.GhostBlockProvider;
import com.wynprice.secretrooms.client.model.providers.SecretQuadProvider;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class GhostBlock extends SecretBaseBlock {
    public GhostBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Nullable
    @Override
    public SecretQuadProvider getProvider(IBlockReader world, BlockPos pos, BlockState state) {
        return getMirrorState(world, pos).map(BlockState::isSolid).orElse(true) ? GhostBlockProvider.GHOST_BLOCK : SecretQuadProvider.INSTANCE;
    }

}
