package com.wynprice.secretrooms.server.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.wynprice.secretrooms.SecretRooms7;
import com.wynprice.secretrooms.client.model.TrueVisionGogglesModel;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TrueVisionGoggles extends ArmorItem {

    private static final String ARMOR_TEXTURE = new ResourceLocation(SecretRooms7.MODID, "textures/models/armor/true_vision_goggles.png").toString();

    @OnlyIn(Dist.CLIENT)
    private TrueVisionGogglesModel model;

    public TrueVisionGoggles(Properties properties) {
        super(DummyArmorMaterial.INSTANCE, Type.HELMET, properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return ImmutableMultimap.of();
    }

// TODO (port): Mixin
//
//    @Override
//    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
//        super.initializeClient(consumer);
//        consumer.accept(new IItemRenderProperties() {
//            @Override
//            public HumanoidModel<?> getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
//                if(model == null) {
//                    refreshArmorModel();
//                }
//                return model;
//            }
//        });
//    }

    @OnlyIn(Dist.CLIENT)
    public void refreshArmorModel() {
        this.model = new TrueVisionGogglesModel(Minecraft.getInstance().getEntityModels().bakeLayer(TrueVisionGogglesModel.TRUE_VISION_GOGGLES_MODEL));
    }

//    @Nullable
//    @Override
//    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
//        return ARMOR_TEXTURE;
//    }


    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }



//    @Override
//    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
//        return false;
//    }
//
//    @Override
//    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
//        return false;
//    }

    private enum DummyArmorMaterial implements ArmorMaterial {
        INSTANCE;

        @Override
        public int getDefenseForType(Type type) {
            return 0;
        }

        @Override
        public int getDurabilityForType(Type type) {
            return 0;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public SoundEvent getEquipSound() {
            return SoundEvents.ARMOR_EQUIP_CHAIN;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return Ingredient.EMPTY;
        }

        @Override
        public String getName() {
            return "true_sight_goggles";
        }

        @Override
        public float getToughness() {
            return 0;
        }

        @Override
        public float getKnockbackResistance() {
            return 0;
        }
    }
}
