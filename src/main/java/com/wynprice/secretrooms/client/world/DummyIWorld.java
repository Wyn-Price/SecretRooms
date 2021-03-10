package com.wynprice.secretrooms.client.world;

import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.*;
import java.util.stream.Stream;

public class DummyIWorld implements IWorld {
    private final IWorld world;

    public DummyIWorld(IWorld world) {
        this.world = world;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return SecretBaseBlock.getMirrorData(this.world, pos).map(SecretData::getTileEntityCache).orElseGet(() -> this.world.getTileEntity(pos));
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return SecretBaseBlock.getMirrorState(this.world, pos).orElseGet(() -> this.world.getBlockState(pos));
    }

    @Override
    public long func_241851_ab() {
        return world.func_241851_ab();
    }

    @Override
    public ITickList<Block> getPendingBlockTicks() {
        return world.getPendingBlockTicks();
    }

    @Override
    public ITickList<Fluid> getPendingFluidTicks() {
        return world.getPendingFluidTicks();
    }

    @Override
    public IWorldInfo getWorldInfo() {
        return world.getWorldInfo();
    }

    @Override
    public DifficultyInstance getDifficultyForLocation(BlockPos pos) {
        return world.getDifficultyForLocation(pos);
    }

    @Override
    public Difficulty getDifficulty() {
        return world.getDifficulty();
    }

    @Override
    public AbstractChunkProvider getChunkProvider() {
        return world.getChunkProvider();
    }

    @Override
    public boolean chunkExists(int chunkX, int chunkZ) {
        return world.chunkExists(chunkX, chunkZ);
    }

    @Override
    public Random getRandom() {
        return world.getRandom();
    }

    @Override
    public void func_230547_a_(BlockPos p_230547_1_, Block p_230547_2_) {
        world.func_230547_a_(p_230547_1_, p_230547_2_);
    }

    @Override
    public void playSound(PlayerEntity player, BlockPos pos, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
        world.playSound(player, pos, soundIn, category, volume, pitch);
    }

    @Override
    public void addParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        world.addParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void playEvent(PlayerEntity player, int type, BlockPos pos, int data) {
        world.playEvent(player, type, pos, data);
    }

    @Override
    public int func_234938_ad_() {
        return world.func_234938_ad_();
    }

    @Override
    public void playEvent(int type, BlockPos pos, int data) {
        world.playEvent(type, pos, data);
    }

    @Override
    public Stream<VoxelShape> func_230318_c_(Entity p_230318_1_, AxisAlignedBB p_230318_2_, Predicate<Entity> p_230318_3_) {
        return world.func_230318_c_(p_230318_1_, p_230318_2_, p_230318_3_);
    }

    @Override
    public boolean checkNoEntityCollision(Entity entityIn, VoxelShape shape) {
        return world.checkNoEntityCollision(entityIn, shape);
    }

    @Override
    public BlockPos getHeight(Heightmap.Type heightmapType, BlockPos pos) {
        return world.getHeight(heightmapType, pos);
    }

    @Override
    public DynamicRegistries func_241828_r() {
        return world.func_241828_r();
    }

    @Override
    public Optional<RegistryKey<Biome>> func_242406_i(BlockPos p_242406_1_) {
        return world.func_242406_i(p_242406_1_);
    }

    @Override
    public List<Entity> getEntitiesInAABBexcluding(Entity entityIn, AxisAlignedBB boundingBox, Predicate<? super Entity> predicate) {
        return world.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb, Predicate<? super T> filter) {
        return world.getEntitiesWithinAABB(clazz, aabb, filter);
    }

    @Override
    public <T extends Entity> List<T> getLoadedEntitiesWithinAABB(Class<? extends T> p_225316_1_, AxisAlignedBB p_225316_2_, Predicate<? super T> p_225316_3_) {
        return world.getLoadedEntitiesWithinAABB(p_225316_1_, p_225316_2_, p_225316_3_);
    }

    @Override
    public List<? extends PlayerEntity> getPlayers() {
        return world.getPlayers();
    }

    @Override
    public List<Entity> getEntitiesWithinAABBExcludingEntity(Entity entityIn, AxisAlignedBB bb) {
        return world.getEntitiesWithinAABBExcludingEntity(entityIn, bb);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> p_217357_1_, AxisAlignedBB p_217357_2_) {
        return world.getEntitiesWithinAABB(p_217357_1_, p_217357_2_);
    }

