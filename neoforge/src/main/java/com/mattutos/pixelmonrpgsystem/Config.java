package com.mattutos.pixelmonrpgsystem;

import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue PLAYER_XP_MULTIPLIER = BUILDER
            .comment("XP multiplier gained per Pokémon based on the player's level")
            .defineInRange("playerXpMultiplier", 0.5, 0.01, 10.0);

    public static final ModConfigSpec.IntValue MAX_PLAYER_LEVEL = BUILDER
            .comment("Maximum player level")
            .defineInRange("maxPlayerLevel", 200, 1, 10000);

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

}
