package com.wynprice.secretrooms.server.blocks;

import com.sun.javafx.geom.Vec3d;
import com.wynprice.secretrooms.server.data.SecretData;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public class SecretBaseBlock extends Block {

    public static final BooleanProperty SOLID = BooleanProperty.create("solid");

    public SecretBaseBlock(Properties properties) {
        super(properties.variableOpacity());
        this.setDefaultState(this.getStateContainer().getBaseState().with(SOLID, false));
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        return getValue(world, pos, (mirror, reader, pos1) -> mirror.getSoundType(reader, pos1, entity), () -> super.getSoundType(state, world, pos, entity));
    }

    @Nullable
    @Override
    public PathNodeType getAiPathNodeType(BlockState state, IBlockReader world, BlockPos pos, @Nullable MobEntity entity) {
        return getValue(world, pos, (mirror, reader, pos1) -> mirror.getAiPathNodeType(reader, pos1, entity), () -> super.getAiPathNodeType(state, world, pos, entity));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return getValue(worldIn, pos, (mirror, reader, pos1) -> mirror.getShape(reader, pos1, context), () -> super.getShape(state, worldIn, pos, context));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return getValue(worldIn, pos, (mirror, reader, pos1) -> mirror.getCollisionShape(reader, pos1, context), () -> super.getCollisionShape(state, worldIn, pos, context));
    }

    @Override
    public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return getValue(worldIn, pos, BlockState::getRenderShape, () -> super.getRenderShape(state, worldIn, pos));
    }

    /*@Override
    public VoxelShape getRaytraceShape(BlockState state, IBlockReader world, BlockPos pos) {
        return getValue(world, pos, BlockState::getRaytraceShape, () -> super.getRaytraceShape(state, world, pos));
    }*/

    /*@Nullable
    @Override
    public RayTraceResult getRayTraceResult(BlockState state, World world, BlockPos pos, Vec3d start, Vec3d end, RayTraceResult original) {
        return getValue(world, pos, (mirror, reader, pos1) -> mirror.getBlock().getRayTraceResult(mirror, world, pos, start, end, original), () -> super.getRayTraceResult(state, world, pos, start, end, original));
    }

    @Override
    public float getBlockHardness(BlockState state, IBlockReader world, BlockPos pos) {
        return getValue(world, pos, BlockState::getBlockHardness, () -> super.getBlockHardness(state, world, pos));
    }*/


    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        int result = getValue(world, pos, BlockState::getLightValue, () -> super.getLightValue(state, world, pos));
        //This is needed so we can control AO. Try to remove this asap
        if ("net.minecraft.client.renderer.BlockModelRenderer".equals(Thread.currentThread().getStackTrace()[3].getClassName())) {
            Optional<BlockState> mirrorState = getMirrorState(world, pos);
            if(mirrorState.isPresent()) {
                Boolean isAoModel = DistExecutor.callWhenOn(Dist.CLIENT, () -> () ->
                        Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(mirrorState.get()).isAmbientOcclusion());
                if(isAoModel != null) {
                    return result == 0 && isAoModel ? 0 : 1;
                }
            }
        }
        return result;
    }

    @Override
    public int getOpacity(BlockState state, IBlockReader world, BlockPos pos) {
        return getValue(world, pos, BlockState::getOpacity, () -> super.getOpacity(state, world, pos));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, IBlockReader world, BlockPos pos) {
        return getValue(world, pos, BlockState::propagatesSkylightDown, () -> super.propagatesSkylightDown(state, world, pos));
    }

    /*@Override
    public boolean isNormalCube(IBlockReader world, BlockPos pos) {
        return getValue(world, pos, BlockState::isNormalCube, () -> super.getDefaultState().isNormalCube(world, pos));
    }*/



    /*@Override
    public boolean canRenderInLayer(BlockState state, BlockRenderLayer layer) {
        return true;
    }*/

    public boolean isSolid(BlockState state) {
        return state.get(SOLID);
    }



    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(SOLID);
    }

    //Entity#createRunningParticles
    @Override
    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        Optional<BlockState> mirrorState = getMirrorState(world, pos);
        if(mirrorState.isPresent()) {
            BlockState blockstate = mirrorState.get();
            if (blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
                Vector3d vec3d = entity.getMotion();
                world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate),
                        entity.getPosX()+ (world.rand.nextFloat() - 0.5D) * entity.getWidth(),
                        entity.getPosY() + 0.1D,
                        entity.getPosZ() + (world.rand.nextFloat() - 0.5D) * entity.getWidth(),

                        vec3d.x * -4.0D, 1.5D, vec3d.z * -4.0D);
            }
        }
        return true;
    }
    //ParticleManager#addBlockHitEffects

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if(target instanceof BlockRayTraceResult) {
            BlockPos pos = ((BlockRayTraceResult) target).getPos();
            Optional<BlockState> mirrorState = getMirrorState(world, pos);
            if(mirrorState.isPresent()) {
                BlockState blockstate = mirrorState.get();
                Direction side = ((BlockRayTraceResult) target).getFace();
                if (blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
                    int x = pos.getX();
                    int y = pos.getY();
                    int z = pos.getZ();
                    AxisAlignedBB axisalignedbb = blockstate.getShape(world, pos).getBoundingBox();
                    double xPos = x + world.rand.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.2F) + 0.1F + axisalignedbb.minX;
                    double yPos = y + world.rand.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.2F) + 0.1F + axisalignedbb.minY;
                    double zPos = z + world.rand.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.2F) + 0.1F + axisalignedbb.minZ;

                    switch (side) {
                        case UP: yPos = y + axisalignedbb.maxY + 0.1F; break;
                        case DOWN: yPos = y + axisalignedbb.minY - 0.1F; break;
                        case NORTH: zPos = z + axisalignedbb.minZ - 0.1F; break;
                        case SOUTH: zPos = z + axisalignedbb.maxZ + 0.1F; break;
                        case WEST: xPos = x + axisalignedbb.minX - 0.1F; break;
                        case EAST: xPos = x + axisalignedbb.maxX + 0.1F; break;
                    }

                    Minecraft.getInstance().particles.addEffect(
                            new DiggingParticle((ClientWorld) world, xPos, yPos, zPos, 0.0D, 0.0D, 0.0D, blockstate)
                                    .setBlockPos(pos)
                                    .multiplyVelocity(0.2F)
                                    .multiplyParticleScaleBy(0.6F)
                    );
                }
            }
        }
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addDestroyEffects(BlockState stateIn, World world, BlockPos pos, ParticleManager manager) {
        Optional<BlockState> mirrorState = getMirrorState(world, pos);
        if (mirrorState.isPresent()) {
            if(mirrorState.get().isAir(world, pos)) {
                return false;
            }
            BlockState state = mirrorState.get();
            VoxelShape voxelshape = state.getShape(world, pos);
            voxelshape.forEachBox((x1, y1, z1, x2, y2, z2) -> {
                double xDelta = Math.min(1.0D, x2 - x1);
                double yDelta = Math.min(1.0D, y2 - y1);
                double zDelta = Math.min(1.0D, z2 - z1);
                int xAmount = Math.max(2, MathHelper.ceil( xDelta / 0.25D));
                int yAmount = Math.max(2, MathHelper.ceil( yDelta / 0.25D));
                int zAmount = Math.max(2, MathHelper.ceil( zDelta / 0.25D));

                for(int x = 0; x < xAmount; ++x) {
                    for(int y = 0; y < yAmount; ++y) {
                        for(int z = 0; z < zAmount; ++z) {
                            double dx = (x + 0.5D) / xAmount;
                            double dy = (y + 0.5D) / yAmount;
                            double dz = (z + 0.5D) / zAmount;
                            double xPos = dx * xDelta + x1;
                            double yPos = dy * yDelta + y1;
                            double zPos = dz * zDelta + z1;
                            Minecraft.getInstance().particles.addEffect(
                                    new DiggingParticle((ClientWorld) world,
                                            pos.getX() + xPos,pos.getY() + yPos, pos.getZ() + zPos,
                                            dx - 0.5D, dy - 0.5D,dz - 0.5D, state)
                                            .setBlockPos(pos)
                            );
                        }
                    }
                }
            });
        }
        return true;
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerWorld worldserver, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        Optional<BlockState> mirrorState = getMirrorState(worldserver, pos);
        if(mirrorState.isPresent()) {
            worldserver.spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, mirrorState.get()), entity.getPosX(), entity.getPosY(), entity.getPosZ(), numberOfParticles, 0.0D, 0.0D, 0.0D, 0.15F);
        }
        return true;
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

    public void applyExtraModelData(IBlockReader world, BlockPos pos, BlockState state, ModelDataMap.Builder builder) {
    }

    public BlockState getPlaceState(IBlockReader wold, BlockPos placedOnPos, BlockState placedOn) {
        return this.getDefaultState().with(SOLID, placedOn.isSolid());
    }

    public static <T, W extends IBlockReader> T getValue(W reader, BlockPos pos, StateFunction<T, W> function, Supplier<T> defaultValue) {
        return getMirrorState(reader, pos).map(mirror -> function.getValue(mirror, reader, pos)).orElseGet(defaultValue);
    }

    public static Optional<BlockState> getMirrorState(IBlockReader world, BlockPos pos) {
        return getMirrorData(world, pos).map(SecretData::getBlockState);
    }

    public static Optional<SecretData> getMirrorData(IBlockReader world, BlockPos pos) {
        if(world == null || pos == null) {
            return Optional.empty();
        }
        TileEntity te = world.getTileEntity(pos);
        return world.getBlockState(pos).getBlock() instanceof SecretBaseBlock && te instanceof SecretTileEntity ?
            Optional.of(((SecretTileEntity) te).getData()) : Optional.empty();
    }

    public static void requestModelRefresh(IBlockReader world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity != null) {
            tileEntity.requestModelDataUpdate();
        }
    }

    private interface StateFunction<T, W extends IBlockReader> {
        T getValue(BlockState mirror, W reader, BlockPos pos);
    }

}
