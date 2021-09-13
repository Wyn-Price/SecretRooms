package com.wynprice.secretrooms.server.items;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.wynprice.secretrooms.SecretRooms6;
import com.wynprice.secretrooms.client.model.TrueVisionGogglesModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IItemRenderProperties;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class TrueVisionGoggles extends ArmorItem {

    private static final String ARMOR_TEXTURE = new ResourceLocation(SecretRooms6.MODID, "textures/models/armor/true_vision_goggles.png").toString();

    @OnlyIn(Dist.CLIENT)
    private TrueVisionGogglesModel model;

    public TrueVisionGoggles(Properties properties) {
        super(DummyArmorMaterial.INSTANCE, EquipmentSlot.HEAD, properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot equipmentSlot) {
        return ImmutableMultimap.of();
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IItemRenderProperties() {
            @Override
            public <A extends HumanoidModel<?>> A getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, A _default) {
                if(model == null) {
                    refreshArmorModel();
                }
                return (A) model;
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public void refreshArmorModel() {
        this.model = new TrueVisionGogglesModel(Minecraft.getInstance().getEntityModels().bakeLayer(TrueVisionGogglesModel.TRUE_VISION_GOGGLES_MODEL));
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return ARMOR_TEXTURE;
    }


    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    private enum DummyArmorMaterial implements ArmorMaterial {
        INSTANCE;

        @Override
        public int getDurabilityForSlot(EquipmentSlot slotIn) {
            return 0;
        }

        @Override
        public int getDefenseForSlot(EquipmentSlot slotIn) {
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