    @Override
    public <T extends Entity> List<T> getLoadedEntitiesWithinAABB(Class<? extends T> p_225317_1_, AxisAlignedBB p_225317_2_) {
        return world.getLoadedEntitiesWithinAABB(p_225317_1_, p_225317_2_);
    }

    @Override
    @Nullable
    public PlayerEntity getClosestPlayer(double x, double y, double z, double distance, Predicate<Entity> predicate) {
        return world.getClosestPlayer(x, y, z, distance, predicate);
    }

    @Override
    @Nullable
    public PlayerEntity getClosestPlayer(Entity entityIn, double distance) {
        return world.getClosestPlayer(entityIn, distance);
    }

    @Override
    @Nullable
    public PlayerEntity getClosestPlayer(double x, double y, double z, double distance, boolean creativePlayers) {
        return world.getClosestPlayer(x, y, z, distance, creativePlayers);
    }

    @Override
    public boolean isPlayerWithin(double x, double y, double z, double distance) {
        return world.isPlayerWithin(x, y, z, distance);
    }

    @Override
    @Nullable
    public PlayerEntity getClosestPlayer(EntityPredicate predicate, LivingEntity target) {
        return world.getClosestPlayer(predicate, target);
    }

    @Override
    @Nullable
    public PlayerEntity getClosestPlayer(EntityPredicate predicate, LivingEntity target, double p_217372_3_, double p_217372_5_, double p_217372_7_) {
        return world.getClosestPlayer(predicate, target, p_217372_3_, p_217372_5_, p_217372_7_);
    }

    @Override
    @Nullable
    public PlayerEntity getClosestPlayer(EntityPredicate predicate, double x, double y, double z) {
        return world.getClosestPlayer(predicate, x, y, z);
    }

    @Override
    @Nullable
    public <T extends LivingEntity> T getClosestEntityWithinAABB(Class<? extends T> entityClazz, EntityPredicate p_217360_2_, LivingEntity target, double x, double y, double z, AxisAlignedBB boundingBox) {
        return world.getClosestEntityWithinAABB(entityClazz, p_217360_2_, target, x, y, z, boundingBox);
    }

    @Override
    @Nullable
    public <T extends LivingEntity> T func_225318_b(Class<? extends T> p_225318_1_, EntityPredicate p_225318_2_, LivingEntity p_225318_3_, double p_225318_4_, double p_225318_6_, double p_225318_8_, AxisAlignedBB p_225318_10_) {
        return world.func_225318_b(p_225318_1_, p_225318_2_, p_225318_3_, p_225318_4_, p_225318_6_, p_225318_8_, p_225318_10_);
    }

    @Override
    @Nullable
    public <T extends LivingEntity> T getClosestEntity(List<? extends T> entities, EntityPredicate predicate, LivingEntity target, double x, double y, double z) {
        return world.getClosestEntity(entities, predicate, target, x, y, z);
    }

    @Override
    public List<PlayerEntity> getTargettablePlayersWithinAABB(EntityPredicate predicate, LivingEntity target, AxisAlignedBB box) {
        return world.getTargettablePlayersWithinAABB(predicate, target, box);
    }

    @Override
    public <T extends LivingEntity> List<T> getTargettableEntitiesWithinAABB(Class<? extends T> p_217374_1_, EntityPredicate p_217374_2_, LivingEntity p_217374_3_, AxisAlignedBB p_217374_4_) {
        return world.getTargettableEntitiesWithinAABB(p_217374_1_, p_217374_2_, p_217374_3_, p_217374_4_);
    }

    @Override
    @Nullable
    public PlayerEntity getPlayerByUuid(UUID uniqueIdIn) {
        return world.getPlayerByUuid(uniqueIdIn);
    }

    @Override
    @Nullable
    public IChunk getChunk(int x, int z, ChunkStatus requiredStatus, boolean nonnull) {
        return world.getChunk(x, z, requiredStatus, nonnull);
    }

    @Override
    public int getHeight(Heightmap.Type heightmapType, int x, int z) {
        return world.getHeight(heightmapType, x, z);
    }

