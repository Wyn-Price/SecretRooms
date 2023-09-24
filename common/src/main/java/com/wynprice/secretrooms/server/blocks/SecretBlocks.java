package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.platform.SecretRoomsServices;
import com.wynprice.secretrooms.registry.RegistryHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Supplier;

public class SecretBlocks {

    public static final RegistryHolder<Block> REGISTRY = SecretRoomsServices.PLATFORM.createBlockRegistryHolder();

    private static final Block.Properties SRM_PROPS = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).noOcclusion();

    private static final Block.Properties SRM_METAL_PROPS = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).noCollission();

    public static final Supplier<Block> GHOST_BLOCK = REGISTRY.register("ghost_block", () -> new GhostBlock(SRM_PROPS));
    public static final Supplier<Block> SECRET_STAIRS = REGISTRY.register("secret_stairs", () -> new SecretStairs(SRM_PROPS));
    public static final Supplier<Block> SECRET_LEVER = REGISTRY.register("secret_lever", () -> new SecretLever(SRM_PROPS));
    public static final Supplier<Block> SECRET_REDSTONE = REGISTRY.register("secret_redstone", () -> new SecretRedstone(SRM_PROPS));
    public static final Supplier<Block> ONE_WAY_GLASS = REGISTRY.register("one_way_glass", () -> new OneWayGlass(SRM_PROPS));
    public static final Supplier<Block> SECRET_WOODEN_BUTTON = REGISTRY.register("secret_wooden_button", () -> new SecretButton(SRM_PROPS, true));
    public static final Supplier<Block> SECRET_STONE_BUTTON = REGISTRY.register("secret_stone_button", () -> new SecretButton(SRM_PROPS, false));
    public static final Supplier<Block> TORCH_LEVER = REGISTRY.register("torch_lever", () -> new TorchLever(BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel(s -> 14).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> WALL_TORCH_LEVER = REGISTRY.register("wall_torch_lever", () -> new WallTorchLever(BlockBehaviour.Properties.of().noCollission().instabreak().lightLevel(s -> 14).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY)));
    public static final Supplier<Block> SECRET_PRESSURE_PLATE = REGISTRY.register("secret_pressure_plate", () -> new SecretPressurePlate(SRM_PROPS, entity -> true));
    public static final Supplier<Block> SECRET_PLAYER_PRESSURE_PLATE = REGISTRY.register("secret_player_pressure_plate", () -> new SecretPressurePlate(SRM_PROPS, entity -> entity instanceof Player));
    public static final Supplier<Block> SECRET_DOOR = REGISTRY.register("secret_door", () -> new SecretDoor(SRM_PROPS));
    public static final Supplier<Block> SECRET_IRON_DOOR = REGISTRY.register("secret_iron_door", () -> new SecretDoor(SRM_METAL_PROPS));
    public static final Supplier<Block> SECRET_CHEST = REGISTRY.register("secret_chest", () -> new SecretChest(SRM_PROPS));
    public static final Supplier<Block> SECRET_TRAPDOOR = REGISTRY.register("secret_trapdoor", () -> new SecretTrapdoor(SRM_PROPS));
    public static final Supplier<Block> SECRET_IRON_TRAPDOOR = REGISTRY.register("secret_iron_trapdoor", () -> new SecretTrapdoor(SRM_METAL_PROPS));
    public static final Supplier<Block> SECRET_TRAPPED_CHEST = REGISTRY.register("secret_trapped_chest", () -> new SecretTrappedChest(SRM_PROPS));
    public static final Supplier<Block> SECRET_GATE = REGISTRY.register("secret_gate", () -> new SecretGateBlock(SRM_PROPS));
    public static final Supplier<Block> SECRET_DUMMY_BLOCK = REGISTRY.register("secret_dummy_block", () -> new SecretBaseBlock(SRM_PROPS.dropsLike(Blocks.AIR)));
    public static final Supplier<Block> SECRET_DAYLIGHT_DETECTOR = REGISTRY.register("secret_daylight_detector", () -> new SecretDaylightDetector(SRM_PROPS));
    public static final Supplier<Block> SECRET_OBSERVER = REGISTRY.register("secret_observer", () -> new SecretObserver(SRM_PROPS));
    public static final Supplier<Block> SECRET_CLAMBER = REGISTRY.register("secret_clamber", () -> new SecretClamber(SRM_PROPS));

}
