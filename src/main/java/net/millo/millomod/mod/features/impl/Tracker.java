package net.millo.millomod.mod.features.impl;

import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.HandlePacket;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

public class Tracker extends Feature {

    private static double x, z;
    private static double plotX, plotZ;
    private static Sequence step = Sequence.WAIT_FOR_CLEAR;

    public static boolean isInArea(Vec3d pos) {
        double x = pos.getX();
        double z = pos.getZ();

        boolean inX = x >= plotX && x <= plotX + 301;
        boolean inZ = z >= plotX && z <= plotX + 301;

        return inX && inZ;
    }


    public enum Mode {
        PLAY, DEV, BUILD, SPAWN
    }


    public static Mode mode = Mode.SPAWN;


    private static void toDev() {
        setMode(Mode.DEV);
        plotX = x + 9.5;
        plotZ = z - 10.5;
    }
    private static void setMode(Mode mode) {
        Tracker.mode = mode;
        step = Sequence.WAIT_FOR_CLEAR;
    }

    public static Vec3d getPos() {
        return new Vec3d(plotX, 0, plotZ);
    }

    @HandlePacket
    public boolean clearTitle(ClearTitleS2CPacket clear) {
        if (clear.shouldReset()) step = Sequence.WAIT_FOR_POS;
        return false;
    }

    @HandlePacket
    public boolean positionLook(PlayerPositionLookS2CPacket pos) {
        if (step == Sequence.WAIT_FOR_POS) {
            x = pos.getX();
            z = pos.getZ();
            step = Sequence.WAIT_FOR_MESSAGE;
        }
        return false;
    }

    @HandlePacket
    public boolean overlay(OverlayMessageS2CPacket overlay) {
        if (step == Sequence.WAIT_FOR_MESSAGE && overlay.getMessage().getString().startsWith("DiamondFire - ")) setMode(Mode.SPAWN);
        return false;
    }

    @HandlePacket
    public boolean gameMessage(GameMessageS2CPacket message) {
        if (step == Sequence.WAIT_FOR_MESSAGE) {
            String content = message.content().getString();
            if (content.equals("» You are now in dev mode.")) toDev();
            if (content.equals("» You are now in build mode.")) setMode(Mode.BUILD);
            if (content.startsWith("» Joined game: ")) setMode(Mode.PLAY);
        }
        return false;
    }


    @Override
    public boolean alwaysActive() {
        return true;
    }
    @Override
    public String getKey() {
        return "tracker";
    }



    private enum Sequence {
        WAIT_FOR_CLEAR,
        WAIT_FOR_POS,
        WAIT_FOR_MESSAGE,
    }
}
