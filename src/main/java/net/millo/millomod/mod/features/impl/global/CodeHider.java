package net.millo.millomod.mod.features.impl.global;

import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.FeatureHandler;
import net.millo.millomod.mod.features.impl.util.Tracker;
import net.minecraft.util.math.BlockPos;

public class CodeHider extends Feature {


    @Override
    public String getKey() {
        return "code_hider";
    }


    public boolean hideBlock(BlockPos pos) {
        boolean withinDev = Tracker.getPlot().isInDev(pos.toCenterPos());
        if (!withinDev) return false;

        if (Tracker.mode == Tracker.Mode.PLAY) return true;

        if (MilloMod.MC.player == null) return false;
        return !pos.toCenterPos().isInRange(MilloMod.MC.player.getPos(), 15d);
    }

}
