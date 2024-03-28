package net.millo.millomod.mod.util.gui;

import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;

public enum GUIStyles {
    COMMENT (Style.EMPTY.withItalic(true).withColor(Color.gray.hashCode())),
    TRUE (Style.EMPTY.withColor(Color.GREEN.hashCode())),
    FALSE (Style.EMPTY.withColor(Color.RED.hashCode())),
    HEADER (Style.EMPTY.withColor(new Color(172, 255, 231).hashCode())),
    TITLE (Style.EMPTY.withColor(Color.YELLOW.hashCode()));


    private final Style style;
    GUIStyles(Style style) {
        this.style = style;
    }

    public static Text getTrueFalse(boolean state) {
        if (state) return Text.literal("True").setStyle(TRUE.getStyle());
        return Text.literal("False").setStyle(FALSE.getStyle());
    }

    public Style getStyle() {
        return style;
    }

}
