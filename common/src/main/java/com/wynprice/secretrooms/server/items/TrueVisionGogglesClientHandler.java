package com.wynprice.secretrooms.server.items;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;

public class TrueVisionGogglesClientHandler {

    private static boolean clientWearingItem;

    // TODO (port): Mixin???
//    public static void onClientWorldLoad(ClientPlayerNetworkEvent.LoggedInEvent event) {
//        clientWearingItem = isWearingGoggles(event.getPlayer());
//    }

//    public static void onClientWorldTick(TickEvent.ClientTickEvent event) {
//        if(event.phase != TickEvent.Phase.START) {
//            return;
//        }
//        LocalPlayer player = Minecraft.getInstance().player;
//        if(player == null) return;
//        boolean wearing = isWearingGoggles(player);
//        if(wearing != clientWearingItem) {
//            clientWearingItem = wearing;
//            LevelRenderer renderer = Minecraft.getInstance().levelRenderer;
//            ClientChunkCache source = player.clientLevel.getChunkSource();
//
//            ChunkPos chunkPos = new ChunkPos(player.blockPosition());
//            int d = Minecraft.getInstance().options.renderDistance;
//            for (int x = -d; x <= d; x++) {
//                for (int z = -d; z <= d; z++) {
//                    if(source.hasChunk(chunkPos.x + x, chunkPos.z + z)) {
//                        for (BlockEntity blockEntity : source.getChunk(chunkPos.x + x, chunkPos.z + z, false).getBlockEntities().values()) {
//                            if(blockEntity instanceof SecretTileEntity) {
//                                BlockPos pos = blockEntity.getBlockPos();
//                                renderer.blockChanged(null, pos, null, null, 8);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    public static boolean isWearingGoggles(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() == SecretItems.TRUE_VISION_GOGGLES.get();
    }
}
