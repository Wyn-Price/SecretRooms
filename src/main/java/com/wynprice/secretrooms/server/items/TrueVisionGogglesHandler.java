package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.RandomChance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TrueVisionGogglesHandler {

    private static final ResourceLocation END_CHEST_LOOT = new ResourceLocation("chests/end_city_treasure");

    private static boolean clientWearingItem;

    @SubscribeEvent
    public static void onWorldLoad(ClientPlayerNetworkEvent.LoggedInEvent event) {
        clientWearingItem = isWearingGoggles(event.getPlayer());
    }

    @SubscribeEvent
    public static void onClientWorldTick(TickEvent.ClientTickEvent event) {
        ClientPlayerEntity player = Minecraft.getInstance().player;
        if(player == null) return;
        boolean wearing = isWearingGoggles(player);
        if(wearing != clientWearingItem) {
            clientWearingItem = wearing;
            WorldRenderer renderer = Minecraft.getInstance().worldRenderer;
            for (TileEntity tileEntity : player.world.loadedTileEntityList) {
                if(tileEntity instanceof SecretTileEntity) {
                    BlockPos pos = tileEntity.getPos();
                    renderer.notifyBlockUpdate(null, pos, null, null, 8);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        if(END_CHEST_LOOT.equals(event.getName())) {
            event.getTable().addPool(
                LootPool.builder()
                    .addEntry(ItemLootEntry.builder(SecretItems.TRUE_VISION_GOGGLES.get())
                        .acceptCondition(RandomChance.builder(0.05f))
                    )
                .build());
        }
    }

    public static boolean isWearingGoggles(PlayerEntity player) {
        return player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == SecretItems.TRUE_VISION_GOGGLES.get();
    }
}
