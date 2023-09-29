package com.wynprice.secretrooms;

import com.wynprice.secretrooms.client.SecretModelHandler;
import com.wynprice.secretrooms.client.SimpleUnbakedGeometryLoader;
import com.wynprice.secretrooms.client.SwitchProbeTooltip;
import com.wynprice.secretrooms.client.SwitchProbeTooltipComponent;
import com.wynprice.secretrooms.client.model.OneWayGlassModel;
import com.wynprice.secretrooms.client.model.SecretBlockModel;
import com.wynprice.secretrooms.client.model.SecretMappedModel;
import com.wynprice.secretrooms.server.SecretCreativeTab;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import com.wynprice.secretrooms.server.data.SecretBlockLootTableProvider;
import com.wynprice.secretrooms.server.data.SecretBlockTagsProvider;
import com.wynprice.secretrooms.server.data.SecretItemTagsProvider;
import com.wynprice.secretrooms.server.data.SecretRecipeProvider;
import com.wynprice.secretrooms.server.items.SecretItems;
import com.wynprice.secretrooms.server.items.SwitchProbe;
import com.wynprice.secretrooms.server.items.TrueVisionGogglesClientHandler;
import com.wynprice.secretrooms.server.items.TrueVisionGogglesHandler;
import com.wynprice.secretrooms.server.registry.ForgeRegistryHolder;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntities;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mod(SecretRooms7.MODID)
public class SecretRooms7Forge {
    public SecretRooms7Forge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        bus.addListener(this::gatherData);
        ((ForgeRegistryHolder<?>) SecretBlocks.REGISTRY).register(bus);
        ((ForgeRegistryHolder<?>) SecretItems.REGISTRY).register(bus);
        ((ForgeRegistryHolder<?>) SecretTileEntities.REGISTRY).register(bus);
        ((ForgeRegistryHolder<?>) SecretCreativeTab.REGISTRY).register(bus);

        bus.addListener(this::onRegisterReloads);

        forgeBus.addListener(this::modifyBreakSpeed);
//        forgeBus.addListener(TrueVisionGogglesHandler::onLootTableLoad);
//        forgeBus.addListener(TrueVisionGogglesHandler::onPlayerTick);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener((RegisterColorHandlersEvent.Block event) -> SecretModelHandler.onBlockColors(event::register));
            bus.addListener((EntityRenderersEvent.RegisterLayerDefinitions event) -> SecretModelHandler.onEntityModelRegistered(event::registerLayerDefinition));

            bus.addListener(this::clientSetup);
            bus.addListener(this::registerClientTooltipComponentFactory);
            bus.addListener(this::registerCustomGeometryLoader);

            forgeBus.addListener((RenderTooltipEvent.GatherComponents event) -> SwitchProbe.appendHover(event.getItemStack(), event.getTooltipElements()));

//            forgeBus.addListener(TrueVisionGogglesClientHandler::onClientWorldLoad);
//            forgeBus.addListener(TrueVisionGogglesClientHandler::onClientWorldTick);
        });

    }


    // TODO (port): rethink this?
    public void modifyBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();
        Optional<BlockPos> position = event.getPosition();
        if (position.isEmpty()) {
            return;
        }
        SecretRooms7.modifyBreakSpeed(player, position.get()).ifPresent(event::setNewSpeed);
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        SecretBlockTagsProvider block = new SecretBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        gen.addProvider(event.includeServer(), block);
        gen.addProvider(event.includeServer(), new SecretItemTagsProvider(packOutput, lookupProvider, block.contentsGetter(), existingFileHelper));
        gen.addProvider(event.includeServer(), new SecretRecipeProvider(packOutput));
        gen.addProvider(event.includeServer(), new SecretBlockLootTableProvider(packOutput));
    }

    public void clientSetup(FMLClientSetupEvent clientSetupEvent) {
        for (Block block : new Block[]{
                SecretBlocks.GHOST_BLOCK.get(), SecretBlocks.SECRET_STAIRS.get(), SecretBlocks.SECRET_LEVER.get(),
                SecretBlocks.SECRET_REDSTONE.get(), SecretBlocks.ONE_WAY_GLASS.get(), SecretBlocks.SECRET_WOODEN_BUTTON.get(),
                SecretBlocks.SECRET_STONE_BUTTON.get(), SecretBlocks.SECRET_PRESSURE_PLATE.get(),
                SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE.get(), SecretBlocks.SECRET_DOOR.get(), SecretBlocks.SECRET_IRON_DOOR.get(),
                SecretBlocks.SECRET_CHEST.get(), SecretBlocks.SECRET_TRAPDOOR.get(), SecretBlocks.SECRET_IRON_TRAPDOOR.get(),
                SecretBlocks.SECRET_TRAPPED_CHEST.get(), SecretBlocks.SECRET_GATE.get(), SecretBlocks.SECRET_DUMMY_BLOCK.get(),
                SecretBlocks.SECRET_DAYLIGHT_DETECTOR.get(),SecretBlocks.SECRET_OBSERVER.get(), SecretBlocks.SECRET_CLAMBER.get()
        }) {
            ItemBlockRenderTypes.setRenderLayer(block, type -> true);
        }

        ItemBlockRenderTypes.setRenderLayer(SecretBlocks.TORCH_LEVER.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(SecretBlocks.WALL_TORCH_LEVER.get(), RenderType.cutout());
}

    public void registerClientTooltipComponentFactory(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(SwitchProbeTooltipComponent.class, SwitchProbeTooltip::new);
    }

    private void registerCustomGeometryLoader(ModelEvent.RegisterGeometryLoaders event) {
        event.register("secret_block", SimpleUnbakedGeometryLoader.create(SecretBlockModel::new));
        event.register("secret_mapped_model", SimpleUnbakedGeometryLoader.create(SecretMappedModel::new));
        event.register("one_way_glass", SimpleUnbakedGeometryLoader.create(OneWayGlassModel::new));
    }


    public void onRegisterReloads(RegisterClientReloadListenersEvent event) {
        ResourceManagerReloadListener listener = rm -> SecretRooms7.onResourceReload();
        event.registerReloadListener(listener);
    }
}
