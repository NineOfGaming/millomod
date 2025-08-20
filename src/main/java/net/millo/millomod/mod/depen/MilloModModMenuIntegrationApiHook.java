package net.millo.millomod.mod.depen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.millo.millomod.mod.features.gui.MilloGUI;

public class MilloModModMenuIntegrationApiHook implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parentScreen -> {
            MilloGUI gui = new MilloGUI();
            gui.setParent(parentScreen);
            return gui;
        };
    }
}
