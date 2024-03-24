package net.millo.millomod.mod.features.settings;

import net.millo.millomod.config.Config;
import net.millo.millomod.mod.features.gui.ElementFadeIn;
import net.millo.millomod.mod.features.gui.GUI;
import net.millo.millomod.mod.features.gui.GUIStyles;
import net.millo.millomod.mod.features.gui.elements.ButtonElement;
import net.millo.millomod.mod.features.gui.elements.ScrollableElement;
import net.millo.millomod.mod.features.gui.elements.TextElement;
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

        ScrollableElement settingsList = new ScrollableElement(padding, padding, backgroundWidth, backgroundHeight, Text.literal("asdf"));
        int x = backgroundWidth / 2 - 100;

        settingsList.addDrawableChild(new ButtonElement(x, 0, 200, 20, Text.literal("Modify Positions"), (button) -> {
            GUI gui = new PositionsGUI(this);
            gui.open();
        }, textRenderer));

        TextElement text = new TextElement(0, 0, backgroundWidth, 20,
                Text.literal("Lagslayer HUD").setStyle(GUIStyles.HEADER.getStyle()), textRenderer);
//        text.alignCenter();
        settingsList.addDrawableChild(text);


        addBooleanOption(settingsList, "Enabled", "lagslayer.enabled", true);

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

    private void addBooleanOption(ScrollableElement list, String name, String key, boolean def) {
        boolean state = config.getOrDefault(key, def);
        int x = backgroundWidth / 2 - 100;
        ButtonElement b = new ButtonElement(x, 0, 200, 20, Text.literal(name+": "+state), button -> {
            boolean newState = !config.getOrDefault(key, def);
            config.set(key, newState);
            button.setText(Text.literal(name+": "+newState));
        }, textRenderer);
        b.setFade(new ElementFadeIn(ElementFadeIn.Direction.RIGHT));
        list.addDrawableChild(b);
    }


}
