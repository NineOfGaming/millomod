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

public class SettingsGUI extends GUI {
    Config config;
    public SettingsGUI() {
        super(Text.literal("Settings"));
        config = Config.getInstance();
    }

    @Override
    protected void init() {
        super.init();

        ScrollableElement settingsList = new ScrollableElement(paddingX, paddingY, backgroundWidth, backgroundHeight, Text.literal("asdf"));
        int x = backgroundWidth / 2 - 100;

        addTitle(settingsList, "Millo Mod Settings");

        settingsList.addDrawableChild(new ButtonElement(x, 0, 200, 20, Text.literal("Modify Positions"), (button) -> {
            GUI gui = new PositionsGUI(this);
            gui.open();
        }, textRenderer));

        addHeader(settingsList, "Features");

        addBooleanOption(settingsList, "lagslayer.enabled","Lagslayer Overlay", "Replaces the actionbar lagslayer with a custom GUI widget");
        addBooleanOption(settingsList, "menusearch.enabled", "Menu Search", "Enables a searchbar in menus to highlight named items");

        addHeader(settingsList, "Unstable Features");
        addBooleanOption(settingsList, "previewskin.enabled","Preview Skin", "Do not use. Will crash your game");

        addDrawableChild(settingsList);
    }

    @Override
    public void close() {
        super.close();
        try {
            config.saveConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    private void addTitle(ScrollableElement list, String name) {
        list.addDrawableChild(new TextElement(0, 0, backgroundWidth, 30,
                Text.literal(name).setStyle(GUIStyles.TITLE.getStyle()), textRenderer)
        );
    }
    private void addHeader(ScrollableElement list, String name) {
        list.addDrawableChild(new TextElement(0, 0, backgroundWidth, 30,
                Text.literal(name).setStyle(GUIStyles.HEADER.getStyle()), textRenderer)
        );
    }

    private void addBooleanOption(ScrollableElement list, String key, String name, String tooltip) {
        boolean state = config.get(key);
        int x = backgroundWidth / 2 - 100;
        ButtonElement b = new ButtonElement(x, 0, 200, 20, Text.literal(name+": ").append(GUIStyles.getTrueFalse(state)), button -> {
            boolean newState = !(boolean)config.get(key);
            config.set(key, newState);
            button.setText(Text.literal(name+": ").append(GUIStyles.getTrueFalse(newState)));
        }, textRenderer);
        b.setTooltip(Text.literal(tooltip));
        b.setFade(new ElementFadeIn(ElementFadeIn.Direction.RIGHT));
        list.addDrawableChild(b);
    }


}
