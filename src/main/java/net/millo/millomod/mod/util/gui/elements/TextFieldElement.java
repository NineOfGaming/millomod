package net.millo.millomod.mod.util.gui.elements;

import net.millo.millomod.mod.Callback;
import net.millo.millomod.mod.util.gui.ElementFadeIn;
import net.millo.millomod.mod.util.gui.ScrollableEntryI;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.awt.*;

public class TextFieldElement extends TextFieldWidget implements ScrollableEntryI {


    private int x, y, realX, realY;

    ElementFadeIn fade = new ElementFadeIn(ElementFadeIn.Direction.RIGHT);
    TextRenderer textRenderer;
    public TextFieldElement(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
        setDrawsBackground(false);
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.realX = x;
        this.realY = y;
    }


    @Override
    protected boolean clicked(double mouseX, double mouseY) {
        return hovered && active;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        fade.fadeIn(delta);

        this.hovered = mouseX >= this.getRealX() && mouseY >= this.getRealY() && mouseX < this.getRealX() + this.width && mouseY < this.getRealY() + this.height;

        context.getMatrices().push();
        context.getMatrices().translate(fade.getXOffset(), fade.getYOffset(), 0);

        int x = getX();
        int y = getY();
        int color = new Color(0, 0, 0, (int)(fade.getProgress() * 150)).hashCode();
        int underlineColor = new Color(51, 51, 51, (int)(fade.getProgress() * 150)).hashCode();
        if (isHovered()) {
            color = new Color(12, 11, 9, (int)(fade.getProgress() * 150)).hashCode();
            underlineColor = new Color(151, 151, 151, (int)(fade.getProgress() * 150)).hashCode();
        }
        if (isFocused()) {
            underlineColor = new Color(200, 200, 200, (int)(fade.getProgress() * 150)).hashCode();
        }

        context.fill(x, y, x + getWidth(), y + getHeight(), color);
        context.fill(x, y + getHeight() - 1, x + getWidth(), y + getHeight(), underlineColor);

        context.getMatrices().translate(4f, 4f, 0);
        super.renderWidget(context, mouseX, mouseY, delta);
        context.getMatrices().pop();

    }

    public void setRealX(int x) { realX = x; }
    public void setRealY(int y) { realY = y; }
    public int getRealX() {
        return realX;
    }
    public int getRealY() {
        return realY;
    }

    public void setFade(ElementFadeIn fade) {
        this.fade = fade;
    }
}
