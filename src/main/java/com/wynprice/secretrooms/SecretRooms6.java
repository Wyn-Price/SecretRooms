package com.wynprice.secretrooms;

import com.wynprice.secretrooms.client.SecretModelHandler;
import com.wynprice.secretrooms.client.SwitchProbeTooltipRenderer;
import com.wynprice.secretrooms.client.compat.SecretRoomsTOPCompat;
import com.wynprice.secretrooms.client.model.OneWayGlassModel;
import com.wynprice.secretrooms.client.model.quads.TrueVisionBakedQuad;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import com.wynprice.secretrooms.server.data.SecretBlockLootTableProvider;
import com.wynprice.secretrooms.server.data.SecretBlockTagsProvider;
import com.wynprice.secretrooms.server.data.SecretItemTagsProvider;
import com.wynprice.secretrooms.server.data.SecretRecipeProvider;
import com.wynprice.secretrooms.server.items.SecretItems;
import com.wynprice.secretrooms.server.items.TrueVisionGogglesClientHandler;
import com.wynprice.secretrooms.server.items.TrueVisionGogglesHandler;
import com.wynprice.secretrooms.server.tileentity.SecretTileEntities;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
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
        bus.addListener(this::onInterModEnqueue);
        SecretBlocks.REGISTRY.register(bus);
        SecretItems.REGISTRY.register(bus);
        SecretTileEntities.REGISTRY.register(bus);

        forgeBus.addListener(this::modifyBreakSpeed);
        forgeBus.addListener(TrueVisionGogglesHandler::onLootTableLoad);
        forgeBus.addListener(TrueVisionGogglesHandler::onPlayerTick);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            bus.addListener(SecretModelHandler::onBlockColors);
            bus.addListener(SecretModelHandler::onModelBaked);
            bus.addListener(OneWayGlassModel::onModelsReady);
            bus.addListener(TrueVisionBakedQuad::onTextureStitch);
            bus.addListener(TrueVisionBakedQuad::onTextureStitched);

            bus.addListener(this::clientSetup);

            forgeBus.addListener(SwitchProbeTooltipRenderer::onTooltip);

            forgeBus.addListener(TrueVisionGogglesClientHandler::onClientWorldLoad);
            forgeBus.addListener(TrueVisionGogglesClientHandler::onClientWorldTick);
        });

    }

    public void onInterModEnqueue(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe"))
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", SecretRoomsTOPCompat::new);
    }


    public static final ItemGroup TAB = new ItemGroup(-1, MODID) {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(SecretItems.CAMOUFLAGE_PASTE.get());
        }
    };

    public void modifyBreakSpeed(PlayerEvent.BreakSpeed event) {
        PlayerEntity player = event.getPlayer();
        SecretBaseBlock.getMirrorState(player.world, event.getPos()).ifPresent(mirror -> {
            //Copied and pasted from PlayerEntity#getDigSpeed
            float f = player.inventory.getDestroySpeed(mirror);
            if (f > 1.0F) {
                int i = EnchantmentHelper.getEfficiencyModifier(player);
                ItemStack itemstack = player.getHeldItemMainhand();
                if (i > 0 && !itemstack.isEmpty()) {
                    f += (i * i + 1);
                }
            }

            if (EffectUtils.hasMiningSpeedup(player)) {
                f *= 1.0F + (float)(EffectUtils.getMiningSpeedup(player) + 1) * 0.2F;
            }

            if (player.isPotionActive(Effects.MINING_FATIGUE)) {
                float f1;
                switch(player.getActivePotionEffect(Effects.MINING_FATIGUE).getAmplifier()) {
                    case 0:
                        f1 = 0.3F;
                        break;
                    case 1:
                        f1 = 0.09F;
                        break;
                    case 2:
                        f1 = 0.0027F;
                        break;
                    case 3:
                    default:
                        f1 = 8.1E-4F;
                }

                f *= f1;
            }

            if (player.areEyesInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {
                f /= 5.0F;
            }

            if (!player.isOnGround()) {
                f /= 5.0F;
            }

            event.setNewSpeed(f);
        });
    }

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
            SecretBlocks.SECRET_STONE_BUTTON.get(), SecretBlocks.SECRET_PRESSURE_PLATE.get(),
            SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE.get(), SecretBlocks.SECRET_DOOR.get(), SecretBlocks.SECRET_IRON_DOOR.get(),
            SecretBlocks.SECRET_CHEST.get(), SecretBlocks.SECRET_TRAPDOOR.get(), SecretBlocks.SECRET_IRON_TRAPDOOR.get(),
            SecretBlocks.SECRET_TRAPPED_CHEST.get(), SecretBlocks.SECRET_GATE.get(), SecretBlocks.SECRET_DUMMY_BLOCK.get(),
            SecretBlocks.SECRET_DAYLIGHT_DETECTOR.get(),SecretBlocks.SECRET_OBSERVER.get(), SecretBlocks.SECRET_CLAMBER.get()
        }) {
            RenderTypeLookup.setRenderLayer(block, type -> true);
        }

        RenderTypeLookup.setRenderLayer(SecretBlocks.TORCH_LEVER.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(SecretBlocks.WALL_TORCH_LEVER.get(), RenderType.getCutout());
    }
}
