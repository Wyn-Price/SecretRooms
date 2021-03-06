package com.wynprice.secretrooms.server.items;

import com.wynprice.secretrooms.server.tileentity.SecretTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;

public class TrueVisionGogglesClientHandler {

    private static boolean clientWearingItem;

    public static void onClientWorldLoad(ClientPlayerNetworkEvent.LoggedInEvent event) {
        clientWearingItem = isWearingGoggles(event.getPlayer());
    }

    public static void onClientWorldTick(TickEvent.ClientTickEvent event) {
        if(event.phase != TickEvent.Phase.START) {
            return;
        }
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

    public static boolean isWearingGoggles(PlayerEntity player) {
        return player.getItemStackFromSlot(EquipmentSlotType.HEAD).getItem() == SecretItems.TRUE_VISION_GOGGLES.get();
    }
}
