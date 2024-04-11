package net.millo.millomod.mod.util.gui;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.awt.*;

public enum GUIStyles {
    // Common
    COMMENT (Style.EMPTY.withItalic(true).withColor(Color.gray.hashCode())),
    TRUE (Color.GREEN),
    FALSE (Color.RED),
    HEADER (new Color(0xacffe7)),
    TITLE (Color.YELLOW),
    NAME (new Color(0xf0acff)),
    ACTION(new Color(0xEFB598)),
    SELECT(new Color(0xd958ea)),

    // Scopes
    UNSAVED(Formatting.GRAY),
    LOCAL(Formatting.GREEN),
    SAVED(Formatting.YELLOW),
    LINE(new Color(0x55aaff)),

    // Item Types
    TEXT(Formatting.AQUA),
    NUMBER(Formatting.RED),
    VARIABLE(Formatting.YELLOW),
    BLOCK_TAG(Formatting.AQUA),
    GAME_VALUE(new Color(0xFFD47F)),
    ITEM(Formatting.GOLD),
    PARAMETER(new Color(0xFFD47F)),


    // Parameter Colors
    NUM(NUMBER.style),
    VAR(VARIABLE.style),
    LIST(Formatting.DARK_GREEN),
    DICT(new Color(0x55AAFF)),



    ;
    private final Style style;
    GUIStyles(Color color) {
        this(Style.EMPTY.withColor(color.hashCode()));
    }
    GUIStyles(Style style) {
        this.style = style;
    }

    GUIStyles(Formatting formatting) {
        this(Style.EMPTY.withColor(formatting));
    }

    public static MutableText getTrueFalse(boolean state) {
        if (state) return Text.translatable("config.millo.enabled").setStyle(TRUE.getStyle());
        return Text.translatable("config.millo.disabled").setStyle(FALSE.getStyle());
    }

    public Style getStyle() {
        return style;
    }

}
