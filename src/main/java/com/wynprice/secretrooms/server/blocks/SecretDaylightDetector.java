package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.server.tileentity.SecretDaylightDetectorTileEntity;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DaylightDetectorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DaylightDetectorBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.Objects;

public class SecretDaylightDetector extends SecretBaseBlock {

    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    public static final BooleanProperty INVERTED = BlockStateProperties.INVERTED;

    public SecretDaylightDetector(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(POWER, 0).setValue(INVERTED, false));
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(POWER);
    }

    public static void updatePower(BlockState state, Level world, BlockPos pos) {
        if (world.dimensionType().hasSkyLight()) {

            int light = world.getBrightness(LightLayer.SKY, pos) - world.getSkyDarken();
            for (Direction value : Direction.values()) {
                light = Math.max(light, world.getBrightness(LightLayer.SKY, pos.relative(value)) - world.getSkyDarken());
            }
            float sunAngle = world.getSunAngle(1.0F);
            boolean flag = state.getValue(INVERTED);
            if (flag) {
                light = 15 - light;
            } else if (light > 0) {
                float f1 = sunAngle < (float)Math.PI ? 0.0F : ((float)Math.PI * 2F);
                sunAngle = sunAngle + (f1 - sunAngle) * 0.2F;
                light = Math.round((float)light * Mth.cos(sunAngle));
            }

            light = Mth.clamp(light, 0, 15);
            if (state.getValue(POWER) != light) {
                world.setBlock(pos, state.setValue(POWER, light), 3);
            }

        }
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if (player.mayBuild()) {
            if (worldIn.isClientSide) {
                return InteractionResult.SUCCESS;
            } else {
                BlockState blockstate = state.cycle(INVERTED);
                worldIn.setBlock(pos, blockstate, 4);
                updatePower(blockstate, worldIn, pos);
                return InteractionResult.SUCCESS;
            }
        } else {
            return super.use(state, worldIn, pos, player, handIn, hit);
        }
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, LevelReader world, BlockPos pos, Direction side) {
        return false;
    }

    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SecretDaylightDetectorTileEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return !level.isClientSide && level.dimensionType().hasSkyLight() ? BaseEntityBlock.createTickerHelper(type, SecretTileEntities.SECRET_DAYLIGHT_DETECTOR_TILE_ENTITY.get(), SecretDaylightDetector::tickEntity) : null;
    }

    private static void tickEntity(Level level, BlockPos pos, BlockState state, SecretDaylightDetectorTileEntity t) {
        if (level.getGameTime() % 20L == 0L) {
            updatePower(state, level, pos);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWER, INVERTED);
    }
}
