package com.wynprice.secretrooms.server.blocks;

import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.server.utils.InjectedUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(
                new GhostBlock(Block.Properties.create(Material.ROCK)).setRegistryName("ghost_block"),
                new SecretStairs(Block.Properties.create(Material.ROCK)).setRegistryName("secret_stairs"),
                new SecretLever(Block.Properties.create(Material.ROCK)).setRegistryName("secret_lever")
        );
    }
}
