package net.millo.millomod.mod.features;

import net.millo.millomod.config.Config;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

import java.util.ArrayList;
import java.util.List;

public class FeatureHandler {

    private static final List<Feature> features = new ArrayList<>();

    public static void load() {
        features.clear();
        register(new LagslayerHUD());
        configUpdate(Config.getInstance());
    }

    private static void register(Feature feature) {
        features.add(feature);
    }

    static ArrayList<String> l = new ArrayList<>();
    public static <T extends PacketListener> boolean handlePacket(Packet<T> packet) {

        for (Feature feature : features) {
            if (feature.onReceivePacket(packet)) return true;
        }

//        if (packet instanceof GameMessageS2CPacket) {
//            // On receive chat message
////            System.out.println(">>> " + ((GameMessageS2CPacket) packet).content().getString());
////            System.out.println(">> " + ((GameMessageS2CPacket) packet).content());
//        }

//        if (l.contains(packet.getClass().getName())) return false;
//        l.add(packet.getClass().getName());

//        System.out.println(packet.getClass().getName());

        return false;
    }

    public static void renderHUD(DrawContext context, float tickDelta, TextRenderer textRenderer) {
        for (Feature feature : features) {
            if (feature instanceof Renderable) {
                ((Renderable) feature).render(context, tickDelta, textRenderer);
            }
        }
    }

    public static void configUpdate(Config config) {
        features.forEach(feature -> feature.onConfigUpdate(config));
    }

    public static List<Feature> getFeatures() {
        return features;
    }
}
