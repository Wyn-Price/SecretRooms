package com.wynprice.secretrooms.client.world;

import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.data.SecretData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.LevelTickAccess;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DummyIWorld implements LevelAccessor {
    private final LevelAccessor world;

    public DummyIWorld(LevelAccessor world) {
        this.world = world;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return SecretBaseBlock.getMirrorData(this.world, pos).map(SecretData::getTileEntityCache).orElseGet(() -> this.world.getBlockEntity(pos));
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        return SecretBaseBlock.getMirrorState(this.world, pos).orElseGet(() -> this.world.getBlockState(pos));
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.getBlockState(pos).getFluidState();
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        Optional<SecretData> data = SecretBaseBlock.getMirrorData(this.world, pos);
        if(data.isPresent()) {
            data.get().setBlockState(state);
            return true;
        }
        return world.setBlock(pos, state, flags, recursionLeft);
    }

    @Override
    public boolean removeBlock(BlockPos p_46951_, boolean p_46952_) {
        return false;
    }

    @Override
    public boolean destroyBlock(BlockPos p_46957_, boolean p_46958_,  Entity p_46959_, int p_46960_) {
        return false;
    }

    @Override
    public long nextSubTickCount() {
        return this.world.nextSubTickCount();
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return this.world.getBlockTicks();
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return this.world.getFluidTicks();
    }

    @Override
    public LevelData getLevelData() {
        return this.world.getLevelData();
    }

    @Override
    public DifficultyInstance getCurrentDifficultyAt(BlockPos p_46800_) {
        return this.world.getCurrentDifficultyAt(p_46800_);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public MinecraftServer getServer() {
        return this.world.getServer();
    }

    @Override
    public ChunkSource getChunkSource() {
        return this.world.getChunkSource();
    }

    @Override
    public Random getRandom() {
        return this.world.getRandom();
    }

    @Override
    public void playSound(@org.jetbrains.annotations.Nullable Player p_46775_, BlockPos p_46776_, SoundEvent p_46777_, SoundSource p_46778_, float p_46779_, float p_46780_) {
        this.world.playSound(p_46775_, p_46776_, p_46777_, p_46778_, p_46779_, p_46780_);
    }

    @Override
    public void addParticle(ParticleOptions p_46783_, double p_46784_, double p_46785_, double p_46786_, double p_46787_, double p_46788_, double p_46789_) {
        this.world.addParticle(p_46783_, p_46784_, p_46785_, p_46786_, p_46787_, p_46788_, p_46789_);
    }

    @Override
    public void levelEvent(@org.jetbrains.annotations.Nullable Player p_46771_, int p_46772_, BlockPos p_46773_, int p_46774_) {
        this.world.levelEvent(p_46771_, p_46772_, p_46773_, p_46774_);
    }

    @Override
    public void gameEvent(@org.jetbrains.annotations.Nullable Entity p_151549_, GameEvent p_151550_, BlockPos p_151551_) {
        this.world.gameEvent(p_151549_, p_151550_, p_151551_);
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.world.registryAccess();
    }

    @Override
    public float getShade(Direction p_45522_, boolean p_45523_) {
        return this.world.getShade(p_45522_, p_45523_);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.world.getLightEngine();
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
    }

    @Override
    public List<Entity> getEntities(@org.jetbrains.annotations.Nullable Entity p_45936_, AABB p_45937_, Predicate<? super Entity> p_45938_) {
        return this.world.getEntities(p_45936_, p_45937_, p_45938_);
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> p_151464_, AABB p_151465_, Predicate<? super T> p_151466_) {
        return this.world.getEntities(p_151464_, p_151465_, p_151466_);
    }

    @Override
    public List<? extends Player> players() {
        return this.world.players();
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public ChunkAccess getChunk(int p_46823_, int p_46824_, ChunkStatus p_46825_, boolean p_46826_) {
        return this.world.getChunk(p_46823_, p_46824_, p_46825_, p_46826_);
    }

    @Override
    public int getHeight(Heightmap.Types p_46827_, int p_46828_, int p_46829_) {
        return this.world.getHeight(p_46827_, p_46828_, p_46829_);
    }

    @Override
    public int getSkyDarken() {
        return this.world.getSkyDarken();
    }

    @Override
    public BiomeManager getBiomeManager() {
        return this.world.getBiomeManager();
    }

    @Override
    public Biome getUncachedNoiseBiome(int p_46809_, int p_46810_, int p_46811_) {
        return this.world.getUncachedNoiseBiome(p_46809_, p_46810_, p_46811_);
    }

    @Override
    public boolean isClientSide() {
        return this.world.isClientSide();
    }

    @Override
    public int getSeaLevel() {
        return this.world.getSeaLevel();
    }

    @Override
    public DimensionType dimensionType() {
        return this.world.dimensionType();
    }

    @Override
    public boolean isStateAtPosition(BlockPos p_46938_, Predicate<BlockState> p_46939_) {
        return this.world.isStateAtPosition(p_46938_, p_46939_);
    }

    @Override
    public boolean isFluidAtPosition(BlockPos p_151584_, Predicate<FluidState> p_151585_) {
        return this.world.isFluidAtPosition(p_151584_, p_151585_);
    }
}
