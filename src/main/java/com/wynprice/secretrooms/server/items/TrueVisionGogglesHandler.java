package com.wynprice.secretrooms.server.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TrueVisionGogglesHandler {

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
            Minecraft.getInstance().worldRenderer.loadRenderers();
        }
    }

    public static boolean isWearingGoggles(PlayerEntity player) {
        return player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == SecretItems.TRUE_VISION_GOGGLES.get();
    }
}
