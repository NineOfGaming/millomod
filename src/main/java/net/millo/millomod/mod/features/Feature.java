package net.millo.millomod.mod.features;

import net.millo.millomod.config.Config;
import net.minecraft.network.packet.Packet;

public abstract class Feature {

    protected boolean enabled = true;

    abstract String getKey();


    public boolean onReceivePacket(Packet<?> packet) {
        return false;
    }

    // Does not work yet
    public boolean onSendPacket(Packet<?> packet) {
        return false;
    }

    // Does not work yet
    public void onTick() {}

    public void onConfigUpdate(Config config) {
        enabled = config.getOrDefault(getKey()+".enabled", true);
    }

}
