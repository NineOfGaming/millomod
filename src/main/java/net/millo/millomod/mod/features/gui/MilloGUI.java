package net.millo.millomod.mod.features.gui;

import net.millo.millomod.config.Config;
import net.millo.millomod.mod.commands.Command;
import net.millo.millomod.mod.commands.CommandHandler;
import net.millo.millomod.mod.features.Feature;
import net.millo.millomod.mod.features.FeatureHandler;
import net.millo.millomod.mod.util.gui.ElementFadeIn;
import net.millo.millomod.mod.util.gui.GUI;
import net.millo.millomod.mod.util.gui.GUIStyles;
import net.millo.millomod.mod.util.gui.elements.ButtonElement;
import net.millo.millomod.mod.util.gui.elements.ScrollableElement;
import net.millo.millomod.mod.util.gui.elements.TextElement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.Tooltip;
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
        addDrawableChild(new TextElement(x, y + 10, backgroundWidth, 16,
                Text.literal("Millo Mod").setStyle(GUIStyles.NAME.getStyle()), textRenderer));
        addDrawableChild(new TextElement(x, y + 40, backgroundWidth, 16,
                Text.literal("ye fr...").setStyle(GUIStyles.COMMENT.getStyle()), textRenderer));

        addDrawableChild(new TextElement(x, y + 58, backgroundWidth, 16,
                Text.literal("Credits:").setStyle(GUIStyles.LINE.getStyle()), textRenderer));
        addDrawableChild(new TextElement(x, y + 68, backgroundWidth, 16,
                Text.literal("Millo5 - I um.. me").setStyle(GUIStyles.UNSAVED.getStyle()), textRenderer));
        addDrawableChild(new TextElement(x, y + 78, backgroundWidth, 16,
                Text.literal("GeorgeRNG - I stole his code. [Code Client]").setStyle(GUIStyles.UNSAVED.getStyle()), textRenderer));


        addDrawableChild(new ButtonElement(x + backgroundWidth/2 - 50, y + 96, 100, 16, Text.of("Settings"), (button) -> {
            GUI gui = new SettingsGUI();
            gui.setParent(this);
            gui.setFade(this.getFade());
            gui.open();
        }, textRenderer));


        ScrollableElement featureList = new ScrollableElement(x + backgroundWidth/2 - 80, y + 124, 160, backgroundHeight - y - 124,
                Text.of("Feature List"));

        // Features
        featureList.addDrawableChild(
                new TextElement(0, 0, 150, 16, Text.literal("Features").setStyle(GUIStyles.HEADER.getStyle()),textRenderer)
        );
        for (Feature feature : FeatureHandler.getFeatures()) {
            TextElement text = new TextElement(0, 0, 150, 12,
                    Text.translatable("millo.feature."+feature.getKey()).setStyle(GUIStyles.CONTROL.getStyle()), textRenderer);
            text.setTooltip(Tooltip.of(Text.translatable("millo.feature."+feature.getKey()+".desc")));
            featureList.addDrawableChild(text);
        }

        // Commands
        featureList.addDrawableChild(
                new TextElement(0, 0, 150, 16, Text.literal("Commands").setStyle(GUIStyles.HEADER.getStyle()),textRenderer)
        );
        for (Command command : CommandHandler.getCommands()) {
            TextElement text = new TextElement(0, 0, 150, 12,
                    Text.literal("/").append(Text.translatable("millo.command." + command.getKey())).setStyle(GUIStyles.CONTROL.getStyle()), textRenderer);
            text.setTooltip(Tooltip.of(Text.translatable("millo.command."+command.getKey()+".desc")));
            featureList.addDrawableChild(text);
        }
        addDrawableChild(featureList);


    }

}
