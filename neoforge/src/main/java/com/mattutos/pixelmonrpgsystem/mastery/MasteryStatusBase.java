package com.mattutos.pixelmonrpgsystem.mastery;

import com.pixelmonmod.pixelmon.api.pokemon.stats.BattleStatsType;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.status.StatusBase;
import com.pixelmonmod.pixelmon.battles.status.StatusType;

public class MasteryStatusBase extends StatusBase {
    private final double multiplier;

    public MasteryStatusBase(double multiplier) {
        super(StatusType.None);
        this.multiplier = multiplier;
    }

    @Override
    public int[] modifyBaseStats(PixelmonWrapper user, int[] stats) {
        stats[BattleStatsType.ATTACK.getStatIndex()] = applyMultiplier(stats, BattleStatsType.ATTACK);
        stats[BattleStatsType.DEFENSE.getStatIndex()] = applyMultiplier(stats, BattleStatsType.DEFENSE);
        stats[BattleStatsType.SPECIAL_ATTACK.getStatIndex()] = applyMultiplier(stats, BattleStatsType.SPECIAL_ATTACK);
        stats[BattleStatsType.SPECIAL_DEFENSE.getStatIndex()] = applyMultiplier(stats, BattleStatsType.SPECIAL_DEFENSE);
        stats[BattleStatsType.SPEED.getStatIndex()] = applyMultiplier(stats, BattleStatsType.SPEED);
        return stats;
    }

    private int applyMultiplier(int[] stats, BattleStatsType type) {
        int statsIndex = type.getStatIndex();
        return (int) (stats[statsIndex] * multiplier);
    }

}
