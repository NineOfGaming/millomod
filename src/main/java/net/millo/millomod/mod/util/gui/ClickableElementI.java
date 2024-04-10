package net.millo.millomod.mod.util.gui;

public interface ClickableElementI {
    void onPress(double mouseX, double mouseY, int button);
    boolean isHovered();
}
