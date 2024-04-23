package net.millo.millomod.mod.features.impl;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.Keybound;
import net.millo.millomod.system.Config;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.MathHelper;

public class FSToggle extends Feature implements Keybound {

    KeyBinding toggleKey;
    private boolean on = false;
    private int speed = 300;
    @Override
    public String getKey() {
        return "fs_toggle";
    }

    public void setSpeed(int speed) {
        this.speed = MathHelper.clamp(speed, 0, 1000);
    }

    @Override
    public void defaultConfig(Config config) {
        super.defaultConfig(config);
        config.set("fs_toggle.speed", 300);
    }


    @Override
    public void loadKeybinds() {
        toggleKey = KeyBindingRegistryImpl.registerKeyBinding(
                new KeyBinding(
                        "key.millo.fs_toggle",
                        InputUtil.Type.KEYSYM,
                        -1,
                        "key.category.millo"
                )
        );
    }

    @Override
    public void triggerKeybind(Config config) {
        if (MilloMod.MC.getNetworkHandler() == null) return;
        while (toggleKey.wasPressed()) {
            int spd = on ? 100 : speed;
            on = !on;
            MilloMod.MC.getNetworkHandler().sendCommand("fs "+spd);
        }
    }
}
