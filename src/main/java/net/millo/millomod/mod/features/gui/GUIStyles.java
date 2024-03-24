package net.millo.millomod.mod.features.gui;

import net.minecraft.text.Style;

import java.awt.*;

public enum GUIStyles {
    COMMENT (Style.EMPTY.withItalic(true).withColor(Color.gray.hashCode())),
    HEADER (Style.EMPTY.withColor(Color.white.hashCode()));


    private final Style style;
    GUIStyles(Style style) {
        this.style = style;
    }

    public Style getStyle() {
        return style;
    }

}
