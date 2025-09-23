package com.mattutos.pixelmonrpgsystem.mastery;

import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.status.StatusBase;
import com.pixelmonmod.pixelmon.battles.status.StatusType;

public class MasteryStatusBase extends StatusBase {

    public MasteryStatusBase() {
        super(StatusType.None);
    }

    @Override
    public int[] modifyBaseStats(PixelmonWrapper user, int[] stats) {
        return super.modifyBaseStats(user, stats);
    }

    @Override
    public int[] modifyStats(PixelmonWrapper user, int[] stats) {
        return super.modifyStats(user, stats);
    }

}
