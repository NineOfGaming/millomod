package net.millo.millomod.mod.features.gui.elements;

import net.millo.millomod.mod.features.gui.ScrollableEntryI;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

public class TextElement extends TextWidget implements ScrollableEntryI {

    public TextElement(Text message, TextRenderer textRenderer) {
        super(message, textRenderer);
    }

    public TextElement(int width, int height, Text message, TextRenderer textRenderer) {
        super(width, height, message, textRenderer);
    }

    public TextElement(int x, int y, int width, int height, Text message, TextRenderer textRenderer) {
        super(x, y, width, height, message, textRenderer);
    }

    public void setRealX(int x) {

    }
    public void setRealY(int y) {

    }
    @Override
    public int getRealX() {
        return 0;
    }

    @Override
    public int getRealY() {
        return 0;
    }
}
