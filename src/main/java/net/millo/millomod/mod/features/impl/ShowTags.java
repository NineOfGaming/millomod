package net.millo.millomod.mod.features.impl;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.Keybound;
import net.millo.millomod.system.Config;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.commons.lang3.reflect.FieldUtils;

public class ShowTags extends Feature implements Keybound {

    private KeyBinding showKey;

    @Override
    public String getKey() {
        return "show_tags";
    }

    public boolean isPressed() {
        try {
            String cname = FabricLoader.getInstance().isDevelopmentEnvironment() ? "boundKey" : "field_1655";
            int keycode = ((InputUtil.Key) FieldUtils.getField(KeyBinding.class, cname, true).get(showKey)).getCode();
            return InputUtil.isKeyPressed(MilloMod.MC.getWindow().getHandle(), keycode);
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    @Override
    public void loadKeybinds() {
        showKey = KeyBindingRegistryImpl.registerKeyBinding(
                new KeyBinding(
                        "key.millo.show_tags",
                        InputUtil.Type.KEYSYM,
                        -1,
                        "key.category.millo"
                )
        );
    }

    @Override
    public void triggerKeybind(Config config) {}
}
