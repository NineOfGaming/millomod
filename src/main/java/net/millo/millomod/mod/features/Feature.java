package net.millo.millomod.mod.features;

import net.millo.millomod.config.Config;
import net.minecraft.network.packet.Packet;

public abstract class Feature {

    protected boolean enabled = true;

    public abstract String getKey();


    // Does not work yet
    public void onTick() {}

    public void onConfigUpdate(Config config) {
        enabled = config.get(getKey()+".enabled");
    }
    public void defaultConfig(Config config) {
        config.setIfNull(getKey()+".enabled", !disabledByDefault());
        if (this instanceof IRenderable) ((IRenderable) this).setHudConfig(config);
    }
    public void toggleEnabled() {
        enabled = !enabled;
        Config.getInstance().set(getKey()+".enabled", enabled);
    }

    public boolean disabledByDefault() { return false; }
}
