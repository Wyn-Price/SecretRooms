package com.wynprice.secretrooms.server.items;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.TickEvent;

public class TrueVisionGogglesHandler {

    private static final ResourceLocation END_CHEST_LOOT = new ResourceLocation("chests/end_city_treasure");

    public static final float GOGGLES_BREAK_CHANCE = 0.2F;

    public static void onLootTableLoad(LootTableLoadEvent event) {
        if(END_CHEST_LOOT.equals(event.getName())) {
            event.getTable().addPool(
                LootPool.lootPool()
                    .add(LootItem.lootTableItem(SecretItems.TRUE_VISION_GOGGLES.get())
                        .when(LootItemRandomChanceCondition.randomChance(0.1f))
                    )
                .build());
        }
    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(event.side.isServer() && event.phase == TickEvent.Phase.END && event.player.tickCount % 20 == 0 && event.player.getRandom().nextFloat() < GOGGLES_BREAK_CHANCE) {
            ItemStack stack = event.player.getItemBySlot(EquipmentSlot.HEAD);
            if(stack.getItem() == SecretItems.TRUE_VISION_GOGGLES.get()) {
                stack.hurtAndBreak(1, event.player, (p) -> p.broadcastBreakEvent(EquipmentSlot.HEAD));
            }
        }
    }
}
