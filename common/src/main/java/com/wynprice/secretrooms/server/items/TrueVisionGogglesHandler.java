package com.wynprice.secretrooms.server.items;

import net.minecraft.resources.ResourceLocation;

public class TrueVisionGogglesHandler {

    private static final ResourceLocation END_CHEST_LOOT = new ResourceLocation("chests/end_city_treasure");

    public static final float GOGGLES_BREAK_CHANCE = 0.2F;

    // TODO (port): Mixin?
//    public static void onLootTableLoad(LootTableLoadEvent event) {
//        if(END_CHEST_LOOT.equals(event.getName())) {
//            event.getTable().addPool(
//                LootPool.lootPool()
//                    .add(LootItem.lootTableItem(SecretItems.TRUE_VISION_GOGGLES.get())
//                        .when(LootItemRandomChanceCondition.randomChance(0.1f))
//                    )
//                .build());
//        }
//    }
//
//    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        if(event.side.isServer() && event.phase == TickEvent.Phase.END && event.player.tickCount % 20 == 0 && event.player.getRandom().nextFloat() < GOGGLES_BREAK_CHANCE) {
//            ItemStack stack = event.player.getItemBySlot(EquipmentSlot.HEAD);
//            if(stack.getItem() == SecretItems.TRUE_VISION_GOGGLES.get()) {
//                stack.hurtAndBreak(1, event.player, (p) -> p.broadcastBreakEvent(EquipmentSlot.HEAD));
//            }
//        }
//    }
}
