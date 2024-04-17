package net.millo.millomod.mod.features.impl;

import net.millo.millomod.mod.Callback;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.HandlePacket;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tracker extends Feature {

    private static double x, z;
    private static double plotX, plotZ;
    private static Sequence step = Sequence.WAIT_FOR_CLEAR;
    private static int plotId = 0;
    private static boolean requestPlotId = false;
    private static ArrayList<Callback> plotIdCallbacks = new ArrayList<>();




    public enum Mode {
        PLAY, DEV, BUILD, SPAWN
    }


    public static Mode mode = Mode.SPAWN;

    public static boolean isInArea(Vec3d pos) {
        double x = pos.getX();
        double z = pos.getZ();

        boolean inX = x >= plotX && x <= plotX + 301;
        boolean inZ = z >= plotX && z <= plotX + 301;

        return inX && inZ;
    }
    private static void toDev() {
        setMode(Mode.DEV);
        plotX = x + 9.5;
        plotZ = z - 10.5;
    }
    private static void setMode(Mode mode) {
        Tracker.mode = mode;
        step = Sequence.WAIT_FOR_CLEAR;
        requestPlotId = true;
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
        String content = message.content().getString();
        if (step == Sequence.WAIT_FOR_MESSAGE) {
            if (content.equals("» You are now in dev mode.")) toDev();
            if (content.equals("» You are now in build mode.")) setMode(Mode.BUILD);
            if (content.startsWith("» Joined game: ")) setMode(Mode.PLAY);
        }
        // example: `                                       \nYou are currently coding on:\n\n→ 🌊 MENACES [41800]\n→ Owner: BupBoi_ \n→ Server: Node 2\n                                       `
        if (requestPlotId && content.startsWith("                          ")) {
            String regex = "\\[\\d+\\]\\n";
            Matcher matcher = Pattern.compile(regex).matcher(content);
            if (matcher.find()) {
                String plotIdString = matcher.group().trim().replace("[", "").replace("]", "");
                plotId = Integer.parseInt(plotIdString);
                requestPlotId = false;
                plotIdCallbacks.forEach(Callback::run);
                plotIdCallbacks.clear();
                return true;
            }
        }
        return false;
    }

    public static void requestPlotId(Callback callback) {
        requestPlotId = true;
        plotIdCallbacks.add(callback);
    }
    public static int getPlotId() {
        return plotId;
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
