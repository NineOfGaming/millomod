package net.millo.millomod.mod.features.impl.cache;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.millo.millomod.mod.util.gui.ClickableElementI;
import net.millo.millomod.mod.util.gui.ElementFadeIn;
import net.millo.millomod.mod.util.gui.ScrollableEntryI;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class LineElement implements ScrollableEntryI, Element, Widget, Selectable, ClickableElementI {
    private int x, y, realX, realY;
    protected int width, height;
    protected boolean hovered;
    public boolean visible = true;


    ElementFadeIn fade = new ElementFadeIn(ElementFadeIn.Direction.RIGHT);

    ArrayList<TextWidget> textWidgets = new ArrayList<>();
    HashMap<TextWidget, PressAction> pressActionMap = new HashMap<>();
    TextRenderer textRenderer;
    public LineElement() {
        this.textRenderer = CacheGUI.lastOpenedGUI.getTextRenderer();
        this.x = 0;
        this.y = 0;
        this.realX = x;
        this.realY = y;
        this.height = 12;
    }
    public void init(int width, int height) {
        this.width = width;
        this.height = height;
    }




    public LineElement addComponent(Text message) {
        textWidgets.add(new TextWidget(x, y, textRenderer.getWidth(message), height, message, textRenderer));
        return this;
    }
    public LineElement addComponent(Text message, Tooltip tooltip) {
        TextWidget w = new TextWidget(x, y, textRenderer.getWidth(message), height, message, textRenderer);
        w.setTooltip(tooltip);
        textWidgets.add(w);
        return this;
    }
    public LineElement addComponent(Text message, PressAction action) {
        TextWidget w = new TextWidget(x, y, textRenderer.getWidth(message), height, message, textRenderer);
        textWidgets.add(w);
        pressActionMap.put(w, action);
        return this;
    }
    public LineElement addComponent(Text message, Tooltip tooltip, PressAction action) {
        TextWidget w = new TextWidget(x, y, textRenderer.getWidth(message), height, message, textRenderer);
        w.setTooltip(tooltip);
        textWidgets.add(w);
        pressActionMap.put(w, action);
        return this;
    }

    public LineElement addSpace() {
        return addComponent(Text.literal(" "));
    }
    public void setIndent(int indentation) {
        Text message = Text.literal("   ".repeat(indentation));
        textWidgets.add(0, new TextWidget(x, y, textRenderer.getWidth(message), height, message, textRenderer));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.visible) {
            this.hovered = mouseX >= this.getRealX() && mouseY >= this.getRealY() && mouseX < this.getRealX() + this.width && mouseY < this.getRealY() + this.height;
            this.renderWidget(context, mouseX, mouseY, delta);
        }
    }
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        fade.fadeIn(delta);

        int x = getX() + fade.getXOffset();
        int y = getY() + fade.getYOffset();
        int color = new Color(0, 0, 0, (int)(fade.getProgress() * 150)).hashCode();
        if (isHovered()) color = new Color(12, 11, 9, (int)(fade.getProgress() * 150)).hashCode();

//        context.fill(x, y, x+width, y+height, 0, color);

        int xOff = 0;

        context.getMatrices().push();
        for (TextWidget textWidget : textWidgets) {
            textWidget.setX(x);
            textWidget.setY(y);

            // Background Hover
            int xx = textWidget.getX() + xOff + getRealX();
            int yy = textWidget.getY() + getRealY();
            boolean hovered = mouseX >= xx && mouseX < xx+textWidget.getWidth() && mouseY >= yy && mouseY < yy + textWidget.getHeight();
            if (hovered) {
                context.fill(x, y, x+textWidget.getWidth(), y + textWidget.getHeight(), new Color(255, 255, 255, 20).hashCode());
            }

            // Text
            textWidget.renderWidget(context, mouseX, mouseY, delta);

            // Tooltip
            if (hovered && textWidget.getTooltip() != null) {
                textWidget.getTooltip().render(isHovered(), isFocused(), getNavigationFocus());
            }

            xOff += textWidget.getWidth();
            context.getMatrices().translate(textWidget.getWidth(), 0, 0);
        }
        context.getMatrices().pop();
    }



    public void setFade(ElementFadeIn fade) {
        this.fade = fade;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setRealX(int x) {
        realX = x;
    }

    public void setRealY(int y) {
        realY = y;
    }

    public int getRealX() {
        return realX;
    }

    public int getRealY() {
        return realY;
    }


    public void forEachChild(Consumer<ClickableWidget> consumer) {}



    @Override
    public void setFocused(boolean focused) {}

    @Override
    public boolean isFocused() {
        return false;
    }

    @Override
    public SelectionType getType() {
        return SelectionType.NONE;
    }
    public boolean isHovered() {
        return this.hovered;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }

    public ScreenRect getNavigationFocus() {
        return new ScreenRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isHovered()) return false;
        onPress(mouseX, mouseY, button);
        return true;
    }

    @Override
    public void onPress(double mouseX, double mouseY, int button) {
        int xOff = 0;
        for (TextWidget textWidget : textWidgets) {
            int xx = textWidget.getX() + xOff + getRealX();
            int yy = textWidget.getY() + getRealY();
            boolean hovered = mouseX >= xx && mouseX < xx+textWidget.getWidth() && mouseY >= yy && mouseY < yy + textWidget.getHeight();

            if (hovered && pressActionMap.containsKey(textWidget)) {
                pressActionMap.get(textWidget).onPress(this);
            }

            xOff += textWidget.getWidth();
        }
    }

    public LineElement addArguments(ArrayList<ArgumentItem> arguments) {
        addComponent(Text.literal("("));
        var args = arguments.iterator();
        while (args.hasNext()) {
            ArgumentItem arg = args.next();
            arg.addTo(this);
            if (args.hasNext()) addComponent(Text.literal(", "));
        }
        addComponent(Text.literal(")"));

        return this;
    }


    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(LineElement button);
    }
}
