package net.millo.millomod.mod.features.gui;

import net.millo.millomod.SoundHandler;
import net.millo.millomod.mod.util.gui.elements.IntegerElement;
import net.millo.millomod.system.Config;
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

        ScrollableElement settingsList = new ScrollableElement(paddingX, paddingY, backgroundWidth, backgroundHeight, Text.literal(""));
        int x = backgroundWidth / 2 - 100;

        addTitle(settingsList, "config.millo.title");

        settingsList.addDrawableChild(new ButtonElement(x, 0, 200, 20, Text.translatable("config.millo.modify_positions"), (button) -> {
            GUI gui = new PositionsGUI(this);
            gui.open();
        }, textRenderer));

        addHeader(settingsList, "config.millo.features");

        addBooleanOption(settingsList, "lagslayer");
        addBooleanOption(settingsList, "menu_search");
        addBooleanOption(settingsList, "auto_command");
        addBooleanOption(settingsList, "notification_tray");
        addBooleanOption(settingsList, "auto_chat_local");
//        addIntegerOption(settingsList, "fs_toggle.speed", 0, 1000);

//        addHeader(settingsList, "Unstable Features");
//        addBooleanOption(settingsList, "preview_skin.enabled","Preview Skin", "Do not use. Will crash your game");

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

    private void addTitle(ScrollableElement list, String titleKey) {
        list.addDrawableChild(new TextElement(0, 0, backgroundWidth, 30,
                Text.translatable(titleKey).setStyle(GUIStyles.TITLE.getStyle()), textRenderer)
        );
    }
    private void addHeader(ScrollableElement list, String headerKey) {
        list.addDrawableChild(new TextElement(0, 0, backgroundWidth, 30,
                Text.translatable(headerKey).setStyle(GUIStyles.HEADER.getStyle()), textRenderer)
        );
    }

    private void addBooleanOption(ScrollableElement list, String key) {
        boolean state = config.get(key+".enabled");
        int x = backgroundWidth / 2 - 100;

        String name = Text.translatable("millo.feature."+key).getString();
        String tooltip = Text.translatable("millo.feature."+key+".desc").getString();

        ButtonElement b = new ButtonElement(x, 0, 200, 20, Text.literal(name+": ").append(GUIStyles.getTrueFalse(state)), button -> {
            SoundHandler.playClick();
            boolean newState = !(boolean)config.get(key+".enabled");
            config.set(key+".enabled", newState);
            button.setText(Text.literal(name+": ").append(GUIStyles.getTrueFalse(newState)));
        }, textRenderer);
        b.setTooltip(Text.literal(tooltip));
        b.setFade(new ElementFadeIn(ElementFadeIn.Direction.RIGHT));
        list.addDrawableChild(b);
    }

    private void addIntegerOption(ScrollableElement list, String key, int min, int max) {
        int current = config.get(key);

        String name = Text.translatable("millo.feature."+key).getString();
        String tooltip = Text.translatable("millo.feature."+key+".desc").getString();

        int x = backgroundWidth / 2 - 100;

        IntegerElement b = new IntegerElement(x, 0, min, max, Text.literal(name), current, (change) -> config.set(key, change), textRenderer);

        list.addDrawableChild(b);
    }

}