    @Override
    public int getSkylightSubtracted() {
        return world.getSkylightSubtracted();
    }

    @Override
    public BiomeManager getBiomeManager() {
        return world.getBiomeManager();
    }

    @Override
    public Biome getBiome(BlockPos pos) {
        return world.getBiome(pos);
    }

    @Override
    public Stream<BlockState> getStatesInArea(AxisAlignedBB aabb) {
        return world.getStatesInArea(aabb);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getBlockColor(BlockPos blockPosIn, ColorResolver colorResolverIn) {
        return world.getBlockColor(blockPosIn, colorResolverIn);
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        return world.getNoiseBiome(x, y, z);
    }

    @Override
    public Biome getNoiseBiomeRaw(int x, int y, int z) {
        return world.getNoiseBiomeRaw(x, y, z);
    }

    @Override
    public boolean isRemote() {
        return world.isRemote();
    }

    @Override
    @Deprecated
    public int getSeaLevel() {
        return world.getSeaLevel();
    }

    @Override
    public DimensionType getDimensionType() {
        return world.getDimensionType();
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        return world.isAirBlock(pos);
    }

    @Override
    public boolean canBlockSeeSky(BlockPos pos) {
        return world.canBlockSeeSky(pos);
    }

    @Override
    @Deprecated
    public float getBrightness(BlockPos pos) {
        return world.getBrightness(pos);
    }

    @Override
    public int getStrongPower(BlockPos pos, Direction direction) {
        return world.getStrongPower(pos, direction);
    }

    @Override
    public IChunk getChunk(BlockPos pos) {
        return world.getChunk(pos);
    }

    @Override
    public IChunk getChunk(int chunkX, int chunkZ) {
        return world.getChunk(chunkX, chunkZ);
    }

    @Override
    public IChunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus) {
        return world.getChunk(chunkX, chunkZ, requiredStatus);
    }

    @Override
    @Nullable
    public IBlockReader getBlockReader(int chunkX, int chunkZ) {
        return world.getBlockReader(chunkX, chunkZ);
    }

    @Override
    public boolean hasWater(BlockPos pos) {
        return world.hasWater(pos);
    }

    @Override
    public boolean containsAnyLiquid(AxisAlignedBB bb) {
        return world.containsAnyLiquid(bb);
    }

    @Override
    public int getLight(BlockPos pos) {
        return world.getLight(pos);
    }

    @Override
    public int getNeighborAwareLightSubtracted(BlockPos pos, int amount) {
        return world.getNeighborAwareLightSubtracted(pos, amount);
    }

    @Override
    @Deprecated
    public boolean isBlockLoaded(BlockPos pos) {
        return world.isBlockLoaded(pos);
    }

    @Override
    public boolean isAreaLoaded(BlockPos center, int range) {
        return world.isAreaLoaded(center, range);
    }

    @Override
    @Deprecated
    public boolean isAreaLoaded(BlockPos from, BlockPos to) {
        return world.isAreaLoaded(from, to);
    }

