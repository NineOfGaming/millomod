package net.millo.millomod.mod.features.impl;

import net.millo.millomod.MilloMod;
import net.millo.millomod.mod.features.Feature;

public class AutoChatLocal extends Feature {
    @Override
    public String getKey() {
        return "auto_chat_local";
    }

    public void trigger() {
        if (MilloMod.MC.getNetworkHandler() == null || !enabled) return;
        MilloMod.MC.getNetworkHandler().sendCommand("c l");
    }

    @Override
    public boolean disabledByDefault() {
        return true;
    }
}
