package net.millo.millomod.mod.util.gui;

import net.minecraft.client.gui.Drawable;

public interface ScrollableEntryI extends Drawable {
    int getHeight();


    void setRealX(int x);
    void setRealY(int y);
    int getRealX();
    int getRealY();
    void setX(int x);
    void setY(int y);
    int getX();
    int getY();
}
