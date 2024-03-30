package net.millo.millomod.mod.util;

public class MathUtil {

    public static float lerp(float a, float b, float f)  {
        return a * (1f - f) + (b * f);
    }

}
