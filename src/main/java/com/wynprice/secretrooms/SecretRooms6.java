package com.wynprice.secretrooms;

import com.wynprice.secretrooms.client.SecretModelHandler;
import com.wynprice.secretrooms.client.SwitchProbeTooltipRenderer;
import com.wynprice.secretrooms.client.model.OneWayGlassModel;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import com.wynprice.secretrooms.server.data.SecretBlockLootTableProvider;
import com.wynprice.secretrooms.server.data.SecretBlockTagsProvider;
import com.wynprice.secretrooms.server.data.SecretItemTagsProvider;
import com.wynprice.secretrooms.server.data.SecretRecipeProvider;
import com.wynprice.secretrooms.server.items.SecretItems;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntities;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SecretRooms6.MODID)
public class SecretRooms6 {
    public static final String MODID = "secretroomsmod";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public SecretRooms6() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        
        bus.addListener(this::gatherData);
        SecretBlocks.REGISTRY.register(bus);
        SecretItems.REGISTRY.register(bus);
        SecretTileEntities.REGISTRY.register(bus);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(SecretModelHandler::onBlockColors);
            bus.addListener(SecretModelHandler::onModelBaked);
            bus.addListener(OneWayGlassModel::onModelsReady);

            bus.addListener(this::clientSetup);

            forgeBus.addListener(SwitchProbeTooltipRenderer::onTooltip);
        });
    }


    public static final ItemGroup TAB = new ItemGroup(-1, MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(SecretItems.CAMOUFLAGE_PASTE.get());
        }
    };

    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeServer()) {
            gen.addProvider(new SecretRecipeProvider(gen));
            gen.addProvider(new SecretItemTagsProvider(gen, helper));
            gen.addProvider(new SecretBlockTagsProvider(gen, helper));
            gen.addProvider(new SecretBlockLootTableProvider(gen));
        }
    }

    public void clientSetup(FMLClientSetupEvent clientSetupEvent) {
        for (Block block : new Block[]{
            SecretBlocks.GHOST_BLOCK.get(), SecretBlocks.SECRET_STAIRS.get(), SecretBlocks.SECRET_LEVER.get(),
            SecretBlocks.SECRET_REDSTONE.get(), SecretBlocks.ONE_WAY_GLASS.get(), SecretBlocks.SECRET_WOODEN_BUTTON.get(),
            SecretBlocks.SECRET_STONE_BUTTON.get(), SecretBlocks.TORCH_LEVER.get(), SecretBlocks.WALL_TORCH_LEVER.get(),
            SecretBlocks.SECRET_PRESSURE_PLATE.get(), SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE.get(),
            SecretBlocks.SECRET_DOOR.get(), SecretBlocks.SECRET_IRON_DOOR.get(), SecretBlocks.SECRET_CHEST.get(),
            SecretBlocks.SECRET_TRAPDOOR.get(), SecretBlocks.SECRET_IRON_TRAPDOOR.get(), SecretBlocks.SECRET_TRAPPED_CHEST.get(),
            SecretBlocks.SECRET_GATE.get(), SecretBlocks.SECRET_DUMMY_BLOCK.get(), SecretBlocks.SECRET_DAYLIGHT_DETECTOR.get(),
            SecretBlocks.SECRET_OBSERVER.get(), SecretBlocks.SECRET_CLAMBER.get()
        }) {
            RenderTypeLookup.setRenderLayer(block, type -> true);
        }
//        RenderTypeLookup.setRenderLayer(SecretBlocks.GHOST_BLOCK.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_STAIRS.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_LEVER.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_REDSTONE.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_WOODEN_BUTTON.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_STONE_BUTTON.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_PRESSURE_PLATE.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_CHEST.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_TRAPPED_CHEST.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_GATE.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_DUMMY_BLOCK.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_DAYLIGHT_DETECTOR.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_OBSERVER.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_CLAMBER.get(), RenderType.getCutout());
//
//
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_DOOR.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_IRON_DOOR.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_TRAPDOOR.get(), RenderType.getCutout());
//        RenderTypeLookup.setRenderLayer(SecretBlocks.SECRET_IRON_TRAPDOOR.get(), RenderType.getCutout());
    }
}
