package com.wynprice.secretrooms.server.data;

import com.wynprice.secretrooms.SecretRooms6;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static com.wynprice.secretrooms.server.blocks.SecretBlocks.*;
import static com.wynprice.secretrooms.server.items.SecretItems.CAMOUFLAGE_PASTE;
import static com.wynprice.secretrooms.server.items.SecretItems.SWITCH_PROBE;

public class SecretRecipeProvider extends RecipeProvider {
    public SecretRecipeProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(TORCH_LEVER).key('T', Items.TORCH).key('L', Items.LEVER).patternLine("T").patternLine("L").setGroup("torch_lever.json").addCriterion("has_torch_lever", this.hasItems(Items.TORCH, Items.LEVER)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(CAMOUFLAGE_PASTE, 9).key('X', Tags.Items.DYES).key('#', Items.CLAY_BALL).patternLine("XXX").patternLine("X#X").patternLine("XXX").setGroup("camouflage_paste").addCriterion("has_earth_item", this.hasItem(SecretItemTags.EARTH_ITEM)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(GHOST_BLOCK, 4).key('X', CAMOUFLAGE_PASTE).key('0', SecretItemTags.SECRET_RECIPE_ITEMS).patternLine("X0X").patternLine("0 0").patternLine("X0X").setGroup("ghost_block").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(ONE_WAY_GLASS, 9).key('X', CAMOUFLAGE_PASTE).key('0', SecretItemTags.CLEAR_GLASS).patternLine("XXX").patternLine("000").patternLine("000").setGroup("one_way_glass").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer, new ResourceLocation(SecretRooms6.MODID, "one_way_glass_up"));
        ShapedRecipeBuilder.shapedRecipe(ONE_WAY_GLASS, 9).key('X', CAMOUFLAGE_PASTE).key('0', SecretItemTags.CLEAR_GLASS).patternLine("000").patternLine("000").patternLine("XXX").setGroup("one_way_glass").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer, new ResourceLocation(SecretRooms6.MODID, "one_way_glass_down"));
        ShapedRecipeBuilder.shapedRecipe(ONE_WAY_GLASS, 9).key('X', CAMOUFLAGE_PASTE).key('0', SecretItemTags.CLEAR_GLASS).patternLine("X00").patternLine("X00").patternLine("X00").setGroup("one_way_glass").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer, new ResourceLocation(SecretRooms6.MODID, "one_way_glass_left"));
        ShapedRecipeBuilder.shapedRecipe(ONE_WAY_GLASS, 9).key('X', CAMOUFLAGE_PASTE).key('0', SecretItemTags.CLEAR_GLASS).patternLine("00X").patternLine("00X").patternLine("00X").setGroup("one_way_glass").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer, new ResourceLocation(SecretRooms6.MODID, "one_way_glass_right"));
        ShapelessRecipeBuilder.shapelessRecipe(SWITCH_PROBE).addIngredient(CAMOUFLAGE_PASTE).addIngredient(Items.REDSTONE_TORCH).setGroup("switch_probe").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(ONE_WAY_GLASS).addIngredient(CAMOUFLAGE_PASTE).addIngredient(SecretItemTags.CLEAR_GLASS).setGroup("one_way_glass_shapeless").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_CHEST).addIngredient(CAMOUFLAGE_PASTE).addIngredient(Tags.Items.CHESTS_WOODEN).setGroup("secret_chest").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_PRESSURE_PLATE).addIngredient(CAMOUFLAGE_PASTE).addIngredient(ItemTags.WOODEN_PRESSURE_PLATES).setGroup("secret_pressure_plate").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_PLAYER_PRESSURE_PLATE).addIngredient(CAMOUFLAGE_PASTE).addIngredient(Items.STONE_PRESSURE_PLATE).setGroup("secret_player_pressure_plate").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_DOOR).addIngredient(CAMOUFLAGE_PASTE).addIngredient(ItemTags.WOODEN_DOORS).setGroup("secret_door").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_IRON_DOOR).addIngredient(CAMOUFLAGE_PASTE).addIngredient(Items.IRON_DOOR).setGroup("secret_iron_door").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_LEVER).addIngredient(CAMOUFLAGE_PASTE).addIngredient(Items.LEVER).setGroup("secret_lever").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_DAYLIGHT_DETECTOR).addIngredient(CAMOUFLAGE_PASTE).addIngredient(Items.DAYLIGHT_DETECTOR).setGroup("secret_daylight_detector").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_REDSTONE).addIngredient(CAMOUFLAGE_PASTE).addIngredient(Items.REDSTONE).setGroup("secret_redstone").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_STAIRS).addIngredient(CAMOUFLAGE_PASTE).addIngredient(ItemTags.STAIRS).setGroup("secret_stairs").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_WOODEN_BUTTON).addIngredient(CAMOUFLAGE_PASTE).addIngredient(ItemTags.WOODEN_BUTTONS).setGroup("secret_wooden_button").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_STONE_BUTTON).addIngredient(CAMOUFLAGE_PASTE).addIngredient(Items.STONE_BUTTON).setGroup("secret_stone_button").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_TRAPPED_CHEST).addIngredient(CAMOUFLAGE_PASTE).addIngredient(Tags.Items.CHESTS_TRAPPED).setGroup("secret_trapped_chest").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_TRAPDOOR).addIngredient(CAMOUFLAGE_PASTE).addIngredient(ItemTags.WOODEN_TRAPDOORS).setGroup("secret_trapdoor").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_IRON_TRAPDOOR).addIngredient(CAMOUFLAGE_PASTE).addIngredient(Items.IRON_TRAPDOOR).setGroup("secret_iron_trapdoor").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_OBSERVER).addIngredient(CAMOUFLAGE_PASTE).addIngredient(Items.OBSERVER).setGroup("secret_observer").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SECRET_CLAMBER).addIngredient(CAMOUFLAGE_PASTE).addIngredient(Items.LADDER).setGroup("secret_clamber").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);
        ShapedRecipeBuilder.shapedRecipe(SECRET_GATE).key('X', ItemTags.PLANKS).key('0', CAMOUFLAGE_PASTE).key('R', Items.REDSTONE).key('A', Items.ENDER_PEARL).patternLine("X0X").patternLine("0A0").patternLine("XRX").setGroup("secret_gate").addCriterion("has_camo", this.hasItem(CAMOUFLAGE_PASTE)).build(consumer);

    }

    private InventoryChangeTrigger.Instance hasItems(IItemProvider... itemIn) {
        ItemPredicate.Builder builder = ItemPredicate.Builder.create();
        for (IItemProvider provider : itemIn) {
            builder.item(provider);
        }
        return this.hasItem(builder.build());
    }
}

