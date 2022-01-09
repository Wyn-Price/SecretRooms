package com.wynprice.secretrooms.server.blocks;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import com.wynprice.secretrooms.client.world.DelegateWorld;
import com.wynprice.secretrooms.client.world.DummyIWorld;
import com.wynprice.secretrooms.server.blocks.states.SecretBaseState;
import com.wynprice.secretrooms.server.data.SecretData;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.IBlockRenderProperties;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SecretBaseBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    public static final BooleanProperty SOLID = BooleanProperty.create("solid");

    private final StateDefinition<Block, BlockState> stateContainerOverride;

    public SecretBaseBlock(Properties properties) {
        super(properties.dynamicShape());
        StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
        this.createBlockStateDefinition(builder);
        this.stateContainerOverride = builder.create(Block::defaultBlockState, this::createNewState);
        this.registerDefaultState(this.getStateDefinition().any()
            .setValue(SOLID, false)
            .setValue(BlockStateProperties.WATERLOGGED, false)
        );
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    protected BlockState createNewState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertiesToValueMap, MapCodec<BlockState> codec) {
        return new SecretBaseState(block, propertiesToValueMap, codec);
    }

    @Override
    public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        boolean value = getValue(worldIn, pos,
            (m, reader, p) -> m.getBlock() instanceof SimpleWaterloggedBlock && ((SimpleWaterloggedBlock) m.getBlock()).canPlaceLiquid(reader, pos, m, fluidStateIn.getType()),
            () -> false
        );
        if(value) {
            boolean fluid = SimpleWaterloggedBlock.super.placeLiquid(worldIn, pos, state, fluidStateIn);
            if(fluid) {
                getMirrorData(worldIn, pos).ifPresent(d -> {
                    BlockState mirror = d.getBlockState();
                    if(mirror.hasProperty(BlockStateProperties.WATERLOGGED)) {
                        d.setBlockState(mirror.setValue(BlockStateProperties.WATERLOGGED, true));
                    }
                });
            }
            return fluid;
        } else {
            return false;
        }

    }

    @Override
    public ItemStack pickupBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        ItemStack itemStack = SimpleWaterloggedBlock.super.pickupBlock(worldIn, pos, state);
        if(!itemStack.isEmpty()) {
            getMirrorData(worldIn, pos).ifPresent(d -> {
                BlockState mirror = d.getBlockState();
                if(mirror.hasProperty(BlockStateProperties.WATERLOGGED)) {
                    d.setBlockState(mirror.setValue(BlockStateProperties.WATERLOGGED, false));
                }
            });
        }
        return itemStack;
    }

    @Override
    public StateDefinition<Block, BlockState> getStateDefinition() {
        return this.stateContainerOverride;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    private int calls = 0;

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        getMirrorData(worldIn, currentPos).ifPresent(data -> {
            BlockState mirror = data.getBlockState();

            DummyIWorld world = new DummyIWorld(worldIn);
            if(!(facingState.getBlock() instanceof SecretBaseBlock) && calls != 3) {
                calls++;
                BlockState facingNewState = facingState.updateShape(facing.getOpposite(), mirror, world, facingPos, currentPos);
                calls--;
                Block.updateOrDestroy(facingState, facingNewState, worldIn, facingPos, 3);
            }

            BlockState newState = mirror.updateShape(facing, world.getBlockState(facingPos), world, currentPos, facingPos);
            if(newState != mirror) {
                data.setBlockState(newState);
                BlockEntity tileEntity = world.getBlockEntity(currentPos);
                data.setTileEntityNBT(tileEntity != null ? tileEntity.serializeNBT() : null);
            }
        });
        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    //We need to make sure stuff like fences are connected properly.
    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        getMirrorState(worldIn, pos).ifPresent(mirror -> mirror.updateNeighbourShapes(new DummyIWorld(worldIn), pos, 3));
        super.setPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
        return getValue(world, pos, (mirror, reader, pos1) -> mirror.getSoundType(world, pos1, entity), () -> super.getSoundType(state, world, pos, entity));
    }

    @Nullable
    @Override
    public BlockPathTypes getAiPathNodeType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity) {
        return getValue(world, pos, (mirror, reader, pos1) -> mirror.getBlockPathType(reader, pos1, entity), () -> super.getAiPathNodeType(state, world, pos, entity));
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return getValue(world, pos, (mirror, reader, pos1) -> mirror.isPathfindable(world, pos, type), () -> super.isPathfindable(state, world, pos, type));
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return getValue(world, pos, (mirror, reader, pos1) -> mirror.getShadeBrightness(world, pos), () -> super.getShadeBrightness(state, world, pos));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return getValue(worldIn, pos, (mirror, reader, pos1) -> mirror.getShape(reader, pos1, context), () -> super.getShape(state, worldIn, pos, context));
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return getValue(worldIn, pos, BlockStateBase::getBlockSupportShape, () -> super.getBlockSupportShape(state, worldIn, pos));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return getValue(worldIn, pos, (mirror, reader, pos1) -> mirror.getCollisionShape(reader, pos1, context), () -> super.getCollisionShape(state, worldIn, pos, context));
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return getValue(worldIn, pos, BlockState::getBlockSupportShape, () -> super.getOcclusionShape(state, worldIn, pos));
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return getValue(reader, pos, (m, w, p) -> m.getVisualShape(w, p, context), () -> super.getVisualShape(state, reader, pos, context));
    }

    @Override
    public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return getValue(worldIn, pos, BlockState::getInteractionShape, () -> super.getInteractionShape(state, worldIn, pos));
    }

    @Override
    public float getFriction(BlockState state, LevelReader world, BlockPos pos, @Nullable Entity entity) {
        return getValue(world, pos, (m, w, p) -> m.getFriction(world, p, entity), () -> super.getFriction(state, world, pos, entity));
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        int result = getValue(world, pos, BlockState::getLightEmission, () -> super.getLightEmission(state, world, pos));
        StackTraceElement element = Thread.currentThread().getStackTrace()[3];
        //This is needed so we can control AO. Try to remove this asap
        if ("net.minecraft.client.renderer.block.ModelBlockRenderer".equals(element.getClassName()) && "tesselateBlock".equals(element.getMethodName())) {
            Optional<BlockState> mirrorState = getMirrorState(world, pos);
            if(mirrorState.isPresent()) {
                Boolean isAoModel = DistExecutor.callWhenOn(Dist.CLIENT, () -> () ->
                        Minecraft.getInstance().getBlockRenderer().getBlockModel(mirrorState.get()).useAmbientOcclusion());
                if(isAoModel != null) {
                    return result == 0 && isAoModel ? 0 : 1;
                }
            }
        }
        return result;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return getValue(world, pos, BlockState::getLightBlock, () -> super.getLightBlock(state, world, pos));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return getValue(world, pos, BlockState::propagatesSkylightDown, () -> super.propagatesSkylightDown(state, world, pos));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SOLID, BlockStateProperties.WATERLOGGED);
    }

    //Entity#createRunningParticles
    @Override
    public boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity) {
        Optional<BlockState> mirrorState = getMirrorState(world, pos);
        if(mirrorState.isPresent()) {
            BlockState blockstate = mirrorState.get();
            if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                Vec3 vec3d = entity.getDeltaMovement();
                world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate),
                        entity.getX()+ (world.random.nextFloat() - 0.5D) * entity.getBbWidth(),
                        entity.getY() + 0.1D,
                        entity.getZ() + (world.random.nextFloat() - 0.5D) * entity.getBbWidth(),

                        vec3d.x * -4.0D, 1.5D, vec3d.z * -4.0D);
            }
        }
        return true;
    }

    @Override
    public void initializeClient(Consumer<IBlockRenderProperties> consumer) {
        consumer.accept(new IBlockRenderProperties() {
            @Override
            //ParticleEngine#crack
            public boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
                if(target instanceof BlockHitResult) {
                    BlockPos pos = ((BlockHitResult) target).getBlockPos();
                    Optional<BlockState> mirrorState = getMirrorState(level, pos);
                    if(mirrorState.isPresent()) {
                        BlockState blockstate = mirrorState.get();
                        Direction side = ((BlockHitResult) target).getDirection();
                        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                            int x = pos.getX();
                            int y = pos.getY();
                            int z = pos.getZ();
                            AABB axisalignedbb = blockstate.getShape(level, pos).bounds();
                            double xPos = x + level.random.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.2F) + 0.1F + axisalignedbb.minX;
                            double yPos = y + level.random.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.2F) + 0.1F + axisalignedbb.minY;
                            double zPos = z + level.random.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.2F) + 0.1F + axisalignedbb.minZ;

                            switch (side) {
                                case UP -> yPos = y + axisalignedbb.maxY + 0.1F;
                                case DOWN -> yPos = y + axisalignedbb.minY - 0.1F;
                                case NORTH -> zPos = z + axisalignedbb.minZ - 0.1F;
                                case SOUTH -> zPos = z + axisalignedbb.maxZ + 0.1F;
                                case WEST -> xPos = x + axisalignedbb.minX - 0.1F;
                                case EAST -> xPos = x + axisalignedbb.maxX + 0.1F;
                            }

                            Minecraft.getInstance().particleEngine.add(
                                new TerrainParticle((ClientLevel) level, xPos, yPos, zPos, 0.0D, 0.0D, 0.0D, blockstate, pos)
                                    .setPower(0.2F)
                                    .scale(0.6F)
                            );
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean addDestroyEffects(BlockState state, Level level, BlockPos pos, ParticleEngine manager) {
                Optional<BlockState> mirrorState = getMirrorState(level, pos);
                if (mirrorState.isPresent()) {
                    if(mirrorState.get().isAir()) {
                        return false;
                    }
                    BlockState blockstate = mirrorState.get();
                    VoxelShape voxelshape = blockstate.getShape(level, pos);
                    voxelshape.forAllBoxes((x1, y1, z1, x2, y2, z2) -> {
                        double xDelta = Math.min(1.0D, x2 - x1);
                        double yDelta = Math.min(1.0D, y2 - y1);
                        double zDelta = Math.min(1.0D, z2 - z1);
                        int xAmount = Math.max(2, Mth.ceil( xDelta / 0.25D));
                        int yAmount = Math.max(2, Mth.ceil( yDelta / 0.25D));
                        int zAmount = Math.max(2, Mth.ceil( zDelta / 0.25D));

                        for(int x = 0; x < xAmount; ++x) {
                            for(int y = 0; y < yAmount; ++y) {
                                for(int z = 0; z < zAmount; ++z) {
                                    double dx = (x + 0.5D) / xAmount;
                                    double dy = (y + 0.5D) / yAmount;
                                    double dz = (z + 0.5D) / zAmount;
                                    double xPos = dx * xDelta + x1;
                                    double yPos = dy * yDelta + y1;
                                    double zPos = dz * zDelta + z1;
                                    Minecraft.getInstance().particleEngine.add(
                                        new TerrainParticle((ClientLevel) level,
                                            pos.getX() + xPos,pos.getY() + yPos, pos.getZ() + zPos,
                                            dx - 0.5D, dy - 0.5D,dz - 0.5D, blockstate, pos)
                                    );
                                }
                            }
                        }
                    });
                }
                return true;
            }
        });
    }


    @Override
    public boolean addLandingEffects(BlockState state1, ServerLevel worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        getMirrorState(worldserver, pos).ifPresent(blockState -> worldserver.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), entity.getX(), entity.getY(), entity.getZ(), numberOfParticles, 0.0D, 0.0D, 0.0D, 0.15F));
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SecretTileEntity(pos, state);
    }

    public void applyExtraModelData(BlockGetter world, BlockPos pos, BlockState state, ModelDataMap.Builder builder) {
    }

    public Boolean getSolidValue() {
        return null;
    }

    public BlockState getPlaceState(BlockGetter wold, BlockPos placedOnPos, BlockState placedOn, BlockState fallback) {
        boolean waterlogged = placedOn.hasProperty(BlockStateProperties.WATERLOGGED) && placedOn.getValue(BlockStateProperties.WATERLOGGED);
        return fallback
            .setValue(SOLID, placedOn.canOcclude())
            .setValue(BlockStateProperties.WATERLOGGED, waterlogged);
    }

    public static <T> T getValue(BlockGetter world, BlockPos pos, StateFunction<T> function, Supplier<T> defaultValue) {
        return getMirrorState(world, pos)
            .map(DelegateWorld.createFunction(world,
                (reader, mirror) -> function.getValue(mirror, reader, pos)
            )).orElseGet(defaultValue);
    }

    public static Optional<BlockState> getMirrorState(BlockGetter world, BlockPos pos) {
        return getMirrorData(world, pos).map(SecretData::getBlockState);
    }

    public static Optional<SecretData> getMirrorData(BlockGetter world, BlockPos pos) {
        if(world == null || pos == null) {
            return Optional.empty();
        }

        // Fixes a deadlock that occurs when there is a SecretBlock in a spawn chunk (see https://github.com/Wyn-Price/SecretRooms/pull/53)
        BlockState blockState = null;
        if (world instanceof Level) {
            LevelChunk chunk = ((Level) world).getChunkSource().getChunkNow(pos.getX() >> 4, pos.getZ() >> 4);
            if (chunk != null) {
                blockState = chunk.getBlockState(pos);
            }
        } else {
            blockState = world.getBlockState(pos);
        }

        BlockEntity te = world.getBlockEntity(pos);
        return blockState != null && blockState.getBlock() instanceof SecretBaseBlock && te instanceof SecretTileEntity ?
            Optional.of(((SecretTileEntity) te).getData()) : Optional.empty();
    }

    public static void requestModelRefresh(BlockGetter world, BlockPos pos) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity != null) {
            tileEntity.requestModelDataUpdate();
        }
    }

    public interface StateFunction<T> {
        T getValue(BlockState mirror, BlockGetter reader, BlockPos pos);
    }

}
