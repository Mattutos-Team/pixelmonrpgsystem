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
        applyMultiplier(stats, BattleStatsType.ATTACK);
        applyMultiplier(stats, BattleStatsType.DEFENSE);
        applyMultiplier(stats, BattleStatsType.SPECIAL_ATTACK);
        applyMultiplier(stats, BattleStatsType.SPECIAL_DEFENSE);
        applyMultiplier(stats, BattleStatsType.SPEED);
        return stats;
    }

    private void applyMultiplier(int[] stats, BattleStatsType type) {
        stats[type.getStatIndex()] = (int) (stats[type.getStatIndex()] * multiplier);
    }

}
