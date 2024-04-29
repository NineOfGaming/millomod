package net.millo.millomod.mod.features.impl.global;

import net.millo.millomod.mod.features.Feature;

public class PreviewSkin extends Feature {
    @Override
    public String getKey() {
        return "preview_skin";
    }

    @Override
    public boolean disabledByDefault() {
        return true;
    }
}
