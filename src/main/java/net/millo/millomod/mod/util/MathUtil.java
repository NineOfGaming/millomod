package net.millo.millomod.mod.util;

import net.minecraft.util.math.MathHelper;

public class MathUtil {

    public static double clampLerp(double start, double end, double delta) {
        return start + MathHelper.clamp(delta, 0, 1) * (end - start);
    }
    public static float clampLerp(float start, float end, float delta) {
        return start + MathHelper.clamp(delta, 0, 1) * (end - start);
    }

}
