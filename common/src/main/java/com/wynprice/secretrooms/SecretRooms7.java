package com.wynprice.secretrooms;

import com.wynprice.secretrooms.client.SwitchProbeTooltip;
import com.wynprice.secretrooms.client.SwitchProbeTooltipComponent;
import com.wynprice.secretrooms.server.blocks.SecretBaseBlock;
import com.wynprice.secretrooms.server.blocks.SecretBlocks;
import com.wynprice.secretrooms.server.data.SecretBlockLootTableProvider;
import com.wynprice.secretrooms.server.data.SecretBlockTagsProvider;
import com.wynprice.secretrooms.server.data.SecretItemTagsProvider;
import com.wynprice.secretrooms.server.data.SecretRecipeProvider;
import com.wynprice.secretrooms.server.items.SecretItems;
import com.wynprice.secretrooms.server.items.TrueVisionGoggles;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.data.DataGenerator;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class SecretRooms7 {
    public static final String MODID = "secretroomsmod";
    public static final Logger LOGGER = LogManager.getLogger(MODID);


    //TODO (port): creative tab
//    public static final CreativeModeTab TAB = new CreativeModeTab(-1, MODID) {
//        @Override
//        public ItemStack makeIcon() {
//            return new ItemStack(SecretItems.CAMOUFLAGE_PASTE.get());
//        }
//    };

    // TODO (port): move to somewhere appropiate
    public static Optional<Float> modifyBreakSpeed(Player player, BlockPos pos) {
        return SecretBaseBlock.getMirrorState(player.level(), pos).map(mirror -> {
            //Copied and pasted from PlayerEntity#getDigSpeed
            float f = player.getInventory().getDestroySpeed(mirror);
            if (f > 1.0F) {
                int i = EnchantmentHelper.getBlockEfficiency(player);
                ItemStack itemstack = player.getMainHandItem();
                if (i > 0 && !itemstack.isEmpty()) {
                    f += (i * i + 1);
                }
            }

            if (MobEffectUtil.hasDigSpeed(player)) {
                f *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(player) + 1) * 0.2F;
            }

            if (player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
                float f1 = switch (player.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                    case 0 -> 0.3F;
                    case 1 -> 0.09F;
                    case 2 -> 0.0027F;
                    default -> 8.1E-4F;
                };

                f *= f1;
            }

            if (player.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {
                f /= 5.0F;
            }

            if (!player.onGround()) {
                f /= 5.0F;
            }

            return f;
        });
    }

    // Are render layers a thing in fabric?
//    public void clientSetup(FMLClientSetupEvent clientSetupEvent) {
//        for (Block block : new Block[]{
//            SecretBlocks.GHOST_BLOCK.get(), SecretBlocks.SECRET_STAIRS.get(), SecretBlocks.SECRET_LEVER.get(),
//            SecretBlocks.SECRET_REDSTONE.get(), SecretBlocks.ONE_WAY_GLASS.get(), SecretBlocks.SECRET_WOODEN_BUTTON.get(),
//            SecretBlocks.SECRET_STONE_BUTTON.get(), SecretBlocks.SECRET_PRESSURE_PLATE.get(),
//            SecretBlocks.SECRET_PLAYER_PRESSURE_PLATE.get(), SecretBlocks.SECRET_DOOR.get(), SecretBlocks.SECRET_IRON_DOOR.get(),
//            SecretBlocks.SECRET_CHEST.get(), SecretBlocks.SECRET_TRAPDOOR.get(), SecretBlocks.SECRET_IRON_TRAPDOOR.get(),
//            SecretBlocks.SECRET_TRAPPED_CHEST.get(), SecretBlocks.SECRET_GATE.get(), SecretBlocks.SECRET_DUMMY_BLOCK.get(),
//            SecretBlocks.SECRET_DAYLIGHT_DETECTOR.get(),SecretBlocks.SECRET_OBSERVER.get(), SecretBlocks.SECRET_CLAMBER.get()
//        }) {
//            ItemBlockRenderTypes.setRenderLayer(block, type -> true);
//        }
//
//        ItemBlockRenderTypes.setRenderLayer(SecretBlocks.TORCH_LEVER.get(), RenderType.cutout());
//        ItemBlockRenderTypes.setRenderLayer(SecretBlocks.WALL_TORCH_LEVER.get(), RenderType.cutout());

//        MinecraftForgeClient.registerTooltipComponentFactory(SwitchProbeTooltipComponent.class, SwitchProbeTooltip::new);
//    }

    // TODO (port) impliment caller
    public static void onResourceReload() {
        SecretItems.TRUE_VISION_GOGGLES.get().refreshArmorModel();
    }
}
