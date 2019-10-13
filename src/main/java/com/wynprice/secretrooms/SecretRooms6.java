package com.wynprice.secretrooms;

import com.wynprice.secretrooms.client.SecretModelHandler;
import com.wynprice.secretrooms.client.SwitchProbeTooltipRenderer;
import com.wynprice.secretrooms.client.model.OneWayGlassModel;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import com.wynprice.secretrooms.server.data.SecretItemTagsProvider;
import com.wynprice.secretrooms.server.data.SecretBlockLootTableProvider;
import com.wynprice.secretrooms.server.data.SecretRecipeProvider;
import com.wynprice.secretrooms.server.items.SecretItems;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntities;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
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

        if (event.includeServer()) {
            gen.addProvider(new SecretRecipeProvider(gen));
            gen.addProvider(new SecretItemTagsProvider(gen));
            gen.addProvider(new SecretBlockLootTableProvider(gen));
        }
    }
}
