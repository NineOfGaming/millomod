package net.millo.millomod.mod.util.gui.elements;

import net.millo.millomod.mod.util.gui.ClickableElementI;
import net.millo.millomod.mod.util.gui.ElementFadeIn;
import net.millo.millomod.mod.util.gui.ScrollableEntryI;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class IntegerElement implements Element, Selectable, ScrollableEntryI {

    private int x, y, realX, realY;
    private int min, max, value;
    private int width, height;
    private int startX, endX;

    private OnChange onChange;
    private TextRenderer textRenderer;

    private Text name;
    private boolean hovered = false;

    private final ElementFadeIn fade = new ElementFadeIn(ElementFadeIn.Direction.RIGHT);

    public IntegerElement(int x, int y, int min, int max, Text name, int current, OnChange onChange, TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
        this.x = x;
        this.y = y;
        this.min = min;
        this.max = max;
        this.value = current;
        this.onChange = onChange;
        this.name = name;

        height = 16;
        width = 200;

        startX = x + width / 2;
        endX = x + width - 3;
    }

    boolean dragging = false;


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        dragging = isHovered();
        if (isHovered())
            value = MathHelper.clamp((((int) mouseX - (startX - x + realX)) * max / (endX-startX)), min, max);
        return isHovered();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging) {
            value = MathHelper.clamp((((int) mouseX - (startX - x + realX)) * max / (endX-startX)), min, max);
        }
        return false;
    }

    public boolean isHovered() {
        return hovered;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        fade.fadeIn(delta);

        hovered = mouseX >= realX && mouseX < realX + width && mouseY >= realY && mouseY < realY + height;

        int color = new Color(0, 0, 0, (int)(fade.getProgress() * 150)).hashCode();
        if (isHovered()) color = new Color(12, 11, 9, (int)(fade.getProgress() * 150)).hashCode();


        context.getMatrices().push();
        context.getMatrices().translate(fade.getXOffset(), fade.getYOffset(), 0);

        context.fill(x, y, x+width, y+height, color);
        int xx = startX + (value * (endX-startX-2) / max);
        context.fill(startX, y+height/2, endX, y+height/2+1, 0xffffffff);
        context.fill(xx, y, xx+2, y + height, 0xffffffff);

        context.getMatrices().pop();

    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setRealX(int x) {
        realX = x;
    }

    @Override
    public void setRealY(int y) {
        realY = y;
    }

    @Override
    public int getRealX() {
        return realX;
    }

    @Override
    public int getRealY() {
        return realY;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    public interface OnChange {
        void onChange(int value);
    }
}
