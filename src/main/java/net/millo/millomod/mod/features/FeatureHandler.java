package net.millo.millomod.mod.features;

import net.millo.millomod.config.Config;
import net.millo.millomod.mod.features.impl.*;
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
        register(
                new LagslayerHUD(),
                new PreviewSkin(),
                new MenuSearch(),
                new AutoCommand(),
                new NotificationTray()
        );

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

    private static void register(Feature ...features) {
        for (Feature feature : features) {
            register(feature);
        }
    }



//     Pass through
    public static <T extends PacketListener> boolean handlePacket(Packet<T> packet) {
        return packetHandler.handlePacket(packet);
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
