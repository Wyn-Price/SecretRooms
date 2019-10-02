package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.utils.InjectedUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(SecretRooms6.MODID)
@Mod.EventBusSubscriber(modid = SecretRooms6.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SecretBlocks {

    public static final Block GHOST_BLOCK = InjectedUtils.injected();
    public static final Block SECRET_STAIRS = InjectedUtils.injected();
    public static final Block SECRET_LEVER = InjectedUtils.injected();
    public static final Block SECRET_REDSTONE = InjectedUtils.injected();
    public static final Block ONE_WAY_GLASS = InjectedUtils.injected();
    public static final Block SECRET_WOODEN_BUTTON = InjectedUtils.injected();
    public static final Block SECRET_STONE_BUTTON = InjectedUtils.injected();
    public static final Block TORCH_LEVER = InjectedUtils.injected();
    public static final Block WALL_TORCH_LEVER = InjectedUtils.injected();
    public static final Block SECRET_PRESSURE_PLATE = InjectedUtils.injected();
    public static final Block SECRET_PLAYER_PRESSURE_PLATE = InjectedUtils.injected();
    public static final Block SECRET_DOOR = InjectedUtils.injected();
    public static final Block SECRET_IRON_DOOR = InjectedUtils.injected();
    public static final Block SECRET_CHEST = InjectedUtils.injected();
    public static final Block SECRET_TRAPDOOR = InjectedUtils.injected();
    public static final Block SECRET_IRON_TRAPDOOR = InjectedUtils.injected();
    public static final Block SECRET_TRAPPED_CHEST = InjectedUtils.injected();
    public static final Block SECRET_GATE = InjectedUtils.injected();
    public static final Block SECRET_DUMMY_BLOCK = InjectedUtils.injected();
    public static final Block SECRET_DAYLIGHT_DETECTOR = InjectedUtils.injected();
    public static final Block SECRET_OBSERVER = InjectedUtils.injected();
    public static final Block SECRET_CLAMBER = InjectedUtils.injected();

    //TODO: add a non full stairs block, that uses the mapped model blockstates

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
            new GhostBlock(Block.Properties.create(Materials.SRM_MATERIAL)).setRegistryName("ghost_block"),
            new SecretStairs(Block.Properties.create(Materials.SRM_MATERIAL)).setRegistryName("secret_stairs"),
            new SecretLever(Block.Properties.create(Materials.SRM_MATERIAL)).setRegistryName("secret_lever"),
            new SecretRedstone(Block.Properties.create(Materials.SRM_MATERIAL)).setRegistryName("secret_redstone"),
            new OneWayGlass(Block.Properties.create(Materials.SRM_MATERIAL)).setRegistryName("one_way_glass"),
            new SecretButton(Block.Properties.create(Materials.SRM_MATERIAL), true).setRegistryName("secret_wooden_button"),
            new SecretButton(Block.Properties.create(Materials.SRM_MATERIAL), false).setRegistryName("secret_stone_button"),
            new TorchLever(Block.Properties.create(Material.WOOD)).setRegistryName("torch_lever"),
            new WallTorchLever(Block.Properties.create(Material.WOOD)).setRegistryName("wall_torch_lever"),
            new SecretPressurePlate(Block.Properties.create(Materials.SRM_MATERIAL), entity -> true).setRegistryName("secret_pressure_plate"),
            new SecretPressurePlate(Block.Properties.create(Materials.SRM_MATERIAL), entity -> entity instanceof PlayerEntity).setRegistryName("secret_player_pressure_plate"),
            new SecretDoor(Block.Properties.create(Materials.SRM_MATERIAL)).setRegistryName("secret_door"),
            new SecretDoor(Block.Properties.create(Materials.SRM_MATERIAL_IRON)).setRegistryName("secret_iron_door"),
            new SecretChest(Block.Properties.create(Materials.SRM_MATERIAL)).setRegistryName("secret_chest"),
            new SecretTrapdoor(Block.Properties.create(Materials.SRM_MATERIAL)).setRegistryName("secret_trapdoor"),
            new SecretTrapdoor(Block.Properties.create(Materials.SRM_MATERIAL_IRON)).setRegistryName("secret_iron_trapdoor"),
            new SecretTrappedChest(Block.Properties.create(Materials.SRM_MATERIAL)).setRegistryName("secret_trapped_chest"),
            new SecretGateBlock(Block.Properties.create(Materials.SRM_MATERIAL)).setRegistryName("secret_gate"),
            new SecretBaseBlock(Block.Properties.create(Materials.SRM_MATERIAL)).setRegistryName("secret_dummy_block"),
            new SecretDaylightDetector(Block.Properties.create(Materials.SRM_MATERIAL)).setRegistryName("secret_daylight_detector"),
            new SecretObserver(Block.Properties.create(Materials.SRM_MATERIAL)).setRegistryName("secret_observer"),
            new SecretClamber(Block.Properties.create(Materials.SRM_MATERIAL)).setRegistryName("secret_clamber")
        );
    }

    public static class Materials {
        public static final Material SRM_MATERIAL = new Material.Builder(MaterialColor.STONE).build();
        public static final Material SRM_MATERIAL_IRON = new Material.Builder(MaterialColor.IRON).build();
    }

}
