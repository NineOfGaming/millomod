package net.millo.millomod.config;

import java.io.IOException;

public class ModConfigs {

    public static void loadDefaults() {
        Config config = Config.getInstance();
        config.set("lagslayer.enabled", true);
        config.set("lagslayer.x", 20);
        config.set("lagslayer.y", 20);
    }


}
