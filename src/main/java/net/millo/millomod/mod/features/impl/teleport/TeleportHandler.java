package net.millo.millomod.mod.features.impl.teleport;

import net.millo.millomod.mod.Callback;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.HandlePacket;
import net.millo.millomod.mod.features.OnSendPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

public class TeleportHandler extends Feature {

    private static TeleportHandler INSTANCE;

    private Teleport teleport;

    public TeleportHandler() {
        INSTANCE = this;
    }

    public static void abort() {
        INSTANCE.teleport.abort();
    }


    @HandlePacket
    public boolean positionLook(PlayerPositionLookS2CPacket pos) {
        if (teleport == null || teleport.aborting()) return false;
        return teleport.positionLook(pos);
    }
    @OnSendPacket
    public boolean positionLookSend(PlayerPositionLookS2CPacket pos) {
        if (teleport == null || teleport.aborting()) return false;
        return teleport.positionLookSend(pos);
    }

    public static void teleportTo(Vec3d target, Callback callback) {
        INSTANCE.teleport = new Teleport(target, callback);
    }
    public static void teleportTo(Vec3d target) {
        INSTANCE.teleport = new Teleport(target, () -> {});
    }

    @Override
    public void onTick() {
        if (teleport == null || teleport.aborting()) return;
        teleport.onTick();
    }



    @Override
    public String getKey() {
        return "teleport_handler";
    }
    @Override
    public boolean alwaysActive() {
        return true;
    }
}
