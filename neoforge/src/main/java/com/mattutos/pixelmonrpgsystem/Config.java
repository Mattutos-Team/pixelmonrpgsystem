package com.mattutos.pixelmonrpgsystem;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER
            .comment("Whether to log the dirt block on common setup")
            .define("logDirtBlock", true);

    public static final ModConfigSpec.IntValue MAGIC_NUMBER = BUILDER
            .comment("A magic number")
            .defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER
            .comment("What you want the introduction message to be for the magic number")
            .define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A list of items to log on common setup.")
            .defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), () -> "", Config::validateItemName);

    public static final ModConfigSpec.DoubleValue PLAYER_XP_MULTIPLIER = BUILDER
            // comentario em ingles explicando para que serve a config
            .comment("XP multiplier gained per Pokémon based on the player's level")
            .defineInRange("playerXpMultiplier", 0.5, 0.01, 10.0);

    public static final ModConfigSpec.IntValue MAX_PLAYER_LEVEL = BUILDER
            .comment("Maximum player level")
            .defineInRange("maxPlayerLevel", 200, 1, 1000);

    public static final ModConfigSpec.BooleanValue ENABLE_CAPTURE_RESTRICTIONS = BUILDER
            .comment("Enable capture restrictions based on the player's level")
            .define("enableCaptureRestrictions", true);

    public static final ModConfigSpec.BooleanValue ENABLE_BATTLE_RESTRICTIONS = BUILDER
            .comment("Enable warnings when using Pokémon above the player's level")
            .define("enableBattleRestrictions", true);

    public static final ModConfigSpec.IntValue MASTERY_XP_CAPTURE = BUILDER
            .comment("XP gained for capturing a Pokémon")
            .defineInRange("masteryXpCapture", 20, 1, 1000);

    public static final ModConfigSpec.IntValue MASTERY_XP_VICTORY = BUILDER
            .comment("XP gained for winning a battle")
            .defineInRange("masteryXpVictory", 30, 1, 1000);

    public static final ModConfigSpec.BooleanValue MASTERY_DUAL_TYPE_XP = BUILDER
            .comment("Whether dual-type Pokémon grant XP to both types")
            .define("masteryDualTypeXp", true);

    public static final ModConfigSpec.DoubleValue MASTERY_ASPIRANTE_BONUS = BUILDER
            .comment("Bonus percentage for Aspirante mastery level")
            .defineInRange("masteryAspiranteBonus", 5.0, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue MASTERY_EXPERIENTE_BONUS = BUILDER
            .comment("Bonus percentage for Experiente mastery level")
            .defineInRange("masteryExperienteBonus", 7.5, 0.0, 100.0);

    public static final ModConfigSpec.DoubleValue MASTERY_MESTRE_BONUS = BUILDER
            .comment("Bonus percentage for Mestre mastery level")
            .defineInRange("masteryMestreBonus", 10.0, 0.0, 100.0);

    static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }
}
