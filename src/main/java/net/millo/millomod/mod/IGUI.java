package net.millo.millomod.mod;

import net.millo.millomod.MilloMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public interface IGUI {
    default void openGui(Screen gui) {
        try {
            MinecraftClient mc = MilloMod.MC;
            mc.execute(() -> mc.setScreen(gui));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
