package net.millo.millomod.mod.features.gui;

import net.millo.millomod.config.Config;
import net.millo.millomod.mod.util.gui.ElementFadeIn;
import net.millo.millomod.mod.util.gui.GUI;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.millo.millomod.mod.util.gui.elements.ButtonElement;
import net.millo.millomod.mod.util.gui.elements.ScrollableElement;
import net.millo.millomod.mod.util.gui.elements.TextElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.io.IOException;

public class MilloGUI extends GUI {
    Config config;
    public MilloGUI() {
        super(Text.literal("Settings"));
        config = Config.getInstance();
    }

    @Override
    protected void init() {
        super.init();

        int x = paddingX;
        int y = paddingY;
        addDrawableChild(new TextElement(x, y, width, 16, Text.literal("Millo Mod"),textRenderer));
        addDrawableChild(new TextElement(x, y + 16, width, 16, Text.literal("Woah! some text!"),textRenderer));

        addDrawableChild(new ButtonElement(x, y + 32, 100, 16, Text.of("Settings"), (button) -> {
            GUI gui = new ColorsGUI();
            gui.setParent(this);
            gui.open();
        }, textRenderer));

        addDrawableChild(new TextElement(x, y + 48, width, 16, Text.literal("Credits n such"),textRenderer));

    }

}
