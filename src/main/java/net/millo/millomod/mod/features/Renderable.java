package net.millo.millomod.mod.features;

import net.millo.millomod.config.Config;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public interface Renderable {

    // Something
    default void render(DrawContext context, float delta, TextRenderer textRenderer) {}
    default void renderContainer(DrawContext context, float delta, TextRenderer textRenderer) {}

    default void updatePosFromConfig(Config config, int defX, int defY) {
        setX(config.getOrDefault("hud."+getKeyName()+".x", defX));
        setY(config.getOrDefault("hud."+getKeyName()+".y", defY));
    }

    void setX(int x);
    void setY(int y);
    int getX();
    int getY();
    int getWidth();
    int getHeight();
    String getKeyName();


}
