package net.millo.millomod.mod.features;

import net.millo.millomod.config.Config;
import net.minecraft.client.option.KeyBinding;

public interface Keybound {

    void loadKeybinds();
    void triggerKeybind(Config config);

}
