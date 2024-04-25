package net.millo.millomod.mod.features;

import net.millo.millomod.mod.util.RenderInfo;
import net.millo.millomod.system.Config;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public interface IRenderable {

    // Something
    default void render(RenderInfo info) {}



    default void setHudConfig(Config config) {
        config.setIfNull("hud."+getKey()+".x", getX());
        config.setIfNull("hud."+getKey()+".y", getX());
    }

    default void updatePosFromConfig(Config config) {
        setX(config.get("hud."+getKey()+".x"));
        setY(config.get("hud."+getKey()+".y"));
    }


    void setX(int x);
    void setY(int y);
    int getX();
    int getY();
    int getWidth();
    int getHeight();
    String getKey();


}
