package com.wynprice.secretrooms.client.chunkrender;

import com.wynprice.secretrooms.SecretRooms6;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRender;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.client.renderer.chunk.IChunkRendererFactory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SecretRooms6.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DelegateChunkRender extends ChunkRender {
    private final ChunkRender delegate;

    public DelegateChunkRender(World worldIn, WorldRenderer worldRendererIn, ChunkRender delegate) {
        super(worldIn, worldRendererIn);
        this.delegate = delegate;
    }

    @SubscribeEvent
    public static void onModelReady(ModelRegistryEvent event) {
        WorldRenderer worldRenderer = Minecraft.getInstance().worldRenderer;
        IChunkRendererFactory chunkFactory = worldRenderer.renderChunkFactory;
        worldRenderer.renderChunkFactory = (world, renderer) -> new DelegateChunkRender(world, renderer, chunkFactory.create(world, renderer));
    }

    @Override
    public ChunkRenderCache createRegionRenderCache(World world, BlockPos from, BlockPos to, int subtract) {
        ChunkRenderCache cache = this.delegate.createRegionRenderCache(world, from, to, subtract);
        return cache != null ? new DelegateChunkRenderCache(world, cache) : null;
    }
}
