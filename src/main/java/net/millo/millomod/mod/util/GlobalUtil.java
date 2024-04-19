package net.millo.millomod.mod.util;

import net.fabricmc.loader.api.FabricLoader;
import net.millo.millomod.MilloMod;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.HashMap;

public class GlobalUtil {

    public static boolean isKeyDown(KeyBinding keyBind) {
        try {
            String cname = FabricLoader.getInstance().isDevelopmentEnvironment() ? "boundKey" : "field_1655";
            int keycode = ((InputUtil.Key) FieldUtils.getField(KeyBinding.class, cname, true).get(keyBind)).getCode();
            return InputUtil.isKeyPressed(MilloMod.MC.getWindow().getHandle(), keycode);
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    private static HashMap<KeyBinding, Integer> keyDuration = new HashMap<>();
    public static boolean isKeyPressed(KeyBinding keyBind) {
        if (!keyDuration.containsKey(keyBind)) keyDuration.put(keyBind, 0);
        if (isKeyDown(keyBind)) {
            int dur = keyDuration.get(keyBind) + 1;
            keyDuration.put(keyBind, dur);
            return dur == 1;
        }
        keyDuration.put(keyBind, 0);
        return false;
    }


}