    @Override
    @Deprecated
    public boolean isAreaLoaded(int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        return world.isAreaLoaded(fromX, fromY, fromZ, toX, toY, toZ);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float func_230487_a_(Direction p_230487_1_, boolean p_230487_2_) {
        return world.func_230487_a_(p_230487_1_, p_230487_2_);
    }

    @Override
    public WorldLightManager getLightManager() {
        return world.getLightManager();
    }

    @Override
    public int getLightFor(LightType lightTypeIn, BlockPos blockPosIn) {
        return world.getLightFor(lightTypeIn, blockPosIn);
    }

    @Override
    public int getLightSubtracted(BlockPos blockPosIn, int amount) {
        return world.getLightSubtracted(blockPosIn, amount);
    }

    @Override
    public boolean canSeeSky(BlockPos blockPosIn) {
        return world.canSeeSky(blockPosIn);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return world.getFluidState(pos);
    }

    @Override
    public int getLightValue(BlockPos pos) {
        return world.getLightValue(pos);
    }

    @Override
    public int getMaxLightLevel() {
        return world.getMaxLightLevel();
    }

    @Override
    public int getHeight() {
        return world.getHeight();
    }

    @Override
    public Stream<BlockState> func_234853_a_(AxisAlignedBB p_234853_1_) {
        return world.func_234853_a_(p_234853_1_);
    }

    @Override
    public BlockRayTraceResult rayTraceBlocks(RayTraceContext context) {
        return world.rayTraceBlocks(context);
    }

    @Override
    @Nullable
    public BlockRayTraceResult rayTraceBlocks(Vector3d startVec, Vector3d endVec, BlockPos pos, VoxelShape shape, BlockState state) {
        return world.rayTraceBlocks(startVec, endVec, pos, shape, state);
    }

    @Override
    public double func_242402_a(VoxelShape p_242402_1_, Supplier<VoxelShape> p_242402_2_) {
        return world.func_242402_a(p_242402_1_, p_242402_2_);
    }

    @Override
    public double func_242403_h(BlockPos p_242403_1_) {
        return world.func_242403_h(p_242403_1_);
    }

    public static <T> T doRayTrace(RayTraceContext context, BiFunction<RayTraceContext, BlockPos, T> rayTracer, Function<RayTraceContext, T> missFactory) {
        return IBlockReader.doRayTrace(context, rayTracer, missFactory);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return world.getWorldBorder();
    }

    @Override
    public boolean placedBlockCollides(BlockState state, BlockPos pos, ISelectionContext context) {
        return world.placedBlockCollides(state, pos, context);
    }

    @Override
    public boolean checkNoEntityCollision(Entity entity) {
        return world.checkNoEntityCollision(entity);
    }

    @Override
    public boolean hasNoCollisions(AxisAlignedBB aabb) {
        return world.hasNoCollisions(aabb);
    }

    @Override
    public boolean hasNoCollisions(Entity entity) {
        return world.hasNoCollisions(entity);
    }

    @Override
    public boolean hasNoCollisions(Entity entity, AxisAlignedBB aabb) {
        return world.hasNoCollisions(entity, aabb);
    }

    @Override
    public boolean hasNoCollisions(Entity entity, AxisAlignedBB aabb, Predicate<Entity> entityPredicate) {
        return world.hasNoCollisions(entity, aabb, entityPredicate);
    }

    @Override
    public Stream<VoxelShape> func_234867_d_(Entity entity, AxisAlignedBB aabb, Predicate<Entity> entityPredicate) {
        return world.func_234867_d_(entity, aabb, entityPredicate);
    }

    @Override
    public Stream<VoxelShape> getCollisionShapes(Entity entity, AxisAlignedBB aabb) {
        return world.getCollisionShapes(entity, aabb);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean func_242405_a(Entity p_242405_1_, AxisAlignedBB p_242405_2_, BiPredicate<BlockState, BlockPos> p_242405_3_) {
        return world.func_242405_a(p_242405_1_, p_242405_2_, p_242405_3_);
    }

    @Override
    public Stream<VoxelShape> func_241457_a_(Entity entity, AxisAlignedBB aabb, BiPredicate<BlockState, BlockPos> statePosPredicate) {
        return world.func_241457_a_(entity, aabb, statePosPredicate);
    }

    @Override
    public boolean hasBlockState(BlockPos pos, Predicate<BlockState> state) {
        return world.hasBlockState(pos, state);
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        return world.setBlockState(pos, state, flags, recursionLeft);
    }

    @Override
    public boolean setBlockState(BlockPos pos, BlockState newState, int flags) {
        return world.setBlockState(pos, newState, flags);
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean isMoving) {
        return world.removeBlock(pos, isMoving);
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean dropBlock) {
        return world.destroyBlock(pos, dropBlock);
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean dropBlock, Entity entity) {
        return world.destroyBlock(pos, dropBlock, entity);
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean dropBlock, Entity entity, int recursionLeft) {
        return world.destroyBlock(pos, dropBlock, entity, recursionLeft);
    }

    @Override
    public boolean addEntity(Entity entityIn) {
        return world.addEntity(entityIn);
    }

    @Override
    public float getMoonFactor() {
        return world.getMoonFactor();
    }

    @Override
    public float func_242415_f(float p_242415_1_) {
        return world.func_242415_f(p_242415_1_);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getMoonPhase() {
        return world.getMoonPhase();
    }
}
