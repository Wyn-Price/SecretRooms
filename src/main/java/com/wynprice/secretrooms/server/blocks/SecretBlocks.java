package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.SecretRooms6;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class SecretBlocks {

    public static final DeferredRegister<Block> REGISTRY = new DeferredRegister<>(ForgeRegistries.BLOCKS, SecretRooms6.MODID);

    public static final Supplier<Block> GHOST_BLOCK = REGISTRY.register("ghost_block", () -> new GhostBlock(Block.Properties.create(Materials.SRM_MATERIAL)));
    public static final Supplier<Block> SECRET_STAIRS = REGISTRY.register("secret_stairs", () -> new SecretStairs(Block.Properties.create(Materials.SRM_MATERIAL)));
    public static final Supplier<Block> SECRET_LEVER = REGISTRY.register("secret_lever", () -> new SecretLever(Block.Properties.create(Materials.SRM_MATERIAL)));
    public static final Supplier<Block> SECRET_REDSTONE = REGISTRY.register("secret_redstone", () -> new SecretRedstone(Block.Properties.create(Materials.SRM_MATERIAL)));
    public static final Supplier<Block> ONE_WAY_GLASS = REGISTRY.register("one_way_glass", () -> new OneWayGlass(Block.Properties.create(Materials.SRM_MATERIAL)));
    public static final Supplier<Block> SECRET_WOODEN_BUTTON = REGISTRY.register("secret_wooden_button", () -> new SecretButton(Block.Properties.create(Materials.SRM_MATERIAL), true));
    public static final Supplier<Block> SECRET_STONE_BUTTON = REGISTRY.register("secret_stone_button", () -> new SecretButton(Block.Properties.create(Materials.SRM_MATERIAL), false));
    public static final Supplier<Block> TORCH_LEVER = REGISTRY.register("torch_lever", () -> new TorchLever(Block.Properties.create(Material.WOOD).lightValue(14)));
    public static final Supplier<Block> WALL_TORCH_LEVER = REGISTRY.register("wall_torch_lever", () -> new WallTorchLever(Block.Properties.create(Material.WOOD).lightValue(14)));
    public static final Supplier<Block> SECRET_PRESSURE_PLATE = REGISTRY.register("secret_pressure_plate", () -> new SecretPressurePlate(Block.Properties.create(Materials.SRM_MATERIAL), entity -> true));
    public static final Supplier<Block> SECRET_PLAYER_PRESSURE_PLATE = REGISTRY.register("secret_player_pressure_plate", () -> new SecretPressurePlate(Block.Properties.create(Materials.SRM_MATERIAL), entity -> entity instanceof PlayerEntity));
    public static final Supplier<Block> SECRET_DOOR = REGISTRY.register("secret_door", () -> new SecretDoor(Block.Properties.create(Materials.SRM_MATERIAL)));
    public static final Supplier<Block> SECRET_IRON_DOOR = REGISTRY.register("secret_iron_door", () -> new SecretDoor(Block.Properties.create(Materials.SRM_MATERIAL_IRON)));
    public static final Supplier<Block> SECRET_CHEST = REGISTRY.register("secret_chest", () -> new SecretChest(Block.Properties.create(Materials.SRM_MATERIAL)));
    public static final Supplier<Block> SECRET_TRAPDOOR = REGISTRY.register("secret_trapdoor", () -> new SecretTrapdoor(Block.Properties.create(Materials.SRM_MATERIAL)));
    public static final Supplier<Block> SECRET_IRON_TRAPDOOR = REGISTRY.register("secret_iron_trapdoor", () -> new SecretTrapdoor(Block.Properties.create(Materials.SRM_MATERIAL_IRON)));
    public static final Supplier<Block> SECRET_TRAPPED_CHEST = REGISTRY.register("secret_trapped_chest", () -> new SecretTrappedChest(Block.Properties.create(Materials.SRM_MATERIAL)));
    public static final Supplier<Block> SECRET_GATE = REGISTRY.register("secret_gate", () -> new SecretGateBlock(Block.Properties.create(Materials.SRM_MATERIAL)));
    public static final Supplier<Block> SECRET_DUMMY_BLOCK = REGISTRY.register("secret_dummy_block", () -> new SecretBaseBlock(Block.Properties.create(Materials.SRM_MATERIAL)));
    public static final Supplier<Block> SECRET_DAYLIGHT_DETECTOR = REGISTRY.register("secret_daylight_detector", () -> new SecretDaylightDetector(Block.Properties.create(Materials.SRM_MATERIAL)));
    public static final Supplier<Block> SECRET_OBSERVER = REGISTRY.register("secret_observer", () -> new SecretObserver(Block.Properties.create(Materials.SRM_MATERIAL)));
    public static final Supplier<Block> SECRET_CLAMBER = REGISTRY.register("secret_clamber", () -> new SecretClamber(Block.Properties.create(Materials.SRM_MATERIAL)));

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(






















        );
    }

    public static class Materials {
        public static final Material SRM_MATERIAL = new Material.Builder(MaterialColor.STONE).build();
        public static final Material SRM_MATERIAL_IRON = new Material.Builder(MaterialColor.IRON).build();
    }

}
