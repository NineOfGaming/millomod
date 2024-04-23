package net.millo.millomod.mod.util.gui.elements;

import net.millo.millomod.mod.util.gui.ClickableElementI;
import net.millo.millomod.mod.util.gui.ElementFadeIn;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.function.Consumer;

public class ContextElement implements Element, Widget, Selectable, ClickableElementI, Drawable {

    private int x, y, height;
    private final int width;

    private final ElementFadeIn fade = new ElementFadeIn(ElementFadeIn.Direction.UP);

    TextRenderer textRenderer;
    public ContextElement(int width, TextRenderer textRenderer) {
        this.width = width;
        height = 0;
        this.textRenderer = textRenderer;
    }

    private void updateHeight() {
        height = 16 * buttons.size();
    }

    ArrayList<ButtonElement> buttons = new ArrayList<>();

    public ContextElement add(Text message, ButtonElement.PressAction onPress) {
        var button = new ButtonElement(0, 16 * buttons.size(), width, 16, message, onPress, textRenderer);
        button.fade.setProgress(5f);
        buttons.add(button);
        updateHeight();
        return this;
    }

    public boolean inBounds(double x, double y) {
        return x >= this.x && x < this.x + width && y >= this.y && y < this.y + height;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for (ButtonElement button : buttons) {
                if (button.isHovered()) button.onPress(mouseX, mouseY, mouseButton);
            }
        }
        return Element.super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onPress(double mouseX, double mouseY, int mouseButton) {
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        fade.fadeIn(delta);

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 1f);
        float scale = -0.5f * (fade.getProgress() + 0.41f) * (fade.getProgress() - 2.41f);
        context.getMatrices().scale(scale, scale, 0f);

        context.fill(0, 0, width, height, 0x13000000);
        buttons.forEach(i -> i.render(context, mouseX - x, mouseY - y, delta));
        context.drawBorder(0, 0, width, height, 0xffffffff);

        context.getMatrices().pop();
    }



    @Override
    public boolean isHovered() {
        return false;
    }

    @Override
    public void setFocused(boolean focused) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return new ScreenRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

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
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {

    }

}
