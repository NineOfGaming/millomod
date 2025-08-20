package net.millo.millomod.mod.features.impl.coding;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.Keybound;
import net.millo.millomod.system.Config;
import net.millo.millomod.system.PlayerUtil;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class CodeClientPlotFix extends Feature implements Keybound {

    KeyBinding key;
    @Override
    public String getKey() {
        return "cc_plot_fix";
    }

    @Override
    public void loadKeybinds() {
        key = KeyBindingRegistryImpl.registerKeyBinding(
                new KeyBinding(
                        "key.millo.cc_plot_fix",
                        InputUtil.Type.KEYSYM,
                        -1,
                        "key.category.millo"
                )
        );
    }


    @Override
    public void triggerKeybind(Config config) {
        if (MilloMod.MC.getNetworkHandler() == null) return;
        while (key.wasPressed()) {
//            MilloMod.MC.getNetworkHandler().sendCommand("worldplot massive");
            PlayerUtil.sendCommand("fixcc");
        }
    }
}
