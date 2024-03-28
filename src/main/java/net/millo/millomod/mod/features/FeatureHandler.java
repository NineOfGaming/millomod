package net.millo.millomod.mod.features;

import net.millo.millomod.config.Config;
import net.millo.millomod.mod.features.impl.LagslayerHUD;
import net.millo.millomod.mod.features.impl.MenuSearch;
import net.millo.millomod.mod.features.impl.PacketHandler;
import net.millo.millomod.mod.features.impl.PreviewSkin;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

import java.io.IOException;
import java.util.*;

public class FeatureHandler {

    private static final HashMap<String, Feature> features = new HashMap<>();

    static PacketHandler packetHandler;
    public static void load() {
        packetHandler = new PacketHandler();

        features.clear();
        register(new LagslayerHUD());
        register(new PreviewSkin());
        register(new MenuSearch());

        Config config = Config.getInstance();
        defaultConfig(config);
        configUpdate(config);

        try {
            config.saveConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static void register(Feature feature) {
        packetHandler.register(feature);
        features.put(feature.getKey(), feature);
    }



//     Pass through
//    static ArrayList<String> packets = new ArrayList<>();
    public static <T extends PacketListener> boolean handlePacket(Packet<T> packet) {
        return packetHandler.handlePacket(packet);

//        for (Feature feature : features.values()) {
//            if (feature.onReceivePacket(packet)) return true;
//        }
//
//        if (packet instanceof GameMessageS2CPacket) {
//            String message = ((GameMessageS2CPacket) packet).content().getString();
//            System.out.println("> "+ message);
//        }
//
//        if (!packets.contains(packet.getClass().toString())) {
//            System.out.println(packet.getClass());
//            packets.add(packet.getClass().toString());
//        }
////        packet instanceof GameMessageS2CPacket  // On receive chat message;

    }

    public static void renderHUD(DrawContext context, float tickDelta, TextRenderer textRenderer) {
        for (Feature feature : getFeatures()) {
            if (feature instanceof IRenderable) {
                ((IRenderable) feature).render(context, tickDelta, textRenderer);
            }
        }
    }

    public static void configUpdate(Config config) {
        features.forEach((key, feature) -> feature.onConfigUpdate(config));
    }
    private static void defaultConfig(Config config) {
        features.forEach((key, feature) -> feature.defaultConfig(config));
    }


    // Getters // setters

    public static Collection<Feature> getFeatures() {
        return features.values();
    }
    public static Feature getFeature(String key) {
        return features.get(key);
    }
}
