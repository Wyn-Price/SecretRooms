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
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        Optional<SecretData> data = SecretBaseBlock.getMirrorData(this.world, pos);
        if(data.isPresent()) {
            data.get().setBlockState(state);
            return true;
        }
        return world.setBlock(pos, state, flags, recursionLeft);
    }

    public long dayTime() {
        return this.world.dayTime();
    }

    public TickList<Block> getBlockTicks() {
        return this.world.getBlockTicks();
    }

    public TickList<Fluid> getLiquidTicks() {
        return this.world.getLiquidTicks();
    }

    public LevelData getLevelData() {
        return this.world.getLevelData();
    }

    public DifficultyInstance getCurrentDifficultyAt(BlockPos p_46800_) {
        return this.world.getCurrentDifficultyAt(p_46800_);
    }

    public MinecraftServer getServer() {
        return this.world.getServer();
    }

    public Difficulty getDifficulty() {
        return this.world.getDifficulty();
    }

    public ChunkSource getChunkSource() {
        return this.world.getChunkSource();
    }

    public boolean hasChunk(int p_46794_, int p_46795_) {
        return this.world.hasChunk(p_46794_, p_46795_);
    }

    public Random getRandom() {
        return this.world.getRandom();
    }

    public void blockUpdated(BlockPos p_46781_, Block p_46782_) {
        this.world.blockUpdated(p_46781_, p_46782_);
    }

    public void playSound(Player p_46775_, BlockPos p_46776_, SoundEvent p_46777_, SoundSource p_46778_, float p_46779_, float p_46780_) {
        this.world.playSound(p_46775_, p_46776_, p_46777_, p_46778_, p_46779_, p_46780_);
    }

    public void addParticle(ParticleOptions p_46783_, double p_46784_, double p_46785_, double p_46786_, double p_46787_, double p_46788_, double p_46789_) {
        this.world.addParticle(p_46783_, p_46784_, p_46785_, p_46786_, p_46787_, p_46788_, p_46789_);
    }

    public void levelEvent(Player p_46771_, int p_46772_, BlockPos p_46773_, int p_46774_) {
        this.world.levelEvent(p_46771_, p_46772_, p_46773_, p_46774_);
    }

    public int getLogicalHeight() {
        return this.world.getLogicalHeight();
    }

    public void levelEvent(int p_46797_, BlockPos p_46798_, int p_46799_) {
        this.world.levelEvent(p_46797_, p_46798_, p_46799_);
    }

    public void gameEvent(Entity p_151549_, GameEvent p_151550_, BlockPos p_151551_) {
        this.world.gameEvent(p_151549_, p_151550_, p_151551_);
    }

    public void gameEvent(GameEvent p_151556_, BlockPos p_151557_) {
        this.world.gameEvent(p_151556_, p_151557_);
    }

    public void gameEvent(GameEvent p_151553_, Entity p_151554_) {
        this.world.gameEvent(p_151553_, p_151554_);
    }

    public void gameEvent(Entity p_151546_, GameEvent p_151547_, Entity p_151548_) {
        this.world.gameEvent(p_151546_, p_151547_, p_151548_);
    }

    public <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos p_151452_, BlockEntityType<T> p_151453_) {
        return this.world.getBlockEntity(p_151452_, p_151453_);
    }

    public Stream<VoxelShape> getEntityCollisions(Entity p_45834_, AABB p_45835_, Predicate<Entity> p_45836_) {
        return this.world.getEntityCollisions(p_45834_, p_45835_, p_45836_);
    }

    public boolean isUnobstructed(Entity p_45828_, VoxelShape p_45829_) {
        return this.world.isUnobstructed(p_45828_, p_45829_);
    }

    public BlockPos getHeightmapPos(Heightmap.Types p_45831_, BlockPos p_45832_) {
        return this.world.getHeightmapPos(p_45831_, p_45832_);
    }

    public RegistryAccess registryAccess() {
        return this.world.registryAccess();
    }

    public Optional<ResourceKey<Biome>> getBiomeName(BlockPos p_45838_) {
        return this.world.getBiomeName(p_45838_);
    }

    public List<Entity> getEntities(Entity p_45936_, AABB p_45937_, Predicate<? super Entity> p_45938_) {
        return this.world.getEntities(p_45936_, p_45937_, p_45938_);
    }

    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> p_151464_, AABB p_151465_, Predicate<? super T> p_151466_) {
        return this.world.getEntities(p_151464_, p_151465_, p_151466_);
    }

    public <T extends Entity> List<T> getEntitiesOfClass(Class<T> p_45979_, AABB p_45980_, Predicate<? super T> p_45981_) {
        return this.world.getEntitiesOfClass(p_45979_, p_45980_, p_45981_);
    }

    public List<? extends Player> players() {
        return this.world.players();
    }

    public List<Entity> getEntities(Entity p_45934_, AABB p_45935_) {
        return this.world.getEntities(p_45934_, p_45935_);
    }

    public <T extends Entity> List<T> getEntitiesOfClass(Class<T> p_45977_, AABB p_45978_) {
        return this.world.getEntitiesOfClass(p_45977_, p_45978_);
    }

    public Player getNearestPlayer(double p_45919_, double p_45920_, double p_45921_, double p_45922_, Predicate<Entity> p_45923_) {
        return this.world.getNearestPlayer(p_45919_, p_45920_, p_45921_, p_45922_, p_45923_);
    }

    public Player getNearestPlayer(Entity p_45931_, double p_45932_) {
        return this.world.getNearestPlayer(p_45931_, p_45932_);
    }

    public Player getNearestPlayer(double p_45925_, double p_45926_, double p_45927_, double p_45928_, boolean p_45929_) {
        return this.world.getNearestPlayer(p_45925_, p_45926_, p_45927_, p_45928_, p_45929_);
    }

    public boolean hasNearbyAlivePlayer(double p_45915_, double p_45916_, double p_45917_, double p_45918_) {
        return this.world.hasNearbyAlivePlayer(p_45915_, p_45916_, p_45917_, p_45918_);
    }

    public Player getNearestPlayer(TargetingConditions p_45947_, LivingEntity p_45948_) {
        return this.world.getNearestPlayer(p_45947_, p_45948_);
    }

    public Player getNearestPlayer(TargetingConditions p_45950_, LivingEntity p_45951_, double p_45952_, double p_45953_, double p_45954_) {
        return this.world.getNearestPlayer(p_45950_, p_45951_, p_45952_, p_45953_, p_45954_);
    }

    public Player getNearestPlayer(TargetingConditions p_45942_, double p_45943_, double p_45944_, double p_45945_) {
        return this.world.getNearestPlayer(p_45942_, p_45943_, p_45944_, p_45945_);
    }

    public <T extends LivingEntity> T getNearestEntity(Class<? extends T> p_45964_, TargetingConditions p_45965_, LivingEntity p_45966_, double p_45967_, double p_45968_, double p_45969_, AABB p_45970_) {
        return this.world.getNearestEntity(p_45964_, p_45965_, p_45966_, p_45967_, p_45968_, p_45969_, p_45970_);
    }

    public <T extends LivingEntity> T getNearestEntity(List<? extends T> p_45983_, TargetingConditions p_45984_, LivingEntity p_45985_, double p_45986_, double p_45987_, double p_45988_) {
        return this.world.getNearestEntity(p_45983_, p_45984_, p_45985_, p_45986_, p_45987_, p_45988_);
    }

    public List<Player> getNearbyPlayers(TargetingConditions p_45956_, LivingEntity p_45957_, AABB p_45958_) {
        return this.world.getNearbyPlayers(p_45956_, p_45957_, p_45958_);
    }

    public <T extends LivingEntity> List<T> getNearbyEntities(Class<T> p_45972_, TargetingConditions p_45973_, LivingEntity p_45974_, AABB p_45975_) {
        return this.world.getNearbyEntities(p_45972_, p_45973_, p_45974_, p_45975_);
    }

    public Player getPlayerByUUID(UUID p_46004_) {
        return this.world.getPlayerByUUID(p_46004_);
    }

    public ChunkAccess getChunk(int p_46823_, int p_46824_, ChunkStatus p_46825_, boolean p_46826_) {
        return this.world.getChunk(p_46823_, p_46824_, p_46825_, p_46826_);
    }

    public int getHeight(Heightmap.Types p_46827_, int p_46828_, int p_46829_) {
        return this.world.getHeight(p_46827_, p_46828_, p_46829_);
    }

    public int getSkyDarken() {
        return this.world.getSkyDarken();
    }

    public BiomeManager getBiomeManager() {
        return this.world.getBiomeManager();
    }

    public Biome getBiome(BlockPos p_46858_) {
        return this.world.getBiome(p_46858_);
    }

    public Stream<BlockState> getBlockStatesIfLoaded(AABB p_46848_) {
        return this.world.getBlockStatesIfLoaded(p_46848_);
    }

    public int getBlockTint(BlockPos p_46836_, ColorResolver p_46837_) {
        return this.world.getBlockTint(p_46836_, p_46837_);
    }

    public Biome getNoiseBiome(int p_46841_, int p_46842_, int p_46843_) {
        return this.world.getNoiseBiome(p_46841_, p_46842_, p_46843_);
    }

    public Biome getUncachedNoiseBiome(int p_46809_, int p_46810_, int p_46811_) {
        return this.world.getUncachedNoiseBiome(p_46809_, p_46810_, p_46811_);
    }

    public boolean isClientSide() {
        return this.world.isClientSide();
    }

    public int getSeaLevel() {
        return this.world.getSeaLevel();
    }

    public DimensionType dimensionType() {
        return this.world.dimensionType();
    }

    public int getMinBuildHeight() {
        return this.world.getMinBuildHeight();
    }

    public int getHeight() {
        return this.world.getHeight();
    }

    public boolean isEmptyBlock(BlockPos p_46860_) {
        return this.world.isEmptyBlock(p_46860_);
    }

    public boolean canSeeSkyFromBelowWater(BlockPos p_46862_) {
        return this.world.canSeeSkyFromBelowWater(p_46862_);
    }

    public float getBrightness(BlockPos p_46864_) {
        return this.world.getBrightness(p_46864_);
    }

    public int getDirectSignal(BlockPos p_46853_, Direction p_46854_) {
        return this.world.getDirectSignal(p_46853_, p_46854_);
    }

    public ChunkAccess getChunk(BlockPos p_46866_) {
        return this.world.getChunk(p_46866_);
    }

    public ChunkAccess getChunk(int p_46807_, int p_46808_) {
        return this.world.getChunk(p_46807_, p_46808_);
    }

    public ChunkAccess getChunk(int p_46820_, int p_46821_, ChunkStatus p_46822_) {
        return this.world.getChunk(p_46820_, p_46821_, p_46822_);
    }

    public BlockGetter getChunkForCollisions(int p_46845_, int p_46846_) {
        return this.world.getChunkForCollisions(p_46845_, p_46846_);
    }

    public boolean isWaterAt(BlockPos p_46802_) {
        return this.world.isWaterAt(p_46802_);
    }

    public boolean containsAnyLiquid(AABB p_46856_) {
        return this.world.containsAnyLiquid(p_46856_);
    }

    public int getMaxLocalRawBrightness(BlockPos p_46804_) {
        return this.world.getMaxLocalRawBrightness(p_46804_);
    }

    public int getMaxLocalRawBrightness(BlockPos p_46850_, int p_46851_) {
        return this.world.getMaxLocalRawBrightness(p_46850_, p_46851_);
    }

    public boolean hasChunkAt(int p_151578_, int p_151579_) {
        return this.world.hasChunkAt(p_151578_, p_151579_);
    }

    public boolean hasChunkAt(BlockPos p_46806_) {
        return this.world.hasChunkAt(p_46806_);
    }

    public boolean isAreaLoaded(BlockPos center, int range) {
        return this.world.isAreaLoaded(center, range);
    }

    public boolean hasChunksAt(BlockPos p_46833_, BlockPos p_46834_) {
        return this.world.hasChunksAt(p_46833_, p_46834_);
    }

    public boolean hasChunksAt(int p_46813_, int p_46814_, int p_46815_, int p_46816_, int p_46817_, int p_46818_) {
        return this.world.hasChunksAt(p_46813_, p_46814_, p_46815_, p_46816_, p_46817_, p_46818_);
    }

    public boolean hasChunksAt(int p_151573_, int p_151574_, int p_151575_, int p_151576_) {
        return this.world.hasChunksAt(p_151573_, p_151574_, p_151575_, p_151576_);
    }

    public float getShade(Direction p_45522_, boolean p_45523_) {
        return this.world.getShade(p_45522_, p_45523_);
    }

    public LevelLightEngine getLightEngine() {
        return this.world.getLightEngine();
    }

    public int getBrightness(LightLayer p_45518_, BlockPos p_45519_) {
        return this.world.getBrightness(p_45518_, p_45519_);
    }

    public int getRawBrightness(BlockPos p_45525_, int p_45526_) {
        return this.world.getRawBrightness(p_45525_, p_45526_);
    }

    public boolean canSeeSky(BlockPos p_45528_) {
        return this.world.canSeeSky(p_45528_);
    }

    public FluidState getFluidState(BlockPos p_45569_) {
        return this.world.getFluidState(p_45569_);
    }

    public int getLightEmission(BlockPos p_45572_) {
        return this.world.getLightEmission(p_45572_);
    }

    public int getMaxLightLevel() {
        return this.world.getMaxLightLevel();
    }

    public Stream<BlockState> getBlockStates(AABB p_45557_) {
        return this.world.getBlockStates(p_45557_);
    }

    public BlockHitResult isBlockInLine(ClipBlockStateContext p_151354_) {
        return this.world.isBlockInLine(p_151354_);
    }

    public BlockHitResult clip(ClipContext p_45548_) {
        return this.world.clip(p_45548_);
    }

    public BlockHitResult clipWithInteractionOverride(Vec3 p_45559_, Vec3 p_45560_, BlockPos p_45561_, VoxelShape p_45562_, BlockState p_45563_) {
        return this.world.clipWithInteractionOverride(p_45559_, p_45560_, p_45561_, p_45562_, p_45563_);
    }

    public double getBlockFloorHeight(VoxelShape p_45565_, Supplier<VoxelShape> p_45566_) {
        return this.world.getBlockFloorHeight(p_45565_, p_45566_);
    }

    public double getBlockFloorHeight(BlockPos p_45574_) {
        return this.world.getBlockFloorHeight(p_45574_);
    }

    public int getMaxBuildHeight() {
        return this.world.getMaxBuildHeight();
    }

    public int getSectionsCount() {
        return this.world.getSectionsCount();
    }

    public int getMinSection() {
        return this.world.getMinSection();
    }

    public int getMaxSection() {
        return this.world.getMaxSection();
    }

    public boolean isOutsideBuildHeight(BlockPos p_151571_) {
        return this.world.isOutsideBuildHeight(p_151571_);
    }

    public boolean isOutsideBuildHeight(int p_151563_) {
        return this.world.isOutsideBuildHeight(p_151563_);
    }

    public int getSectionIndex(int p_151565_) {
        return this.world.getSectionIndex(p_151565_);
    }

    public int getSectionIndexFromSectionY(int p_151567_) {
        return this.world.getSectionIndexFromSectionY(p_151567_);
    }

    public int getSectionYFromSectionIndex(int p_151569_) {
        return this.world.getSectionYFromSectionIndex(p_151569_);
    }

    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
    }

    public boolean isUnobstructed(BlockState p_45753_, BlockPos p_45754_, CollisionContext p_45755_) {
        return this.world.isUnobstructed(p_45753_, p_45754_, p_45755_);
    }

    public boolean isUnobstructed(Entity p_45785_) {
        return this.world.isUnobstructed(p_45785_);
    }

    public boolean noCollision(AABB p_45773_) {
        return this.world.noCollision(p_45773_);
    }

    public boolean noCollision(Entity p_45787_) {
        return this.world.noCollision(p_45787_);
    }

    public boolean noCollision(Entity p_45757_, AABB p_45758_) {
        return this.world.noCollision(p_45757_, p_45758_);
    }

    public boolean noCollision(Entity p_45769_, AABB p_45770_, Predicate<Entity> p_45771_) {
        return this.world.noCollision(p_45769_, p_45770_, p_45771_);
    }

    public Stream<VoxelShape> getCollisions(Entity p_45781_, AABB p_45782_, Predicate<Entity> p_45783_) {
        return this.world.getCollisions(p_45781_, p_45782_, p_45783_);
    }

    public Stream<VoxelShape> getBlockCollisions(Entity p_45762_, AABB p_45763_) {
        return this.world.getBlockCollisions(p_45762_, p_45763_);
    }

    public boolean hasBlockCollision(Entity p_151415_, AABB p_151416_, BiPredicate<BlockState, BlockPos> p_151417_) {
        return this.world.hasBlockCollision(p_151415_, p_151416_, p_151417_);
    }

    public Stream<VoxelShape> getBlockCollisions(Entity p_45765_, AABB p_45766_, BiPredicate<BlockState, BlockPos> p_45767_) {
        return this.world.getBlockCollisions(p_45765_, p_45766_, p_45767_);
    }

    public Optional<Vec3> findFreePosition(Entity p_151419_, VoxelShape p_151420_, Vec3 p_151421_, double p_151422_, double p_151423_, double p_151424_) {
        return this.world.findFreePosition(p_151419_, p_151420_, p_151421_, p_151422_, p_151423_, p_151424_);
    }

    public Biome getPrimaryBiome(ChunkPos p_151755_) {
        return this.world.getPrimaryBiome(p_151755_);
    }

    public boolean isStateAtPosition(BlockPos p_46938_, Predicate<BlockState> p_46939_) {
        return this.world.isStateAtPosition(p_46938_, p_46939_);
    }

    public boolean isFluidAtPosition(BlockPos p_151584_, Predicate<FluidState> p_151585_) {
        return this.world.isFluidAtPosition(p_151584_, p_151585_);
    }

    public boolean setBlock(BlockPos p_46944_, BlockState p_46945_, int p_46946_) {
        return this.world.setBlock(p_46944_, p_46945_, p_46946_);
    }

    public boolean removeBlock(BlockPos p_46951_, boolean p_46952_) {
        return this.world.removeBlock(p_46951_, p_46952_);
    }

    public boolean destroyBlock(BlockPos p_46962_, boolean p_46963_) {
        return this.world.destroyBlock(p_46962_, p_46963_);
    }

    public boolean destroyBlock(BlockPos p_46954_, boolean p_46955_, Entity p_46956_) {
        return this.world.destroyBlock(p_46954_, p_46955_, p_46956_);
    }

    public boolean destroyBlock(BlockPos p_46957_, boolean p_46958_, Entity p_46959_, int p_46960_) {
        return this.world.destroyBlock(p_46957_, p_46958_, p_46959_, p_46960_);
    }

    public boolean addFreshEntity(Entity p_46964_) {
        return this.world.addFreshEntity(p_46964_);
    }

    public float getMoonBrightness() {
        return this.world.getMoonBrightness();
    }

    public float getTimeOfDay(float p_46943_) {
        return this.world.getTimeOfDay(p_46943_);
    }

    public int getMoonPhase() {
        return this.world.getMoonPhase();
    }
}
