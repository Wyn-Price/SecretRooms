package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.server.tileentity.SecretDaylightDetectorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SecretDaylightDetector extends SecretBaseBlock {

    public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;

    public SecretDaylightDetector(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(POWER, 0).with(INVERTED, false));
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        return blockState.get(POWER);
    }

    public static void updatePower(BlockState state, World world, BlockPos pos) {
        if (world.getDimensionType().hasSkyLight()) {

            int light = world.getLightFor(LightType.SKY, pos) - world.getSkylightSubtracted();
            for (Direction value : Direction.values()) {
                light = Math.max(light, world.getLightFor(LightType.SKY, pos.offset(value)) - world.getSkylightSubtracted());
            }
            float sunAngle = world.getCelestialAngleRadians(1.0F);
            boolean flag = state.get(INVERTED);
            if (flag) {
                light = 15 - light;
            } else if (light > 0) {
                float f1 = sunAngle < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
                sunAngle = sunAngle + (f1 - sunAngle) * 0.2F;
                light = Math.round((float)light * MathHelper.cos(sunAngle));
            }

            light = MathHelper.clamp(light, 0, 15);
            if (state.get(POWER) != light) {
                world.setBlockState(pos, state.with(POWER, light), 3);
            }

        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (player.isAllowEdit()) {
            if (worldIn.isRemote) {
                return ActionResultType.SUCCESS;
            } else {
                BlockState blockstate = state.func_235896_a_(INVERTED);
                worldIn.setBlockState(pos, blockstate, 4);
                updatePower(blockstate, worldIn, pos);
                return ActionResultType.SUCCESS;
            }
        } else {
            return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
        }
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public boolean canProvidePower(BlockState state) {
        return true;
    }


    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SecretDaylightDetectorTileEntity();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(POWER, INVERTED);
    }
}
