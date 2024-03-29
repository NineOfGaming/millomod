package net.millo.millomod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistryImpl;
import net.millo.millomod.config.Config;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.FeatureHandler;
import net.millo.millomod.mod.features.Keybound;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;

public class KeybindHandler {


    static ArrayList<Keybound> features = new ArrayList<>();
    public static void load() {

        for (Feature feature : FeatureHandler.getFeatures()) {
            if (feature instanceof Keybound) {
                ((Keybound) feature).loadKeybinds();
                features.add((Keybound) feature);
            }
        }

        Config config = Config.getInstance();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            features.forEach(feature -> feature.triggerKeybind(config));
        });

    }

}
