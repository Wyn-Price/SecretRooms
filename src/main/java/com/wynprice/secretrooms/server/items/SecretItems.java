package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.WallOrFloorItem;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;
import java.util.function.Supplier;

public class SecretItems {

    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, SecretRooms6.MODID);

    public static final Supplier<Item> GHOST_BLOCK = REGISTRY.register("ghost_block", () -> new SecretBlockItem(SecretBlocks.GHOST_BLOCK.get(), prop()));
    public static final Supplier<Item> SECRET_STAIRS = REGISTRY.register("secret_stairs", () -> new SecretBlockItem(SecretBlocks.SECRET_STAIRS.get(), prop()));
    public static final Supplier<Item> SECRET_LEVER = REGISTRY.register("secret_lever", () -> new SecretBlockItem(SecretBlocks.SECRET_LEVER.get(), prop()));
    public static final Supplier<Item> SECRET_REDSTONE = REGISTRY.register("secret_redstone", () -> new SecretBlockItem(SecretBlocks.SECRET_REDSTONE.get(), prop()));
    public static final Supplier<Item> ONE_WAY_GLASS = REGISTRY.register("one_way_glass", () -> new SecretBlockItem(SecretBlocks.ONE_WAY_GLASS.get(), new Item.Properties()));
    public static final Supplier<Item> SECRET_WOODEN_BUTTON = REGISTRY.register("secret_wooden_button", () -> new SecretBlockItem(SecretBlocks.SECRET_WOODEN_BUTTON.get(), prop()));
    public static final Supplier<Item> SECRET_STONE_BUTTON = REGISTRY.register("secret_stone_button", () -> new SecretBlockItem(SecretBlocks.SECRET_STONE_BUTTON.get(), prop()));
    public static final Supplier<Item> TORCH_LEVER = REGISTRY.register("torch_lever", () -> new WallOrFloorItem(Objects.requireNonNull(SecretBlocks.TORCH_LEVER.get()), Objects.requireNonNull(SecretBlocks.WALL_TORCH_LEVER.get()), prop()));
    public static final Supplier<Item> SECRET_PRESSURE_PLATE = REGISTRY.register("secret_pressure_plate", () -> new SecretBlockItem(SecretBlocks.SECRET_PRESSURE_PLATE.get(), prop()));
    public static final Supplier<Item> SECRET_PLAYER_PRESSURE_PLATE = REGISTRY.register("secret_player_pressure_plate", () -> new SecretBlockItem(SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE.get(), prop()));
    public static final Supplier<Item> SECRET_DOOR = REGISTRY.register("secret_door", () -> new SecretDoubleBlockItem(SecretBlocks.SECRET_DOOR.get(), prop()));
    public static final Supplier<Item> SECRET_IRON_DOOR = REGISTRY.register("secret_iron_door", () -> new SecretDoubleBlockItem(SecretBlocks.SECRET_IRON_DOOR.get(), prop()));
    public static final Supplier<Item> SECRET_CHEST = REGISTRY.register("secret_chest", () -> new SecretBlockItem(SecretBlocks.SECRET_CHEST.get(), prop()));
    public static final Supplier<Item> SECRET_TRAPDOOR = REGISTRY.register("secret_trapdoor", () -> new SecretBlockItem(SecretBlocks.SECRET_TRAPDOOR.get(), prop()));
    public static final Supplier<Item> SECRET_IRON_TRAPDOOR = REGISTRY.register("secret_iron_trapdoor", () -> new SecretBlockItem(SecretBlocks.SECRET_IRON_TRAPDOOR.get(), prop()));
    public static final Supplier<Item> SECRET_TRAPPED_CHEST = REGISTRY.register("secret_trapped_chest", () -> new SecretBlockItem(SecretBlocks.SECRET_TRAPPED_CHEST.get(), prop()));
    public static final Supplier<Item> SECRET_GATE = REGISTRY.register("secret_gate", () -> new SecretBlockItem(SecretBlocks.SECRET_GATE.get(), prop()));
    public static final Supplier<Item> SECRET_DUMMY_BLOCK = REGISTRY.register("secret_dummy_block", () -> new SecretBlockItem(SecretBlocks.SECRET_DUMMY_BLOCK.get(), prop()));
    public static final Supplier<Item> SECRET_DAYLIGHT_DETECTOR = REGISTRY.register("secret_daylight_detector", () -> new SecretBlockItem(SecretBlocks.SECRET_DAYLIGHT_DETECTOR.get(), prop()));
    public static final Supplier<Item> SECRET_OBSERVER = REGISTRY.register("secret_observer", () -> new SecretBlockItem(SecretBlocks.SECRET_OBSERVER.get(), prop()));
    public static final Supplier<Item> SECRET_CLAMBER = REGISTRY.register("secret_clamber", () -> new SecretBlockItem(SecretBlocks.SECRET_CLAMBER.get(), prop()));

    public static final Supplier<Item> CAMOUFLAGE_PASTE = REGISTRY.register("camouflage_paste", () -> new Item(prop()));
    public static final RegistryObject<Item> SWITCH_PROBE = REGISTRY.register("switch_probe", () -> new SwitchProbe(prop()));

    private static Item.Properties prop() {
        return new Item.Properties().group(SecretRooms6.TAB);
    }
}
